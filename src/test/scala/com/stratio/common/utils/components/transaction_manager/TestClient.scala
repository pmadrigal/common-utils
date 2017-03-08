
package com.stratio.common.utils.components.transaction_manager

import com.stratio.common.utils.components.config.impl.TypesafeConfigComponent
import com.stratio.common.utils.components.logger.impl.Slf4jLoggerComponent
import com.stratio.common.utils.components.transaction_manager.impl.ZookeeperRepositoryWithTransactionsComponent

object TestClient extends App {

  val ZookeeperTestPort: Int = 2181 //10666

  case class Resource(n: Int) extends TransactionResource {
    val id: String = n.toString
  }

  val Array(
    label,
    resourcePrefix,
    nResources,
    segmentsStr @ _*
  ) = args

  val segments = segmentsStr.grouped(2)

  val resources = (0 until nResources.toInt) map Resource

  val transactionManager = new ZookeeperRepositoryWithTransactionsComponent
    with TypesafeConfigComponent with Slf4jLoggerComponent

  segments.zipWithIndex foreach { case (segment, iteration) =>
    val Seq(millis, nParts) = segment.map(_.toLong)
    transactionManager.repository.atomically("test", resources.head, resources.tail:_*) {
      Thread.sleep(millisStr.toLong)
    }
  }
}

