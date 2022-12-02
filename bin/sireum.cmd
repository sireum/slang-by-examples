::/*#! 2> /dev/null                                 #
@ 2>/dev/null # 2>nul & echo off & goto BOF         #
if [ -z ${SIREUM_HOME} ]; then                      #
  echo "Please set SIREUM_HOME env var"             #
  exit -1                                           #
fi                                                  #
exec ${SIREUM_HOME}/bin/sireum slang run "$0" "$@"  #
:BOF
setlocal
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
%SIREUM_HOME%\bin\sireum.bat slang run "%0" %*
exit /B %errorlevel%
::!#*/
// #Sireum

import org.sireum._

val version = GitHub.repo("sireum", "kekinian").releases.take(1).toISZ(0).name

println(s"Loading Sireum v$version ...")
val deps = Coursier.fetch(Sireum.scalaVer, ISZ(s"org.sireum.kekinian::cli:$version"))
println()

val javaArgs = ISZ[ST](
  st"-classpath",
  st"${(for (dep <- deps) yield dep.path, Os.pathSep)}",
  st"org.sireum.Sireum"
) ++ (for (arg <- Os.cliArgs) yield st"$arg")

val javaArgsFile = Os.temp()
javaArgsFile.writeOver(st"${(javaArgs, "\n")}".render)
javaArgsFile.removeOnExit()

val java = Os.path(Os.env("JAVA_HOME").get) / "bin" / (if (Os.isWin) "java.exe" else "java")
proc"$java @$javaArgsFile".console.runCheck()
