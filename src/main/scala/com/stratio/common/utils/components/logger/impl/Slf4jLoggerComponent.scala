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
package com.stratio.common.utils.components.logger.impl

import com.stratio.common.utils.components.logger.LoggerComponent
import org.slf4j.LoggerFactory

trait Slf4jLoggerComponent extends LoggerComponent {

  val logger: Logger = new Slf4jLogger(LoggerFactory.getLogger(getClass.getName))

  class Slf4jLogger(logger: org.slf4j.Logger) extends Logger {

    def name: String = logger.getName

    def debug(msg: String): Unit = logger.debug(msg)

    def error(msg: String): Unit = logger.error(msg)

    def error(msg: String, ex: Throwable): Unit = logger.error(msg, ex)

    def info(msg: String): Unit = logger.info(msg)

    def warn(msg: String): Unit = logger.warn(msg)

    def trace(msg: String): Unit = logger.trace(msg)

    def isDebugEnabled: Boolean = logger.isDebugEnabled

    def isErrorEnabled: Boolean = logger.isErrorEnabled

    def isInfoEnabled: Boolean = logger.isInfoEnabled

    def isWarnEnabled: Boolean = logger.isWarnEnabled

    def isTraceEnabled: Boolean = logger.isTraceEnabled

  }
}
