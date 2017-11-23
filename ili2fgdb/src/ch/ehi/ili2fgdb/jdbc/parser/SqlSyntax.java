// $ANTLR 2.7.7 (20060906): "ili2fgdb/src/ch/ehi/ili2fgdb/jdbc/parser/SqlSyntax.g" -> "SqlSyntax.java"$

	package ch.ehi.ili2fgdb.jdbc.parser;
	import ch.ehi.ili2fgdb.jdbc.sql.*;
	import java.util.*;
	import ch.ehi.basics.logging.EhiLogger;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class SqlSyntax extends antlr.LLkParser       implements SqlSyntaxTokenTypes
 {


protected SqlSyntax(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public SqlSyntax(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected SqlSyntax(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public SqlSyntax(TokenStream lexer) {
  this(lexer,1);
}

public SqlSyntax(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
}

	public final SqlStmt  statement() throws RecognitionException, TokenStreamException {
		SqlStmt c;
		
		
			c=new SqlStmt();
			
		
		switch ( LA(1)) {
		case LITERAL_DELETE:
		{
			delete_statement();
			break;
		}
		case LITERAL_INSERT:
		{
			c=insert_statement();
			break;
		}
		case LITERAL_SELECT:
		{
			c=select_statement();
			break;
		}
		case LITERAL_UPDATE:
		{
			c=update_statement_ce();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return c;
	}
	
	public final void delete_statement() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_DELETE);
		match(LITERAL_FROM);
		tablename();
		{
		if ((LA(1)==LITERAL_WHERE)) {
			match(LITERAL_WHERE);
			search_condition();
		}
		else if ((LA(1)==EOF)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final InsertStmt  insert_statement() throws RecognitionException, TokenStreamException {
		InsertStmt s;
		
		Token  t = null;
		Token  c0 = null;
		Token  c1 = null;
		
			s=new InsertStmt();
			int paramIdx=0;
			
		
		match(LITERAL_INSERT);
		match(LITERAL_INTO);
		t = LT(1);
		match(NAME);
		s.setTableName(t.getText());
		{
		if ((LA(1)==LPAREN)) {
			match(LPAREN);
			c0 = LT(1);
			match(NAME);
			s.addField(c0.getText());
			{
			_loop7:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					c1 = LT(1);
					match(NAME);
					s.addField(c1.getText());
				}
				else {
					break _loop7;
				}
				
			} while (true);
			}
			match(RPAREN);
		}
		else if ((LA(1)==LITERAL_VALUES)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		match(LITERAL_VALUES);
		match(LPAREN);
		{
		match(QUESTION);
		s.addValue(new Param(paramIdx++));
		}
		{
		_loop12:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				{
				match(QUESTION);
				s.addValue(new Param(paramIdx++));
				}
			}
			else {
				break _loop12;
			}
			
		} while (true);
		}
		match(RPAREN);
		}
		return s;
	}
	
	public final AbstractSelectStmt  select_statement() throws RecognitionException, TokenStreamException {
		AbstractSelectStmt stmt;
		
		Token  n = null;
		Token  s = null;
		
			stmt=null;
			AbstractSelectStmt stmt2=null;
			List<SelectValue> fv=null;
			SqlQname w0=null;
			SqlQname w1=null;
			SqlQname c=null;
			int paramIdx=0;
			Value v0=null;
			JoinStmt jstmt=null;
			
		
		match(LITERAL_SELECT);
		fv=select_list_ce();
		match(LITERAL_FROM);
		{
		stmt=from_item(fv);
		{
		_loop16:
		do {
			if ((LA(1)==LITERAL_LEFT)) {
				match(LITERAL_LEFT);
				match(LITERAL_JOIN);
				stmt2=from_item(fv);
				match(LITERAL_ON);
				w0=sqlqname();
				match(EQUALS);
				w1=sqlqname();
				
							if(jstmt==null){
					                       			jstmt=new JoinStmt(stmt,w0);
					                       			stmt=jstmt;
							}
							jstmt.addRight(stmt2,w1);
						
			}
			else {
				break _loop16;
			}
			
		} while (true);
		}
		
							if(jstmt!=null){
								for(SelectValue f:fv){
									stmt.addField(f);
								}
							}
				
		}
		{
		if ((LA(1)==LITERAL_WHERE)) {
			match(LITERAL_WHERE);
			w0=sqlqname();
			{
			if ((LA(1)==LITERAL_IS)) {
				{
				match(LITERAL_IS);
				match(LITERAL_NULL);
				
							stmt.addCond(new ColRef(w0.getLocalName()),new IsNull());
						
				}
			}
			else if ((LA(1)==EQUALS)) {
				{
				match(EQUALS);
				{
				switch ( LA(1)) {
				case QUESTION:
				{
					{
					match(QUESTION);
					v0=new Param(paramIdx++);
					}
					break;
				}
				case NUMBER:
				{
					{
					n = LT(1);
					match(NUMBER);
					v0=new IntConst(Integer.valueOf(n.getText()));
					}
					break;
				}
				case STRING:
				{
					{
					s = LT(1);
					match(STRING);
					v0=new StringConst(s.getText());
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				
						stmt.addCond(new ColRef(w0.getLocalName()),v0);
						
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			{
			_loop26:
			do {
				if ((LA(1)==LITERAL_AND)) {
					match(LITERAL_AND);
					w1=sqlqname();
					match(EQUALS);
					match(QUESTION);
					
							stmt.addCond(new ColRef(w1.getLocalName()),new Param(paramIdx++));
							
				}
				else {
					break _loop26;
				}
				
			} while (true);
			}
		}
		else if ((LA(1)==EOF||LA(1)==RPAREN||LA(1)==LITERAL_ORDER)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		if ((LA(1)==LITERAL_ORDER)) {
			match(LITERAL_ORDER);
			match(LITERAL_BY);
			c=sqlqname();
			stmt.orderBy(c.getLocalName());
			{
			if ((LA(1)==LITERAL_ASC)) {
				match(LITERAL_ASC);
				stmt.orderAsc();
			}
			else if ((LA(1)==EOF||LA(1)==RPAREN)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		else if ((LA(1)==EOF||LA(1)==RPAREN)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		return stmt;
	}
	
	public final UpdateStmt  update_statement_ce() throws RecognitionException, TokenStreamException {
		UpdateStmt stmt;
		
		Token  t = null;
		Token  c0 = null;
		Token  c1 = null;
		Token  w0 = null;
		Token  w1 = null;
		
			stmt=new UpdateStmt();
			List f=null;
			int paramIdx=0;
			
		
		match(LITERAL_UPDATE);
		t = LT(1);
		match(NAME);
		stmt.setTableName(t.getText());
		match(LITERAL_SET);
		c0 = LT(1);
		match(NAME);
		match(EQUALS);
		match(QUESTION);
		
					stmt.addSet(new ColRef(c0.getText()),new Param(paramIdx++));
				
		{
		_loop38:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				c1 = LT(1);
				match(NAME);
				match(EQUALS);
				match(QUESTION);
				
						stmt.addSet(new ColRef(c1.getText()),new Param(paramIdx++));
					
			}
			else {
				break _loop38;
			}
			
		} while (true);
		}
		{
		if ((LA(1)==LITERAL_WHERE)) {
			match(LITERAL_WHERE);
			w0 = LT(1);
			match(NAME);
			match(EQUALS);
			match(QUESTION);
			
					stmt.addCond(new ColRef(w0.getText()),new Param(paramIdx++));
					
			{
			_loop41:
			do {
				if ((LA(1)==LITERAL_AND)) {
					match(LITERAL_AND);
					w1 = LT(1);
					match(NAME);
					match(EQUALS);
					match(QUESTION);
					
							stmt.addCond(new ColRef(w1.getText()),new Param(paramIdx++));
							
				}
				else {
					break _loop41;
				}
				
			} while (true);
			}
		}
		else if ((LA(1)==EOF)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		return stmt;
	}
	
	public final void tablename() throws RecognitionException, TokenStreamException {
		
		
		identifier();
	}
	
	public final void search_condition() throws RecognitionException, TokenStreamException {
		
		
		boolean_term();
		{
		if ((LA(1)==LITERAL_OR)) {
			match(LITERAL_OR);
			search_condition();
		}
		else if ((LA(1)==EOF||LA(1)==RPAREN)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final List<SelectValue>  select_list_ce() throws RecognitionException, TokenStreamException {
		List<SelectValue> c;
		
		
		c=new ArrayList<SelectValue>();
		SelectValue n0=null;
		SelectValue n1=null;
		
		
		n0=select_sublist_ce();
		c.add(n0);
		{
		_loop97:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				n1=select_sublist_ce();
				c.add(n1);
			}
			else {
				break _loop97;
			}
			
		} while (true);
		}
		return c;
	}
	
	public final AbstractSelectStmt  from_item(
		List<SelectValue> fv
	) throws RecognitionException, TokenStreamException {
		AbstractSelectStmt stmt;
		
		Token  t = null;
		Token  ta = null;
		Token  t2 = null;
		
			stmt=null;
			AbstractSelectStmt subselect=null;
			
		
		if ((LA(1)==NAME)) {
			{
			t = LT(1);
			match(NAME);
			{
			if ((LA(1)==NAME||LA(1)==LITERAL_AS)) {
				{
				if ((LA(1)==LITERAL_AS)) {
					match(LITERAL_AS);
				}
				else if ((LA(1)==NAME)) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				ta = LT(1);
				match(NAME);
			}
			else if ((_tokenSet_0.member(LA(1)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			
					stmt=new FgdbSelectStmt();
					stmt.setTableName(t.getText());
					if(ta!=null){
						stmt.setTableAlias(ta.getText());
					}
					for(SelectValue f:fv){
						AbstractSelectStmt.addField(stmt,f);
					}
				
			}
		}
		else if ((LA(1)==LPAREN)) {
			{
			match(LPAREN);
			subselect=select_statement();
			match(RPAREN);
			{
			if ((LA(1)==LITERAL_AS)) {
				match(LITERAL_AS);
			}
			else if ((LA(1)==NAME)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			t2 = LT(1);
			match(NAME);
			
					stmt=new ComplexSelectStmt(subselect);
					stmt.setTableName(t2.getText());
					for(SelectValue f:fv){
						AbstractSelectStmt.addField(stmt,f);
					}
				
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		return stmt;
	}
	
	public final SqlQname  sqlqname() throws RecognitionException, TokenStreamException {
		SqlQname ret;
		
		Token  n0 = null;
		Token  n1 = null;
		
		ArrayList<String> c=new ArrayList<String>();
		ret=null;
		
		
		n0 = LT(1);
		match(NAME);
		c.add(n0.getText());
		{
		_loop119:
		do {
			if ((LA(1)==DOT)) {
				match(DOT);
				n1 = LT(1);
				match(NAME);
				c.add(n1.getText());
			}
			else {
				break _loop119;
			}
			
		} while (true);
		}
		ret=new SqlQname(c);
		return ret;
	}
	
	public final void sub_query() throws RecognitionException, TokenStreamException {
		
		
		AbstractSelectStmt c=null;
		
		
		c=select_statement();
	}
	
	public final void update_statement() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_UPDATE);
		tablename();
		match(LITERAL_SET);
		columnname();
		match(EQUALS);
		{
		if ((_tokenSet_1.member(LA(1)))) {
			expression();
		}
		else if ((LA(1)==LITERAL_NULL)) {
			match(LITERAL_NULL);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		_loop46:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				columnname();
				match(EQUALS);
				{
				if ((_tokenSet_1.member(LA(1)))) {
					expression();
				}
				else if ((LA(1)==LITERAL_NULL)) {
					match(LITERAL_NULL);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
			}
			else {
				break _loop46;
			}
			
		} while (true);
		}
		{
		if ((LA(1)==LITERAL_WHERE)) {
			match(LITERAL_WHERE);
			search_condition();
		}
		else if ((LA(1)==EOF)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void columnname() throws RecognitionException, TokenStreamException {
		
		
		identifier();
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		
		term();
		{
		_loop78:
		do {
			if ((LA(1)==43||LA(1)==44)) {
				{
				if ((LA(1)==43)) {
					match(43);
				}
				else if ((LA(1)==44)) {
					match(44);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				term();
			}
			else {
				break _loop78;
			}
			
		} while (true);
		}
	}
	
	public final void table() throws RecognitionException, TokenStreamException {
		
		
		tablename();
		{
		if ((LA(1)==NAME||LA(1)==LITERAL_AS)) {
			{
			if ((LA(1)==LITERAL_AS)) {
				match(LITERAL_AS);
			}
			else if ((LA(1)==NAME)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			correlationname();
		}
		else if ((LA(1)==EOF)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void correlationname() throws RecognitionException, TokenStreamException {
		
		
		identifier();
	}
	
	public final void boolean_term() throws RecognitionException, TokenStreamException {
		
		
		boolean_factor();
		{
		if ((LA(1)==LITERAL_AND)) {
			match(LITERAL_AND);
			boolean_term();
		}
		else if ((LA(1)==EOF||LA(1)==RPAREN||LA(1)==LITERAL_OR)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void boolean_factor() throws RecognitionException, TokenStreamException {
		
		
		{
		if ((LA(1)==LITERAL_NOT)) {
			match(LITERAL_NOT);
		}
		else if ((_tokenSet_2.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		boolean_primary();
	}
	
	public final void boolean_primary() throws RecognitionException, TokenStreamException {
		
		
		if ((_tokenSet_2.member(LA(1)))) {
			predicate();
		}
		else if ((LA(1)==LPAREN)) {
			match(LPAREN);
			search_condition();
			match(RPAREN);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void predicate() throws RecognitionException, TokenStreamException {
		
		
		if ((LA(1)==NAME)) {
			{
			columnname();
			match(LITERAL_IS);
			{
			if ((LA(1)==LITERAL_NOT)) {
				match(LITERAL_NOT);
			}
			else if ((LA(1)==LITERAL_NULL)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(LITERAL_NULL);
			}
		}
		else if ((_tokenSet_1.member(LA(1)))) {
			{
			expression();
			{
			if ((LA(1)==LITERAL_NOT)) {
				match(LITERAL_NOT);
			}
			else if ((LA(1)==LITERAL_LIKE)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(LITERAL_LIKE);
			pattern();
			{
			if ((LA(1)==LITERAL_ESCAPE)) {
				match(LITERAL_ESCAPE);
				escape_character();
			}
			else if ((_tokenSet_3.member(LA(1)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			}
		}
		else if ((_tokenSet_1.member(LA(1)))) {
			{
			expression();
			{
			if ((LA(1)==LITERAL_NOT)) {
				match(LITERAL_NOT);
			}
			else if ((LA(1)==LITERAL_IN)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(LITERAL_IN);
			match(LPAREN);
			value();
			{
			_loop67:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					value();
				}
				else {
					break _loop67;
				}
				
			} while (true);
			}
			match(RPAREN);
			}
		}
		else if ((_tokenSet_1.member(LA(1)))) {
			{
			expression();
			{
			if ((LA(1)==LITERAL_NOT)) {
				match(LITERAL_NOT);
			}
			else if ((LA(1)==LITERAL_IN)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(LITERAL_IN);
			match(LPAREN);
			sub_query();
			match(RPAREN);
			}
		}
		else if ((_tokenSet_1.member(LA(1)))) {
			{
			expression();
			comparison_operator();
			expression();
			}
		}
		else if ((_tokenSet_1.member(LA(1)))) {
			{
			expression();
			{
			if ((LA(1)==LITERAL_NOT)) {
				match(LITERAL_NOT);
			}
			else if ((LA(1)==LITERAL_BETWEEN)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(LITERAL_BETWEEN);
			expression();
			match(LITERAL_AND);
			expression();
			}
		}
		else if ((LA(1)==LITERAL_EXISTS)) {
			{
			match(LITERAL_EXISTS);
			match(LPAREN);
			sub_query();
			match(RPAREN);
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void pattern() throws RecognitionException, TokenStreamException {
		
		
		character_string_literal();
	}
	
	public final void escape_character() throws RecognitionException, TokenStreamException {
		
		
		character_string_literal();
	}
	
	public final void value() throws RecognitionException, TokenStreamException {
		
		
		if ((LA(1)==NUMBER||LA(1)==STRING||LA(1)==LITERAL_DATE)) {
			literal();
		}
		else if ((LA(1)==LITERAL_NULL)) {
			match(LITERAL_NULL);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void comparison_operator() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case EQUALS:
		{
			match(EQUALS);
			break;
		}
		case 38:
		{
			match(38);
			break;
		}
		case 39:
		{
			match(39);
			break;
		}
		case 40:
		{
			match(40);
			break;
		}
		case 41:
		{
			match(41);
			break;
		}
		case 42:
		{
			match(42);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void term() throws RecognitionException, TokenStreamException {
		
		
		factor();
		{
		_loop82:
		do {
			if ((LA(1)==45||LA(1)==46)) {
				{
				if ((LA(1)==45)) {
					match(45);
				}
				else if ((LA(1)==46)) {
					match(46);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				factor();
			}
			else {
				break _loop82;
			}
			
		} while (true);
		}
	}
	
	public final void factor() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case 43:
		{
			match(43);
			break;
		}
		case 44:
		{
			match(44);
			break;
		}
		case NAME:
		case LPAREN:
		case QUESTION:
		case NUMBER:
		case STRING:
		case LITERAL_DATE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		primary();
	}
	
	public final void primary() throws RecognitionException, TokenStreamException {
		
		
		if ((LA(1)==LPAREN)) {
			match(LPAREN);
			expression();
			match(RPAREN);
		}
		else if ((LA(1)==NAME)) {
			columnname();
		}
		else if ((LA(1)==NUMBER||LA(1)==STRING||LA(1)==LITERAL_DATE)) {
			literal();
		}
		else if ((LA(1)==NAME)) {
			function();
		}
		else if ((LA(1)==LPAREN)) {
			match(LPAREN);
			sub_query();
			match(RPAREN);
		}
		else if ((LA(1)==QUESTION)) {
			match(QUESTION);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void literal() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case STRING:
		{
			character_string_literal();
			break;
		}
		case NUMBER:
		{
			numeric_literal();
			break;
		}
		case LITERAL_DATE:
		{
			date_time_literal();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void function() throws RecognitionException, TokenStreamException {
		
		
		functionname();
		match(LPAREN);
		expression();
		{
		_loop88:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				expression();
			}
			else {
				break _loop88;
			}
			
		} while (true);
		}
		match(RPAREN);
	}
	
	public final void functionname() throws RecognitionException, TokenStreamException {
		
		
		identifier();
	}
	
	public final void character_string_literal() throws RecognitionException, TokenStreamException {
		
		
		match(STRING);
	}
	
	public final void numeric_literal() throws RecognitionException, TokenStreamException {
		
		
		match(NUMBER);
	}
	
	public final void date_time_literal() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_DATE);
		match(66);
	}
	
	public final void column() throws RecognitionException, TokenStreamException {
		
		
		{
		if ((LA(1)==NAME)) {
			qualifier();
			match(DOT);
		}
		else if ((LA(1)==NAME)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		columnname();
	}
	
	public final void qualifier() throws RecognitionException, TokenStreamException {
		
		
		identifier();
	}
	
	public final void valuelist() throws RecognitionException, TokenStreamException {
		
		
		value();
		{
		_loop94:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				value();
			}
			else {
				break _loop94;
			}
			
		} while (true);
		}
	}
	
	public final SelectValue  select_sublist_ce() throws RecognitionException, TokenStreamException {
		SelectValue c;
		
		Token  t = null;
		Token  n1 = null;
		Token  n2 = null;
		
		c=null;
		SqlQname n0=null;
		
		
		switch ( LA(1)) {
		case NAME:
		{
			n0=sqlqname();
			c=new SelectValueField(n0);
			break;
		}
		case STRING:
		{
			t = LT(1);
			match(STRING);
			{
			if ((LA(1)==LITERAL_AS)) {
				match(LITERAL_AS);
			}
			else if ((LA(1)==NAME)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			n1 = LT(1);
			match(NAME);
			c=new SelectValueString(n1.getText(),t.getText());
			break;
		}
		case LITERAL_NULL:
		{
			match(LITERAL_NULL);
			{
			if ((LA(1)==LITERAL_AS)) {
				match(LITERAL_AS);
			}
			else if ((LA(1)==NAME)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			n2 = LT(1);
			match(NAME);
			c=new SelectValueNull(n2.getText());
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return c;
	}
	
	public final void select_list() throws RecognitionException, TokenStreamException {
		
		
		if ((LA(1)==45)) {
			match(45);
		}
		else if ((_tokenSet_1.member(LA(1)))) {
			select_sublist();
			{
			_loop100:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					select_sublist();
				}
				else {
					break _loop100;
				}
				
			} while (true);
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void select_sublist() throws RecognitionException, TokenStreamException {
		
		
		if ((_tokenSet_1.member(LA(1)))) {
			expression();
			{
			if ((LA(1)==NAME||LA(1)==LITERAL_AS)) {
				{
				if ((LA(1)==LITERAL_AS)) {
					match(LITERAL_AS);
				}
				else if ((LA(1)==NAME)) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				columnalias();
			}
			else if ((LA(1)==EOF||LA(1)==COMMA)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		else if ((LA(1)==NAME)) {
			{
			{
			if ((LA(1)==NAME)) {
				tablename();
			}
			else if ((LA(1)==NAME)) {
				correlationname();
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(DOT);
			match(45);
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void columnalias() throws RecognitionException, TokenStreamException {
		
		
		identifier();
	}
	
	public final void orderby() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_ORDER);
		match(LITERAL_BY);
		sort_specification();
		{
		_loop111:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				sort_specification();
			}
			else {
				break _loop111;
			}
			
		} while (true);
		}
		matchNot(EOF);
	}
	
	public final void sort_specification() throws RecognitionException, TokenStreamException {
		
		
		columnname();
		{
		if ((LA(1)==LITERAL_ASC)) {
			match(LITERAL_ASC);
		}
		else if ((LA(1)==LITERAL_DESC)) {
			match(LITERAL_DESC);
		}
		else if (((LA(1) >= LITERAL_DELETE && LA(1) <= POSINT))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void length() throws RecognitionException, TokenStreamException {
		
		
		numeric_literal();
	}
	
	public final void precision() throws RecognitionException, TokenStreamException {
		
		
		numeric_literal();
	}
	
	public final void scale() throws RecognitionException, TokenStreamException {
		
		
		numeric_literal();
	}
	
	public final void identifier() throws RecognitionException, TokenStreamException {
		
		
		match(NAME);
	}
	
	public final void indexname() throws RecognitionException, TokenStreamException {
		
		
		identifier();
	}
	
	public final void column_definition() throws RecognitionException, TokenStreamException {
		
		
		columnname();
		data_type();
		{
		if ((LA(1)==LITERAL_DEFAULT)) {
			match(LITERAL_DEFAULT);
			default_value();
		}
		else if ((LA(1)==EOF||LA(1)==LITERAL_NULL||LA(1)==LITERAL_NOT)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		if ((LA(1)==LITERAL_NULL||LA(1)==LITERAL_NOT)) {
			{
			if ((LA(1)==LITERAL_NOT)) {
				match(LITERAL_NOT);
			}
			else if ((LA(1)==LITERAL_NULL)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(LITERAL_NULL);
		}
		else if ((LA(1)==EOF)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void data_type() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_CHAR:
		case LITERAL_VARCHAR:
		{
			character_string_type();
			break;
		}
		case LITERAL_INTEGER:
		case LITERAL_INT:
		case LITERAL_SMALLINT:
		case LITERAL_NUMERIC:
		case LITERAL_DECIMAL:
		{
			exact_numeric_type();
			break;
		}
		case LITERAL_REAL:
		case LITERAL_DOUBLE:
		case LITERAL_FLOAT:
		{
			approximate_numeric_type();
			break;
		}
		case LITERAL_DATE:
		case LITERAL_TIME:
		case LITERAL_TIMESTAMP:
		{
			datetime_type();
			break;
		}
		case LITERAL_BINARY:
		case LITERAL_VARBINARY:
		{
			binary_type();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void default_value() throws RecognitionException, TokenStreamException {
		
		
		value();
	}
	
	public final void character_string_type() throws RecognitionException, TokenStreamException {
		
		
		if ((LA(1)==LITERAL_CHAR)) {
			{
			match(LITERAL_CHAR);
			match(LPAREN);
			length();
			match(RPAREN);
			}
		}
		else if ((LA(1)==LITERAL_VARCHAR)) {
			{
			match(LITERAL_VARCHAR);
			match(LPAREN);
			length();
			match(RPAREN);
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void exact_numeric_type() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_INTEGER:
		{
			match(LITERAL_INTEGER);
			break;
		}
		case LITERAL_INT:
		{
			match(LITERAL_INT);
			break;
		}
		case LITERAL_SMALLINT:
		{
			match(LITERAL_SMALLINT);
			break;
		}
		case LITERAL_NUMERIC:
		{
			{
			match(LITERAL_NUMERIC);
			match(LPAREN);
			precision();
			{
			if ((LA(1)==COMMA)) {
				match(COMMA);
				scale();
			}
			else if ((LA(1)==RPAREN)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(RPAREN);
			}
			break;
		}
		case LITERAL_DECIMAL:
		{
			{
			match(LITERAL_DECIMAL);
			match(LPAREN);
			precision();
			{
			if ((LA(1)==COMMA)) {
				match(COMMA);
				scale();
			}
			else if ((LA(1)==RPAREN)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(RPAREN);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void approximate_numeric_type() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_REAL:
		{
			match(LITERAL_REAL);
			break;
		}
		case LITERAL_DOUBLE:
		{
			match(LITERAL_DOUBLE);
			match(LITERAL_PRECISION);
			break;
		}
		case LITERAL_FLOAT:
		{
			{
			match(LITERAL_FLOAT);
			{
			if ((LA(1)==LPAREN)) {
				match(LPAREN);
				precision();
				match(RPAREN);
			}
			else if ((_tokenSet_4.member(LA(1)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void datetime_type() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_DATE:
		{
			match(LITERAL_DATE);
			break;
		}
		case LITERAL_TIME:
		{
			match(LITERAL_TIME);
			break;
		}
		case LITERAL_TIMESTAMP:
		{
			match(LITERAL_TIMESTAMP);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void binary_type() throws RecognitionException, TokenStreamException {
		
		
		if ((LA(1)==LITERAL_BINARY)) {
			match(LITERAL_BINARY);
		}
		else if ((LA(1)==LITERAL_VARBINARY)) {
			match(LITERAL_VARBINARY);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"DELETE\"",
		"\"FROM\"",
		"\"WHERE\"",
		"\"INSERT\"",
		"\"INTO\"",
		"NAME",
		"'('",
		"','",
		"')'",
		"\"VALUES\"",
		"'?'",
		"\"SELECT\"",
		"\"LEFT\"",
		"\"JOIN\"",
		"\"ON\"",
		"'='",
		"\"IS\"",
		"\"NULL\"",
		"NUMBER",
		"STRING",
		"\"AND\"",
		"\"ORDER\"",
		"\"BY\"",
		"\"ASC\"",
		"\"AS\"",
		"\"UPDATE\"",
		"\"SET\"",
		"\"OR\"",
		"\"NOT\"",
		"\"LIKE\"",
		"\"ESCAPE\"",
		"\"IN\"",
		"\"BETWEEN\"",
		"\"EXISTS\"",
		"\"<>\"",
		"\"<\"",
		"\">\"",
		"\"<=\"",
		"\">=\"",
		"\"+\"",
		"\"-\"",
		"\"*\"",
		"\"/\"",
		"'.'",
		"\"DESC\"",
		"\"DEFAULT\"",
		"\"CHAR\"",
		"\"VARCHAR\"",
		"\"INTEGER\"",
		"\"INT\"",
		"\"SMALLINT\"",
		"\"NUMERIC\"",
		"\"DECIMAL\"",
		"\"REAL\"",
		"\"DOUBLE\"",
		"\"PRECISION\"",
		"\"FLOAT\"",
		"\"DATE\"",
		"\"TIME\"",
		"\"TIMESTAMP\"",
		"\"BINARY\"",
		"\"VARBINARY\"",
		"\"yyyy-mm-dd hh:mm:ss.ss\"",
		"WS",
		"DIGIT",
		"HEXDIGIT",
		"LETTER",
		"ESC",
		"POSINT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 33886274L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 2305869397505361408L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 2305869534944314880L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 2164264962L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 562954250485762L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	
	}
