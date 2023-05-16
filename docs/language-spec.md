# Language specification

PL/0 (actually more like PL/0.5) is specified by the following EBNF:

```
program = {constant},
          {variable},
          {procedure},
          block, "." ;

block = "begin", statement, { ";", statement }, ";", "end" ;

constant = "const, ident, ":=", number, { ",", ident, ":=", number }, ";" ;

variable = "var", ident, { ",", ident}, ";" ;

procedure = "procedure", ident, ";", block, ";", {procedure};

statement = [ assignment | call | statement_block |
            "if", condition, "then", statement |
            "while", condition, "do", statement ] ;

assignment = ident, ":=", expression ;

call = "call", ident ;

condition = "odd", expression | expression, ("=" | "!=" | "<" | ">"), expression ;

expression = term, { ("+" | "-"), term } ;

term = factor, { ("*" | "/"), factor } ;

factor = ident | number | "(" expression ")" ;

ident = letter, {number | letter} ;
number = digit, {digit} ;

letter = ? all lower- and upper-case letters ? ;
digit = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 ;
```
