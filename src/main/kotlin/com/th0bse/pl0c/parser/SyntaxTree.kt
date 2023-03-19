package com.th0bse.pl0c.parser

import com.th0bse.pl0c.definitions.Token

class SyntaxTree(var root: SyntaxTreeNode)

class SyntaxTreeNode(
    val symbol: Sym,
    val token: Token? = null,
    val children: ArrayList<SyntaxTreeNode> = ArrayList()
) {
    override fun toString(): String {
        return "SyntaxTreeNode(symbol=$symbol, token=$token)"
    }
}

interface Sym

enum class Terminal : Sym {
    CALL, NUMBER, IDENTIFIER, STOP, PLUS, ODD, MINUS, DIVIDE, MULTIPLY, EQUAL,
    GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, DIFFERENT
}

enum class NonTerminal : Sym {
    PROGRAM, BLOCK, CONSTANT, VARIABLE, PROCEDURE, ASSIGNMENT, CALL, CONDITION,
    WHILE, EXPRESSION, TERM, FACTOR
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

statement = [ assignment | call | statement_block |
            "if", condition, "then", statement |
            "while", condition, "do", statement ] ;

statement_block = "begin", statement, { ";", statement }, ";", "end"

assignment = ident, ":=", expression ;

call = "call", ident ;

condition = "odd", expression | expression, ("=" | "#" | "<" | ">"), expression ;

expression = term, { ("+" | "-"), term } ;

term = factor, { ("*" | "/"), factor } ;

factor = ident | number | "(" expression ")" ;

ident = letter, {number | letter} ;
number = digit, {digit} ;

letter = ? all lower- and upper-case letters ? ;
digit = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 ;
 */
