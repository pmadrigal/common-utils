
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
package com.stratio.common.utils.components.transaction_manager.impl

import com.stratio.common.utils.components.config.ConfigComponent
import com.stratio.common.utils.components.logger.LoggerComponent
import com.stratio.common.utils.components.repository.impl.ZookeeperRepositoryComponent
import com.stratio.common.utils.components.transaction_manager.{TransactionManagerComponent, TransactionResource}
import org.apache.curator.framework.recipes.locks.InterProcessMutex

trait ZookeeperRepositoryWithTransactionsComponent extends ZookeeperRepositoryComponent
  with TransactionManagerComponent[String, Array[Byte]] {

  self: ConfigComponent with LoggerComponent =>

  override val repository: ZookeeperRepositoryWithTransactions = new ZookeeperRepositoryWithTransactions(None)

  //TODO Improve paths and locksPath behaviour
  class ZookeeperRepositoryWithTransactions(path: Option[String] = None) extends ZookeeperRepository(path)
    with TransactionalRepository {

    //TODO: Improve path option usage
    private def acquisitionResource: String = "/locks" + path.map("/" + _).getOrElse("")

    private object AcquiredLocks {

      import collection.mutable.Map

      private val acquisitionLock: InterProcessMutex = new InterProcessMutex(curatorClient, acquisitionResource)

      private val path2lock: Map[String, InterProcessMutex] = Map.empty

      def acquireResources(paths: Seq[String]): Unit = {
        acquisitionLock.acquire()
        path2lock.synchronized {
          paths foreach { path =>
            val lock = path2lock.get(path) getOrElse {
              val newLock = new InterProcessMutex(curatorClient, path)
              path2lock += (path -> newLock)
              newLock
            }
            lock.acquire()
          }
        }
        acquisitionLock.release()
      }

      def freeResources(paths: Seq[String]): Unit = path2lock.synchronized {
        for {
          path <- paths
          lock <- path2lock.get(path)
        } {
          lock.release()
          if(!lock.isAcquiredInThisProcess)
            path2lock -= path
        }
      }

    }

    private def lockPath(entity: String)(resource: TransactionResource): String = {
      s"/locks/$entity/${resource.id}"
    }

    override def atomically[T](
                                entity: String,
                                firstResource: TransactionResource,
                                resources: TransactionResource*)(block: => T): T = {

      val paths = (firstResource +: resources).map(lockPath(entity))
      AcquiredLocks.acquireResources(paths)
      val res = try {
        block
      } finally {
        AcquiredLocks.freeResources(paths)
      }
      res
    }

  }

}
