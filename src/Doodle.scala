// #Sireum
/*
 * Copyright 2015-2020 Noel Welsh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.sireum._

// Adapted from https://github.com/creativescala/doodle/tree/v0.12.0

object Doodle {

  object Image {

    @datatype class Path(val closed: B, val elements: ISZ[Path.Element]) extends Image

    object Path {

      @datatype trait Element

      object Element {

        @datatype class MoveTo(val to: Point) extends Element

        @datatype class LineTo(val to: Point) extends Element

        @datatype class BezierCurveTo(val cp1: Point, val cp2: Point, val to: Point) extends Element

      }
    }

    val empty: Image = Util.Empty()

    @strictpure def closedPath(elements: ISZ[Image.Path.Element]): Image.Path =
      Path(T, Util.cmoveTo(0d, 0d) +: elements)

    @strictpure def openPath(elements: ISZ[Image.Path.Element]): Image.Path =
      Path(F, Util.cmoveTo(0d, 0d) +: elements)

    @strictpure def line(x: F64, y: F64): Image = {
      val startX = -x / 2d
      val startY = -y / 2d
      val endX = x / 2d
      val endY = y / 2d
      openPath(ISZ(
        Util.cmoveTo(startX, startY),
        Util.clineTo(endX, endY)
      ))
    }

    @strictpure def circle(diameter: F64): Image = Util.Circle(diameter)

    @strictpure def rectangle(width: F64, height: F64): Image = Util.Rectangle(width, height)

    @strictpure def square(side: F64): Image = rectangle(side, side)

    @strictpure def regularPolygon(sides: Z, radius: F64): Image = {
      val rotation = Angle.one / Ext.toF64(sides)
      closedPath(Util.pmoveTo(radius, Angle.zero) +:
        (for (i <- 1 to sides) yield Util.plineTo(radius, rotation * Ext.toF64(i))))
    }

    @strictpure def star(points: Z, outerRadius: F64, innerRadius: F64): Image = {
      val rotation = Angle.one / (Ext.toF64(points) * 2d)
      val path: ISZ[Image.Path.Element] = for (i <- 1 to points * 2) yield
        if (i % 2 == z"0") Util.plineTo(outerRadius, rotation * Ext.toF64(i))
        else Util.plineTo(innerRadius, rotation * Ext.toF64(i))
      closedPath(Util.pmoveTo(outerRadius, Angle.zero) +: path)
    }

    @strictpure def rightArrow(width: F64, height: F64): Image = closedPath(ISZ(
      Util.cmoveTo(width / 2d, 0d),
      Util.clineTo(0d, height / 2d),
      Util.clineTo(0d, height * 0.2d),
      Util.clineTo(-width / 2d, height * 0.2d),
      Util.clineTo(-width / 2d, -height * 0.2d),
      Util.clineTo(0d, -height * 0.2d),
      Util.clineTo(0d, -height / 2d),
      Util.clineTo(width / 2d, 0d)
    ))

    @strictpure def roundedRectangle(width: F64, height: F64, radius: F64): Image = {
      val cornerRadius: F64 = if (radius > width / 2d || radius > height / 2d) {
        val w2 = width / 2d
        val h2 = height / 2d
        if (w2 < h2) w2 else h2
      } else {
        radius
      }

      val c = (4d / 3d) * (Ext.sqrt(2d) - 1d)
      val cR = c * cornerRadius
      closedPath(ISZ(
        Util.cmoveTo(width / 2d - cornerRadius, height / 2d),
        Util.curveTo(
          width / 2d - cornerRadius + cR,
          height / 2d,
          width / 2d,
          height / 2d - cornerRadius + cR,
          width / 2d,
          height / 2d - cornerRadius
        ),
        Util.clineTo(width / 2d, -height / 2d + cornerRadius),
        Util.curveTo(
          width / 2d,
          -height / 2d + cornerRadius - cR,
          width / 2d - cornerRadius + cR,
          -height / 2d,
          width / 2d - cornerRadius,
          -height / 2d
        ),
        Util.clineTo(-width / 2d + cornerRadius, -height / 2d),
        Util.curveTo(
          -width / 2d + cornerRadius - cR,
          -height / 2d,
          -width / 2d,
          -height / 2d + cornerRadius - cR,
          -width / 2d,
          -height / 2d + cornerRadius
        ),
        Util.clineTo(-width / 2d, height / 2d - cornerRadius),
        Util.curveTo(
          -width / 2d,
          height / 2d - cornerRadius + cR,
          -width / 2d + cornerRadius - cR,
          height / 2d,
          -width / 2d + cornerRadius,
          height / 2d
        ),
        Util.clineTo(width / 2d - cornerRadius, height / 2d)
      ))
    }

    @strictpure def equilateralTriangle(width: F64): Image = closedPath(ISZ(
      Util.cmoveTo(0d, 0d),
      Util.cmoveTo(0d, width * Util.oneOnSqrt3),
      Util.clineTo(width / 2.0d, -width * Util.oneOnSqrt3 * 0.5d),
      Util.clineTo(-width / 2.0d, -width * Util.oneOnSqrt3 * 0.5d),
      Util.clineTo(0d, width * Util.oneOnSqrt3)
    ))

    @strictpure def triangle(width: F64, height: F64): Image = Util.Triangle(width, height)

    @strictpure def interpolatingSpline(points: ISZ[Point]): Path = openPath(Util.catmulRom(points))

  }

  @datatype trait Image {

    @strictpure def beside(right: Image): Image = Util.Beside(this, right)

    @strictpure def on(bottom: Image): Image = Util.On(this, bottom)

    @strictpure def under(top: Image): Image = Util.On(top, this)

    @strictpure def above(bottom: Image): Image = Util.Above(this, bottom)

    @strictpure def below(top: Image): Image = Util.Above(top, this)

    @strictpure def strokeColor(color: Color): Image = Util.StrokeColor(this, color)

    @strictpure def strokeWidth(width: F64): Image = Util.StrokeWidth(this, width)

    @strictpure def fillColor(color: Color): Image = Util.FillColor(this, color)

    @strictpure def fillGradient(gradient: Gradient): Image = Util.FillGradient(this, gradient)

    @strictpure def noStroke: Image = Util.NoStroke(this)

    @strictpure def noFill: Image = Util.NoFill(this)

    @strictpure def transform(tx: Transform): Image = Util.Transform(tx, this)

    @strictpure def rotate(angle: Angle): Image = Util.Transform(Util.rotate(angle), this)

    @strictpure def scale(x: F64, y: F64): Image = Util.Transform(Util.scale(x, y), this)

    @strictpure def at(pt: Point): Image = Util.At(this, pt.x, pt.y)

    @strictpure def atPoint(x: F64, y: F64): Image = Util.At(this, x, y)

    @strictpure def atPolar(rad: F64, a: Angle): Image = {
      val p = Point.Polar(rad, a)
      Util.At(this, p.x, p.y)
    }

    @strictpure def atVec(vector: Vector): Image = Util.At(this, vector.x, vector.y)

    @strictpure def debugColor(color: Color): Image = Util.Debug(this, color)

    @strictpure def debug: Image = Util.Debug(this, Color.crimson)

    def draw(): Unit = {
      Ext.draw(this)
    }

    def writeTo(path: Os.Path): Unit = {
      Ext.write(this, path.canon)
    }
  }

  @datatype trait Point {
    @pure def x: F64
    @pure def y: F64
    @pure def radius: F64
    @pure def angle: Angle
    @strictpure def cartesian: Point.Cartesian = Point.Cartesian(x, y)
    @strictpure def polar: Point.Polar = Point.Polar(radius, angle)
  }

  object Point {

    @datatype class Polar(val radius: F64, val angle: Angle) extends Point {
      @strictpure def x: F64 = radius * Ext.cos(angle.radians)
      @strictpure def y: F64 = radius * Ext.sin(angle.radians)
    }

    @datatype class Cartesian(val x: F64, val y: F64) extends Point {
      @strictpure def radius: F64 = Ext.sqrt(x * x + y * y)
      @strictpure def angle: Angle = Angle(Ext.atan2(y, x))
    }

  }

  object Angle {
    val TwoPi: F64 = 2d * Ext.pi
    val zero: Angle = Angle(0d)
    val one: Angle = Angle(TwoPi)
  }

  @datatype class Angle(val radians: F64) {

    @strictpure def +(that: Angle): Angle = Angle(radians + that.radians)

    @strictpure def -(that: Angle): Angle = Angle(radians - that.radians)

    @strictpure def unary_- : Angle = Angle(-radians)

    @strictpure def *(m: F64): Angle = Angle(radians * m)

    @strictpure def /(m: F64): Angle = Angle(radians / m)

    @strictpure def >(that: Angle): B = radians > that.radians

    @strictpure def <(that: Angle): B = radians < that.radians

    @strictpure def sin: F64 = Ext.sin(radians)

    @strictpure def cos: F64 = Ext.cos(radians)

    @pure def normalize: Angle = {
      var stop = F
      var r = radians
      while (!stop) {
        if (r < 0.0) {
          r = r + Angle.TwoPi
        } else if (r > Angle.TwoPi) {
          r = r - Angle.TwoPi
        } else {
          stop = T
        }
      }
      return Angle(r)
    }

    @strictpure def toTurns: F64 = radians / Angle.TwoPi

    @strictpure def toDegrees: F64 = (radians / Angle.TwoPi) * 360d
  }

  @datatype class Color(val red: U8, val green: U8, val blue: U8, val alpha: F64)

  object Color {
    val aliceBlue: Color = Color.rgb(0xf0, 0xf8, 0xff)
    val antiqueWhite: Color = Color.rgb(0xfa, 0xeb, 0xd7)
    val aqua: Color = Color.rgb(0x00, 0xff, 0xff)
    val aquamarine: Color = Color.rgb(0x7f, 0xff, 0xd4)
    val azure: Color = Color.rgb(0xf0, 0xff, 0xff)
    val beige: Color = Color.rgb(0xf5, 0xf5, 0xdc)
    val bisque: Color = Color.rgb(0xff, 0xe4, 0xc4)
    val black: Color = Color.rgb(0x00, 0x00, 0x00)
    val blanchedAlmond: Color = Color.rgb(0xff, 0xeb, 0xcd)
    val blue: Color = Color.rgb(0x00, 0x00, 0xff)
    val blueViolet: Color = Color.rgb(0x8a, 0x2b, 0xe2)
    val brown: Color = Color.rgb(0xa5, 0x2a, 0x2a)
    val burlyWood: Color = Color.rgb(0xde, 0xb8, 0x87)
    val cadetBlue: Color = Color.rgb(0x5f, 0x9e, 0xa0)
    val chartReuse: Color = Color.rgb(0x7f, 0xff, 0x00)
    val chocolate: Color = Color.rgb(0xd2, 0x69, 0x1e)
    val coral: Color = Color.rgb(0xff, 0x7f, 0x50)
    val cornflowerBlue: Color = Color.rgb(0x64, 0x95, 0xed)
    val cornSilk: Color = Color.rgb(0xff, 0xf8, 0xdc)
    val crimson: Color = Color.rgb(0xdc, 0x14, 0x3c)
    val cyan: Color = Color.rgb(0x00, 0xff, 0xff)
    val darkBlue: Color = Color.rgb(0x00, 0x00, 0x8b)
    val darkCyan: Color = Color.rgb(0x00, 0x8b, 0x8b)
    val darkGoldenrod: Color = Color.rgb(0xb8, 0x86, 0x0b)
    val darkGray: Color = Color.rgb(0xa9, 0xa9, 0xa9)
    val darkGrey: Color = Color.rgb(0xa9, 0xa9, 0xa9)
    val darkGreen: Color = Color.rgb(0x00, 0x64, 0x00)
    val darkKhaki: Color = Color.rgb(0xbd, 0xb7, 0x6b)
    val darkMagenta: Color = Color.rgb(0x8b, 0x00, 0x8b)
    val darkOliveGreen: Color = Color.rgb(0x55, 0x6b, 0x2f)
    val darkOrange: Color = Color.rgb(0xff, 0x8c, 0x00)
    val darkOrchid: Color = Color.rgb(0x99, 0x32, 0xcc)
    val darkRed: Color = Color.rgb(0x8b, 0x00, 0x00)
    val darkSalmon: Color = Color.rgb(0xe9, 0x96, 0x7a)
    val darkSeaGreen: Color = Color.rgb(0x8f, 0xbc, 0x8f)
    val darkSlateBlue: Color = Color.rgb(0x48, 0x3d, 0x8b)
    val darkSlateGray: Color = Color.rgb(0x2f, 0x4f, 0x4f)
    val darkSlateGrey: Color = Color.rgb(0x2f, 0x4f, 0x4f)
    val darkTurquoise: Color = Color.rgb(0x00, 0xce, 0xd1)
    val darkViolet: Color = Color.rgb(0x94, 0x00, 0xd3)
    val deepPink: Color = Color.rgb(0xff, 0x14, 0x93)
    val deepSkyBlue: Color = Color.rgb(0x00, 0xbf, 0xff)
    val dimGray: Color = Color.rgb(0x69, 0x69, 0x69)
    val dimGrey: Color = Color.rgb(0x69, 0x69, 0x69)
    val dodgerBlue: Color = Color.rgb(0x1e, 0x90, 0xff)
    val fireBrick: Color = Color.rgb(0xb2, 0x22, 0x22)
    val floralWhite: Color = Color.rgb(0xff, 0xfa, 0xf0)
    val forestGreen: Color = Color.rgb(0x22, 0x8b, 0x22)
    val fuchsia: Color = Color.rgb(0xff, 0x00, 0xff)
    val gainsboro: Color = Color.rgb(0xdc, 0xdc, 0xdc)
    val ghostWhite: Color = Color.rgb(0xf8, 0xf8, 0xff)
    val gold: Color = Color.rgb(0xff, 0xd7, 0x00)
    val goldenrod: Color = Color.rgb(0xda, 0xa5, 0x20)
    val gray: Color = Color.rgb(0x80, 0x80, 0x80)
    val grey: Color = Color.rgb(0x80, 0x80, 0x80)
    val green: Color = Color.rgb(0x00, 0x80, 0x00)
    val greenYellow: Color = Color.rgb(0xad, 0xff, 0x2f)
    val honeydew: Color = Color.rgb(0xf0, 0xff, 0xf0)
    val hotpink: Color = Color.rgb(0xff, 0x69, 0xb4)
    val indianRed: Color = Color.rgb(0xcd, 0x5c, 0x5c)
    val indigo: Color = Color.rgb(0x4b, 0x00, 0x82)
    val ivory: Color = Color.rgb(0xff, 0xff, 0xf0)
    val khaki: Color = Color.rgb(0xf0, 0xe6, 0x8c)
    val lavender: Color = Color.rgb(0xe6, 0xe6, 0xfa)
    val lavenderBlush: Color = Color.rgb(0xff, 0xf0, 0xf5)
    val lawngreen: Color = Color.rgb(0x7c, 0xfc, 0x00)
    val lemonChiffon: Color = Color.rgb(0xff, 0xfa, 0xcd)
    val lightBlue: Color = Color.rgb(0xad, 0xd8, 0xe6)
    val lightCoral: Color = Color.rgb(0xf0, 0x80, 0x80)
    val lightCyan: Color = Color.rgb(0xe0, 0xff, 0xff)
    val lightGoldenrodYellow: Color = Color.rgb(0xfa, 0xfa, 0xd2)
    val lightGray: Color = Color.rgb(0xd3, 0xd3, 0xd3)
    val lightGrey: Color = Color.rgb(0xd3, 0xd3, 0xd3)
    val lightGreen: Color = Color.rgb(0x90, 0xee, 0x90)
    val lightPink: Color = Color.rgb(0xff, 0xb6, 0xc1)
    val lightSalmon: Color = Color.rgb(0xff, 0xa0, 0x7a)
    val lightSeaGreen: Color = Color.rgb(0x20, 0xb2, 0xaa)
    val lightSkyBlue: Color = Color.rgb(0x87, 0xce, 0xfa)
    val lightSlateGray: Color = Color.rgb(0x77, 0x88, 0x99)
    val lightSlateGrey: Color = Color.rgb(0x77, 0x88, 0x99)
    val lightSteelBlue: Color = Color.rgb(0xb0, 0xc4, 0xde)
    val lightYellow: Color = Color.rgb(0xff, 0xff, 0xe0)
    val lime: Color = Color.rgb(0x00, 0xff, 0x00)
    val limeGreen: Color = Color.rgb(0x32, 0xcd, 0x32)
    val linen: Color = Color.rgb(0xfa, 0xf0, 0xe6)
    val magenta: Color = Color.rgb(0xff, 0x00, 0xff)
    val maroon: Color = Color.rgb(0x80, 0x00, 0x00)
    val mediumAquamarine: Color = Color.rgb(0x66, 0xcd, 0xaa)
    val mediumBlue: Color = Color.rgb(0x00, 0x00, 0xcd)
    val mediumOrchid: Color = Color.rgb(0xba, 0x55, 0xd3)
    val mediumPurple: Color = Color.rgb(0x93, 0x70, 0xd8)
    val mediumSeaGreen: Color = Color.rgb(0x3c, 0xb3, 0x71)
    val mediumSlateBlue: Color = Color.rgb(0x7b, 0x68, 0xee)
    val mediumSpringGreen: Color = Color.rgb(0x00, 0xfa, 0x9a)
    val mediumTurquoise: Color = Color.rgb(0x48, 0xd1, 0xcc)
    val mediumVioletRed: Color = Color.rgb(0xc7, 0x15, 0x85)
    val midnightBlue: Color = Color.rgb(0x19, 0x19, 0x70)
    val mintCream: Color = Color.rgb(0xf5, 0xff, 0xfa)
    val mistyRose: Color = Color.rgb(0xff, 0xe4, 0xe1)
    val moccasin: Color = Color.rgb(0xff, 0xe4, 0xb5)
    val navajoWhite: Color = Color.rgb(0xff, 0xde, 0xad)
    val navy: Color = Color.rgb(0x00, 0x00, 0x80)
    val oldLace: Color = Color.rgb(0xfd, 0xf5, 0xe6)
    val olive: Color = Color.rgb(0x80, 0x80, 0x00)
    val oliveDrab: Color = Color.rgb(0x6b, 0x8e, 0x23)
    val orange: Color = Color.rgb(0xff, 0xa5, 0x00)
    val orangeRed: Color = Color.rgb(0xff, 0x45, 0x00)
    val orchid: Color = Color.rgb(0xda, 0x70, 0xd6)
    val paleGoldenrod: Color = Color.rgb(0xee, 0xe8, 0xaa)
    val paleGreen: Color = Color.rgb(0x98, 0xfb, 0x98)
    val paleTurquoise: Color = Color.rgb(0xaf, 0xee, 0xee)
    val paleVioletRed: Color = Color.rgb(0xd8, 0x70, 0x93)
    val papayaWhip: Color = Color.rgb(0xff, 0xef, 0xd5)
    val peachPuff: Color = Color.rgb(0xff, 0xda, 0xb9)
    val peru: Color = Color.rgb(0xcd, 0x85, 0x3f)
    val pink: Color = Color.rgb(0xff, 0xc0, 0xcb)
    val plum: Color = Color.rgb(0xdd, 0xa0, 0xdd)
    val powderBlue: Color = Color.rgb(0xb0, 0xe0, 0xe6)
    val purple: Color = Color.rgb(0x80, 0x00, 0x80)
    val red: Color = Color.rgb(0xff, 0x00, 0x00)
    val rosyBrown: Color = Color.rgb(0xbc, 0x8f, 0x8f)
    val royalBlue: Color = Color.rgb(0x41, 0x69, 0xe1)
    val saddleBrown: Color = Color.rgb(0x8b, 0x45, 0x13)
    val salmon: Color = Color.rgb(0xfa, 0x80, 0x72)
    val sandyBrown: Color = Color.rgb(0xf4, 0xa4, 0x60)
    val seaGreen: Color = Color.rgb(0x2e, 0x8b, 0x57)
    val seaShell: Color = Color.rgb(0xff, 0xf5, 0xee)
    val sienna: Color = Color.rgb(0xa0, 0x52, 0x2d)
    val silver: Color = Color.rgb(0xc0, 0xc0, 0xc0)
    val skyBlue: Color = Color.rgb(0x87, 0xce, 0xeb)
    val slateBlue: Color = Color.rgb(0x6a, 0x5a, 0xcd)
    val slateGray: Color = Color.rgb(0x70, 0x80, 0x90)
    val slateGrey: Color = Color.rgb(0x70, 0x80, 0x90)
    val snow: Color = Color.rgb(0xff, 0xfa, 0xfa)
    val springGreen: Color = Color.rgb(0x00, 0xff, 0x7f)
    val steelBlue: Color = Color.rgb(0x46, 0x82, 0xb4)
    val tan: Color = Color.rgb(0xd2, 0xb4, 0x8c)
    val teal: Color = Color.rgb(0x00, 0x80, 0x80)
    val thistle: Color = Color.rgb(0xd8, 0xbf, 0xd8)
    val tomato: Color = Color.rgb(0xff, 0x63, 0x47)
    val turquoise: Color = Color.rgb(0x40, 0xe0, 0xd0)
    val violet: Color = Color.rgb(0xee, 0x82, 0xee)
    val wheat: Color = Color.rgb(0xf5, 0xde, 0xb3)
    val white: Color = Color.rgb(0xff, 0xff, 0xff)
    val whiteSmoke: Color = Color.rgb(0xf5, 0xf5, 0xf5)
    val yellow: Color = Color.rgb(0xff, 0xff, 0x00)
    val yellowGreen: Color = Color.rgb(0x9a, 0xcd, 0x33)

    @strictpure def rgb(red: Z, green: Z, blue: Z): Color = rgba(
      conversions.Z.toU8(red), conversions.Z.toU8(green), conversions.Z.toU8(blue), 1d
    )

    @strictpure def rgba(red: U8, green: U8, blue: U8, alpha: F64): Color =
      Color(red, green, blue, alpha)
  }

  @datatype trait Gradient

  object Gradient {

    @enum object CycleMethod {
      "NoCycle"
      "Reflect"
      "Repeat"
    }

    @datatype class Linear(val start: Point,
                           val end: Point,
                           val stops: ISZ[(Color, F64)],
                           val cycleMethod: CycleMethod.Type) extends Gradient

    @datatype class Radial(val outer: Point,
                           val inner: Point,
                           val radius: F64,
                           val stops: ISZ[(Color, F64)],
                           val cycleMethod: CycleMethod.Type) extends Gradient

  }

  @datatype class Vector(val x: F64, val y: F64)

  @datatype class Transform(val elements: ISZ[F64])

  object Util {
    @datatype class Circle(val d: F64) extends Image

    @datatype class Rectangle(val w: F64, val h: F64) extends Image

    @datatype class Triangle(val w: F64, val h: F64) extends Image

    @datatype class Beside(val l: Image, val r: Image) extends Image

    @datatype class Above(val l: Image, val r: Image) extends Image

    @datatype class On(val t: Image, val b: Image) extends Image

    @datatype class At(val image: Image, val x: F64, val y: F64) extends Image

    @datatype class Transform(val tx: Doodle.Transform, val i: Image) extends Image

    @datatype class StrokeWidth(val image: Image, val width: F64) extends Image

    @datatype class StrokeColor(val image: Image, val color: Color) extends Image

    @datatype class FillColor(val image: Image, val color: Color) extends Image

    @datatype class FillGradient(val image: Image, val gradient: Gradient) extends Image

    @datatype class NoStroke(val image: Image) extends Image

    @datatype class NoFill(val image: Image) extends Image

    @datatype class Debug(val image: Image, val color: Color) extends Image

    @datatype class Empty extends Image

    val oneOnSqrt3: F64 = 1d / Ext.sqrt(3d)

    @strictpure def plineTo(radius: F64, angle: Angle): Image.Path.Element =
      Image.Path.Element.LineTo(Point.Polar(radius, angle))

    @strictpure def pmoveTo(radius: F64, angle: Angle): Image.Path.Element = Image.Path.Element.MoveTo(Point.Polar(radius, angle))

    @strictpure def clineTo(x: F64, y: F64): Image.Path.Element = Image.Path.Element.LineTo(Point.Cartesian(x, y))

    @strictpure def cmoveTo(x: F64, y: F64): Image.Path.Element = Image.Path.Element.MoveTo(Point.Cartesian(x, y))

    @strictpure def curveTo(cp1X: F64,
                            cp1Y: F64,
                            cp2X: F64,
                            cp2Y: F64,
                            toX: F64,
                            toY: F64): Image.Path.Element = Image.Path.Element.BezierCurveTo(
      Point.Cartesian(cp1X, cp1Y),
      Point.Cartesian(cp2X, cp2Y),
      Point.Cartesian(toX, toY)
    )

    @strictpure def scale(x: F64, y: F64): Doodle.Transform = Doodle.Transform(ISZ(x, 0d, 0d, 0d, y, 0d, 0d, 0d, 1d))

    @strictpure def rotate(angle: Angle): Doodle.Transform =
      Doodle.Transform(ISZ(angle.cos, -angle.sin, 0d, angle.sin, angle.cos, 0d, 0d, 0d, 1d))

    @strictpure def catmulRom(points: ISZ[Point]): ISZ[Image.Path.Element] = catmulRomWithTension(points, 0.5d)

    @pure def catmulRomWithTension(points: ISZ[Point], tension: F64): ISZ[Image.Path.Element] = {
      @strictpure def toCurve(pt0: Point, pt1: Point, pt2: Point, pt3: Point): Image.Path.Element =
        curveTo(
          ((-tension * pt0.x) + 3d * pt1.x + (tension * pt2.x)) / 3d,
          ((-tension * pt0.y) + 3d * pt1.y + (tension * pt2.y)) / 3d,
          ((tension * pt1.x) + 3d * pt2.x - (tension * pt3.x)) / 3d,
          ((tension * pt1.y) + 3d * pt2.y - (tension * pt3.y)) / 3d,
          pt2.x,
          pt2.y
        )
      if (points.isEmpty) {
        return ISZ()
      }
      @pure def rec(pts: ISZ[Point]): ISZ[Image.Path.Element] = {
        if (pts.size >= 4) {
          return toCurve(pts(0), pts(1), pts(2), pts(3)) +: rec(ops.ISZOps(pts).drop(1))
        } else if (pts.size >= 3) {
          return ISZ(toCurve(pts(0), pts(1), pts(2), pts(2)))
        } else {
          return ISZ()
        }
      }
      return cmoveTo(points(0).x, points(0).y) +: rec(points(0) +: points)
    }

  }

  @ext("Doodle_Ext") object Ext {
    @pure def pi: F64 = $

    @pure def sin(value: F64): F64 = $

    @pure def cos(value: F64): F64 = $

    @pure def sqrt(value: F64): F64 = $

    @pure def atan2(y: F64, x: F64): F64 = $

    @pure def toF64(value: Z): F64 = $

    def draw(image: Image): Unit = $

    def write(image: Image, path: Os.Path): Unit = $
  }

  def pause(prompt: String): Unit = {
    extension.Console.pause(prompt)
  }

}