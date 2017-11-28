header
{
	package ch.ehi.ili2fgdb.jdbc.parser;
	import ch.ehi.ili2fgdb.jdbc.sql.*;
	import java.util.*;
	import ch.ehi.basics.logging.EhiLogger;
}

class SqlSyntax extends Parser;

options
{
  codeGenMakeSwitchThreshold = 3;
  codeGenBitsetTestThreshold = 4;
  buildAST=false;
  defaultErrorHandler=false;
}

{
}

statement 
	returns [SqlStmt c]
	{
	c=new SqlStmt();
	}
	:
	delete_statement
	| c=insert_statement
	| c=select_statement
	| c=update_statement_ce;


  delete_statement : "DELETE" "FROM" tablename ("WHERE" search_condition)?;

  insert_statement 
	returns [InsertStmt s]
	{
	s=new InsertStmt();
	int paramIdx=0;
	}
  : "INSERT" "INTO" t:NAME {s.setTableName(t.getText());} 
  	( LPAREN c0:NAME {s.addField(c0.getText());}
  		(COMMA c1:NAME {s.addField(c1.getText());}
  		)*  RPAREN )?
        ( "VALUES" LPAREN 
        		(QUESTION {s.addValue(new Param(paramIdx++));}
        		) 
        		
        	(COMMA  
        		(QUESTION {s.addValue(new Param(paramIdx++));}
        		) 
        	)* RPAREN );
                       

  select_statement 
	returns [AbstractSelectStmt stmt]
	{
	stmt=null;
	AbstractSelectStmt stmt2=null;
	List<SelectValue> fv=null;
	SqlQname w0=null;
	SqlQname w1=null;
	SqlQname c=null;
	int paramIdx=0;
	Value v0=null;
	JoinStmt jstmt=null;
	}
  : "SELECT" fv=select_list_ce 
                       "FROM"  (stmt=from_item[fv]
                       		( "LEFT" "JOIN" stmt2=from_item[fv] "ON" w0=sqlqname EQUALS w1=sqlqname {
                       			if(jstmt==null){
	                       			jstmt=new JoinStmt(stmt,w0);
	                       			stmt=jstmt;
                       			}
                       			jstmt.addRight(stmt2,w1);
                       		})*
                       		{
					if(jstmt!=null){
						for(SelectValue f:fv){
							stmt.addField(f);
						}
					}
                       		}
                       	)
                       // ("WHERE" search_condition)?
                       ("WHERE" w0=sqlqname 
                       		(("IS" "NULL"
                       			{
                           			stmt.addCond(new ColRef(w0.getLocalName()),new IsNull());
                           		}
                       		)
                       		| (EQUALS ( 
                       			(QUESTION {v0=new Param(paramIdx++);}) 
                       			|  (n:NUMBER {v0=new IntConst(Integer.valueOf(n.getText()));})
                       			|  (s:STRING {v0=new StringConst(s.getText());})
                       		)
                       		{
                           		stmt.addCond(new ColRef(w0.getLocalName()),v0);
                       		}
                       		))
                           ("AND" w1=sqlqname EQUALS QUESTION 
                       		{
                           		stmt.addCond(new ColRef(w1.getLocalName()),new Param(paramIdx++));
                       		}
                           )*
                       )?                       
                       
                       ("ORDER" "BY" c=sqlqname {stmt.orderBy(c.getLocalName());} ("ASC" {stmt.orderAsc();})?)?;

from_item[List<SelectValue> fv]
	returns [AbstractSelectStmt stmt]
	{
	stmt=null;
	AbstractSelectStmt subselect=null;
	}
  :
  (t:NAME (("AS")? ta:NAME)? { 
		stmt=new FgdbSelectStmt();
		stmt.setTableName(t.getText());
		if(ta!=null){
			stmt.setTableAlias(ta.getText());
		}
		for(SelectValue f:fv){
			AbstractSelectStmt.addField(stmt,f);
		}
	})
	| ( LPAREN subselect=select_statement RPAREN ("AS")? t2:NAME {
		stmt=new ComplexSelectStmt(subselect);
		stmt.setTableName(t2.getText());
		for(SelectValue f:fv){
			AbstractSelectStmt.addField(stmt,f);
		}
	})
  ;
  
