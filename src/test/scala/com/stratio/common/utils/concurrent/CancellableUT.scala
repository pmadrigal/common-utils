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
package com.stratio.common.utils.concurrent

import java.util.concurrent.CancellationException

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.concurrent.Timeouts._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure


@RunWith(classOf[JUnitRunner])
class CancellableUT extends FlatSpec with Matchers {

  val tsleep = 1 second
  val expectedRes = 42

  "A cancellable task" should "provide a future interface" in {

    val ct = Cancellable[Int] {
      Thread.sleep(tsleep.toMillis)
      expectedRes
    }

    Await.result(ct.future, 2 seconds) shouldBe 42

  }

  it should "be able to cancel the running task" in {

    val ct = Cancellable[Int] {
      Thread.sleep(tsleep.toMillis)
      expectedRes
    }

    failAfter(tsleep) {
      ct.cancel
      Await.ready(ct.future, tsleep*2).value should matchPattern {
        case Some(Failure(_: CancellationException)) =>
      }
    }

  }

}
