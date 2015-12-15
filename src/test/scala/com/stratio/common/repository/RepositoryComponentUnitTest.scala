/**
 * Copyright (C) 2014 Stratio (http://stratio.com)
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
package com.stratio.common.repository

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

import scala.util.Try

@RunWith(classOf[JUnitRunner])
class RepositoryComponentUnitTest extends WordSpec
  with Matchers {

  trait DummyRepositoryContext extends DummyRepositoryComponent

  val keyNotFound = "-1"

  "A repository component" when {
    "get a value" should {

      "return an option with the value if the value exists" in new DummyRepositoryContext {
        repository.get("key1") should be(Try(Some("value1")))
      }

      "return None if the value doesn't exist" in new DummyRepositoryContext {
        repository.get(keyNotFound) should be(Try(None))
      }
    }

    "check if a value exists" should {

      "return true if the value exists" in new DummyRepositoryContext {
        repository.exists("key1") should be(Try(true))
      }

      "return false if the value doesn't exist" in new DummyRepositoryContext {
        repository.exists(keyNotFound) should be(Try(false))
      }
    }

    "create a new value" should {

      "return true if the operation is successful" in new DummyRepositoryContext {
        repository.get(keyNotFound) should be(Try(None))
        repository.create(keyNotFound, "newValue") should be(Try(true))
        repository.get(keyNotFound) should be(Try(Some("newValue")))
      }

      "return false if the operation is not successful" in new DummyRepositoryContext {
        repository.create("key1", "newValue") should be(Try(false))
        repository.getConfig.size should be(3)
      }
    }

    "remove a value" should {

      "return true if the operation is successful" in new DummyRepositoryContext {
        repository.get("key1") should be(Try(Some("value1")))
        repository.delete("key1") should be(Try(true))
        repository.get("key1") should be(Try(None))
      }

      "return false if the operation is not successful" in new DummyRepositoryContext {
        repository.delete(keyNotFound) should be(Try(false))
        repository.getConfig.size should be(3)
      }
    }

    "update a value" should {

      "return true if the operation is successful" in new DummyRepositoryContext {
        repository.get("key1") should be(Try(Some("value1")))
        repository.update("key1", "newValue") should be(Try(true))
        repository.get("key1") should be(Try(Some("newValue")))
      }

      "return false if the operation is not successful" in new DummyRepositoryContext {
        repository.update(keyNotFound, "newValue") should be(Try(false))
        repository.getConfig.size should be(3)
      }
    }

    "get a subrepository" should {

      "return a list with the keys of the sub repository if the operation is successful" in new DummyRepositoryContext {
        repository.getSubRepository(keyNotFound) should be(Try(List.empty[String]))
      }

      "return and empty list if the operation is not successful" in new DummyRepositoryContext {
        repository.getSubRepository(keyNotFound) should be(Try(List.empty[String]))
      }
    }
  }
}
