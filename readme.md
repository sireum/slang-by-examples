# Slang by Examples

This repository holds Slang scripts designed for people familiar with
programming to learn Slang quickly by reading the scripts and
experimenting with them.


## Read the Code

* [Boolean, String, and Basic Statements](src/00-boolean-string-basic-statements.sc)
* [Integers, Character, and Reals](src/01-integers-character-reals.sc)
* [Sequences and Mutability](src/02-sequences-mutability.sc)
* [Enum and Tuples](src/03-enum-tuples.sc)
* [Blocks, Conditionals, and Loops](src/04-blocks-conditionals-loops.sc)
* [Methods and Functions](src/05-methods-functions.sc)
* [ADTs, Sigs, and Module](src/06-adts-sigs-module.sc)
* [Pattern Matching and Mutability](src/07-pattern-matching.sc)
* [String Template](src/08-string-template.sc)
* [Extension](src/09-extension.sc)
* [**Slash**: a universal Slang shell script](src/slash.cmd)
  * [Spawning a process](src/sireum.cmd)
  * [A musical transcription transposer](src/transpose.cmd)


## Prerequisite

[Sireum Kekinian](https://github.com/sireum/kekinian) installed.


## Using Sireum IVE

Execute the following in a terminal (assuming `SIREUM_HOME` is 
set to Sireum's installation path):

* **macOS/Linux**

  ```bash
  git clone https://github.com/sireum/slang-by-examples
  cd slang-by-examples
  ${SIREUM_HOME}/bin/sireum proyek ive .
  ```

* **Windows**

  ```batch
  git clone https://github.com/sireum/slang-by-examples
  cd slang-by-examples
  %SIREUM_HOME%\bin\sireum proyek ive .
  ```
  
Then, load the `slang-by-examples` directory in Sireum IVE, and
read/edit/run the scripts.


## Using Sireum CLI

You can run the example from the command line, e.g.,:

* **macOS/Linux**
  
  ```bash
  ${SIREUM_HOME}/bin/sireum slang run src/00-boolean-string-basic-statements.sc
  ```
  
* **Windows**

  ```batch
  %SIREUM_HOME%\bin\sireum.bat slang run src\00-boolean-string-basic-statements.sc
  ```


### Native Executable Generation
  
On macOS and Linux, you can additionally generate native executable of the scripts
by adding `--native` as an option (`-n` for short) if [GraalVM](http://graalvm.org)'s
`native-image` is available in the `PATH` env var.
 