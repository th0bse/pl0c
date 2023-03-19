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
