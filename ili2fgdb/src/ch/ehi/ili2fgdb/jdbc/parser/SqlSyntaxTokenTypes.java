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
	int NUMBER = 18;
	int STRING = 19;
	int LITERAL_AND = 20;
	int LITERAL_ORDER = 21;
	int LITERAL_BY = 22;
	int LITERAL_ASC = 23;
	int LITERAL_UPDATE = 24;
	int LITERAL_SET = 25;
	int LITERAL_NULL = 26;
	int LITERAL_OR = 27;
	int LITERAL_NOT = 28;
	int LITERAL_IS = 29;
	int LITERAL_LIKE = 30;
	int LITERAL_ESCAPE = 31;
	int LITERAL_IN = 32;
	int LITERAL_BETWEEN = 33;
	int LITERAL_EXISTS = 34;
	// "<>" = 35
	// "<" = 36
	// ">" = 37
	// "<=" = 38
	// ">=" = 39
	// "+" = 40
	// "-" = 41
	// "*" = 42
	// "/" = 43
	int DOT = 44;
	int LITERAL_DESC = 45;
	int LITERAL_DEFAULT = 46;
	int LITERAL_CHAR = 47;
	int LITERAL_VARCHAR = 48;
	int LITERAL_INTEGER = 49;
	int LITERAL_INT = 50;
	int LITERAL_SMALLINT = 51;
	int LITERAL_NUMERIC = 52;
	int LITERAL_DECIMAL = 53;
	int LITERAL_REAL = 54;
	int LITERAL_DOUBLE = 55;
	int LITERAL_PRECISION = 56;
	int LITERAL_FLOAT = 57;
	int LITERAL_DATE = 58;
	int LITERAL_TIME = 59;
	int LITERAL_TIMESTAMP = 60;
	int LITERAL_BINARY = 61;
	int LITERAL_VARBINARY = 62;
	// "yyyy-mm-dd hh:mm:ss.ss" = 63
	int WS = 64;
	int DIGIT = 65;
	int HEXDIGIT = 66;
	int LETTER = 67;
	int ESC = 68;
	int POSINT = 69;
}
