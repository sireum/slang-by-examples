// #Sireum
import org.sireum._


// IS[I, E]: Immutable Sequence with index type I and element type E
// Type I should be either Z, @bits, or @range types; type E has to be an immutable type as well

val s1: ISZ[String] = IS[Z, String]("a", "b", "c") // an Immutable Sequence of String indexed by Z (alias ISZ[String)

println(s1)

assert(!s1.isEmpty) // empty-ness query

assert(s1.nonEmpty)

assert(s1.size == 3 && s1(0) == "a" && s1(1) != "a" && s1(2) == "c") // size, indexing

assert(s1(1 ~> "c", 2 ~> "b") == ISZ("a", "c", "b")) // updates (producing a new seq), structural equality

assert("0" +: s1 :+ "d" == ISZ("0", "a", "b") ++ ISZ("c", "d")) // prepend (+:) and append (:+, ++)

assert(s1 - "a" == ISZ("b", "c")) // removing an element

assert(s1 -- ISZ("b", "c") == IS.create(1, "a")) // removing several elements, creation with size & default


// MS[I, E]: Mutable Sequence with index type I and element type E
// Type I should be either Z, @bits, or @range types; type E can be either immutable or mutable

val s2 = MSZ("a", "b", "c") // a Mutable Sequence of String indexed by Z

println(s2)

// assert(s1 != s2)  // error, cannot compare IS and MS (it will never be true)

s2(0) = "c" // destructive update

assert(s2(1 ~> "c") == MS.create(3, "c")) // Note: false positive from IntelliJ Scala 2018.3.5

assert(s2(1) == "b")


// Non-zero indexing

@range(min = 1, max = 10, index = T) class Z1To10 // index=true means to use min as the first index for sequences
import Z1To10._
val s4 = MS[Z1To10, String]("a", "b", "c")
println(s4(z1To10"1"))
assert(s4(z1To10"1") == "a" && s4(z1To10"2") == "b" && s4(z1To10"3") == "c")

@bits(min = -10, max = 10, index = T) class SM10To10
import SM10To10._
val s5 = IS[SM10To10, String]("a", "b", "c")
assert(s5(sM10To10"-10") == "a" && s5(sM10To10"-9") == "b" && s5(sM10To10"-8") == "c")


// Clone/copy semantics for assignments of mutable types

val s3 = s2 // copy
assert(s3 == s2) // structural equality
println(s"s2 = $s2, s3 = $s3")
s3(0) = "a"
assert(s3 != s2)
println(s"s2 = $s2, s3 = $s3")
