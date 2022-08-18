package com.th0bse.pl0c.lexer

import com.th0bse.pl0c.definitions.*
import java.io.BufferedReader

class Lexer {

    private var tokens = ArrayList<Token>()

    // TODO: add ability to recognize comments
    fun readUntilEndOrError(reader: BufferedReader): List<Token> {
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
                        // if token is colon ":" and last token is "=", remove
                        // last one and replace with assignment operator
                        if (tokens[tokens.size - 1] == Symbol.EQUALS && tokens[tokens.size - 2] == Symbol.COLON) {
                            tokens.remove(tokens[tokens.size - 2])
                            tokens[tokens.size - 1] = Symbol.ASSIGNMENT
                        }
                    }
                    newToken = true
                } else chars.add(char)
            }
            chars.clear()
        }

        return tokens
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