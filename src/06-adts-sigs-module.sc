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
