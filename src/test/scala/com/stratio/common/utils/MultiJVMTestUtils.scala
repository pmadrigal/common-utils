
package com.stratio.common.utils

import java.io.{ByteArrayInputStream, InputStream, OutputStream, PrintStream}

import com.stratio.common.utils.components.translation_manager.{RunningApp, TestApp01}

import scala.reflect.ClassTag

object MultiJVMTestUtils extends App {

  import scala.sys.process._

  def externalProcess[T <: App](app: T)(params: String*)(implicit ct: ClassTag[T]): ProcessBuilder = {

    val separator = System.getProperty("file.separator")
    val javaPath =  System.getProperty("java.home")::"bin"::"java"::Nil mkString separator
    val classPath = System.getProperty("java.class.path")
    val className = ct.runtimeClass.getCanonicalName.reverse.dropWhile(_ == '$').reverse

    javaPath :: "-cp" :: classPath :: className :: Nil

  }


  val is = new ByteArrayInputStream("hello there!".getBytes("UTF-8"))

  val p = (externalProcess(TestApp01)() #< is).run()
  Thread.sleep(2200)
  p.destroy()
  println(p.exitValue())



}
