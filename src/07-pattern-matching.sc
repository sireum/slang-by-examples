// #Sireum
import org.sireum._

@sig sealed trait Top {
  def x: Z
}

@datatype class Foo(val x: Z, val y: Z) extends Top

@datatype class Bar(val x: Z) extends Top

val f1 = Foo(1, 3)
val Foo(x, _) = f1  // extracting f1.x to x; underscore is used to "ignore" a certain value for pattern matching
assert(x == 1)

@pure def yOf(t: Top): Option[Z] = {  // Option, Some, None are defined in Slang runtime library
  t match { // match based on type
    case t: Foo => return Some(t.y)
    case _: Bar => return None()
  }
}


@pure def yOf2(t: Top): Option[Z] = {
  t match {
    case t@Foo(_, _) => return Some(t.y)
    case _ => return None()
  }
}

@pure def yOf3(t: Top): Option[Z] = {
  t match {
    case Foo(_, y) => return Some(y)
    case _ => return None()
  }
}

@pure def posYOf3(t: Top): Option[Z] = {
  t match {
    case Foo(_, y) if y > 0 => Some(y)
    case _ => return None()
  }
}


assert(yOf(f1) == yOf2(f1) && yOf2(f1) == yOf3(f1) && yOf3(f1) == posYOf3(f1))
assert(posYOf3(Foo(1, -2)).isEmpty)


// Note: extracting a mutable object through pattern matching creates a copy

@record class Cell(var data: Z, var left: MOption[Cell], var right: MOption[Cell])

val c1 = Cell(1, MSome(Cell(0, MNone(), MNone())), MSome(Cell(2, MNone(), MNone())))
val Cell(_, _, MSome(right)) = c1
assert(right == c1.right.get)
right.data = 3
assert(right != c1.right.get)

c1 match {
  case Cell(_, MSome(left), _) =>
    left.data = 3
    assert(c1.left.get.data == 0)
}