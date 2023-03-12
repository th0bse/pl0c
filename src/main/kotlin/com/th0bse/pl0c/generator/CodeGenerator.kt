package com.th0bse.pl0c.generator

import com.th0bse.pl0c.definitions.Identifier
import com.th0bse.pl0c.definitions.Num
import com.th0bse.pl0c.parser.NonTerminal
import com.th0bse.pl0c.parser.SyntaxTree
import com.th0bse.pl0c.parser.SyntaxTreeNode
import com.th0bse.pl0c.parser.Terminal
import com.th0bse.pl0c.vm.Instruction
import com.th0bse.pl0c.vm.Mnemonic

class CodeGenerator {

    /**
     * Generated code as sequence of instructions
     */
    private val generatedCode = ArrayList<Instruction>()

    /**
     * Index of the instruction where execution should start
     * (after the program header)
     * Always at least 1 because of the JMP instruction
     */
    private var startIndex = 1

    /**
     * Index of the next free data allocation
     * This keeps track of how many variables and constants are declared
     */
    private var dataAllocationIndex = 2

    /**
     * Current level of nested blocks
     */
    private var currentLevel = 0

    /**
     * Mapping of identifiers to their data allocation index
     */
    private val identifierMapping = HashMap<String, Int>()

    /**
     * Mapping of constants to their values
     */
    private val constantMapping = HashMap<String, Int>()

    /**
     * Generates the program code from the syntax tree
     */
    fun generateProgram(syntaxTree: SyntaxTree): ArrayList<Instruction> {
        generateCode(syntaxTree.root)
        return generatedCode
    }

    /**
     * Generates the code for the given node and all its children
     */
    private fun generateCode(node: SyntaxTreeNode) {
        when (node.symbol) {
            NonTerminal.PROGRAM -> {
                node.children.forEach { generateCode(it) }
                generatedCode.add(0, Instruction(Mnemonic.JMP, 0, startIndex))
                generatedCode.add(1, Instruction(Mnemonic.INT, 0, dataAllocationIndex))
            }

            NonTerminal.BLOCK -> {
                node.children.forEach { generateCode(it) }
            }

            NonTerminal.CONSTANT -> {
                val identifier = (node.children[0].token as Identifier).identifier
                val value = (node.children[2].token as Num).num
                constantMapping[identifier] = value
            }

            NonTerminal.VARIABLE -> {
                val identifier = (node.children[0].token as Identifier).identifier
                identifierMapping[identifier] = dataAllocationIndex
                dataAllocationIndex++
            }

            NonTerminal.PROCEDURE -> {
                val identifier = (node.children[0].token as Identifier).identifier
                identifierMapping[identifier] = startIndex
                val procedureNode = node.children[1]
                currentLevel++
                generateCode(procedureNode)
                currentLevel--
            }

            NonTerminal.ASSIGNMENT -> {
                val identifier = (node.children[0].token as Identifier).identifier
                val index = identifierMapping[identifier] ?: throw Exception("Identifier $identifier not found")
                generateCode(node.children[1])
                generatedCode.add(Instruction(Mnemonic.STO, currentLevel, index))
            }

            NonTerminal.CALL -> {
                val identifier = (node.children[0].token as Identifier).identifier
                val index = identifierMapping[identifier] ?: throw Exception("Identifier $identifier not found")
                generatedCode.add(Instruction(Mnemonic.CAL, currentLevel, index))
            }

            NonTerminal.EXPRESSION -> {
                when (node.children.size) {
                    3 -> { // expression is an addition or subtraction
                        generateCode(node.children[0])
                        generateCode(node.children[2])
                        when (node.children[1].symbol) {
                            Terminal.PLUS -> generatedCode.add(Instruction(Mnemonic.ADD, 0, 0))
                            Terminal.MINUS -> generatedCode.add(Instruction(Mnemonic.SUB, 0, 0))
                            else -> throw Exception("Invalid operator")
                        }
                    }

                    2 -> { // expression is a unary minus
                        generateCode(node.children[1])
                        when (node.children[0].symbol) {
                            Terminal.MINUS -> {
                                generatedCode.add(Instruction(Mnemonic.NEG, 0, 0))
                            }

                            else -> throw Exception("Invalid operator")
                        }
                    }

                    1 -> { // expression is a term (multiplication or division, or a factor)
                        generateCode(node.children[0])
                    }
                }
            }

            NonTerminal.TERM -> {
                when (node.children.size) {
                    3 -> {
                        generateCode(node.children[0])
                        generateCode(node.children[2])
                        when (node.children[1].symbol) {
                            Terminal.MULTIPLY -> generatedCode.add(Instruction(Mnemonic.MUL, 0, 0))
                            Terminal.DIVIDE -> generatedCode.add(Instruction(Mnemonic.DIV, 0, 0))
                            else -> throw Exception("Invalid operator")
                        }
                    }

                    1 -> {
                        generateCode(node.children[0])
                    }
                }
            }

            NonTerminal.FACTOR -> {
                when (node.children[0].symbol) {
                    Terminal.NUMBER -> {
                        val value = (node.children[0].token as Num).num
                        generatedCode.add(Instruction(Mnemonic.LIT, 0, value))
                    }

                    Terminal.IDENTIFIER -> {
                        val identifier = (node.children[0].token as Identifier).identifier
                        if (constantMapping.containsKey(identifier)) {
                            val value = constantMapping[identifier] ?: throw Exception("Identifier $identifier not found")
                            generatedCode.add(Instruction(Mnemonic.LIT, currentLevel, value))
                        } else {
                            val index = identifierMapping[identifier] ?: throw Exception("Identifier $identifier not found")
                            generatedCode.add(Instruction(Mnemonic.LOD, currentLevel, index))
                        }
                    }

                    NonTerminal.EXPRESSION -> {
                        generateCode(node.children[0])
                    }

                    else -> throw Exception("Invalid factor")
                }
            }
        }
    }
}