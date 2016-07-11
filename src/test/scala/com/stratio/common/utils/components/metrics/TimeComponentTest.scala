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
package com.stratio.common.utils.components.metrics

import org.scalatest.{Matchers, FlatSpec}

class TimeComponentTest extends FlatSpec with Matchers {

  behavior of "TimeComponent"

  object Timer extends DummyTimeComponent

  import scala.concurrent.duration._
  import Timer._

  it should "measure time that takes evaluating some expression" in {
    val (result,t) = time { 2 + 2}
    result shouldEqual 4
    t shouldEqual 1.millis
  }

  it should "measure time ignoring the result of some expression" in {
    val t = justTime {
      "hi"
    }
    t shouldEqual 4.millis
  }

}
