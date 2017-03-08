
package com.stratio.common.utils.components.transaction_manager

import com.stratio.common.utils.components.config.impl.TypesafeConfigComponent
import com.stratio.common.utils.components.logger.impl.Slf4jLoggerComponent
import com.stratio.common.utils.components.transaction_manager.impl.ZookeeperRepositoryWithTransactionsComponent

object TestClient extends App {

  case class Resource(id: String) extends TransactionResource

  val usageMsg = "Usage: TestClient <client_label> <nresources> [resourceid] [<no_segment_parts> <part_duration>]"

  require(args.size > 2, usageMsg)

  val Array(
    label,
    nResourcesStr,
    remainingArgs @ _*
  ) = args

  require(nResourcesStr.toInt <= remainingArgs.size)

  val (resourcesStr, segmentsStr) = remainingArgs.splitAt(nResourcesStr.toInt)
  val resources = resourcesStr map Resource

  require((remainingArgs.size - resources.size) % 2 == 0, usageMsg)

  val segments = segmentsStr.grouped(2)


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

