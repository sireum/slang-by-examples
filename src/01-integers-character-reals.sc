// #Sireum
import org.sireum._


// Z (arbitrary-precision integer) Literals
println(1) // can express numbers in [-2147483648, 2147483647]
println(0xFF) // hexadecimal
println(2L) // use l or L suffix for numbers smaller/larger numbers in [-9223372036854775808,  9223372036854775807]
println(0xFFL)
println(z"1000000000000000000000000000000000000000000000") // for any Z number
println(z"2,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000") // can be separated by comma, underscore, or space (or mixed)
println(z"3_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000")
println(z"4 000 000 000 000 000 000 000 000 000 000 000 000 000 000 000")

println(Z.random)

println(1 * 20L + 3 - z"10" / 2L % 10) // arithmetic operators


// Z Comparisons
assert(10L == 10)
assert(11L != 10L)
assert(10L < 100)
assert(100 > z"10")
assert(z"199" <= 0xFFL)
assert(0xFFL >= z"199")


// Range Integer Types
@range(min = 0, max = 9) class Z0To9 // defines (checked) range integer type [0, 9]

import Z0To9._ // import literal notation z0To9"..."

assert(z0To9"3" * z0To9"2" == z0To9"6") // available binary ops: + - * / % == != < <= > >=, unary op: -
// println(z0To9"10") // 10 is out of range; checked at compile-time (type checking)
// println(z0To9"9" * z0To9"2") // 18 is out of range; checked at run-time (i.e., this produces runtime error)

@range(min = 1) class Positive // defines arbitrary-precision positive integer type

@range(max = -1) class Negative // defines arbitrary-precision negative integer type


// Bit-vector Integer Types

@bits(signed = F, width = 8) class Ubyte

import Ubyte._

assert(ubyte"-1" == ubyte"0xFF") // available binary ops: + - * / % == != < <= > >= << >>> >>, unary op: - ~
assert((ubyte"1" << ubyte"3") == ubyte"0x8")


// Notes: Slang runtime library defines range types: Z8, Z16, Z32, Z64, N, N8, N16, N18, N32, and N64,
// and bitvector types: S8, S16, S32, S64, U8, U16, U18, U32, and U64; see
// https://github.com/sireum/runtime/blob/master/library/shared/src/main/scala/org/sireum/BitsRangeTypes.scala


// C (character)
val c1: C = 'A'
assert(c1 == '\u0041') // unicode
assert(c1 + '\u0001' == 'B') // available binary ops: + - == != < <= > >= << >>> >> & | |^


// Floating-point Number Types, available binary ops: + - * / % == != < <= > >=, unary op: -
val f1: F32 = 0.1f // 32-bit literal
println(f32"0.2")  // 32-bit literal
val f2: F64 = 0.1 + f64"10"  // 64-bit literals

// Real Number Type, available binary ops: + - * / % == != < <= > >=, unary op: -
val r: R = r"1.0"
println(r / r"3")