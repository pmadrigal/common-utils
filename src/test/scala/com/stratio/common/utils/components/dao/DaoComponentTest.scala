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
package com.stratio.common.utils.components.dao

import com.stratio.common.utils.components.repository.DummyRepositoryComponent
import org.json4s.DefaultFormats
import org.scalatest.{Matchers, WordSpec}

class DaoComponentTest extends WordSpec with Matchers {

  trait DummyDAOComponentContext extends DummyDAOComponent {
    override val dao : DAO = new GenericDAO(Option("dummy"))
  }

  "A dao component" when {
    "get a value" should {

      "return an option with the value if the value exists" in new DummyDAOComponentContext {
        dao.get("key1") should be(Some(DummyModel("value1")))
        dao.count()
      }
      "return None if the value doesn't exist" in new DummyDAOComponentContext {
        dao.get("keyNotFound") should be(None)
      }
    }

    "check if a value exists" should {

      "return true if the value exists" in new DummyDAOComponentContext {
        dao.exists("key1") should be(true)
      }

      "return false if the value doesn't exist" in new DummyDAOComponentContext {
        dao.exists("keyNotFound") should be(false)
      }
    }

    "create a new value" should {

      "return Model if the operation is successful" in new DummyDAOComponentContext {
        dao.get("keyNotFound") should be(None)
        dao.create("keyNotFound", DummyModel("newValue")) should be(DummyModel("newValue"))
        dao.get("keyNotFound") should be(Some(DummyModel("newValue")))
      }

      "return false if the operation is not successful" in new DummyDAOComponentContext {
        dao.create("key1", new DummyModel("newValue")) should be(DummyModel("newValue"))
        dao.getAll()
      }
    }

    "remove a value" should {

      "return true if the operation is successful" in new DummyDAOComponentContext {
        dao.get("key1") should be(Some(DummyModel("value1")))
        dao.delete("key1")
        dao.get("key1") should be(None)
      }

      "return false if the operation is not successful" in new DummyDAOComponentContext {
        dao.delete("keyNotFound")
        dao.getAll().size should be(3)
      }
    }

    "update a value" should {

      "return true if the operation is successful" in new DummyDAOComponentContext {
        dao.get("key1") should be(Some(DummyModel("value1")))
        dao.update("key1", DummyModel("newValue"))
        dao.get("key1") should be(Some(DummyModel("newValue")))
      }

      "return false if the operation is not successful" in new DummyDAOComponentContext {
        dao.update("keyNotFound", DummyModel("newValue"))
        dao.getAll().size should be(3)
      }
    }

    "upsert a value" should {

      "return true if the operation is successful" in new DummyDAOComponentContext {
        dao.upsert("key1", DummyModel("newValue"))
        dao.get("key1") should be(Some(DummyModel("newValue")))
        dao.upsert("key1", DummyModel("newValue2"))
        dao.get("key1") should be(Some(DummyModel("newValue2")))
        dao.get("key2") should be(None)
        dao.upsert("key2", DummyModel("newValue"))
        dao.get("key2") should be(Some(DummyModel("newValue")))
      }

      "return false if the operation is not successful" in new DummyDAOComponentContext {
        dao.update("keyNotFound", DummyModel("newValue"))
        dao.getAll().size should be(3)
      }
    }

    "getall" should {

      "return a list with all the data in the table" in new DummyDAOComponentContext {
        dao.getAll() should be(List(DummyModel("value1"), DummyModel("value2"), DummyModel("value3")))
      }
    }
  }
}

trait DummyDAOComponent extends DAOComponent[String, String, DummyModel] with DummyRepositoryComponent {

  val dao: DAO = new GenericDAO()

  class GenericDAO(key: Option[String] = None) extends DAO {

    implicit val formats = DefaultFormats

    def entity: String = {
      if(key.isEmpty || key.get.trim.isEmpty) throw new IllegalStateException("EntityName in the DAO must be defined")
      else key.get
    }

    override def fromVtoM[TM >: DummyModel <: DummyModel : Manifest](v: String): TM = new DummyModel(v)

    override def fromMtoV[TM <: DummyModel : Manifest](m: TM): String = m.property
  }
}

case class DummyModel(property: String)