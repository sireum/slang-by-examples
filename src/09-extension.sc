// #Sireum
import org.sireum._

/*
Extension module is a Slang mechanism by which one can use methods defined outside of Slang (e.g., in Scala).
In the JVM, it simply forwards calls to the extension implementation.
 */

// Note: To run, use "Slang Script Runner" in the run configuration combo-box near green ► button at the top-right corner


@ext("MyExtImpl") object extension { // all methods are forwarded to MyExtImpl module which is implemented in Scala
  def hello(): Unit = $
}

extension.hello()


@ext object extension2 { // if no name is specified, by default it forwards to the module name suffixed with _Ext
  def hello(): Unit = $
}

object extension2_Ext {  // extension2 is forwarded here (since Slang is Scala)
  def hello(): Unit = {
    println("Hello to you too!")
  }
}

extension2.hello()