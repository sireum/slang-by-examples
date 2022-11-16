::#! 2> /dev/null                                             #
@ 2>/dev/null # 2>nul & echo off & goto BOF                   #
if [ -f "$0.com" ] && [ "$0.com" -nt "$0" ]; then             #
  exec "$0.com" "$@"                                          #
fi                                                            #
rm -f "$0.com"                                                #
if [ -z ${SIREUM_HOME} ]; then                                #
  echo "Please set SIREUM_HOME env var"                       #
  exit -1                                                     #
fi                                                            #
exec ${SIREUM_HOME}/bin/sireum slang run -n "$0" "$@"         #
:BOF
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
%SIREUM_HOME%\bin\sireum.bat slang run -n "%0" %*
exit /B %errorlevel%
::!#
// #Sireum
/*
 Copyright (c) 2020, Robby, Kansas State University
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

/*
 This command-line tool transposes a musical transcription (note-only).

 Usage: [flat | sharp] <num> <file>

 The argument input <file> should be a plain text file where each note should 
 be written satisfying the following regex:

 [A-G, a-g] ( 0 | [1-9][0-9]* ) ( '#' | 'b' )?

 where the number indicates the octave level.

 For example, some valid notes are A0b, G1#, etc.

 Note that whitespaces and the characters '[', ']', '|' are propagated to
 the output as is.  No other characters are allowed, unless inside a 
 line-comment, which starts on a semicolon (';').

 The argument <num> is an integer number for transposing in the number of
 half-steps/semitones.
 
 For example, 3, -2, etc.

 Note that any note cannot be in an octave level below 0 after transposing.

 The flat or sharp optional argument forces the output to always use the
 flat ('b') or sharp ('#') symbol whenever applicable, respectively.
 Otherwise, the symbol will be chosen based on the preceeding note or #
 if it is the first note.
 
 For example, A2b F2# B2b, will be outputted as G2# G2b A2#. 
 */

import org.sireum._

object Note {

  @enum object Key {
    'C
    'Cs
    'D
    'Ds
    'E
    'F
    'Fs
    'G
    'Gs
    'A
    'As
    'B
  }

  def key2string(sharp: B, key: Key.Type): String = {
    val r: String = key match {
      case Key.C => "C"
      case Key.Cs => if (sharp) "C#" else "Db"
      case Key.D => "D"
      case Key.Ds => if (sharp) "D#" else "Eb"
      case Key.E => "E"
      case Key.F => "F"
      case Key.Fs => if (sharp) "F#" else "Gb"
      case Key.G => "G"
      case Key.Gs => if (sharp) "G#" else "Ab"
      case Key.A => "A"
      case Key.As => if (sharp) "A#" else "Bb"
      case Key.B => "B"
    }
    return r
  }

  def isNatural(key: Key.Type): B = {
    key match {
      case Key.C =>
      case Key.D =>
      case Key.E =>
      case Key.F =>
      case Key.G =>
      case Key.A =>
      case Key.B =>
      case _ => return F
    }
    return T
  }

}

@datatype class Note(key: Note.Key.Type, octave: Z) {
  override def string: String = {
    val r: String = key match {
      case Note.Key.C => s"C$octave"
      case Note.Key.Cs => s"C$octave#"
      case Note.Key.D => s"D$octave"
      case Note.Key.Ds => s"D$octave#"
      case Note.Key.E => s"E$octave"
      case Note.Key.F => s"F$octave"
      case Note.Key.Fs => s"F$octave#"
      case Note.Key.G => s"G$octave"
      case Note.Key.Gs => s"G$octave#"
      case Note.Key.A => s"A$octave"
      case Note.Key.As => s"A$octave#"
      case Note.Key.B => s"B$octave"
    }
    return r
  }
}

val (forceFlat, forceSharp, numArg, fileArg): (B, B, String, String) = {
  Os.cliArgs match {
    case ISZ(dir, n, f) =>
      dir match {
        case string"flat" => (T, F, n, f)
        case string"sharp" => (F, T, n, f)
        case _ =>
          eprintln(s"Expecting 'flat' or 'sharp', but found '$dir'")
          Os.exit(-1)
          halt("")
      }
    case ISZ(n, f) => (F, F, n, f)
    case _ =>
      println("Usage: [flat | sharp] <num> <file>")
      Os.exit(0)
      halt("")
  }
}

