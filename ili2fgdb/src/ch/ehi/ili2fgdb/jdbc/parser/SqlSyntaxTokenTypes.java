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
	int LITERAL_LEFT = 16;
	int LITERAL_JOIN = 17;
	int LITERAL_ON = 18;
	int EQUALS = 19;
	int LITERAL_IS = 20;
	int LITERAL_NULL = 21;
	int NUMBER = 22;
	int STRING = 23;
	int LITERAL_AND = 24;
	int LITERAL_ORDER = 25;
	int LITERAL_BY = 26;
	int LITERAL_ASC = 27;
	int LITERAL_AS = 28;
	int LITERAL_UPDATE = 29;
	int LITERAL_SET = 30;
	int LITERAL_OR = 31;
	int LITERAL_NOT = 32;
	int LITERAL_LIKE = 33;
	int LITERAL_ESCAPE = 34;
	int LITERAL_IN = 35;
	int LITERAL_BETWEEN = 36;
	int LITERAL_EXISTS = 37;
	// "<>" = 38
	// "<" = 39
	// ">" = 40
	// "<=" = 41
	// ">=" = 42
	// "+" = 43
	// "-" = 44
	// "*" = 45
	// "/" = 46
	int DOT = 47;
	int LITERAL_DESC = 48;
	int LITERAL_DEFAULT = 49;
	int LITERAL_CHAR = 50;
	int LITERAL_VARCHAR = 51;
	int LITERAL_INTEGER = 52;
	int LITERAL_INT = 53;
	int LITERAL_SMALLINT = 54;
	int LITERAL_NUMERIC = 55;
	int LITERAL_DECIMAL = 56;
	int LITERAL_REAL = 57;
	int LITERAL_DOUBLE = 58;
	int LITERAL_PRECISION = 59;
	int LITERAL_FLOAT = 60;
	int LITERAL_DATE = 61;
	int LITERAL_TIME = 62;
	int LITERAL_TIMESTAMP = 63;
	int LITERAL_BINARY = 64;
	int LITERAL_VARBINARY = 65;
	// "yyyy-mm-dd hh:mm:ss.ss" = 66
	int WS = 67;
	int DIGIT = 68;
	int HEXDIGIT = 69;
	int LETTER = 70;
	int ESC = 71;
	int POSINT = 72;
}
