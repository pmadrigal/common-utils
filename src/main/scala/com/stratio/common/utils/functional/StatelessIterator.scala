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
package com.stratio.common.utils.functional

/**
 * An [[StatelessIterator]] is just an [[Iterator]] that
 * has no need to track its state for determining if
 * it has next values or not.
 */
trait StatelessIterator[T] extends Iterator[Option[T]]

object StatelessIterator {

  /**
   * It creates a new [[StatelessIterator]] of [[T]]s
   * @param nextFunc Lambda that represents the
   *                 retrieval of the next element in iterator.
   * @tparam T Contained element type.
   * @return A stateless iterator.
   */
  def apply[T](
    hasNextFunc: => Boolean)(
    nextFunc: => T): StatelessIterator[T] = {

    new StatelessIterator[T] {

      override def hasNext: Boolean =
        hasNextFunc

      override def next(): Option[T] =
        if (hasNext) Option(nextFunc)
        else None

    }

  }

  /**
   * It creates a new [[StatelessIterator]] of [[T]]s
   * @param nextFunc Lambda that represents the
   *                 retrieval of the next element in iterator.
   *                 Returning a None represents the end of the
   *                 Iterator.
   * @tparam T Contained element type.
   * @return A stateless iterator.
   */
  def apply[T](nextFunc: => Option[T]): StatelessIterator[T] = {

    new StatelessIterator[T] { self =>

      private var last: Option[T] = nextFunc

      override def next(): Option[T] = self.synchronized{
        val result = last
        last = nextFunc
        result
      }

      override def hasNext: Boolean = self.synchronized(last.isDefined)

    }

  }

}
