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
import org.sireum._

/*
Slash script is a universal (combined sh and batch) shell script (.cmd), where
the actual script functionality is written in Slang.
The Sireum IVE provides IDE support (e.g., Slang type checking) for Slash script files.

As illustrated in this example, Slash script uses CRLF (Windows) newline separator
and its header consists of sh (lines 3-11) and batch (lines 13-18) scripts that run
this file using the Slang script runner. Note that sh part should end with a hash (#).

Like Slang code, Slash script can be compiled to native code (.cmd.com) if
GraalVM's native-image is available in the PATH env var.

In the sh part, it detects if this script is already compiled to native and
if the native binary is newer than this file (based on the files' last modified time);
if so, the native binary is called instead, thus offering no JVM boot up and
no code compilation overhead on subsequent uses.
 */

println(s"Script home: ${Os.slashDir}")
println(s"CLI args: ${Os.cliArgs}")
println(s"Current working directory: ${Os.cwd}")

val foo = Os.cwd / "a d" / "foo.txt"
if (Os.kind == Os.Kind.Win) {
  proc"cmd /c dir".console.run() // spawn a process, command is space-separated; use ␣ for denoting a space char
  proc"cmd /c md a␣d".console.run()
  foo.write("foo")
  proc"cmd /c type $foo".console.run()
} else {
  proc"ls -lah".console.run()
  proc"mkdir a␣d".console.run()
  foo.write("foo")
  proc"cat $foo".console.run()
}
foo.up.removeAll()
println()

/*
For more examples, see:
* Sireum's build script: https://github.com/sireum/kekinian/blob/master/bin/build.cmd
* Mill for Sireum's build script: https://github.com/sireum/mill-build/blob/master/bin/build.cmd
 */
