package com.th0bse.pl0c.lexer

import com.th0bse.pl0c.definitions.*
import java.io.BufferedReader

class Lexer {

    private var tokens = ArrayList<Token>()

    fun readUntilEndOrError(reader: BufferedReader): List<Token> {
        val chars = ArrayList<Char>()
        var newToken = false
        var lastChar = ' '
        while (reader.ready()) {
            while (!newToken) {
                reader.read().toChar().also {
                    when {
                        lastChar == it && it == '/' -> {
                            tokens.removeLast()
                            reader.readLine()
                            newToken = true
                        }

                        it.isDelimiter() -> {
                            if (chars.size > 0) tokens.add(chars.readToken())
                            if (!it.isWhitespace()) {
                                val char = it
                                tokens.add(Symbol.getByToken(CharArray(1) { char })!!)
                            }
                            newToken = true
                        }

                        else -> chars.add(it)
                    }
                    lastChar = it
                }
            }
            chars.clear()
            newToken = false
        }
        return tokens
    }

    private fun ArrayList<Char>.readToken(): Token {
        return when {
            Keyword.check(this.toCharArray()) -> Keyword.getByToken(this.toCharArray())
            Num.check(this.toCharArray()) -> Num(this.toCharArray())
            Identifier.check(this.toCharArray()) -> Identifier(this.toCharArray())
            else -> throw RuntimeException("Token could not be parsed")
        }!!
    }
}