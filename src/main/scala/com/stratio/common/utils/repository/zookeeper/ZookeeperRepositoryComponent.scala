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

package com.stratio.common.utils.repository.zookeeper

import java.util.NoSuchElementException
import java.util.concurrent.ConcurrentHashMap

import com.stratio.common.utils.config.ConfigComponent
import com.stratio.common.utils.logger.LoggerComponent
import com.stratio.common.utils.repository.RepositoryComponent
import org.apache.curator.framework.recipes.cache.{NodeCache, NodeCacheListener}
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.utils.CloseableUtils
import org.json4s.Formats
import org.json4s.jackson.Serialization.read

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

import com.stratio.common.utils.repository.zookeeper.ZookeeperConstants._

trait ZookeeperRepositoryComponent extends RepositoryComponent[String, Array[Byte]] {
  self: ConfigComponent with LoggerComponent =>

  val repository = new ZookeeperRepository {}

  trait ZookeeperRepository extends Repository {

    private def curatorClient: CuratorFramework =
      ZookeeperRepository.getInstance(getZookeeperConfig)

    def get(entity: String, id: String): Option[Array[Byte]] =
      Option(
        curatorClient
          .getData
          .forPath(s"/$entity/$id")
      )

    def getAll(entity: String): List[Array[Byte]] =
      curatorClient
        .getChildren
        .forPath(s"/$entity").map(get(entity, _).get).toList

    def count(entity: String): Long =
      curatorClient
        .getChildren
        .forPath(s"/$entity").size.toLong

    def exists(entity: String, id: String): Boolean =
      Option(curatorClient
        .checkExists()
        .forPath(s"/$entity/$id")
      ).isDefined

    def create(entity: String, id: String, element: Array[Byte]): Array[Byte] = {
      curatorClient
        .create()
        .creatingParentsIfNeeded()
        .forPath(s"/$entity/$id", element)

      get(entity, id)
        .orElse(throw new NoSuchElementException(s"Something were wrong when retrieving element $id after create"))
        .get
    }

    def update(entity: String, id: String, element: Array[Byte]): Unit =
      curatorClient
        .setData()
        .forPath(s"/$entity/$id", element)


    def delete(entity: String, id: String): Unit =
      curatorClient
        .delete()
        .forPath(s"/$entity/$id")

    def getZookeeperConfig: Config =
      config.getConfig(ConfigZookeeper)
        .getOrElse(throw new ZookeeperRepositoryException("Zookeeper config not found"))

    def getConfig: Map[String, Any] =
      getZookeeperConfig.toMap

    def start: Boolean =
      Try(
        curatorClient.start()
      ).isSuccess

    def stop: Boolean = ZookeeperRepository.stop(config)

    def addListener[T <: Serializable](id: String, callback: (T, NodeCache) => Unit)
                                      (implicit jsonFormat: Formats, ev: Manifest[T]): Unit = {

      val nodeCache: NodeCache = new NodeCache(curatorClient, id)

      nodeCache.getListenable.addListener(
        new NodeCacheListener {
          override def nodeChanged(): Unit =
            Try(new String(nodeCache.getCurrentData.getData)) match {
              case Success(value) => callback(read[T](value), nodeCache)
              case Failure(e) => logger.error(s"NodeCache value: ${nodeCache.getCurrentData}", e)
            }
        }
      )
      nodeCache.start()
    }
  }

  private[this] object ZookeeperRepository {

    def getInstance(config: Config): CuratorFramework = synchronized {
      val connectionString = config.getString(ZookeeperConnection, DefaultZookeeperConnection)
      CuratorFactoryMap.curatorFrameworks.getOrElse(connectionString, {
        Try {
          CuratorFrameworkFactory.builder()
            .connectString(connectionString)
            .connectionTimeoutMs(config.getInt(ZookeeperConnectionTimeout, DefaultZookeeperConnectionTimeout))
            .sessionTimeoutMs(config.getInt(ZookeeperSessionTimeout, DefaultZookeeperSessionTimeout))
            .retryPolicy(
              new ExponentialBackoffRetry(
                config.getInt(ZookeeperRetryInterval, DefaultZookeeperRetryInterval),
                config.getInt(ZookeeperRetryAttemps, DefaultZookeeperRetryAttemps)))
            .build()
        } match {
          case Success(client: CuratorFramework) =>
            client.start
            CuratorFactoryMap.curatorFrameworks.putIfAbsent(connectionString, client)
            client
          case Failure(_: Throwable) =>
            throw ZookeeperRepositoryException("Error trying to create a new Zookeeper instance")
        }
      })
    }

    def stopAll: Boolean =
      synchronized {
        Try {
          CuratorFactoryMap.curatorFrameworks.foreach { case (key, curator) => CloseableUtils.closeQuietly(curator) }
        }.isSuccess
      }

    def stop(config: Config): Boolean = {
      synchronized {
        val connectionString = config.getString(ZookeeperConnection, DefaultZookeeperConnection)
        Try {
          CloseableUtils.closeQuietly(CuratorFactoryMap.curatorFrameworks.get(connectionString).get)
        }.isSuccess
      }
    }
  }

}

private[this] object CuratorFactoryMap {

  val curatorFrameworks: scala.collection.concurrent.Map[String, CuratorFramework] =
    new ConcurrentHashMap[String, CuratorFramework]()
}