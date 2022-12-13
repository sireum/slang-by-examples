// #Sireum
import org.sireum._

import Doodle._

object DoodleExample extends App {

  def main(args: ISZ[String]): Z = {

    val blackSquare = Image.square(30d).fillColor(Color.black)

    val redSquare = Image.square(30d).fillColor(Color.red)

    val twoByTwo = redSquare.
      beside(blackSquare).
      above(blackSquare.beside(redSquare))

    val fourByFour = twoByTwo.
      beside(twoByTwo).
      above(twoByTwo.beside(twoByTwo))

    val chessboard = fourByFour.
      beside(fourByFour).
      above(fourByFour.beside(fourByFour))

    print("Drawing ... ")
    chessboard.draw()
    println("done!")

    val p = Os.cwd / "chessboard.png"
    print(s"Writing to $p ... ")
    chessboard.writeTo(Os.cwd / "chessboard.png")
    println("done!")
    pause("Press enter to exit ...")
    return 0
  }

}
