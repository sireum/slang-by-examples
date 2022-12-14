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

import Doodle._

// Adapted from https://github.com/creativescala/doodle/blob/v0.12.0/docs/src/pages/README.md

object DoodleExample extends App {

  def main(args: ISZ[String]): Z = {

    val blackShape = Image.square(30d).fillColor(Color.black)

    val redShape = Image.square(30d).fillColor(Color.red)

    val twoByTwo = redShape.
      beside(blackShape).
      above(blackShape.beside(redShape))

    val fourByFour = twoByTwo.
      beside(twoByTwo).
      above(twoByTwo.beside(twoByTwo))

    val chessboard = fourByFour.
      beside(fourByFour).
      above(fourByFour.beside(fourByFour))

    print("Drawing ... ")
    chessboard.draw()
    println("done!")

    val p = Os.cwd / "board.png"
    print(s"Writing to $p ... ")
    chessboard.writeTo(Os.cwd / "board.png")
    println("done!")

    pause("Press enter to exit ...")
    return 0
  }

}
