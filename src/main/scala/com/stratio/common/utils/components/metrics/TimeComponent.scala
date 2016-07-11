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

import scala.concurrent.duration.Duration

trait TimeComponent {

  type Milliseconds = Long

  /**
   * Executes given [[T]] expression and grabs
   * the time that took evaluating it.
   * @param t Lazy [[T]] expression.
   * @tparam T Expression type
   * @return A tuple with both the result of
   *         evaluating expression 't' and the
   *         duration it took.
   */
  def time[T](t: => T): (T,Duration) = {
    import scala.concurrent.duration._
    val startTime = now()
    val result = t
    (result,(now() - startTime).millis)
  }

  /**
   * Just takes the time that takes evaluating
   * some Unit expression.
   * @param expression Lazy Unit expression to be
   *                   evaluated
   * @return Just the time it took to be evaluated.
   */
  def justTime(expression: => Unit): Duration = {
    val (_, timeItTook) = time(expression)
    timeItTook
  }

  def now(): Milliseconds

}
