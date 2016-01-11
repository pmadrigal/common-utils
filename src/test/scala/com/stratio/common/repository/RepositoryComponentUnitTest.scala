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

@RunWith(classOf[JUnitRunner])
class RepositoryComponentUnitTest extends WordSpec
  with Matchers {

  trait DummyRepositoryContext extends DummyRepositoryComponent

  val keyNotFound = "-1"

  "A repository component" when {
    "get a value" should {

      "return an option with the value if the value exists" in new DummyRepositoryContext {
        repository.get("key1") should be(Some("value1"))
      }

      "return None if the value doesn't exist" in new DummyRepositoryContext {
        repository.get(keyNotFound) should be(None)
      }
    }

    "check if a value exists" should {

      "return true if the value exists" in new DummyRepositoryContext {
        repository.exists("key1") should be(true)
      }

      "return false if the value doesn't exist" in new DummyRepositoryContext {
        repository.exists(keyNotFound) should be(false)
      }
    }

    "create a new value" should {

      "return true if the operation is successful" in new DummyRepositoryContext {
        repository.get(keyNotFound) should be(None)
        repository.create(keyNotFound, "newValue") should be("newValue")
        repository.get(keyNotFound) should be(Some("newValue"))
      }

      "return false if the operation is not successful" in new DummyRepositoryContext {
        repository.create("key1", "newValue") should be("newValue")
        repository.getConfig.size should be(3)
      }
    }

    "remove a value" should {

      "return true if the operation is successful" in new DummyRepositoryContext {
        repository.get("key1") should be(Some("value1"))
        repository.delete("key1")
        repository.get("key1") should be(None)
      }

      "return false if the operation is not successful" in new DummyRepositoryContext {
        repository.delete(keyNotFound)
        repository.getConfig.size should be(3)
      }
    }

    "update a value" should {

      "return true if the operation is successful" in new DummyRepositoryContext {
        repository.get("key1") should be(Some("value1"))
        repository.update("key1", "newValue")
        repository.get("key1") should be(Some("newValue"))
      }

      "return false if the operation is not successful" in new DummyRepositoryContext {
        repository.update(keyNotFound, "newValue")
        repository.getConfig.size should be(3)
      }
    }

    "get a subrepository" should {

      "return a list with the keys of the sub repository if the operation is successful" in new DummyRepositoryContext {
        repository.getChildren("key3") should be(List("key31"))
      }

      "return and empty list if the operation is not successful" in new DummyRepositoryContext {
        repository.getChildren(keyNotFound) should be(List.empty[String])
      }
    }
  }
}
