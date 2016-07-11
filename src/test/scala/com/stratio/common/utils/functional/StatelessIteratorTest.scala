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

import scala.util.Random

import org.scalatest.{FlatSpec, Matchers}

class StatelessIteratorTest extends FlatSpec
  with Matchers
  with StatelessIteratorTestInitialization {

  behavior of "StatelessIterator"

  def myIterator1(): WithDataBaseAndExternalState =
    new WithDataBaseAndExternalState {
      /*
       * Define a new StatelessIterator of Strings.
       * HasNext function is provided by some external state.
       * The 'next' function is defined as invoking an external
       * 'database' method.
       */
      val ite: StatelessIterator[String] = iterator(state < 10,{
        state += 1
        database.read()
      })
    }

  def myIterator2(): WithDataBaseAndExternalState =
    new WithDataBaseAndExternalState {
      val ite: StatelessIterator[String] = iterator {
        val oldState = state
        state += 1
        Option(database.read()).find(_ => oldState < 10)
      }
    }

  it should "allow defining a new stateless iterator of Option[T]" in {

    val ite1 = myIterator1()

    val ite2 = myIterator2()

  }

  it should "allow assigning the StatelessIterator[T] into a Iterator[Option[T]]" in {

    val ite1 = myIterator1()

    val ite2 = myIterator2()

    val ite3: Iterator[Option[String]] = ite1.ite

    val ite4: Iterator[Option[String]] = ite2.ite
  }

  it should "iterate until external call-by-name expression doesn't allows it" in {

    def checkIterator(
      obj: WithDataBaseAndExternalState): Unit = {
      obj.ite.toList.size shouldEqual 10
    }

    checkIterator(myIterator1())

    checkIterator(myIterator2())

  }

  it should "iterating over an ended iterator should return None elements" in {

    def checkIterator(
      obj: WithDataBaseAndExternalState): Unit = {
      val collected = obj.ite.toList
      collected.size shouldBe 10
      collected.distinct.size shouldBe 10
      obj.ite.toList.size shouldBe 0
      obj.ite.hasNext shouldEqual false
      obj.ite.next() shouldEqual None
    }

    checkIterator(myIterator1())

    checkIterator(myIterator2())

  }

}

trait StatelessIteratorTestInitialization {

  /**
    * Dummy database.
   */
  class Database {
    private val length = 5
    def read(): String = Random.nextString(length)
  }

  /**
   * Some dummy object that owes some state
   * and has an instance of a dummy database.
   */
  abstract class WithDataBaseAndExternalState {
    var state = 0
    val database = new Database
    val ite: StatelessIterator[String]
  }

}
