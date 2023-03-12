package com.th0bse.pl0c.vm

/**
 * Available instructions for the virtual machine:
 *
 * 1. LIT 0, a - load constant a onto the stack
 * 3. LOD l, a - load variable at address a from level l onto the stack
 * 4. STO l, a - store variable at address a from level l onto the stack
 * 5. CAL l, a - call procedure at address a from level l
 * 6. INT 0, a - increment stack pointer by a
 * 7. JMP 0, a - jump to instruction at address a
 * 8. JPC 0, a - jump to instruction at address a if top stack element is 0
 * 9. RET 0, 0 - return from procedure
 * 10. NEG 0, 0 - negate top stack element
 * 11. ADD 0, 0 - add top two stack elements
 * 12. SUB 0, 0 - subtract top two stack elements
 * 13. MUL 0, 0 - multiply top two stack elements
 * 14. DIV 0, 0 - divide top two stack elements
 * 15. MOD 0, 0 - modulo top two stack elements
 * 16. EQU 0, 0 - check if top two stack elements are equal
 * 17. NEQ 0, 0 - check if top two stack elements are not equal
 * 18. LTH 0, 0 - check if top stack element is less than second top stack element
 * 19. LEQ 0, 0 - check if top stack element is less than or equal to second top stack element
 * 20. GTH 0, 0 - check if top stack element is greater than second top stack element
 * 21. GEQ 0, 0 - check if top stack element is greater than or equal to second top stack element
 *
 */
class Instruction(
    val mnemonic: Mnemonic,
    val level: Int,
    var address: Int
) {

    override fun toString(): String {
        return "$mnemonic $level $address"
    }
}

enum class Mnemonic {

    LIT, LOD, STO, CAL, INT, JMP, JPC, RET, NEG, ADD, SUB, MUL, DIV, MOD, EQU, NEQ, LTH, LEQ, GTH, GEQ
}