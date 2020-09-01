// #Sireum          // this indicates this Scala script .sc file is a Slang script file
import org.sireum._ // imports Slang standard library


println("Hello World!") // print with a newline to stdout

println(true)
eprintln(false) // print with a newline to stderr

cprintln(false, T == true) // the first argument determines printing to either stdout (false) or stderr (true)
cprintln(T, F != false)

print(B.random) // print a random B without a newline (B is the boolean type object)
println()

println("!F == T: ", !F == T) // negation, println can take any number of arguments (so are the other print methods)
println(s"!T == F: ${!T == F}") // string interpolation

assert(T & T == T) // logical-and, assertion
assert(T | F == T, "Assertion error message") // logical-or, assertion with error message

assume(T |^ F == T) // logical-xor, assumption
assume(T |^ T == F, "Assumption error message") // assumption with error message

val b1: B = (T imply_: F) == F // logical-implication, read-only variable definition with explicit type B
val b2 = (T imply_: T imply_: F) == F // logical-implication right-assoc, read-only variable definition with type inference

var b3: B = T && T == T // conditional-and (short-circuit), (mutable) variable definition
var b4 = T || F == T // conditional-or
assert(b1 && b2 && b3 && b4)

b3 = (F simply_: F) == T // conditional-implication, assignment
b4 = (F simply_: T simply_: F) == T // conditional-implication right-assoc
assert(b3 && b4)

println(
  /* multi-line string interpolation */
  s"""
FAQs

* Why `T` and `F` instead of just using $T and $F?

  Shortcuts for truth table sub-language part of Slang (for pedagogical purposes); also, less is more (typing-wise).

* Why `|^` for xor?

  `^` is reserved for the logical-and operator identifier in Slang proof context (e.g., contract expressions).
  Also note that the capital letter `V` is also a reserved operator identifier for logical-or.""")

println(/* multi-line string */
  """

* Why `imp_:` and `simp_:` for implication operator identifiers?

  Scala recognizes right-associative operator identifier if it ends with a colon (`:`).
  To make sure implications have lower precedence than `&` and `|`, a letter is used as the first character of the
  identifier, which necessitates the use of underscore (`_`) before the colon; see:

  https://scala-lang.org/files/archive/spec/2.12/06-expressions.html#infix-operations
  https://scala-lang.org/files/archive/spec/2.12/01-lexical-syntax.html#identifiers

  In the proof context, `->` and `==>` can be used for logical-implication and conditional-implication, respectively.
""")