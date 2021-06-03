::#! 2> /dev/null                                   #
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
::!#
// #Sireum

import org.sireum._

val version: String = Os.cliArgs match {
  case ISZ(v) => v
  case _ => GitHub.repo("sireum", "kekinian").releases.take(1).toISZ(0).name
}
val home = Os.slashDir.up.canon

println(s"Loading Sireum v$version")

var deps = ISZ[CoursierFileInfo]()
for (cif <- Coursier.fetch(ISZ(s"org.sireum.kekinian::cli:$version"))) {
  deps = deps :+ cif
}

val javaArgs = ISZ[ST](
  st"-classpath",
  st"${(for (dep <- deps) yield dep.path, Os.pathSep)}",
  st"org.sireum.Sireum"
) ++ (for (arg <- Os.cliArgs) yield st"$arg")


val javaArgsFile = Os.home / "Temp" / "java-args"
javaArgsFile.writeOver(st"${(javaArgs, "\n")}".render)
javaArgsFile.removeOnExit()

proc"${Os.path(Os.env("JAVA_HOME").get) / "bin" / (if (Os.isWin) "java.exe" else "java") } @$javaArgsFile".console.runCheck()
