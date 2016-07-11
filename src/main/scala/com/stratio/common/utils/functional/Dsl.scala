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

import scala.reflect.ClassTag

import com.stratio.common.utils.functional.reflect.ClassTagHelper

/**
 * Provides a higher layer of abstraction for using
 * extended functionality at 'functional' package.
 */
trait Dsl {

  def iterator[T,S](
    initialState: S,
    hasNextFunc: S => Boolean,
    nextFunc: S => (S,T)): StatefulIterator[T,S] =
    StatefulIterator.apply[T,S](initialState,hasNextFunc,nextFunc)

  def iterator[T,S](
    initialState: S,
    nextFunc: S => Option[(S,T)]): StatefulIterator[T,S] =
    StatefulIterator.apply[T,S](initialState,nextFunc)

  def iterator[T](
    hasNextFunc: => Boolean,
    nextFunc: => T): StatelessIterator[T] =
    StatelessIterator[T](hasNextFunc)(nextFunc)

  def iterator[T](
    nextFunc: => Option[T]): StatelessIterator[T] =
    StatelessIterator[T](nextFunc)

  implicit class classTagHelper[T:ClassTag](ct: ClassTag[T]) extends ClassTagHelper(ct)

  implicit class classHelper[T](_class: Class[T]) extends ClassTagHelper(ClassTag[T](_class))

}