val num: Z = Z(numArg) match {
  case Some(n) => n
  case _ =>
    eprintln(s"Expecting a number for the first argument, but found '$numArg'")
    Os.exit(-1)
    halt("")
}

val file: Os.Path = Os.path(fileArg)
if (!file.exists) {
  eprintln(s"Input file '$file' does not exist")
  Os.exit(-1)
}

val keys: ISZ[Note.Key.Type] = Note.Key.elements
val octaveShift = num / keys.size
val keyShift: Z =
  if (num < 0) ((num * -1) % keys.size) * -1
  else num % keys.size

val transposedNotes: ISZ[Note] = {
  val r = MSZ.create[Note](keys.size, Note(Note.Key.C, 0))
  for (i <- 0 until keys.size) {
    var n = i + keyShift
    var octave: Z = 0
    if (n < 0) {
      n = n + keys.size
      octave = -1 + octaveShift
    } else if (n > keys.size) {
      n = n - keys.size
      octave = 1 + octaveShift
    }
    r(i) = Note(keys(n), octave)
  }
  r.toIS
}

var prevNoteOpt: Option[Note] = None[Note]()
var prevNoteUp: B = T
var output = ISZ[C]()

@strictpure def computeNum(note: Note): Z = 
  note.key.ordinal + note.octave * keys.size

var natNotes = HashSet.empty[String]
var nonNatNotes = HashSet.empty[String]

def outputNote(note: Note): Unit = {
  def outputOctave(): Unit = {
    for (c <- conversions.String.toCis(note.octave.string)) {
      output = output :+ c
    }
  }
  val up: B =
    if (forceSharp) {
      T
    } else if (forceFlat) {
      F
    } else {
      prevNoteOpt match {
        case Some(prevNote) =>
          if (prevNote == note) {
            prevNoteUp
          } else {
            computeNum(note) >= computeNum(prevNote)
          }
        case _ => T
      }
    }
  note.key match {
    case Note.Key.C =>
      output = output :+ 'C'
      outputOctave()
    case Note.Key.Cs =>
      if (up) {
        output = output :+ 'C'
        outputOctave()
        output = output :+ '#'
      } else {
        output = output :+ 'D'
        outputOctave()
        output = output :+ 'b'
      }
    case Note.Key.D =>
      output = output :+ 'D'
      outputOctave()
    case Note.Key.Ds =>
      if (up) {
        output = output :+ 'D'
        outputOctave()
        output = output :+ '#'
      } else {
        output = output :+ 'E'
        outputOctave()
        output = output :+ 'b'
      }
    case Note.Key.E =>
      output = output :+ 'E'
      outputOctave()
    case Note.Key.F =>
      output = output :+ 'F'
      outputOctave()
    case Note.Key.Fs =>
      if (up) {
        output = output :+ 'F'
        outputOctave()
        output = output :+ '#'
      } else {
        output = output :+ 'G'
        outputOctave()
        output = output :+ 'b'
      }
    case Note.Key.G =>
      output = output :+ 'G'
      outputOctave()
    case Note.Key.Gs =>
      if (up) {
        output = output :+ 'G'
        outputOctave()
        output = output :+ '#'
      } else {
        output = output :+ 'A'
        outputOctave()
        output = output :+ 'b'
      }
    case Note.Key.A =>
      output = output :+ 'A'
      outputOctave()
    case Note.Key.As =>
      if (up) {
        output = output :+ 'A'
        outputOctave()
        output = output :+ '#'
      } else {
        output = output :+ 'B'
        outputOctave()
        output = output :+ 'b'
      }
    case Note.Key.B =>
      output = output :+ 'B'
      outputOctave()
  }
  if (Note.isNatural(note.key)) {
    natNotes = natNotes + Note.key2string(up, note.key)
  } else {
    nonNatNotes = nonNatNotes + Note.key2string(up, note.key)
  }
  prevNoteUp = up
  prevNoteOpt = Some(note)
}

val chars = conversions.String.toCis(file.read)

