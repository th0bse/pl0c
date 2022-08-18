package com.th0bse.pl0c.parser

import com.th0bse.pl0c.definitions.Token

class TokenStream(
    private val tokenList: List<Token>
) {

    private var currentIndex = 0

    fun next(): Token = tokenList[currentIndex++]

    fun peek(): Token = tokenList[currentIndex]
}