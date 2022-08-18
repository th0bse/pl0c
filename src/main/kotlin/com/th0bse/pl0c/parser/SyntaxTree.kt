package com.th0bse.pl0c.parser

import com.th0bse.pl0c.definitions.Token

class SyntaxTree(var root: Leaf)

class Leaf(
    var symbol: SyntaxTreeNode,
    val leaves: ArrayList<Leaf> = ArrayList()
)

class SyntaxTreeNode(
    private val node: Sym,
    private val token: Token? = null
)

interface Sym

enum class Terminal : Sym {
    IF, CALL, NUMBER, ASSIGNMENT, IDENTIFIER, STOP, PLUS, CONST, VAR,
    PROCEDURE, BEGIN, END, THEN, WHILE, DO, ODD, MINUS, DIVIDE, MULTIPLY, EQUAL,
    COMMA, SEMICOLON, GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, DIFFERENT,
    LPAREN, RPAREN
}

enum class NonTerminal : Sym {
    PROGRAM, BLOCK, CONSTANT, VARIABLE, PROCEDURE, STATEMENT, CONDITION, EXPRESSION, TERM, FACTOR
}

/*
EBNF:
program = block, "." ;

block = [const],
        [variable],
        {procedure}, statement ;

constant = "const", ident, "=", number, { ",", ident, "=", number}, ";" ;

variable = "var", ident, { ",", ident}, ";" ;

procedure = "procedure", ident, ";", block, ";" ;

statement = [ ident, ":=", expression | "call", ident |
            "begin", statement, { ";", statement }, ";", "end" |
            "if", condition, "then", statement |
            "while", condition, "do", statement ] ;

condition = "odd", expression | expression, ("=" | "#" | "<" | ">"), expression ;

expression = term, { ("+" | "-"), term } ;

term = factor, { ("*" | "/"), factor } ;

factor = ident | number | "(" expression ")" ;

ident = letter, {number | letter} ;
number = digit, {digit} ;

letter = ? all lower- and upper-case letters ? ;
digit = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 ;
 */