// #Sireum
import org.sireum._

/*
Extension module is a Slang mechanism by which one can use methods defined outside of Slang (e.g., in Scala).
In the JVM, it simply forwards calls to the extension implementation.

In general, extension is the Slang mechanism to use "native" library when Slang is compiled to other languages
(e.g., Javascript or C).
 */

@ext("MyExtImpl") object extension { // all methods are forwarded to MyExtImpl
  def hello(): Unit = $
}

extension.hello()

@ext object extension2 { // if no name is specified, by default it forwards to the module name suffixed with _Ext
  def repeat[T](o: T, n: Z): Unit = $
}

object MyExtImpl {  // extension is forwarded here (since Slang is Scala)
  def hello(): Unit = {
    println("Hello world!")
  }
}

object extension2_Ext {  // extension2 is forwarded here
  def repeat[T](o: T, n: Z): Unit = {
    for (_ <- 0 until n) {
      print(o)
    }
    println()
  }
}

extension2.repeat(1, 5)
extension2.repeat("a", 5)

/*
For more complex examples, see Sireum's runtime Os library:
* https://github.com/sireum/runtime/blob/master/library/jvm/src/main/scala/org/sireum/Os.scala
* https://github.com/sireum/runtime/blob/master/library/jvm/src/main/scala/org/sireum/Os_Ext.scala
 */