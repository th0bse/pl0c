package com.th0bse.pl0c

import java.io.BufferedReader
import java.io.InputStreamReader

class Pl0c {

    private val lexer = Lexer()

    fun read() {
        lexer.readUntilEndOrError(BufferedReader(InputStreamReader(System.`in`)))
        println("DEBUG")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // start program in here
            Pl0c().read()
        }
    }
}