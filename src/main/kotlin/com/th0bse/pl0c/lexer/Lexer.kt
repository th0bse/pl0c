package com.th0bse.pl0c.lexer

import com.th0bse.pl0c.definitions.*
import java.io.BufferedReader
import java.util.*

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
        tokens.findAndConvertExpressions()
        return tokens
    }

    /**
     * Iterate over the token stream, find expressions and convert them to prefix notation
     */
    private fun ArrayList<Token>.findAndConvertExpressions() {
        var inExpression = false
        var expressionStartIndex = 0
        var expression = ArrayList<Token>()
        var newTokenList = this.clone() as ArrayList<Token>
        this.forEachIndexed { index, token ->
            // expression always ends on comma or semicolon
            if (token == Symbol.COMMA || token == Symbol.SEMICOLON) {
                inExpression = false
            }

            if (inExpression) {
                newTokenList.removeAt(index - expression.size)
                expression.add(token)
            }

            if (!inExpression && expression.size > 0) {
                expression.infixToPrefix()
                newTokenList.addAll(expressionStartIndex, expression)
                expression.clear()
            }

            // expression always starts with assignment
            if (token == Symbol.ASSIGNMENT) {
                inExpression = true
                expressionStartIndex = index + 1
            }
        }

        this.clear()
        this.addAll(newTokenList)
    }

    private fun ArrayList<Token>.infixToPrefix() {
        // shunting-yard algorithm for converting infix to postfix notation
        val operatorStack = ArrayList<Symbol>()
        val outputQueue = ArrayList<Token>()
        for (token in this) {
            if (token is Num || token is Identifier) {
                outputQueue.add(token)
            } else if (token is Symbol) {
                if (token == Symbol.RPAREN) {
                    while (operatorStack.last() != Symbol.LPAREN) {
                        outputQueue.add(operatorStack.removeLast())
                    }
                    operatorStack.removeLast()
                } else {
                    while (operatorStack.size > 0 && operatorStack.last().precedence() > token.precedence()) {
                        outputQueue.add(operatorStack.removeLast())
                    }
                    operatorStack.add(token)
                }
            }
        }
        while (operatorStack.size > 0) {
            outputQueue.add(operatorStack.removeLast())
        }
        // convert the postfix notation to prefix notation
        val stack = Stack<ArrayList<Token>>()
        outputQueue.forEach {
            when (it) {
                is Num, is Identifier -> stack.push(ArrayList<Token>().apply { add(it) })
                is Symbol -> {
                    val operand1 = stack.pop()
                    val operand2 = stack.pop()
                    stack.push(ArrayList<Token>().apply { add(it); addAll(operand2); addAll(operand1) })
                }
            }
        }

        this.clear()
        stack.forEach { this.addAll(it) }
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