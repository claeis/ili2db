// $ANTLR 2.7.7 (20060906): "ili2fgdb/src/ch/ehi/ili2fgdb/jdbc/parser/SqlSyntax.g" -> "SqlSyntax.java"$

	package ch.ehi.ili2fgdb.jdbc.parser;
	import ch.ehi.ili2fgdb.jdbc.sql.*;
	import java.util.*;
	import ch.ehi.basics.logging.EhiLogger;

public interface SqlSyntaxTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int LITERAL_DELETE = 4;
	int LITERAL_FROM = 5;
	int LITERAL_WHERE = 6;
	int LITERAL_INSERT = 7;
	int LITERAL_INTO = 8;
	int NAME = 9;
	int LPAREN = 10;
	int COMMA = 11;
	int RPAREN = 12;
	int LITERAL_VALUES = 13;
	int QUESTION = 14;
	int LITERAL_SELECT = 15;
	int LITERAL_AS = 16;
	int EQUALS = 17;
	int LITERAL_AND = 18;
	int LITERAL_ORDER = 19;
	int LITERAL_BY = 20;
	int LITERAL_UPDATE = 21;
	int LITERAL_SET = 22;
	int LITERAL_NULL = 23;
	int LITERAL_OR = 24;
	int LITERAL_NOT = 25;
	int LITERAL_IS = 26;
	int LITERAL_LIKE = 27;
	int LITERAL_ESCAPE = 28;
	int LITERAL_IN = 29;
	int LITERAL_BETWEEN = 30;
	int LITERAL_EXISTS = 31;
	// "<>" = 32
	// "<" = 33
	// ">" = 34
	// "<=" = 35
	// ">=" = 36
	// "+" = 37
	// "-" = 38
	// "*" = 39
	// "/" = 40
	int DOT = 41;
	int LITERAL_ASC = 42;
	int LITERAL_DESC = 43;
	int LITERAL_DEFAULT = 44;
	int LITERAL_CHAR = 45;
	int LITERAL_VARCHAR = 46;
	int LITERAL_INTEGER = 47;
	int LITERAL_INT = 48;
	int LITERAL_SMALLINT = 49;
	int LITERAL_NUMERIC = 50;
	int LITERAL_DECIMAL = 51;
	int LITERAL_REAL = 52;
	int LITERAL_DOUBLE = 53;
	int LITERAL_PRECISION = 54;
	int LITERAL_FLOAT = 55;
	int LITERAL_DATE = 56;
	int LITERAL_TIME = 57;
	int LITERAL_TIMESTAMP = 58;
	int LITERAL_BINARY = 59;
	int LITERAL_VARBINARY = 60;
	// "yyyy-mm-dd hh:mm:ss.ss" = 61
	int STRING = 62;
	int NUMBER = 63;
	int WS = 64;
	int DIGIT = 65;
	int HEXDIGIT = 66;
	int LETTER = 67;
	int ESC = 68;
	int POSINT = 69;
}
