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
        tokens.substituteMultiCharSymbols()
        return tokens
    }

    private fun ArrayList<Token>.substituteMultiCharSymbols() {
        for (i in this.indices) {
            if (i < this.size - 1 && this[i] is Symbol && this[i + 1] is Symbol) {
                val symbol = this[i] as Symbol
                val nextSymbol = this[i + 1] as Symbol
                if (Symbol.check(symbol, nextSymbol)) {
                    this[i] = Symbol.getByToken((symbol + nextSymbol).toCharArray())!!
                    this.removeAt(i + 1)
                }
            }
        }
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