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

import scala.util.Try

import scalaz._
import Scalaz._
import scalaz.ListT._
import scalaz.OptionT._

object MonadTransformerDSL {

  //Type definitions
  type TryFunctor[A] = Either[Throwable, A]
  type ListTryFunctorT[A] = ListT[TryFunctor, A]
  type OptionTryFunctorT[A] = OptionT[TryFunctor, A]

  /** Conversions between Try and TryFunctor (Either[Throwable, A]) */
  object TryFunctorConversionUtils {
    
    implicit def fromTryToTryFunctor[A](t: Try[A]): TryFunctor[A] = t match {
      case scala.util.Success(x) => Right(x)
      case scala.util.Failure(ex: Throwable) => Left(ex)
    }

    implicit def fromTryFunctorToTry[A](t: TryFunctor[A]): Try[A] = t match {
      case Left(ex: Throwable) => Try(throw ex)
      case Right(value) => Try(value)
    }
  }

  /** Utils to OptionT[TryFunctor[Option, A]] */
  implicit class RichOptionTryFunctorT[A](o: OptionTryFunctorT[A]) {
    def orThrow(ex: => Exception): OptionTryFunctorT[A] =
      optionT(o.run map (_ orElse (throw ex)))
  }

  /** Utils to TryFunctor[Option, A] */
  implicit class RichTryFunctorOption[A](t: TryFunctor[Option[A]]) {

    def flatten: TryFunctor[A] =
      flattenOr(new RuntimeException("Error trying to flatten a TryFunctor with None"))

    def flattenOr(ex: => Exception)(implicit d1: DummyImplicit): TryFunctor[A] =
      t map (_ getOrElse (throw ex))

    def flattenOr[B >: A](alternative: => B): TryFunctor[B] =
      t map (_ getOrElse alternative )

  }

  //scalastyle:off
  object get {

    // ListT Functions

    def values[A](v : A): ListTryFunctorT[A] = values(Try(List(v)))

    def values[A](v : List[A]): ListTryFunctorT[A] = values(Try(v))

    def values[A](v : Try[List[A]]): ListT[TryFunctor, A] =
      values(TryFunctorConversionUtils.fromTryToTryFunctor(v))

    def values[A](v : TryFunctor[List[A]]): ListT[TryFunctor, A] = listT(v)

    // OptionT Functions

    def value[A](v : A): OptionTryFunctorT[A] = value(Try(Option(v)))

    def value[A](v : Option[A]): OptionTryFunctorT[A] = value(Try(v))

    def value[A](v : Try[Option[A]]): OptionT[TryFunctor, A] =
      value(TryFunctorConversionUtils.fromTryToTryFunctor(v))

    def value[A](v : TryFunctor[Option[A]]): OptionT[TryFunctor, A] = optionT(v)
  }

}
