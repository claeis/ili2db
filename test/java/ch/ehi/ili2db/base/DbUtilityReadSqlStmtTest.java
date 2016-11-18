package ch.ehi.ili2db.base;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import org.junit.Test;

public class DbUtilityReadSqlStmtTest {

	@Test
	public void testEmptyFile() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader(""));
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void testSingleStmt() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("SELECT;"));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("SELECT;",stmt1);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void test2Stmt1Line() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("SELECT;DROP;"));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("SELECT;",stmt1);
		String stmt2=DbUtility.readSqlStmt(reader);
		assertEquals("DROP;",stmt2);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void test2StmtLf() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("SELECT;\nDROP;"));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("SELECT;",stmt1);
		String stmt2=DbUtility.readSqlStmt(reader);
		assertEquals("DROP;",stmt2);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void testMultipleNewlines() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("\n\nSELECT;\n\nDROP;\n\n"));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("SELECT;",stmt1);
		String stmt2=DbUtility.readSqlStmt(reader);
		assertEquals("DROP;",stmt2);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void testOnlyCmt() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("-- a comment"));
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void testCmtStmt() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("-- a comment\nSELECT;"));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("SELECT;",stmt1);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void testCmtStmtCmt() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("-- a comment\nSELECT;-- comment\n-- more comment "));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("SELECT;",stmt1);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void testQuote() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("'aaa'"));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("'aaa'",stmt1);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void testEmptyQuote() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("''"));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("''",stmt1);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void testEndQuote() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("'a'''"));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("'a'''",stmt1);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}
	@Test
	public void testBeginQuote() throws IOException {
		PushbackReader reader=new PushbackReader(new StringReader("'''a'"));
		String stmt1=DbUtility.readSqlStmt(reader);
		assertEquals("'''a'",stmt1);
		String stmtEnd=DbUtility.readSqlStmt(reader);
		assertNull(stmtEnd);
	}

}
