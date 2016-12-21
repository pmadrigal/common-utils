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

import com.stratio.common.utils.components.repository.RepositoryComponent

import scala.util.Try

trait DAOComponent[K, V, M] {
  self: RepositoryComponent[K, V] =>

  val dao: DAO

  trait DAO {

    def get(id: K) (implicit manifest: Manifest[M]): Try[Option[M]] =
      repository.get(entity, id).map(_.map(entity => fromVtoM(entity)))

    def getAll() (implicit manifest: Manifest[M]): Try[Seq[M]] =
      repository.getAll(entity).map(_.map(fromVtoM))

    def count(): Try[Long] =
      repository.count(entity)

    def exists(id: K): Try[Boolean] =
      repository.exists(entity, id)

    def create(id: K, element: M) (implicit manifest: Manifest[M]): Try[M] =
      repository.create(entity, id, fromMtoV(element)).map(fromVtoM)

    def update(id: K, element: M) (implicit manifest: Manifest[M]): Try[Unit] =
      repository.update(entity, id, fromMtoV(element))

    def upsert(id: K, element: M) (implicit manifest: Manifest[M]): Try[M] =
      repository.upsert(entity, id, fromMtoV(element)).map(fromVtoM)

    def delete(id: K): Try[Unit] =
      repository.delete(entity, id)

    def deleteAll: Try[Unit] =
      repository.deleteAll(entity)

    def entity: String

    def fromVtoM[TM >: M <: M : Manifest](v: V): TM

    def fromMtoV[TM <: M : Manifest](m: TM): V
  }
}
