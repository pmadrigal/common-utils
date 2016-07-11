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
package com.stratio.common.utils.components.config.impl

import com.stratio.common.utils.components.config.ConfigComponent

import scala.util.Try

trait MapConfigComponent extends ConfigComponent {

  val memoryMap: Map[String, Any]

  lazy val config: Config = new DummyConfig()

  class DummyConfig(subPath: Option[String] = None) extends Config {

    val conf: Map[String, Any] =
      subPath match {
        case Some(key) =>
          memoryMap.get(key) match {
            case Some(map: Map[String, Any]@unchecked) => map
            case _ => throw new Exception(s"Path $subPath not found.")
          }
        case None => memoryMap
      }

    def getConfig(key: String): Option[Config] =
      Try {
        new DummyConfig(Option(key))
      }.toOption

    def getString(key: String): Option[String] =
      conf.get(key).flatMap(v => Try(v.toString).toOption)

    def getInt(key: String): Option[Int] =
      conf.get(key).flatMap(v => Try(v.toString.toInt).toOption)

    private val listRegex = """\[(.*)\]""".r

    def getStringList(key: String): List[String] =
      getString(key).fold {
        List.empty[String]
      } {
        case listRegex(listValues) => listValues.split(",").toList
        case v => List.empty[String]
      }

    def toMap: Map[String, Any] = conf

    def toStringMap: Map[String, String] = conf.map(entry => (entry._1, entry._2.toString))
  }
}
