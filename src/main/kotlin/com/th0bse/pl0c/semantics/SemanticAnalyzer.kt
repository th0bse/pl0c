package com.th0bse.pl0c.semantics

import com.th0bse.pl0c.definitions.Identifier
import com.th0bse.pl0c.parser.*

class SemanticAnalyzer {

    private val constantIdentifiers = HashSet<String>()
    private val variableIdentifiers = HashSet<String>()
    private val procedureIdentifiers = HashSet<String>()

    fun analyze(tree: SyntaxTree): SyntaxTree {
        populateSymbolTable(tree)
        checkForAmbiguousIdentifiers()
        checkForUndefinedIdentifiers(tree)
        return tree
    }

    private fun populateSymbolTable(tree: SyntaxTree) {
        val constants = tree.getAllNodesOfType(NonTerminal.CONSTANT)
        val variables = tree.getAllNodesOfType(NonTerminal.VARIABLE)
        val procedures = tree.getAllNodesOfType(NonTerminal.PROCEDURE)

        constants.map { (it.children[0].token!! as Identifier).identifier }.forEach {
            if (!constantIdentifiers.add(it)) throw RuntimeException("Duplicate identifier: $it")
        }
        variables.map { (it.children[0].token!! as Identifier).identifier }.forEach {
            if (!variableIdentifiers.add(it)) throw RuntimeException("Duplicate identifier: $it")
        }
        procedures.map { (it.children[0].token!! as Identifier).identifier }.forEach {
            if (procedureIdentifiers.add(it)) throw RuntimeException("Duplicate identifier: $it")
        }
    }

    private fun checkForAmbiguousIdentifiers() {
        val ambiguousIdentifiers = constantIdentifiers.intersect(variableIdentifiers).toHashSet()
        ambiguousIdentifiers.addAll(constantIdentifiers.intersect(procedureIdentifiers))
        ambiguousIdentifiers.addAll(variableIdentifiers.intersect(procedureIdentifiers))
        if (ambiguousIdentifiers.isNotEmpty()) {
            throw RuntimeException("Ambiguous identifiers: $ambiguousIdentifiers")
        }
    }

    private fun checkForUndefinedIdentifiers(tree: SyntaxTree) {
        val identifiers = tree.getAllNodesOfType(Terminal.IDENTIFIER)
        identifiers.map { (it.token!! as Identifier).identifier }.forEach {
            if (!constantIdentifiers.contains(it) &&
                !variableIdentifiers.contains(it) &&
                !procedureIdentifiers.contains(it)
            ) {
                throw RuntimeException("Undefined identifier: $it")
            }
        }
    }

    private fun SyntaxTree.getAllNodesOfType(type: Sym): List<SyntaxTreeNode> =
        this.root.getAllChildren().filter { it.symbol == type }

    private fun SyntaxTreeNode.getAllChildren(): List<SyntaxTreeNode> {
        val children = ArrayList<SyntaxTreeNode>()
        children.addAll(this.children)
        return children + this.children.flatMap { it.getAllChildren() }
    }
}