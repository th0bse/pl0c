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
    EXCLAMATION("!"),
    NOT_EQUALS(EXCLAMATION + EQUALS),
    COMMA(","),
    PERIOD("."),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_EQUAL(LESS_THAN + EQUALS),
    GREATER_EQUAL(GREATER_THAN + EQUALS),
    SEMICOLON(";"),
    COLON(":"),
    ASSIGNMENT(COLON + EQUALS);

    operator fun plus(other: Symbol): String = this.token + other.token

    fun precedence(): Int {
        return when (this) {
            PLUS, MINUS -> 1
            MULTIPLY, DIVIDE -> 2
            else -> 0
        }
    }

    companion object {
        fun check(chars: CharArray): Boolean =
            Symbol.values().any { it.token == chars.concatToString() }

        fun check(vararg t: Symbol): Boolean =
            Symbol.values().any { it.token == t.map { it.token }.joinToString("") }

        fun getByToken(chars: CharArray): Symbol? =
            Symbol.values().find { it.token == chars.concatToString() }
    }
}

fun Char.isDelimiter(): Boolean =
    this.isWhitespace() || Symbol.values().any { it.token == this.toString() }

class Num(chars: CharArray) : Token {

    val num: Int

    init {
        num = chars.concatToString().toInt()
    }

    companion object {
        fun check(chars: CharArray): Boolean =
            chars.all { it.isDigit() }
    }
}

class Identifier(chars: CharArray) : Token {

    val identifier: String

    init {
        identifier = chars.concatToString()
    }

    companion object {
        fun check(chars: CharArray): Boolean =
            chars.all { it.isLetterOrDigit() } && chars.first().isLetter()
    }
}