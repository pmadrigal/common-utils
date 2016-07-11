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

import org.scalatest.{Matchers, FlatSpec}

class StatefulIteratorTest extends FlatSpec
  with Matchers
  with StatefulIteratorTestInitialization {

  behavior of "StatefulIterator"

  /*
   * Define a new StatefulIterator of Strings (where the state is an int).
   * The initial state is zero and the 'hasNext' condition is the state
   * having having a value lower than 10. The 'next' state-mutating function
   * is defined as incrementing by one the state and returning an external
   * seq value at the current index.
   */
  def myIterator1(): StatefulIterator[String,Int] =
    iterator(
      0,
      _ < 10,
      state => (
        state + 1,
        strings.zipWithIndex.find{ case (s,idx) => idx == state }.map(_._1).get))

  def myIterator2(): StatefulIterator[String,Int] =
    iterator(0, state =>
      strings.zipWithIndex.find{ case (s,idx) => idx == state && state < 10}.map{
        case (s,_) => (state + 1, s)
      })

  it should "allow defining a new stateful iterator of Option[T]" in {


    val ite1: StatefulIterator[String,Int] = myIterator1()

    val ite2: StatefulIterator[String,Int] = myIterator2()

  }

  it should "allow assigning the StatefulIterator[T,S] into a Iterator[Option[T]]" in {

    val ite1: StatefulIterator[String,Int] = myIterator1()

    val ite2: StatefulIterator[String,Int] = myIterator2()

    /*
     * The StatefulIterator is actually an Iterator[Option[T]]
     */
    val ite3: Iterator[Option[String]] = ite1
    val ite4: Iterator[Option[String]] = ite2

  }

  it should "mutate internal state of the iterator when iterating over it" in {

    val ite1: StatefulIterator[String,Int] = myIterator1()

    val ite2: StatefulIterator[String,Int] = myIterator2()

    def checkIterator(ite: StatefulIterator[String,Int]): Unit = {
      val chains: IndexedSeq[Any] = (1 to 2).map { _ =>
        if (ite.hasNext) ite.next()
      }
      ite.state shouldEqual 2
      chains.head shouldEqual Option(strings.head)
      chains(1) shouldEqual Option(strings(1))
      chains.isDefinedAt(2) shouldEqual false
    }

    checkIterator(ite1)
    checkIterator(ite2)

  }

  it should "iterate until state doesn't allows it" in {

    val stopCondition = 10

    val ite1: StatefulIterator[String,Int] = myIterator1()

    val ite2: StatefulIterator[String,Int] = myIterator2()

    def checkIterator(ite: StatefulIterator[String,Int]): Unit = {
      ite.toList shouldEqual strings.take(stopCondition).map(Option.apply).toList
      ite.hasNext shouldEqual false
      ite.next() shouldEqual None
    }

    checkIterator(ite1)
    checkIterator(ite2)

  }

}

trait StatefulIteratorTestInitialization {

  val stringLength = 5

  val strings: IndexedSeq[String] =
    (1 to 20).map(_ => Random.nextString(stringLength))

}
