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

trait ConfigComponent {

  val config: Config

  trait
  Config {

    def getConfig(key: String): Option[Config]

    def getConfigPath(key: String): Option[Config] = None

    def getString(key: String): Option[String]

    def getString(key: String, default: String): String =
      getString(key) getOrElse default

    def getInt(key: String): Option[Int]

    def getInt(key: String, default: Int): Int =
      getInt(key) getOrElse default

    def getStringList(key: String): List[String]

    def getStringList(key: String, default: List[String]): List[String] = {
      val list = getStringList(key)
      if (list.isEmpty) default else list
    }

    def toMap: Map[String, Any]

    def toStringMap: Map[String, String]
  }
}
