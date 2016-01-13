
package com.stratio.common.dao

import org.scalatest.{Matchers, WordSpec}

class DaoComponentTest extends WordSpec with Matchers {

  trait DummyDAOComponentContext extends DummyDAOComponent

  "A dao component" when {
    "get a value" should {

      "return an option with the value if the value exists" in new DummyDAOComponentContext {
        dao.get("key1") should be(Some(Model("value1")))
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
        dao.create("keyNotFound", new Model("newValue")) should be(Model("newValue"))
        dao.get("keyNotFound") should be(Some(Model("newValue")))
      }

      "return false if the operation is not successful" in new DummyDAOComponentContext {
        dao.create("key1", new Model("newValue")) should be(Model("newValue"))
        dao.getAll()
      }
    }

    "remove a value" should {

      "return true if the operation is successful" in new DummyDAOComponentContext {
        dao.get("key1") should be(Some(Model("value1")))
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
        dao.get("key1") should be(Some(Model("value1")))
        dao.update("key1", Model("newValue"))
        dao.get("key1") should be(Some(Model("newValue")))
      }

      "return false if the operation is not successful" in new DummyDAOComponentContext {
        dao.update("keyNotFound", Model("newValue"))
        dao.getAll().size should be(3)
      }
    }

    "getall" should {

      "return a list with all the data in the table" in new DummyDAOComponentContext {
        dao.getAll() should be(List(Model("value1"), Model("value2"), Model("value3")))
      }
    }
  }
}



