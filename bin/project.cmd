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
import org.sireum.project.{Module, Project, Target}

val home = Os.slashDir.up.canon

val example = Module(
  id = "example",
  basePath = (home / "src").string,
  subPathOpt = None(),
  deps = ISZ(),
  targets = ISZ(Target.Jvm),
  ivyDeps = ISZ("org.sireum.kekinian::library:"),
  sources = ISZ("."),
  resources = ISZ(),
  testSources = ISZ(),
  testResources = ISZ(),
  publishInfoOpt = None()
)

val prj = Project.empty + example

println(project.JSON.fromProject(prj, T))
