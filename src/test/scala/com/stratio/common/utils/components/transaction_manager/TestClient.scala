
package com.stratio.common.utils.components.transaction_manager

import com.stratio.common.utils.components.config.impl.TypesafeConfigComponent
import com.stratio.common.utils.components.logger.impl.Slf4jLoggerComponent
import com.stratio.common.utils.components.transaction_manager.impl.ZookeeperRepositoryWithTransactionsComponent

object TestClient extends App {

  case class Resource(n: Int) extends TransactionResource {
    val id: String = n.toString
  }

  require(
    args.size > 2 && args.size % 2 == 1,
    "Usage: TestClient <client_label> <resourceids_prefix> <nres> [<no_segment_parts> <part_duration>]"
  )

  val Array(
    label,
    resourcePrefix,
    nResources,
    segmentsStr @ _*
  ) = args

  val segments = segmentsStr.grouped(2)

  val resources = (0 until nResources.toInt) map Resource

  def mayBeProtected(block: => Unit): Unit =
    if(resources.isEmpty) block
    else transactionManager.repository.atomically("test", resources.head, resources.tail:_*)(block)

  val transactionManager = new ZookeeperRepositoryWithTransactionsComponent
    with TypesafeConfigComponent with Slf4jLoggerComponent

  segments.zipWithIndex foreach { case (segment, iteration) =>
    val Seq(nParts, millis) = segment.map(_.toLong)
    mayBeProtected {
      (1L to nParts) foreach { part =>
        println(s"client=$label resources=[${resources.map(_.id).mkString(", ")}] segment=$iteration part=$part")
        Thread.sleep(millis)
      }
    }
  }

}

