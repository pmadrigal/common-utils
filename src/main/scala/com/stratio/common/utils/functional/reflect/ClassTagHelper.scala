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
package com.stratio.common.utils.functional.reflect

import scala.reflect.ClassTag

/**
 * It provides full support for operating with [[ClassTag]] type
 * runtime-based operations, like:
 *
 * {{{
 *    class Kid
 *    class Daddy extends Kid
 *    class Grandpa extends Daddy
 *
 *    classTag[Kid].isA[Kid] should equal(true)
 *    classTag[Kid].isA[Daddy] should equal(false)
 *    classTag[Daddy].isA[Grandpa] should equal(false)
 *    classTag[Grandpa].isA[Kid] should equal(true)
 *
 * }}}
 */
private[functional] class ClassTagHelper[T](ctag: ClassTag[T]){

  /**
   * Determine (in runtime) whether [[T]] is a subtype of [[U]]
   */
  def isA[U](implicit ev: <:<[T,U] = None.orNull): Boolean =
    Option(ev).isDefined

  /**
   * Determine (in runtime) whether [[T]] is exactly [[U]]
   */
  def isExactlyA[U](implicit ev: =:=[T,U] = None.orNull): Boolean =
    Option(ev).isDefined

}
