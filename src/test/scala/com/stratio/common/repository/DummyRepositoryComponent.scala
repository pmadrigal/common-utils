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

package com.stratio.common.repository

import scala.util.{Failure, Success, Try}

trait DummyRepositoryComponent extends RepositoryComponent[String, String] {

  val repository: Repository = new DummyRepository()

  var memoryMap: Map[String, Any] = Map(
    "key1" -> "value1",
    "key2" -> "value2",
    "key3" -> Map("key31" -> "value31")
  )

  class DummyRepository() extends Repository {

    def get(id: String): Try[Option[String]] =
      Try(memoryMap.get(id).map(_.toString))

    def getChildren(id: String): Try[List[String]] =
      Try(memoryMap.get(id)).map {
        case Some(map: Map[String, Any] @unchecked) => map.keys.toList
        case _ => List.empty[String]
      }

    def exists(id: String): Try[Boolean] =
      Try(memoryMap.contains(id))
    
    def create(id: String, element: String): Try[Boolean] = Try {
      if(exists(id) == Success(false)) {
        memoryMap = memoryMap + (id -> element)
        true
      } else false
    }

    def update(id: String, element: String): Try[Boolean] = Try {
      if(exists(id) == Success(true)) {
        memoryMap = memoryMap + (id -> element)
        true
      } else false
    }

    def delete(id: String): Try[Boolean] = Try {
      if(exists(id) == Success(true)) {
        memoryMap = memoryMap - id
        true
      } else false
    }

    def getConfig: Map[String, Any] = memoryMap

    def start: Boolean = true

    def stop: Boolean = false
    
    def getState: RepositoryState = Started

  }
}
