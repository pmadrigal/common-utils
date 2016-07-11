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


import java.util.concurrent.{Callable, FutureTask}
import scala.concurrent.{ExecutionContext, Future}

object Cancellable {
  /**
    * Factory method
    */
  def apply[T](todo: => T)(implicit executionContext: ExecutionContext): Cancellable[T] =
    new Cancellable[T](executionContext, todo)
}

/**
  * A `Cancellable` is an object which is completed by the future running a code block
  * or by a programmatic cancellation.
  *
  * @param executionContext Used to run the code block.
  * @param todo Code block to execute within a [[Future]] which can be cancellable by the user.
  * @tparam T Type of a successful `todo` execution result.
  */
class Cancellable[T](executionContext: ExecutionContext, todo: => T) {

  private val jf: FutureTask[T] = new FutureTask[T](
    new Callable[T] {
      override def call(): T = todo
    }
  )

  executionContext.execute(jf)

  implicit val _: ExecutionContext = executionContext

  val future: Future[T] = Future {
    jf.get
  }

  /**
    * Cancels the future job completing `fut` with `Failure(InterruptedException)`
    */
  def cancel(): Unit = jf.cancel(true)

}
