/*
 Copyright (c) 2017-2022, Robby, Kansas State University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.sireum._

object Doodle_Ext {

  class ImageSyntax(var done: Boolean = false,
                    var errMessage: Predef.String = null) extends doodle.image.syntax.JvmImageSyntax {
    override def unitCallback[A]: scala.Either[Throwable, A] => Unit = {
      case Left(err) =>
        val sw = new java.io.StringWriter()
        val pw = new java.io.PrintWriter(sw)
        err.printStackTrace(pw)
        errMessage = sw.toString
        done = true
      case Right(_) =>
        done = true
    }
  }

  @pure def pi: F64 = math.Pi

  @pure def sin(value: F64): F64 = math.sin(value.value)

  @pure def cos(value: F64): F64 = math.cos(value.value)

  @pure def sqrt(value: F64): F64 = math.sqrt(value.value)

  @pure def atan2(y: F64, x: F64): F64 = math.atan2(y.value, x.value)

  @pure def toF64(value: Z): F64 = value.toBigInt.toDouble

  def draw(image: Doodle.Image): Unit = {
    val img = translate(image)
    val syntax = new ImageSyntax()

    import cats.effect.unsafe.implicits.global
    import syntax.ImageOps
    import doodle.java2d.java2dRenderer
    img.draw()

    while (!syntax.done) try Thread.sleep(100) catch { case _: Throwable => }
    if (syntax.errMessage != null) {
      halt(syntax.errMessage)
    }
  }

  def write(image: Doodle.Image, path: Os.Path): Unit = {
    val img = translate(image)
    val syntax = new ImageSyntax()

    import cats.effect.unsafe.implicits.global
    import syntax.ImageOps

    ops.StringOps(path.ext).toLower match {
      case string"png" =>
        import doodle.java2d.java2dPngWriter
        import doodle.syntax.writer.WriterOps
        import doodle.core.format.Png
        img.write[Png](path.value.value)
      case string"gif" =>
        import doodle.java2d.java2dGifWriter
        import doodle.syntax.writer.WriterOps
        import doodle.core.format.Gif
        img.write[Gif](path.value.value)
      case string"jpg" | string"jpeg" =>
        import doodle.java2d.java2dJpgWriter
        import doodle.syntax.writer.WriterOps
        import doodle.core.format.Jpg
        img.write[Jpg](path.value.value)
      case string"pdf" =>
        import doodle.java2d.java2dPdfWriter
        import doodle.syntax.writer.WriterOps
        import doodle.core.format.Pdf
        img.write[Pdf](path.value.value)
      case _ => halt(s"Unsupported graphic file extension: ${path.ext}")
    }

    while (!syntax.done) try Thread.sleep(100) catch { case _: Throwable => }
    if (syntax.errMessage != null) {
      halt(syntax.errMessage)
    }
  }

  import doodle.core.Gradient.CycleMethod
  import doodle.core.{Angle, Color, Gradient, PathElement, Point, Transform}
  import doodle.image.Image

  def translate(point: Doodle.Point): Point = point match {
    case point: Doodle.Point.Cartesian => Point.cartesian(point.x.value, point.y.value)
    case point: Doodle.Point.Polar => Point.polar(point.radius.value, Angle(point.angle.radians.value))
  }

  def translate(pe: Doodle.Image.Path.Element): PathElement = pe match {
    case pe: Doodle.Image.Path.Element.LineTo => PathElement.lineTo(translate(pe.to))
    case pe: Doodle.Image.Path.Element.MoveTo => PathElement.moveTo(translate(pe.to))
    case pe: Doodle.Image.Path.Element.BezierCurveTo =>
      PathElement.curveTo(translate(pe.cp1), translate(pe.cp2), translate(pe.to))
  }

  def translate(color: Doodle.Color): Color = Color.rgba(
    conversions.U8.toZ(color.red).toInt,
    conversions.U8.toZ(color.green).toInt,
    conversions.U8.toZ(color.blue).toInt,
    color.alpha.value
  )

  def translate(cycleMethod: Doodle.Gradient.CycleMethod.Type): CycleMethod = cycleMethod match {
    case Doodle.Gradient.CycleMethod.NoCycle => CycleMethod.NoCycle
    case Doodle.Gradient.CycleMethod.Reflect => CycleMethod.Reflect
    case Doodle.Gradient.CycleMethod.Repeat => CycleMethod.Repeat
  }

  def translate(gradient: Doodle.Gradient): Gradient = gradient match {
    case gradient: Doodle.Gradient.Linear => Gradient.Linear(translate(gradient.start), translate(gradient.end),
      (for (stop <- gradient.stops) yield (translate(stop._1), stop._2.value)).elements,
      translate(gradient.cycleMethod))
    case gradient: Doodle.Gradient.Radial => Gradient.Radial(translate(gradient.outer), translate(gradient.inner),
      gradient.radius.value, (for (stop <- gradient.stops) yield (translate(stop._1), stop._2.value)).elements,
      translate(gradient.cycleMethod))
  }

  def translate(tx: Doodle.Transform): Transform = Transform((for (e <- tx.elements) yield e.value).elements.toArray)

  def translate(image: Doodle.Image): Image = image match {
    case image: Doodle.Image.Path =>
      val list = (for (pe <- image.elements) yield translate(pe)).elements.toList
      if (image.closed) Image.Elements.OpenPath(list) else Image.Elements.ClosedPath(list)
    case image: Doodle.Util.Above => Image.Elements.Above(translate(image.l), translate(image.r))
    case image: Doodle.Util.At => Image.Elements.At(translate(image.image), image.x.value, image.y.value)
    case image: Doodle.Util.Beside => Image.Elements.Beside(translate(image.l), translate(image.r))
    case image: Doodle.Util.Circle => Image.Elements.Circle(image.d.value)
    case image: Doodle.Util.Debug => Image.Elements.Debug(translate(image.image), translate(image.color))
    case _: Doodle.Util.Empty => Image.Elements.Empty
    case image: Doodle.Util.FillColor => Image.Elements.FillColor(translate(image.image), translate(image.color))
    case image: Doodle.Util.FillGradient => Image.Elements.FillGradient(translate(image.image), translate(image.gradient))
    case image: Doodle.Util.NoFill => Image.Elements.NoFill(translate(image.image))
    case image: Doodle.Util.NoStroke => Image.Elements.NoStroke(translate(image.image))
    case image: Doodle.Util.On => Image.Elements.On(translate(image.t), translate(image.b))
    case image: Doodle.Util.Rectangle => Image.Elements.Rectangle(image.w.value, image.h.value)
    case image: Doodle.Util.StrokeColor => Image.Elements.StrokeColor(translate(image.image), translate(image.color))
    case image: Doodle.Util.StrokeWidth => Image.Elements.StrokeWidth(translate(image.image), image.width.value)
    case image: Doodle.Util.Transform => Image.Elements.Transform(translate(image.tx), translate(image.i))
    case image: Doodle.Util.Triangle => Image.Elements.Triangle(image.w.value, image.h.value)
  }
}
