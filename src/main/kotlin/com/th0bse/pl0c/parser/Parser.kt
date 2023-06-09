package com.th0bse.pl0c.parser

import com.th0bse.pl0c.definitions.*
import java.util.Stack

class Parser {

  private lateinit var stream: TokenStream

  private lateinit var syntaxTree: SyntaxTree

  fun parse(tokens: List<Token>): SyntaxTree {
    stream = TokenStream(tokens)
    program()
    return syntaxTree
  }

  private fun program() {
    val root = SyntaxTreeNode(NonTerminal.PROGRAM)
    syntaxTree = SyntaxTree(root)
    block(root)
    if (stream.peek() == Symbol.PERIOD)
        root.children.add(SyntaxTreeNode(Terminal.STOP, stream.next()))
    else throw RuntimeException("Dot \".\" expected")
  }

  private fun block(node: SyntaxTreeNode) {
    var blockNode = SyntaxTreeNode(NonTerminal.BLOCK)
    var currentNode: SyntaxTreeNode
    node.children.add(blockNode)
    if (stream.peek() == Keyword.CONST) {
      do {
        stream.next() // discard const keyword / separating comma
        currentNode = SyntaxTreeNode(NonTerminal.CONSTANT)
        blockNode.children.add(currentNode)
        constant(currentNode)
      } while (stream.peek() == Symbol.COMMA)
      if (stream.next() != Symbol.SEMICOLON) throw RuntimeException("Semicolon \";\" expected")
    }
    if (stream.peek() == Keyword.VAR) {
      do {
        stream.next() // discard var keyword / separating comma
        currentNode = SyntaxTreeNode(NonTerminal.VARIABLE)
        blockNode.children.add(currentNode)
        variable(currentNode)
      } while (stream.peek() == Symbol.COMMA)
      if (stream.next() != Symbol.SEMICOLON) throw RuntimeException("Semicolon \";\" expected")
    }
    if (stream.peek() == Keyword.PROCEDURE) {
      while (stream.peek() == Keyword.PROCEDURE) {
        procedure(blockNode)
      }
    }

    statement(blockNode)
  }

