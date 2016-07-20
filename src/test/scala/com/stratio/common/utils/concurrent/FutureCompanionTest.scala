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

import org.junit.runner.RunWith

import scala.concurrent.Future
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class FutureCompanionTest extends FlatSpec with Matchers with ScalaFutures{

  behavior of "FutureCompanion"

  it should "allow converting an optional future into a future optional value" in {

    import scala.concurrent.ExecutionContext.Implicits.global

    val value = Option(Future(1))

    val f: Future[Option[Int]] = Future.option(value)

    whenReady(f){
      _ shouldEqual Option(1)
    }

  }

}
