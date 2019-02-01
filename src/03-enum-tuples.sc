// #Sireum
import org.sireum._


// Lightweight Enum

@enum object Day {
  'Sunday
  'Monday
  'Tuesday
  'Wednesday
  'Thursday
  'Friday
  'Saturday
}

val d1: Day.Type = Day.Monday
println(d1)
assert(d1.ordinal == 1)


// Tuples

val t1: (Z, B, String) = (0, F, "") // immutable triple
assert(t1._1 == 0 && t1._2 == F && t1._3 == "") // tuple element access: _N (Nth element)

val t2 = (t1, ("a", F), 1) // nested tuples
println(t2)

val p1 = (1, MSZ(1)) // mutable pair (at least one mutable element)
val p2 = p1 // copy
println(s"p1 = $p1, p2 = $p2")
assert(p1 == p2)
p1._2(0) = 2
println(s"p1 = $p1, p2 = $p2")
assert(p1 != p2)