  private fun constant(node: SyntaxTreeNode) {
    if (stream.peek() is Identifier) {
      node.children.add(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next()))
      if (stream.peek() == Symbol.ASSIGNMENT) {
        // node.children.add(SyntaxTreeNode(Terminal.EQUAL, stream.next()))
        stream.next() // discard assignment operator
        if (stream.peek() is Num) {
          node.children.add(SyntaxTreeNode(Terminal.NUMBER, stream.next()))
        } else throw RuntimeException("Number expected")
      } else throw RuntimeException("Assignment operator \":=\" expected")
    } else throw RuntimeException("Identifier expected")
  }

  private fun variable(node: SyntaxTreeNode) {
    if (stream.peek() is Identifier) {
      node.children.add(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next()))
    } else throw RuntimeException("Identifier expected")
  }

  private fun procedure(node: SyntaxTreeNode) {
    var currentNode = SyntaxTreeNode(NonTerminal.PROCEDURE, stream.next())
    node.children.add(currentNode)

    if (stream.peek() is Identifier) {
      currentNode.children.add(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next()))
      if (stream.next() != Symbol.SEMICOLON) throw RuntimeException("Semicolon \";\" expected")
      if (stream.next() != Keyword.BEGIN) throw RuntimeException("Begin keyword \"begin\" expected")
      if (stream.next() != Symbol.SEMICOLON) throw RuntimeException("Semicolon \";\" expected")

      block(currentNode)
    }

    if (stream.next() != Symbol.SEMICOLON) throw RuntimeException("Semicolon \";\" expected")
    if (stream.next() != Keyword.END) // discard end keyword
     throw RuntimeException("End keyword \"end\" expected")
    if (stream.next() != Symbol.SEMICOLON) // discard semicolon token
     throw RuntimeException("Semicolon \";\" expected")
  }

  private fun statement(node: SyntaxTreeNode) {
    var currentNode = node
    when (stream.peek()) {
      is Identifier -> {
        assignment(currentNode)
      }
      Keyword.CALL -> {
        currentNode.children.add(SyntaxTreeNode(Terminal.CALL, stream.next()))
        if (stream.peek() is Identifier) {
          currentNode.children.add(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next()))
        }
      }
      Keyword.BEGIN -> {
        stream.next() // discard begin keyword
        statement(currentNode)
        while (stream.next() == Symbol.SEMICOLON) {
          if (stream.peek() == Keyword.END) continue
          statement(currentNode)
        }
      }
      Keyword.IF -> {
        stream.next() // discard if keyword
        var conditionNode = SyntaxTreeNode(NonTerminal.CONDITION)
        node.children.add(conditionNode)
        condition(conditionNode)
        if (stream.next() != Keyword.THEN) throw RuntimeException("Keyword \"then\" expected")
        statement(conditionNode)
      }
      Keyword.WHILE -> {
        stream.next() // discard while keyword
        var loopNode = SyntaxTreeNode(NonTerminal.WHILE)
        node.children.add(loopNode)
        condition(loopNode)
        if (stream.next() != Keyword.DO) throw RuntimeException("Keyword \"do\" expected")
        statement(loopNode)
      }
      Keyword.END -> {
        stream.next() // discard end keyword
      }
      else -> throw RuntimeException("Not a valid statement")
    }
  }

  private fun assignment(node: SyntaxTreeNode) {
    var currentNode = SyntaxTreeNode(NonTerminal.ASSIGNMENT)
    node.children.add(currentNode)
    currentNode.children.add(SyntaxTreeNode(Terminal.IDENTIFIER, stream.next()))
    if (stream.next() == Symbol.ASSIGNMENT) { // discard assignment operator
      expression(currentNode)
    }
  }

  private fun condition(node: SyntaxTreeNode) {
    if (stream.peek() == Keyword.ODD) {
      node.children.add(SyntaxTreeNode(NonTerminal.ODD, stream.next()))
    } else {
      expression(node)
      when (stream.peek()) {
        Symbol.EQUALS -> {
          node.children.add((SyntaxTreeNode(NonTerminal.EQUAL, stream.next())))
        }
        Symbol.NOT_EQUALS -> {
          node.children.add(SyntaxTreeNode(NonTerminal.NOT_EQUAL, stream.next()))
        }
        Symbol.LESS_THAN -> {
          node.children.add(SyntaxTreeNode(NonTerminal.LESS, stream.next()))
        }
        Symbol.GREATER_THAN -> {
          node.children.add(SyntaxTreeNode(NonTerminal.GREATER, stream.next()))
        }
        Symbol.LESS_EQUAL -> {
          node.children.add(SyntaxTreeNode(NonTerminal.LESS_EQUAL, stream.next()))
        }
        Symbol.GREATER_EQUAL -> {
          node.children.add(SyntaxTreeNode(NonTerminal.GREATER_EQUAL, stream.next()))
        }
      }
      expression(node)
    }
  }

  private fun expression(node: SyntaxTreeNode) {
    var nodes: Stack<SyntaxTreeNode> = Stack()

    while (stream.peek() != Symbol.SEMICOLON) {
      if (stream.peek() !is Symbol) {
        nodes.push(SyntaxTreeNode(when (stream.peek()) {
          is Num -> Terminal.NUMBER
          is Identifier -> Terminal.IDENTIFIER
          else -> throw RuntimeException("Not a valid symbol")
        }, stream.next()))
      } else {
        var right = nodes.pop()
        var left = nodes.pop()

        val nodeSymbol =
            when (stream.peek()) {
              Symbol.MINUS -> NonTerminal.MINUS
              Symbol.PLUS -> NonTerminal.PLUS
              Symbol.MULTIPLY -> NonTerminal.MULTIPLY
              Symbol.DIVIDE -> NonTerminal.DIVIDE
              else -> throw RuntimeException("Not a valid symbol")
            }

        val newNode = SyntaxTreeNode(nodeSymbol, stream.next())

        newNode.children.add(left)
        newNode.children.add(right)
        nodes.push(newNode)
      }
    }
    node.children.add(nodes.pop())
  }
}
