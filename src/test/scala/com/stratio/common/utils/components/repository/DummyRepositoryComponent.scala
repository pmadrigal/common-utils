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

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

trait DummyRepositoryComponent extends RepositoryComponent[String, String] {

  val repository: Repository = new DummyRepository()

  var memoryMap = mutable.Map(
    "dummy" ->
      mutable.Map(
        "key1" -> "value1",
        "key2" -> "value2",
        "key3" -> "value3"
      )
  )

  class DummyRepository() extends Repository {

    def get(entity: String, id: String): Try[Option[String]] =
      Try(memoryMap.get(entity).flatMap(_.get(id)))

    def getAll(entity: String): Try[Seq[String]] =
      Try(memoryMap.get(entity).map(_.values.toList.sortBy(identity)).getOrElse(Seq.empty))

    def getNodes(entity: String): Try[Seq[String]] =
      Try(memoryMap.get(entity).map(_.keys.toList.sortBy(identity)).getOrElse(Seq.empty))

    def count(entity: String): Try[Long] =
      Try(memoryMap.get(entity).map(_.size).getOrElse(0).toLong)


    def existsPath(entity: String): Try[Boolean] =
      Try(memoryMap.exists { case (key, _) =>
        key == entity
      })

    def exists(entity: String, id: String): Try[Boolean] =
      Try(memoryMap.exists { case (key, entityMap) =>
        key == entity && entityMap.keys.toList.contains(id)
      })

    def create(entity:String, id: String, element: String): Try[String] = {
      exists(entity, id).foreach {
        case false => memoryMap.put(entity, mutable.Map(id -> element))
        case true => ()
      }
      Success(element)
    }

    override def upsert(entity: String, id: String, element: String): Try[String] = {
      memoryMap.put(entity, mutable.Map(id -> element))
      Success(element)
    }

    def update(entity: String, id: String, element: String): Try[Unit] =
      exists(entity, id).map {
        case false => ()
        case true => memoryMap.put(entity, mutable.Map(id -> element))
      }

    def delete(entity:String, id: String): Try[Unit] = {
      exists(entity, id).map{
        case false => ()
        case true => memoryMap(entity).remove(id)
      }
    }

    def deleteAll(entity:String): Try[Unit] = Try(memoryMap.remove(entity))
  }
}
