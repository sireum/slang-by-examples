// #Sireum
import org.sireum._


// Methods

def printHelloWorld(): Unit = { // return type is always required, Unit means it does not return a value
  println("Hello World")
}
printHelloWorld() // method call

def abs(n: Z): Z = {
  return if (n >= 0) n else -n // return statement is required for methods returning a value
}
assert(abs(4) == 4)
assert(abs(-10) == 10)

def less(n1: Z, n2: Z): B = {
  return n1 < n2
}


def random0To9: Z = { // parameter-less
  return abs(Z.random % 10)
}
val x1 = random0To9 // parameter-less method call
assert(0 <= x1 && x1 <= 9)


def stringEqual[T1, T2](o1: T1, o2: T2): B = { // method with type parameters (generic)
  return o1.string == o2.string // the string method is available for all Slang values
}
println(stringEqual(ISZ(1, 2, 3), MSZ(1, 2, 3)))


// Functions

val absF: Z => Z =  // absF is a function that takes a Z value and returns a Z value
  n => if (n >= 0) n else -n

val lessF = (n1: Z, n2: Z) => n1 < n2
assert(lessF(4, 10))

val absF2 = abs _ // converting a method to a function (eta-expansion)
val x2 = Z.random
assert(absF(x2) == absF2(x2))

val stringEqualZandZ8 = stringEqual[Z, Z8] _ // generic method type has be instantiated for function extraction
import org.sireum.Z8._
assert(stringEqual(0, z8"0"))


// Mutability and parameter passing

def sort[E](s: MSZ[E], p: (E, E) => B): Unit = {
  for (i <- 0 until s.size - 1; j <- i until s.size) {
    if (!p(s(i), s(j))) {
      val t = s(i)
      s(i) = s(j)
      s(j) = t
    }
  }
}

val s1 = MSZ(3, 1, 2)
sort(s1, lessF)
assert(s1 == MSZ(1, 2, 3))

val s2 = MSZ(9, 3, 4, 8)
sort(s2, less _)
assert(s2 == MSZ(3, 4, 8, 9))


/*
Note that mutable objects are passed by reference to method calls/function applications (instead of by value/copy).
That is the reason why the changes to s1 and s2 are visible after sort were invoked.
This is the only place in Slang where aliasing are introduced.
It is required that mutable arguments have to be "separate"; that is, one argument mutable object should not be the same
as or contained in another argument mutable object.
This requirement will be checked in the future version of the Slang static checker.
 */
