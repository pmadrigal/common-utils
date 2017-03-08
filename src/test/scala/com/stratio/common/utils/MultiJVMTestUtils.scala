
/*
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.common.utils

import scala.reflect.ClassTag

object MultiJVMTestUtils {

  import scala.sys.process._

  def externalProcess[T <: App](app: T)(params: String*)(implicit ct: ClassTag[T]): ProcessBuilder = {

    val separator = System.getProperty("file.separator")
    val javaPath =  System.getProperty("java.home")::"bin"::"java"::Nil mkString separator
    val classPath = System.getProperty("java.class.path")
    val className = ct.runtimeClass.getCanonicalName.reverse.dropWhile(_ == '$').reverse

    javaPath :: "-cp" :: classPath :: className :: params.toList

  }

  class TestBatch private (private val batch: Seq[ProcessBuilder] = Seq.empty) {

    def addProcess(process: ProcessBuilder): TestBatch = new TestBatch(process +: batch)

    def launchAndWait(): Seq[String] = {

      val mergedOutputs = new collection.mutable.Queue[String]()

      val pLogger = ProcessLogger { line =>
        mergedOutputs.synchronized(mergedOutputs.enqueue(line))
      }

      val toWait = batch.reverse.map { process =>
        process.run(pLogger)
      }

      toWait.foreach(_.exitValue())

      mergedOutputs synchronized mergedOutputs

    }

  }

  object TestBatch {
    def apply(): TestBatch = new TestBatch()
  }

  /* USE EXAMPLE:

    import com.stratio.common.utils.integration.ZKTransactionTestClient

    val testBatch = TestBatch() addProcess {
      externalProcess(ZKTransactionTestClient)("testclient1", "3", "a", "b", "c", "10", "300")
    } addProcess {
      externalProcess(ZKTransactionTestClient)("testclient2", "2", "c", "d", "10", "200")
    } addProcess {
      externalProcess(ZKTransactionTestClient)("testclient3", "2", "b", "c", "10", "200")
    } addProcess {
      externalProcess(ZKTransactionTestClient)("testclient4", "2", "c", "d", "10", "200")
    }

    testBatch.launchAndWait().foreach(x => println(s"> $x"))

  */


}
