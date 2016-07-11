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

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._
import scala.util.{ Try, Failure => TryFailure }
import scalaz._
import Scalaz._

@RunWith(classOf[JUnitRunner])
class MonadTransformerDSLTest extends WordSpec
with Matchers {

  import MonadTransformerDSL._
  import TryFunctorConversionUtils._

  val exception = new Exception("Error")

  "Monad Tranformer DSL" when {

    "works with Try of List" should {

      "extract the values of the list" in {

        val tryListAB = Try(List("a", "b"))
        val tryListA = Try(List("a"))
        val tryListB = Try(List("b"))
        val tryEmptyList = Try(List.empty[String])
        val tryFailure: Try[List[String]] = Try(throw exception)

        val result1: Try[List[(String, String)]] =
          (
            for {
              x <- get values tryListAB
            } yield (x, x)
          ).run

        result1 should be(Try(List(("a", "a"), ("b", "b"))))

        val result2: Try[List[(String, String)]] =
          (
            for {
              x <- get values tryListA
              y <- get values tryListB
            } yield (x, y)
          ).run

        result2 should be(Try(List(("a", "b"))))

        val result3: Try[List[(String, String)]] =
          (
            for {
              x <- get values tryListA
              y <- get values List("b")
            } yield (x, y)
          ).run

        result3 should be(Try(List(("a", "b"))))

        val result4: Try[List[(String, String)]] =
          (
            for {
              x <- get values tryListA
              y <- get values "b"
            } yield (x, y)
          ).run

        result4 should be(Try(List(("a", "b"))))

        val result5: Try[List[(String, String)]] =
          (
            for {
              x <- get values tryEmptyList
            } yield (x, x)
          ).run

        result5 should be(Try(List.empty[String]))

        val result6: Try[List[(String, String)]] =
          (
            for {
              x <- get values tryFailure
            } yield (x, x)
          ).run

        result6 should be(TryFailure(exception))
      }
    }

    "works with Try of Option" should {

      "extract the values of the option" in {

        val tryOptionA = Try(Option("a"))
        val tryOptionB = Try(Option("b"))
        val tryEmptyOption = Try(Option.empty[String])
        val tryFailure: Try[Option[String]] = Try(throw exception)

        val result1: Try[Option[(String, String)]] =
          (
            for {
              x <- get value tryOptionA
            } yield (x, x)
          ).run

        result1 should be(Try(Option(("a", "a"))))

        val result2: Try[Option[(String, String)]] =
          (
            for {
              x <- get value tryOptionA
              y <- get value tryOptionB
            } yield (x, y)
          ).run

        result2 should be(Try(Option(("a", "b"))))

        val result3: Try[Option[(String, String)]] =
          (
            for {
              x <- get value tryOptionA
              y <- get value Option("b")
            } yield (x, y)
          ).run

        result3 should be(Try(Option(("a", "b"))))

        val result4: Try[Option[(String, String)]] =
          (
            for {
              x <- get value tryOptionA
              y <- get value "b"
            } yield (x, y)
          ).run

        result4 should be(Try(Option(("a", "b"))))

        val result5: Try[Option[(String, String)]] =
          (
            for {
              x <- get value tryEmptyOption
            } yield (x, x)
          ).run

        result5 should be(Try(None))

        val result6: Try[Option[(String, String)]] =
          (
            for {
              x <- get value tryFailure
            } yield (x, x)
          ).run

        result6 should be(TryFailure(exception))
      }

      "throw a new exception if the Option is None" in {

        val tryOptionA = Try(Option("a"))
        val tryEmptyOption = Try(Option.empty[String])

        val result: Try[Option[(String, String)]] =
          (for {
            x <- get value tryOptionA orThrow exception
          } yield (x, x)).run

        result should be(Try(Option(("a", "a"))))

        intercept[Exception] {
          (for {
            x <- get value tryEmptyOption orThrow exception
          } yield (x, x)).run
        }

      }

      "remove the option with the flatten method" in {

        val tryOptionA = Try(Option("a"))
        val tryEmptyOption = Try(Option.empty[String])

        val result: Try[(String, String)] =
          (for {
            x <- get value tryOptionA
          } yield (x, x)).run.flatten

        result should be(Try(("a", "a")))

        intercept[RuntimeException] {
          (for {
            x <- get value tryEmptyOption
          } yield (x, x)).run.flatten
        }

      }

      "remove the option with an alternative with the flatten method" in {

        val tryOptionA = Try(Option("a"))
        val tryEmptyOption = Try(Option.empty[String])

        val result1: Try[(String, String)] =
          (for {
            x <- get value tryOptionA
          } yield (x, x)).run.flattenOr(new Exception("Error"))

        result1 should be(Try(("a", "a")))

        intercept[Exception] {
          (for {
            x <- get value tryEmptyOption
          } yield (x, x)).run.flattenOr(new Exception("Error"))
        }

        val result2: Try[(String, String)] =
          (for {
            x <- get value tryOptionA
          } yield (x, x)).run.flattenOr(("b", "b"))

        result2 should be(Try(("a", "a")))

        val result3: Try[(String, String)] =
          (for {
            x <- get value tryEmptyOption
          } yield (x, x)).run.flattenOr(("b", "b"))

        result3 should be(Try(("b", "b")))

      }
    }

    "uses implicits conversions" should {

      "transform from Try to TryFunctor" in {

        fromTryToTryFunctor(Try(Option("a"))) should be(Right(Option("a")))
        fromTryToTryFunctor(Try(throw exception)) should be(Left(exception))

      }

      "transform from TryFunctor to Try" in {

        fromTryFunctorToTry(Right(Option("a"))) should be(Try(Option("a")))
        fromTryFunctorToTry(Left(exception)) should be(Try(throw exception))
        
      }
    }
  }
}
