// #Sireum
import org.sireum._


// IS[I, E]: Immutable Sequence with index type I and element type E

val s1: ISZ[String] = IS[Z, String]("a", "b", "c") // an Immutable Sequence of String indexed by Z

println(s1)

assert(!s1.isEmpty) // empty-ness query

assert(s1.nonEmpty)

assert(s1.size == 3 && s1(0) == "a" && s1(1) != "a" && s1(2) == "c") // size, indexing

assert(s1(1 ~> "c", 2 ~> "b") == ISZ("a", "c", "b")) // updates (producing a new seq), structural equality

assert("0" +: s1 :+ "d" == ISZ("0", "a", "b") ++ ISZ("c", "d")) // prepend (+:) and append (:+, ++)

assert(s1 - "a" == ISZ("b", "c")) // removing an element

assert(s1 -- ISZ("b", "c") == IS.create(1, "a")) // removing several elements, creation with size & default


// MS[I, E]: Mutable Sequence with index type I and element type E

val s2 = MSZ("a", "b", "c") // an Immutable Sequence of String indexed by Z

println(s2)

// assert(s1 != s2)  // error, cannot compare IS and MS (it will never be true)

s2(0) = "c" // destructive update

assert(s2(1 ~> "c") == MS.create(3, "c")) // Note: false positive from IntelliJ Scala 2018.3.5

assert(s2(1) == "b")

