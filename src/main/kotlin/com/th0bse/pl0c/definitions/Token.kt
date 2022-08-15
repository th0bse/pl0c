package com.th0bse.pl0c.definitions

interface Token

enum class Keyword(
    val token: String,
) : Token {
    BEGIN("begin"),
    CALL("call"),
    CONST("const"),
    DO("do"),
    END("end"),
    IF("if"),
    ODD("odd"),
    PROCEDURE("procedure"),
    THEN("then"),
    VAR("var"),
    WHILE("while");

    companion object {
        fun check(chars: CharArray): Boolean =
            Keyword.values().any { it.token == chars.concatToString() }

        fun getByToken(chars: CharArray): Keyword? =
            Keyword.values().find { it.token == chars.concatToString() }
    }
}


enum class Symbol(
    val token: String
) : Token {
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    LPAREN("("),
    RPAREN(")"),
    EQUALS("="),
    NOT_EQUALS("#"),
    COMMA(","),
    PERIOD("."),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_EQUAL("["),
    GREATER_EQUAL("]"),
    SEMICOLON(";");

    companion object {
        fun check(chars: CharArray): Boolean =
            Symbol.values().any { it.token == chars.concatToString() }

        fun getByToken(chars: CharArray): Symbol? =
            Symbol.values().find { it.token == chars.concatToString() }
    }
}

fun Char.isDelimiter(): Boolean =
    this.isWhitespace() || Symbol.values().any { it.token == this.toString() }

class Num(chars: CharArray) : Token {

    private val num: Int

    init {
        num = chars.concatToString().toInt()
    }

    companion object {
        fun check(chars: CharArray): Boolean =
            chars.all { it.isDigit() }
    }
}

class Identifier(chars: CharArray) : Token {

    private val identifier: String

    init {
        identifier = chars.concatToString()
    }

    companion object {
        fun check(chars: CharArray): Boolean =
            chars.all { it.isLetterOrDigit() } && chars.first().isLetter()
    }
}