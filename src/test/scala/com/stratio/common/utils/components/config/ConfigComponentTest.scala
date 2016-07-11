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
package com.stratio.common.utils.components.config

import com.stratio.common.utils.components.config.impl.MapConfigComponent
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ConfigComponentTest extends WordSpec
  with Matchers
  with DummyConfigComponent {

  val propertyNotFound = "-1"

  "A config component" when {
    "extract a String property" should {

      "return an option with the value if the value can be casted to String" in {
        config.getString("stringProperty") should be(Some("valueA"))
      }

      "return None if the value can't be casted to String" in {
        config.getString("nullProperty") should be(None)
      }

      "return None if the property doesn't exist" in {
        config.getString(propertyNotFound) should be(None)
      }
    }

    "extract a String property with a default value" should {

      val defaultStringProperty = "default"

      "return an option with the value if the value can be casted to String" in {
        config.getString("stringProperty", defaultStringProperty) should be("valueA")
      }

      "return the default value if the value can't be casted to String" in {
        config.getString("nullProperty", defaultStringProperty) should be(defaultStringProperty)
      }

      "return the default value if the property doesn't exist" in {
        config.getString(propertyNotFound, defaultStringProperty) should be(defaultStringProperty)
      }
    }

    "extract an Int property" should {

      "return an option with the value if the value can be casted to Int" in {
        config.getInt("intProperty") should be(Some(2))
      }

      "return None if the value can't be casted to Int" in {
        config.getInt("stringProperty") should be(None)
      }

      "return None if the property doesn't exist" in {
        config.getInt(propertyNotFound) should be(None)
      }
    }

    "extract an Int property with a default value" should {

      val defaultIntProperty = 0

      "return an option with the value if the value can be casted to Int" in {
        config.getInt("intProperty", defaultIntProperty) should be(2)
      }

      "return the default value if the value can't be casted to Int" in {
        config.getInt("stringProperty", defaultIntProperty) should be(defaultIntProperty)
      }

      "return the default value if the property doesn't exist" in {
        config.getInt(propertyNotFound, defaultIntProperty) should be(defaultIntProperty)
      }
    }

    "extract a List of String" should {

      "return an option with the value if the value can be casted to List[Int]" in {
        config.getStringList("listProperty") should be(List("valueB1", "valueB2"))
      }

      "return empty list if the value can't be casted to List[Int]" in {
        config.getStringList("stringProperty") should be(List.empty[String])
      }

      "return empty list if the property doesn't exist" in {
        config.getStringList(propertyNotFound) should be(List.empty[String])
      }
    }

    "extract a List of String with default value" should {

      val defaultListValue = List("default1", "default2")

      "return an option with the value if the value can be casted to List[Int]" in {
        config.getStringList("listProperty", defaultListValue) should be(List("valueB1", "valueB2"))
      }

      "return empty list if the value can't be casted to List[Int]" in {
        config.getStringList("stringProperty", defaultListValue) should be(defaultListValue)
      }

      "return empty list if the property doesn't exist" in {
        config.getStringList(propertyNotFound, defaultListValue) should be(defaultListValue)
      }
    }

    "extract a new Config" should {

      "return an new Config if the key exists" in {
        val newConfig = config.getConfig("mapProperty")
        newConfig should not be empty
      }

      "return None if the key doesn't exist" in {
        config.getConfig("stringProperty") should be(None)
      }
    }

    "generate a Map of properties" should {

      "return a map with the properties" in {
        //scalastyle:off
        config.toMap.size should be(5)
        //scalastyle:on
      }

    }
  }
}

trait DummyConfigComponent extends MapConfigComponent {

  override val memoryMap: Map[String, Any] = Map(
    "stringProperty" -> "valueA",
    "listProperty" -> "[valueB1,valueB2]",
    "mapProperty" -> Map("propC1" -> "valueC1"),
    "intProperty" -> "2",
    "nullProperty" -> None.orNull
  )
}
