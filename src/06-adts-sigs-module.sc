// #Sireum
import org.sireum._


// Immutable algebraic data type (ADT)

@datatype class Coordinate(x: Z, val y: Z) { // by default, x is val/read-only too (i.e., val is optional)

  val isCenter: B = x == 0 && y == 0         // explicit type declaration is required

  def isEqualX(other: Coordinate): B = {
    return x == other.x
  }

  def isEqualY(other: Coordinate): B = {
    return y == other.y
  }
}

val c1 = Coordinate(3, 4)
println(c1)
println(c1.x)
println(c1.y)
assert(!c1.isCenter)

val c2 = c1(x = 5) // produces a coordinate where its x is 5 and its y is c1.y
println(c2)

assert(!c1.isEqualX(c2) && c1.isEqualY(c2))

assert(c1 == c2(x = 3)) // structural equality

// Note: @datatype can only contain val fields and immutable objects


// Mutable ADT

@record class FilePos(filename: String, var line: Z, var column: Z) { // filename is val by default

  def isEqual(other: FilePos): B = { // customize equality (==)
    var r: B = filename == other.filename
    if (line > 0 && other.line > 0) {
      r = r && line == other.line
      if (column > 0 && other.column > 0) {
        r = r && column == other.column
      }
    }
    return r
  }

  override def hash: Z = { // customize hash
    var r: Z = filename.hash
    if (line > 0) {
      r = r + line * 7
      if (column > 0) {
        r = r + column * 15
      }
    }
    return r
  }

  override def string: String = {  // customize string representation
    return if (column < 1)
             if (line < 1) filename else s"$filename:$line"
           else s"$filename:[$line, $column]"
  }
}

val fp1 = FilePos("a.txt", 10, 1)
val fp2 = fp1(column = 0)
println(fp1)
println(fp2)
fp1.column = -1
assert(fp1 == fp2) // uses isEqual


// Immutable interface

@sig trait HasFoo {
  def foo: Z
}

@sig trait HasBar {
  def bar: Z
}

@sig trait HasBaz {
  def baz: Z
}

@datatype class FooBar(val foo: Z, val bar: Z) extends HasFoo with HasBar with HasBaz {
  def baz: Z = {
    return bar
  }
}


// Mutable interface

@msig trait HasInc {
  def inc(): Z
}

@record class ZContainer(var n: Z) extends HasInc {
  def inc(): Z = {
    n = n + 1
    return n
  }
}


// Module

object Items {

  val Min: Z = 1

  var Max: Z = 100

}

println(Items.Max)


// Generics

@sig sealed trait Tree[T] { // sealed means it can only be extended in this file (i.e., guaranteeing closed sub-types)
  def children: ISZ[Tree[T]]
  def data: T
}

object Tree {

  @datatype class InNode[T](val children: ISZ[Tree[T]], val data: T) extends Tree[T]

  @datatype class Leaf[T](val data: T) extends Tree[T] {
    def children: ISZ[Tree[T]] = {
      return ISZ()
    }
  }

  def inNode[T](children: ISZ[Tree[T]], data: T): Tree[T] = {
    return InNode(children, data)
  }

  def leaf[T](data: T): Tree[T] = {
    return Leaf(data)
  }

}

val t1 = Tree.InNode(ISZ[Tree[Z]](Tree.Leaf(5), Tree.Leaf(3)), 1)
// note: Tree[Z] is required because Leaf(x: Z) returns Leaf[Z] (not Tree[Z])
println(t1)

val t2 = Tree.inNode(ISZ(Tree.leaf(5), Tree.leaf(2)), 1)
println(t2)

assert(t2 == t1(children = t1.children(1 ~> Tree.leaf(2))))


// Hidden params

@record class Record(x: Z, y: Z, @hidden z: Z) // z is not taken into account for == and .hash

val r1 = Record(1, 2, 3)
val r2 = Record(1, 2, 4)
assert(r1 == r2 && r1.hash == r2.hash)
println(r1)
println(r2)
