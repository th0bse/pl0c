package com.th0bse.pl0c

import com.th0bse.pl0c.definitions.*
import java.io.BufferedReader

class Lexer {

    private var tokens = ArrayList<Token>()

    fun readUntilEndOrError(reader: BufferedReader) {
        var char: Char
        val chars = ArrayList<Char>()
        var token: Token
        var newToken: Boolean
        while (reader.ready()) {
            newToken = false
            while (!newToken) {
                char = reader.read().toChar()
                if (char.isDelimiter()) {
                    if (chars.size > 0) tokens.add(readToken(chars.toCharArray()))
                    if (!char.isWhitespace()) {
                        // can assert non-null b/c char.isDelimiter always true
                        tokens.add(Symbol.getByToken(CharArray(1) { char })!!)
                    }
                    newToken = true
                } else chars.add(char)
            }
            chars.clear()
        }
    }

    private fun readToken(chars: CharArray): Token {
        return when {
            Keyword.check(chars) -> Keyword.getByToken(chars)
            Num.check(chars) -> Num(chars)
            Identifier.check(chars) -> Identifier(chars)
            else -> throw RuntimeException("Token could not be parsed")
        }!! // can assert non-null b/c everything is explicitly checked beforehand
    }
}