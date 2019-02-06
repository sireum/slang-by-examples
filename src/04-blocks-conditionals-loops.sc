// #Sireum
import org.sireum._


// Blocks

val x1 = Z.random

;{ // block is a sequence of statements with its own scope
  println("Inside block")
  val y = x1 + 1 // can refer to outside scope
  assert(y > x1)
} // y is no longer available

val x2: Z = { // block expression produces a value for val/var definition (explicit type required) or assignment
  println("Inside block expresion")
  x1 + 2
}


// Conditionals

if (B.random) { // conditional statement requires a block on its branches
  println("Randomly printed")
}

val x3 = Z.random % 3

if (x3 == 2) {
  println("A")
} else if (x3 == 1) {
  println("B")
} else {
  println("C")
}


val x4: Z = if (B.random) x3 else -x3 // conditional expression (no blocks)


val x5: Z =
  if (B.random) {                     // conditional block expression
    println("5")
    5
  } else {
    println("1")
    6
  }


// Loops

var i = 0
while (i < 10) { // a while-loop requires a block for its body
  print(i, " ")
  i = i + 1
}

do { // a do-while-loop requires a block for its body
  i = i - 1
  print(i, " ")
} while (i > 0)
println()


for (j <- 0 until 10) { // a for-loop requires a block for its body
  print(j, " ")
}
println()

for (j <- 0 to 12 by 3) { // increments by 3
  print(j, " ")
}
println()

for (j <- 100 to 0 by -3 if (j + 2) % 7 == 0) {
  print(j, " ")
}
println()


val s1 = ISZ('a', 'b', 'c')
for (e <- s1) { // iterating over all elements of s1
  println(e)
}

val s2: ISZ[(Z, C)] = for (j <- s1.indices) yield (j, s1(j)) // for comprehension
println(s2)

val s3 = ISZ("a", "b", "c")
val s4 = ISZ(1, 2, 3)
val s5 = ISZ(T, F)
println()
println("[a, b, c] x [1, 2, 3] x [T, F] = ", for (e3 <- s3; e4 <- s4; e5 <- s5) yield (e3, e4, e5))
println()
println(for (e3 <- s3; e4 <- s4; e5 <- s5 if e4 != 1 || e5) yield (e3, e4, e5))