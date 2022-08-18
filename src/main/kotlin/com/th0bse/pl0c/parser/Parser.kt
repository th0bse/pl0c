package com.th0bse.pl0c.parser

import com.th0bse.pl0c.definitions.*

class Parser {


    private lateinit var stream: TokenStream

    private lateinit var syntaxTree: SyntaxTree

    fun parse(tokens: List<Token>): SyntaxTree {
        stream = TokenStream(tokens)
        program()
        return syntaxTree
    }

    private fun program() {
        val root = Leaf(SyntaxTreeNode(NonTerminal.PROGRAM))
        syntaxTree = SyntaxTree(root)
        block(root)
        if (stream.peek() == Symbol.PERIOD)
            root.leaves.add(Leaf(SyntaxTreeNode(Terminal.STOP, stream.next())))
        else throw RuntimeException("Dot (.) expected")
    }

    private fun block(leaf: Leaf) {
        var blockLeaf = Leaf(SyntaxTreeNode(NonTerminal.BLOCK))
        var currentLeaf: Leaf
        leaf.leaves.add(blockLeaf)
        if (stream.peek() == Keyword.CONST) {
            do {
                stream.next() // discard const keyword / separating comma
                currentLeaf = Leaf(SyntaxTreeNode(NonTerminal.CONSTANT))
                blockLeaf.leaves.add(currentLeaf)
                constant(currentLeaf)
            } while (stream.peek() == Symbol.COMMA)
            if (stream.next() != Symbol.SEMICOLON) throw RuntimeException("Semicolon (;) expected")
        }
        if (stream.peek() == Keyword.VAR) {
            do {
                stream.next() // discard var keyword / separating comma
                currentLeaf = Leaf(SyntaxTreeNode(NonTerminal.VARIABLE))
                blockLeaf.leaves.add(currentLeaf)
                variable(currentLeaf)
            } while (stream.peek() == Symbol.COMMA)
            if (stream.next() != Symbol.SEMICOLON) throw RuntimeException("Semicolon (;) expected")
        }
        if (stream.peek() == Keyword.PROCEDURE) {
            while (stream.peek() == Keyword.PROCEDURE) {
                procedure(blockLeaf)
            }
        }

        println("DEBUG")

        statement(blockLeaf)
    }

    private fun constant(leaf: Leaf) {
        if (stream.peek() is Identifier) {
            leaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next())))
            if (stream.peek() == Symbol.EQUALS) {
                leaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.EQUAL, stream.next())))
                if (stream.peek() is Num) {
                    leaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.NUMBER, stream.next())))
                } else throw RuntimeException("Number expected")
            } else throw RuntimeException("Equality sign (=) expected")
        } else throw RuntimeException("Identifier expected")
    }

    private fun variable(leaf: Leaf) {
        if (stream.peek() is Identifier) {
            leaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next())))
        } else throw RuntimeException("Identifier expected")
    }

    private fun procedure(leaf: Leaf) {
        var currentLeaf = Leaf(SyntaxTreeNode(Terminal.PROCEDURE, stream.next()))
        leaf.leaves.add(currentLeaf)

        if (stream.peek() is Identifier) {
            currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next())))
            if (stream.peek() != Symbol.SEMICOLON) throw RuntimeException("Semicolon (;) expected")
            block(currentLeaf)
        }
    }

    private fun statement(leaf: Leaf) {
        var currentLeaf = Leaf(SyntaxTreeNode(NonTerminal.STATEMENT))
        leaf.leaves.add(currentLeaf)
        when (stream.peek()) {
            is Identifier -> {
                currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next())))
                if (stream.peek() == Symbol.ASSIGNMENT) {
                    currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.ASSIGNMENT, stream.next())))
                    expression(currentLeaf)
                }
            }

            Keyword.CALL -> {
                currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.CALL, stream.next())))
                if (stream.peek() is Identifier) {
                    currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next())))
                }
            }

            Keyword.BEGIN -> {
                stream.next() // discard begin keyword, or semicolon token
                while (stream.next() == Symbol.SEMICOLON) {
                    statement(currentLeaf)
                }
                if (stream.next() != Keyword.END) throw RuntimeException("End expected")
            }

            // add remaining cases: IF, WHILE

            else -> throw RuntimeException("Not a valid statement")
        }
    }

    private fun expression(leaf: Leaf) {
        var currentLeaf = Leaf(SyntaxTreeNode(NonTerminal.EXPRESSION))
        leaf.leaves.add(currentLeaf)
        term(currentLeaf)
        while (stream.peek() == Symbol.PLUS || stream.peek() == Symbol.MINUS) {
            if (stream.peek() == Symbol.PLUS) {
                currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.PLUS, stream.next())))
            } else {
                currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.MINUS, stream.next())))
            }
            term(currentLeaf)
        }
    }

    private fun term(leaf: Leaf) {
        var currentLeaf = Leaf(SyntaxTreeNode(NonTerminal.TERM))
        leaf.leaves.add(currentLeaf)
        factor(currentLeaf)
        while (stream.peek() == Symbol.MULTIPLY || stream.peek() == Symbol.DIVIDE) {
            if (stream.peek() == Symbol.MULTIPLY) {
                currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.MULTIPLY, stream.next())))
            } else {
                currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.DIVIDE, stream.next())))
            }
            factor(currentLeaf)
        }
    }

    private fun factor(leaf: Leaf) {
        var currentLeaf = Leaf(SyntaxTreeNode(NonTerminal.FACTOR))
        leaf.leaves.add(currentLeaf)
        when (stream.peek()) {
            is Identifier -> {
                currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next())))
            }

            is Num -> {
                currentLeaf.leaves.add(Leaf(SyntaxTreeNode(Terminal.NUMBER, stream.next())))
            }

            Symbol.LPAREN -> {
                stream.next() // discard left parenthesis, association is already represented in the tree
                expression(currentLeaf)
                if (stream.next() != Symbol.RPAREN)
                    throw RuntimeException("Right parenthesis ()) expected.")
            }
        }
    }

}