var i: Z = 0

def done(): B = {
  return i >= chars.size
}

@pure def shouldSkip(c: C): B = {
  c match {
    case c"[" =>
    case c"]" =>
    case c"|" =>
    case c";" =>
    case _ => return c.isWhitespace
  }
  return T
}

var line: Z = 1
var lastLineOffset: Z = 0

def skip(): Unit = {
  while (!done() && shouldSkip(chars(i))) {
    val c = chars(i)
    c match {
      case c"\n" =>
        if (c == '\n') {
          line = line + 1
          lastLineOffset = i
        }
        output = output :+ c
        i = i + 1
      case c";" =>
        while (!done() && chars(i) != '\n') {
          output = output :+ chars(i)
          i = i + 1
        }
      case _ =>
        output = output :+ c
        i = i + 1
    }
  }
}

def error(message: String): String = {
  eprintln(s"[$line, ${i - lastLineOffset + 1}] $message")
  Os.exit(-1)
  return ""
}

def transpose(note: Note): Note = {
  val tNote = transposedNotes(note.key.ordinal)
  val octave = note.octave + tNote.octave
  if (octave < 0) {
    halt(error(s"Could not transpose $note by ${if (num > 0) "+" else ""}$num, as it results in octave level $octave"))
  }
  return tNote(octave = octave)
}

@strictpure def isNumChar(c: C): B = c == '-' || ('0' <= c && c <= '9')

def nonNeg(): Z = {
  var cs = ISZ[C]()
  while (!done() && isNumChar(chars(i))) {
    cs = cs :+ chars(i)
    i = i + 1
  }
  Z(conversions.String.fromCis(cs)) match {
    case Some(n) if n >= 0 => return n
    case _ =>
      if (cs.isEmpty) {
        halt(error(s"Expecting a non-negative number, but found nothing"))
      } else {
        halt(error(s"Expecting a non-negative number, but found '${conversions.String.fromCis(cs)}'"))
      }
  }
}

def note(): Note = {
  assert(!done())
  val key: Note.Key.Type = chars(i) match {
    case c"C" => Note.Key.C
    case c"D" => Note.Key.D
    case c"E" => Note.Key.E
    case c"F" => Note.Key.F
    case c"G" => Note.Key.G
    case c"A" => Note.Key.A
    case c"B" => Note.Key.B
    case c"c" => Note.Key.C
    case c"d" => Note.Key.D
    case c"e" => Note.Key.E
    case c"f" => Note.Key.F
    case c"g" => Note.Key.G
    case c"a" => Note.Key.A
    case c"b" => Note.Key.B
    case c => halt(error(s"Expecting a note, but found '$c'"))
  }
  i = i + 1
  var octave = nonNeg()
  var r = Note(key, octave)
  if (!done()) {
    var ord: Z = chars(i) match {
      case c"#" =>
        i = i + 1
        r.key.ordinal + 1
      case c"b" =>
        i = i + 1
        r.key.ordinal - 1
      case _ => 0
    }
    if (ord < 0) {
      ord = ord + keys.size
      octave = octave + 1
    } else if (ord > keys.size) {
      ord = ord - keys.size
      octave = octave - 1
    }
    if (ord != 0) {
      r = Note(keys(ord), octave)
    }
  }
  return r
}

while (!done()) {
  skip()
  if (!done()) {
    outputNote(transpose(note()))
  }
}

val dotIndex = ops.StringOps(file.name).lastIndexOf('.')
val filenameOps = ops.StringOps(file.name)
val outputFile: Os.Path = {
  val sep: String = if (num >= 0) "+" else ""
  if (dotIndex >= 0) file.up / s"${filenameOps.substring(0, dotIndex)}$sep$num${filenameOps.substring(dotIndex, filenameOps.size)}"
  else file.up / s"${file.name}$sep$num"
}
outputFile.writeOver(conversions.String.fromCis(output))
println(st"Natural notes: ${natNotes.size} (${(natNotes.elements, ", ")})".render)
println(st"Sharp/flat notes: ${nonNatNotes.size} (${(nonNatNotes.elements, ", ")})".render)
println(s"Wrote $outputFile")