sub_query 
  {
  AbstractSelectStmt c=null;
  }
  : 
  c=select_statement
  ;
                       
  update_statement_ce 
	returns [UpdateStmt stmt]
	{
	stmt=new UpdateStmt();
	List f=null;
	int paramIdx=0;
	}
  : "UPDATE" t:NAME { stmt.setTableName(t.getText());}
                       "SET" c0:NAME EQUALS QUESTION 
                       		{
                       			stmt.addSet(new ColRef(c0.getText()),new Param(paramIdx++));
                       		}
                           (COMMA c1:NAME EQUALS QUESTION 
                           	{
                           		stmt.addSet(new ColRef(c1.getText()),new Param(paramIdx++));
                           	}
                           )*
                       ("WHERE" w0:NAME EQUALS QUESTION
                       		{
                           		stmt.addCond(new ColRef(w0.getText()),new Param(paramIdx++));
                       		}
                           ("AND" w1:NAME EQUALS QUESTION 
                       		{
                           		stmt.addCond(new ColRef(w1.getText()),new Param(paramIdx++));
                       		}
                           )*
                       )?;
  update_statement 
  : "UPDATE" tablename
                       "SET" columnname EQUALS (expression | "NULL")
                           (COMMA columnname EQUALS (expression | "NULL") )*
                       ("WHERE" search_condition)?;

  table : tablename (("AS")? correlationname)?;

  
  search_condition : boolean_term ("OR" search_condition)?;

  boolean_term     : boolean_factor ("AND" boolean_term)?;

  boolean_factor   : ("NOT")? boolean_primary;

  boolean_primary  : predicate | LPAREN search_condition RPAREN;

  predicate        : (columnname "IS"  ("NOT")? "NULL") |
                       (expression ("NOT")? "LIKE" pattern ("ESCAPE" escape_character)? )|
                       (expression ("NOT")? "IN" LPAREN value (COMMA value)* RPAREN )|
                       (expression ("NOT")? "IN" LPAREN sub_query RPAREN )|
                       (expression comparison_operator expression )|
                       (expression ("NOT")? "BETWEEN" expression "AND" expression )|
                       ("EXISTS" LPAREN sub_query RPAREN);

  comparison_operator : EQUALS | "<>" | "<" | ">" | "<=" | ">=";

  expression  : term ( ( "+" | "-" ) term)*;

  term        : factor (( "*" | "/" ) factor)*;

  factor      : ( "+" | "-" )? primary;

  primary     : LPAREN expression RPAREN | columnname | literal | function | LPAREN sub_query RPAREN | QUESTION;

  function    : functionname LPAREN expression (COMMA expression)* RPAREN;

  literal     : character_string_literal | numeric_literal | date_time_literal;

  column      : (qualifier DOT)? columnname;

  valuelist   : value (COMMA value)*;

  select_list_ce returns[List<SelectValue> c]
  {
  c=new ArrayList<SelectValue>();
  SelectValue n0=null;
  SelectValue n1=null;
  }
  : n0=select_sublist_ce {c.add(n0);}
  ( COMMA n1=select_sublist_ce {c.add(n1);}
  )*;
  
  select_list    : "*" | select_sublist (COMMA select_sublist)*;

  select_sublist : expression (("AS")? columnalias)? | ((tablename | correlationname )DOT "*");
  
  select_sublist_ce returns[SelectValue c]
  {
  c=null;
  SqlQname n0=null;
  }
  : n0=sqlqname {c=new SelectValueField(n0);}
  | t:STRING ("AS")? n1:NAME {c=new SelectValueString(n1.getText(),t.getText());} 
  | "NULL" ("AS")? n2:NAME {c=new SelectValueNull(n2.getText());} 
  ;
  
  orderby            : "ORDER" "BY" sort_specification (COMMA sort_specification)*.;

  sort_specification : columnname ("ASC" | "DESC")?;

  length : numeric_literal;
  precision : numeric_literal;
  scale : numeric_literal;
  

  sqlqname returns[SqlQname ret]
  {
  ArrayList<String> c=new ArrayList<String>();
  ret=null;
  }
  : n0:NAME {c.add(n0.getText());}
  (  DOT n1:NAME {c.add(n1.getText());}
  )* {ret=new SqlQname(c);};
  
  tablename: identifier;
  columnname : identifier;
  indexname : identifier;
  functionname : identifier;
  qualifier : identifier;
  columnalias : identifier;
  correlationname : identifier;

  column_definition : columnname data_type ("DEFAULT" default_value)? (("NOT")? "NULL")?;

  data_type : character_string_type
                | exact_numeric_type
                | approximate_numeric_type
                | datetime_type
                | binary_type;

  character_string_type : ("CHAR" LPAREN length RPAREN)
                            | ("VARCHAR" LPAREN length RPAREN);

  exact_numeric_type    : "INTEGER" | "INT"
                            | "SMALLINT"
                            | ("NUMERIC" LPAREN precision (COMMA scale)? RPAREN)
                            | ("DECIMAL" LPAREN precision (COMMA scale)? RPAREN);

  approximate_numeric_type : "REAL" | "DOUBLE" "PRECISION" | ("FLOAT" (LPAREN precision RPAREN)?);

  datetime_type : "DATE" | "TIME" | "TIMESTAMP";

  binary_type   : "BINARY" | "VARBINARY";

  value     : literal | "NULL";
  default_value     : value;
  pattern : character_string_literal;
  escape_character : character_string_literal;
  date_time_literal        : "DATE" "yyyy-mm-dd hh:mm:ss.ss";
  character_string_literal : STRING;
  numeric_literal : NUMBER;
  identifier : NAME;
  
