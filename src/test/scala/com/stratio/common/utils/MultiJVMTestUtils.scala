
package com.stratio.common.utils

import java.io._

import com.stratio.common.utils.components.transaction_manager.TestClient

import scala.reflect.ClassTag
import scala.collection.mutable.SynchronizedQueue

object MultiJVMTestUtils extends App {

  import scala.sys.process._

  def externalProcess[T <: App](app: T)(params: String*)(implicit ct: ClassTag[T]): ProcessBuilder = {

    val separator = System.getProperty("file.separator")
    val javaPath =  System.getProperty("java.home")::"bin"::"java"::Nil mkString separator
    val classPath = System.getProperty("java.class.path")
    val className = ct.runtimeClass.getCanonicalName.reverse.dropWhile(_ == '$').reverse

    javaPath :: "-cp" :: classPath :: className :: params.toList

  }


  /*private val mergedOutputs = new collection.mutable.Queue[String]()

  val pLogger = ProcessLogger { line =>
    mergedOutputs.synchronized(mergedOutputs.enqueue(line))
  }


  val p1 = externalProcess(TestClient)("testclient1", "2", "a", "b", "10", "300").run(pLogger)
  val p2 = externalProcess(TestClient)("testclient2", "2", "c", "d", "10", "200").run(pLogger)
  val p3 = externalProcess(TestClient)("testclient3", "2", "b", "c", "10", "200").run(pLogger)
  val p4 = externalProcess(TestClient)("testclient4", "2", "c", "d", "10", "200").run(pLogger)

  p1.exitValue()
  p2.exitValue()
  p3.exitValue()
  p4.exitValue()

  mergedOutputs.synchronized {
    mergedOutputs.foreach(println)
  }*/

}
