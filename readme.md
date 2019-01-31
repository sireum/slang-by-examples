# Slang by Examples

This repository holds Slang scripts designed for people familiar with
programming to learn Slang quickly by looking at the scripts and
experimenting with them.


## Read the Code

* [Boolean, String, and Basic Statements](src/00-boolean-string-basic-statements.sc)
* [Integers and Reals](src/01-integers-reals.sc)
* [Sequences and Mutability](src/02-sequences-mutability.sc)

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