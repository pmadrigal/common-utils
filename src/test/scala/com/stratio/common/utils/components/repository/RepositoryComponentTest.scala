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
package com.stratio.common.utils.components.repository

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RepositoryComponentTest extends WordSpec
  with Matchers {

  trait DummyRepositoryContext extends DummyRepositoryComponent

  val keyNotFound = "-1"

  val Entity = "dummy"

  "A repository component" when {
    "get an element" should {

      "return an option with the value if the key exists" in new DummyRepositoryContext {
        repository.get(Entity, "key1") should be(Some("value1"))
      }

      "return None if the key doesn't exist" in new DummyRepositoryContext {
        repository.get(Entity, keyNotFound) should be(None)
      }
    }

    "getAll elements" should {
      "return all elements if the operation is successful" in new DummyRepositoryContext {
        repository.getAll(Entity) should be(List("value1", "value2", "value3"))
      }

      "return and empty list if the operation is not successful" in new DummyRepositoryContext {
        repository.getAll(keyNotFound) should be(List.empty[String])
      }
    }

    "getNodes elements" should {
      "return all nodes if the operation is successful" in new DummyRepositoryContext {
        repository.getNodes(Entity) should be(List("key1", "key2", "key3"))
      }

      "return and empty list if the operation is not successful" in new DummyRepositoryContext {
        repository.getNodes(keyNotFound) should be(List.empty[String])
      }
    }

    "check if a value exists" should {

      "return true if the value exists" in new DummyRepositoryContext {
        repository.exists(Entity, "key1") should be(true)
      }

      "return false if the value doesn't exist" in new DummyRepositoryContext {
        repository.exists(Entity, keyNotFound) should be(false)
      }
    }

    "create a new value" should {

      "return true if the operation is successful" in new DummyRepositoryContext {
        repository.get(Entity, keyNotFound) should be(None)
        repository.create(Entity, keyNotFound, "newValue") should be("newValue")
        repository.get(Entity, keyNotFound) should be(Some("newValue"))
      }

      "return false if the operation is not successful" in new DummyRepositoryContext {
        repository.create(Entity, "key1", "newValue") should be("newValue")
        repository.count(Entity) should be(3)
      }
    }

    "remove a value" should {

      "check that the element does not exist when it has been removed" in new DummyRepositoryContext {
        repository.get(Entity, "key1") should be(Some("value1"))
        repository.delete(Entity, "key1")
        repository.get(Entity, "key1") should be(None)
      }

      "do anything when a not existing element is removed" in new DummyRepositoryContext {
        repository.delete(Entity, keyNotFound)
        repository.count(Entity) should be(3)
      }
    }

    "remove all values" should {

      "check that the element does not exist when it has been removed all" in new DummyRepositoryContext {
        repository.get(Entity, "key1") should be(Some("value1"))
        repository.deleteAll(Entity)
        repository.get(Entity, "key1") should be(None)
      }

      "do anything when a not existing element is removed all" in new DummyRepositoryContext {
        repository.deleteAll(Entity)
        repository.count(Entity) should be(0)
      }
    }

    "update a value" should {

      "check that the element has been updated with the new value" in new DummyRepositoryContext {
        repository.get(Entity, "key1") should be(Some("value1"))
        repository.update(Entity, "key1", "newValue")
        repository.get(Entity, "key1") should be(Some("newValue"))
      }

      "do anything when a not existing element is updated" in new DummyRepositoryContext {
        repository.update(Entity, keyNotFound, "newValue")
        repository.count(Entity) should be(3)
      }
    }
  }
}
