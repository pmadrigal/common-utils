
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
package com.stratio.common.utils.components.transaction_manager

import com.stratio.common.utils.components.repository.RepositoryComponent
import TransactionResource.WholeRepository

trait TransactionManagerComponent[K,V] {

  self: RepositoryComponent[K, V] =>

  trait TransactionalRepository extends Repository {

    /**
      * Code execution exclusion zone over a given set of cluster-wide resources.
      *
      * @param entity Entity prefix used for the content repository providing synchronization mechanisms
      * @param firstResource First resource in the protected resource set
      * @param resources Remaining resources in the protected resource set
      * @param block Code to execute in the exclusion area over the resources
      * @tparam T Return type of the exclusion area block
      * @return Exclusion area result after its executions
      */
    def atomically[T](entity: String,
      firstResource: TransactionResource,
      resources: TransactionResource*
    )(block: => T): T

    /**
      * Code execution exclusion zone over a given repository content. The exlucsion area will be
      * protected over all the resources managed by the `entity`, that is, the entity itself
      *
      * @param entity Entity prefix used for the content repository providing synchronization mechanisms
      * @param block Code to execute in the exclusion area over the resources
      * @tparam T Return type of the exclusion area block
      * @return Exclusion area result after its executions
      */
    final def atomically[T](entity: String)(block: => T): T = atomically[T](entity, WholeRepository)(block)
  }

}