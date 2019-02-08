# Slang by Examples

This repository holds Slang scripts designed for people familiar with
programming to learn Slang quickly by reading the scripts and
experimenting with them.


## Read the Code

* [Boolean, String, and Basic Statements](src/00-boolean-string-basic-statements.sc)
* [Integers. Character, and Reals](src/01-integers-character-reals.sc)
* [Sequences and Mutability](src/02-sequences-mutability.sc)
* [Enum and Tuples](src/03-enum-tuples.sc)
* [Blocks, Conditionals, and Loops](src/04-blocks-conditionals-loops.sc)
* [Methods and Functions](src/05-methods-functions.sc)
* [ADTs, Sigs, and Module](src/06-adts-sigs-module.sc)
* [Pattern Matching and Mutability](src/07-pattern-matching.sc)
* [String Template](src/08-string-template.sc)
* [Extension](src/09-extension.sc)

## Setup

To setup, install [Sireum Kekinian](https://github.com/sireum/kekinian)
and execute the following in a terminal (assuming `SIREUM_HOME` is 
set to Sireum's installation path):

* **macOS/Linux**

  ```bash
  git clone https://github.com/sireum/slang-by-examples
  ${SIREUM_HOME}/bin/sireum tools ivegen -n slang-by-examples .
  ```

* **Windows**

  ```batch
  git clone https://github.com/sireum/slang-by-examples
  %SIREUM_HOME%\bin\sireum tools ivegen -n slang-by-examples .
  ```
  
Then, load the `slang-by-examples` directory in Sireum IVE, and
read/edit/run the scripts.