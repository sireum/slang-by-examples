::#! 2> /dev/null                                          #
@ 2>/dev/null # 2>nul & echo off & goto BOF                #
if [ -z ${SIREUM_HOME} ]; then                             #
  echo "Please set SIREUM_HOME env var"                    #
  exit -1                                                  #
fi                                                         #
exec ${SIREUM_HOME}/bin/sireum slang run -s "$0" "$0" "$@" #
:BOF
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
%SIREUM_HOME%\bin\sireum.bat slang run -s "%0" "%0" %*
exit /B %errorlevel%
::!#
// #Sireum
import org.sireum._

println(s"CLI args: ${Os.cliArgs}")
println(s"Current working directory: ${Os.cwd}")

if (Os.kind == Os.Kind.Win) {
  Os.proc(ISZ("cmd", "/c", "dir")).console.run()
} else {
  Os.proc(ISZ("ls", "-lah")).console.run()
}