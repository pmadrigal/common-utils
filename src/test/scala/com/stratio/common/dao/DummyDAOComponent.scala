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

package com.stratio.common.dao

import com.stratio.common.utils.repository.DummyRepositoryComponent

trait DummyDAOComponent extends DAOComponent[String, String, Model] with DummyRepositoryComponent {

  val dao: DAO = new DummyDAO {}

  trait DummyDAO extends DAO {

    def fromVtoM(v: String): Model = Model(v)

    def fromMtoV(m: Model): String = m.property

    //scalastyle:off
    def entity= "dummy"
    //scalastyle:on
  }
}

case class Model(property: String)