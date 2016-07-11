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
package com.stratio.common.utils.components.metrics

/**
 * Dummy component that each time returns the
 * double of last millisecond number.
 * It starts from zero.
 */
trait DummyTimeComponent extends TimeComponent {

  private var current: Int = 0
  private val times: Stream[Long] = 1L #:: times.map(_ * 2)

  def now(): Milliseconds = synchronized{
    val t = times(current)
    current += 1
    t
  }

  def reset(): Unit = synchronized(current=0)

}
