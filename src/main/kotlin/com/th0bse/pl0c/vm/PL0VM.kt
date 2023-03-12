package com.th0bse.pl0c.vm

import java.util.*

class PL0VM {

    private val stack = Stack<Int>()

    private var programCounter = 0
    private var basePointer = 0

    private lateinit var currentInstruction: Instruction

    private fun calculateBase(level: Int): Int {
        var base = basePointer
        var l = level
        while (l > 0) {
            base = stack[base]
            l--
        }
        return base
    }

    fun execute(instructions: List<Instruction>) {
        println("Executing program...")
        while (programCounter < instructions.size) {
            currentInstruction = instructions[programCounter++]
            when (currentInstruction.mnemonic) {
                Mnemonic.LIT -> {
                    stack.push(currentInstruction.address)
                }

                Mnemonic.LOD -> {
                    stack.push(stack[calculateBase(currentInstruction.level) + currentInstruction.address])
                }

                Mnemonic.STO -> {
                    stack[calculateBase(currentInstruction.level) + currentInstruction.address] = stack.pop()
                    println("STO: ${stack[calculateBase(currentInstruction.level) + currentInstruction.address]}")
                }

                Mnemonic.CAL -> {
                    stack.push(calculateBase(currentInstruction.level))
                    stack.push(programCounter)
                    basePointer = stack.size - 2
                    programCounter = currentInstruction.address
                }

                Mnemonic.INT -> {
                    for (i in 0 until currentInstruction.address) {
                        stack.push(0)
                    }
                }

                Mnemonic.JMP -> {
                    programCounter = currentInstruction.address
                }

                Mnemonic.JPC -> {
                    if (stack.pop() == 0) {
                        programCounter = currentInstruction.address
                    }
                }

                Mnemonic.RET -> {
                    stack.setSize(basePointer)
                    if (stack.size == 0) {
                        programCounter = instructions.size
                        break
                    }
                    programCounter = stack.pop()
                    basePointer = stack.pop()
                }

                Mnemonic.NEG -> {
                    stack.push(-stack.pop())
                }

                Mnemonic.ADD -> {
                    stack.push(stack.pop() + stack.pop())
                }

                Mnemonic.SUB -> {
                    stack.push(-stack.pop() + stack.pop())
                }

                Mnemonic.MUL -> {
                    stack.push(stack.pop() * stack.pop())
                }

                Mnemonic.DIV -> {
                    stack.push(stack.pop() / stack.pop())
                }

                Mnemonic.MOD -> {
                    stack.push(stack.pop() % stack.pop())
                }

                Mnemonic.EQU -> {
                    stack.push(if (stack.pop() == stack.pop()) 1 else 0)
                }

                Mnemonic.NEQ -> {
                    stack.push(if (stack.pop() != stack.pop()) 1 else 0)
                }

                Mnemonic.LTH -> {
                    stack.push(if (stack.pop() < stack.pop()) 1 else 0)
                }

                Mnemonic.LEQ -> {
                    stack.push(if (stack.pop() <= stack.pop()) 1 else 0)
                }

                Mnemonic.GTH -> {
                    stack.push(if (stack.pop() > stack.pop()) 1 else 0)
                }

                Mnemonic.GEQ -> {
                    stack.push(if (stack.pop() >= stack.pop()) 1 else 0)
                }
            }
        }
        println("Program execution finished.")
    }
}