class SqlLexer extends Lexer;
options {
  charVocabulary = '\u0000'..'\u00FF'; // set the vocabulary to be all 8 bit binary values
  k=5;                   // number of lookahead characters
  testLiterals = false;  // do not test for literals by default
}

// Whitespace -- ignored
WS
  : (
      ' '
    | '\t'
    | '\f'

    // handle newlines
    | (
        options { generateAmbigWarnings=false; } :

        "\r\n"  // DOS
        | '\r'    // Macintosh
        | '\n'    // Unix
      )
      { newline(); }
    )+
    { $setType(Token.SKIP); }
  ;

COMMA options { paraphrase = "','"; }
  : ','
  ;

DOT options { paraphrase = "'.'"; }
  : '.'
  ;

QUESTION options { paraphrase = "'?'"; }
  : '?'
  ;
  
LPAREN options { paraphrase = "'('"; }
  : '('
  ;


RPAREN options { paraphrase = "')'"; }
  : ')'
  ;
  
EQUALS options { paraphrase = "'='"; }
  : '='
  ;

  
protected DIGIT
    :   '0' .. '9'
    ;

protected HEXDIGIT
  : DIGIT
    | 'a' .. 'f'
    | 'A' .. 'F'
  ;

protected LETTER
    :   'a' .. 'z' | 'A' .. 'Z'
    ;
  
protected ESC
  : '\\'
    ( '\'' | '\\' | 'u' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT )
  ;


STRING
  : '\''!
    ( ESC | ~( '\'' | '\\' ) )*
    '\''!
  ;
  
protected POSINT
  : ( DIGIT )+
  ;
  
NUMBER          : 
 ( '+'! | '-')? POSINT
  ;


NAME   options { testLiterals = true; }
  :  (LETTER | '_' )
     ( LETTER | '_' | DIGIT )*
  ;


