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
	}
  : "INSERT" "INTO" t:NAME {s.setTableName(t.getText());} 
  	( LPAREN c0:NAME {s.addField(c0.getText());}
  		(COMMA c1:NAME {s.addField(c1.getText());}
  		)*  RPAREN )?
        ( "VALUES" LPAREN QUESTION (COMMA QUESTION )* RPAREN );
                       

  select_statement 
	returns [SelectStmt stmt]
	{
	stmt=new SelectStmt();
	List<List<String>> fv=null;
	List<String> w0=null;
	List<String> w1=null;
	int paramIdx=0;
	}
  : "SELECT" fv=select_list_ce {
  				for(List<String> f:fv){
  					stmt.addField(f.get(f.size()-1));
  				}
  				}
                       "FROM" t:NAME (("AS")? NAME)? { stmt.setTableName(t.getText());}
                       		(COMMA tablename)*
                       // ("WHERE" search_condition)?
                       ("WHERE" w0=name_chain EQUALS QUESTION
                       		{
                           		stmt.addCond(new ColRef(w0.get(w0.size()-1)),new Param(paramIdx++));
                       		}
                           ("AND" w1=name_chain EQUALS QUESTION 
                       		{
                           		stmt.addCond(new ColRef(w1.get(w1.size()-1)),new Param(paramIdx++));
                       		}
                           )*
                       )?                       
                       
                       ("ORDER" "BY" columnname (COMMA columnname)*)?;

sub_query 
  {
  SelectStmt c=null;
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

  select_list_ce returns[List<List<String>> c]
  {
  c=new ArrayList<List<String>>();
  List<String> n0=null;
  List<String> n1=null;
  }
  : n0=name_chain {c.add(n0);}
  ( COMMA n1=name_chain {c.add(n1);}
  )*;
  
  select_list    : "*" | select_sublist (COMMA select_sublist)*;

  select_sublist : expression (("AS")? columnalias)? | ((tablename | correlationname )DOT "*");

  orderby            : "ORDER" "BY" sort_specification (COMMA sort_specification)*.;

  sort_specification : columnname ("ASC" | "DESC")?;

  length : numeric_literal;
  precision : numeric_literal;
  scale : numeric_literal;
  

  name_chain returns[List<String> c]
  {
  c=new ArrayList<String>();
  }
  : n0:NAME {c.add(n0.getText());}
  (  DOT n1:NAME {c.add(n1.getText());}
  )*;
  
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
    ( '"' | '\\' | 'u' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT )
  ;


STRING
  : '"'!
    ( ESC | ~( '"' | '\\' ) )*
    '"'!
  ;
  
protected POSINT
  : ( DIGIT )+
  ;
  
NUMBER          : 
 ( '+'! | '-')? POSINT
  ;


NAME   options { testLiterals = true; }
  :  LETTER
     ( LETTER | '_' | DIGIT )*
  ;


