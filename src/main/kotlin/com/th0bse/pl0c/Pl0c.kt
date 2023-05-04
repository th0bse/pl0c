package com.th0bse.pl0c

import com.th0bse.pl0c.generator.CodeGenerator
import com.th0bse.pl0c.lexer.Lexer
import com.th0bse.pl0c.parser.Parser
import com.th0bse.pl0c.semantics.SemanticAnalyzer
import com.th0bse.pl0c.vm.PL0VM
import java.io.BufferedReader
import java.io.InputStreamReader

class Pl0c {

    private val lexer = Lexer()
    private val parser = Parser()
    private val semanticAnalyzer = SemanticAnalyzer()
    private val generator = CodeGenerator()

    fun read() {
        var tokens = lexer.readUntilEndOrError(BufferedReader(InputStreamReader(System.`in`)))
        var tree = parser.parse(tokens)
        tree = semanticAnalyzer.analyze(tree)
        var program = generator.generateProgram(tree)
        PL0VM().execute(program)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Pl0c().read()
        }
    }
}
