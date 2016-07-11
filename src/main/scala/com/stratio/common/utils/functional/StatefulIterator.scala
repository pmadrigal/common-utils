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
 * An [[StatefulIterator]] is just an [[Iterator]] that
 * needs to track some kind of state for determining if
 * it has next values or not.
 */
trait StatefulIterator[T,S] extends Iterator[Option[T]] {

  /**
   * Internal iterator state.
   */
  def state: S

}

object StatefulIterator {

  /**
   * It creates a new [[StatefulIterator]] of [[T]]s
   * @param initialState Initial internal state of the iterator.
   * @param nextFunc Lambda that represents the state transition and
   *                 retrieval of the next element in iterator.
   * @tparam T Contained element type.
   * @tparam S State type
   * @return An stateful iterator.
   */
  def apply[T,S](
    initialState: S,
    hasNextFunc: S => Boolean,
    nextFunc: S => (S,T)): StatefulIterator[T,S] = {

    new StatefulIterator[T,S] { self =>

      /**
       * It's necessary to synchronize '_state'
       * to make the iterator thread-safe.
       */
      private var _state: S = initialState

      def state: S = self.synchronized(_state)

      override def hasNext: Boolean =
        self.synchronized(hasNextFunc(_state))

      override def next(): Option[T] = self.synchronized{
        if (hasNext) {
          val (newState,t) = nextFunc(_state)
          _state = newState
          Option(t)
        }
        else None
      }

    }

  }

  /**
   * It creates a new [[StatefulIterator]] of [[T]]s
   * @param initialState Initial internal state of the iterator.
   * @param nextFunc Lambda that represents the state transition and
   *                 retrieval of the next element in iterator (if possible).
   * @tparam T Contained element type.
   * @tparam S State type
   * @return An stateful iterator.
   */
  def apply[T,S](
    initialState: S,
    nextFunc: S => Option[(S,T)]): StatefulIterator[T,S] = {

    new StatefulIterator[T,S] { self =>

      private var _state: S = initialState

      def state: S = self.synchronized(_state)

      private var last: Option[(S,T)] = nextFunc(initialState)

      override def next(): Option[T] = self.synchronized{
        val result = last.map{
          case (newState,t) =>
            _state = newState
            t
        }
        last = nextFunc(_state)
        result
      }

      override def hasNext: Boolean = self.synchronized(last.isDefined)

    }

  }

}
