/**
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.common.config

import com.typesafe.config.{ ConfigFactory,  Config => TypesafeConfiguration }

import scala.util.Try
import scala.collection.JavaConversions._

trait TypesafeConfigComponent extends ConfigComponent {

  val config: Config = new TypesafeConfig()

  class TypesafeConfig(subPath: Option[String] = None) extends Config {

    val conf: TypesafeConfiguration =
      subPath.fold(
        ConfigFactory.load()
      ){path =>
        ConfigFactory.load.getConfig(path)
      }

    def getConfig(key: String): Option[Config] =
      Try {
        new TypesafeConfig(Option(key))
      }.toOption

    def getString(key: String): Option[String] =
      Try {
        conf.getString(key)
      }.toOption

    def getInt(key: String): Option[Int] =
      Try {
        conf.getInt(key)
      }.toOption

    def getStringList(key: String): List[String] =
      Try {
        conf.getStringList(key).toList
      }.getOrElse(List.empty[String])

    def toMap: Map[String, Any] = conf.root().toMap

  }
}
