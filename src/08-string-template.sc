// #Sireum
import org.sireum._

/*
Slang string template (ST) is similar to string interpolator except that:
* it computes indentation automatically based on argument position
* it can accept a pair of a sequence and a separator and render each element with the separator in between
  (newline and indentation honored)
* it can accept an Option/MOption and it renders only Some/MSome's element
* it is immutable (mutable object is copied internally without ways to mutate them)
* it does not construct string until its render or renderCompact are called
*/

val x = 1
val y = 3
val st1: ST = st"  $x < $y  is  ${x < y}  "
println(st1.render)
println(st1.renderCompact) // contiguous whitespaces are represented by one space

val s1 = ISZ(1, 2, 3)
println(st"[${(s1, ", ")}]".render)

val s2 = ISZ("a", "b", "c")
val s3 = ISZ("c", "b", "a")

def isz2ST[T](s: ISZ[T]): ST = {
  return st"""{
              |  ${(s, ",\n")}
              |}"""
}

println(
  isz2ST(
    ISZ(
      isz2ST(s2),
      isz2ST(s3)
    )
  ).render)


def lines(l1: Option[String], l2: Option[String], l3: Option[String]): ST = {
  return st"""$l1
             |$l2
             |$l3"""
}

println(lines(Some("Line 1"), Some("Line 2"), Some("Line 3")).render)

println(lines(Some("Line 1"), None(), Some("Line 3")).render)

println(lines(None(), Some("Line 2"), None()).render)

println(lines(None(), None(), Some("Line 3")).render)
