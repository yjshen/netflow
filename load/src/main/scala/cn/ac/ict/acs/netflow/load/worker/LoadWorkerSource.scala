/**
 * Copyright 2015 ICT.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ac.ict.acs.netflow.load.worker

import scala.collection.JavaConversions._

import com.codahale.metrics.{ Gauge, MetricRegistry }

import org.apache.hadoop.fs.FileSystem

import cn.ac.ict.acs.netflow.metrics.source.Source

class LoadWorkerSource(val worker: LoadWorker) extends Source {
  override val metricRegistry = new MetricRegistry()
  override val sourceName = "loadworker"

  private def fileStats(scheme: String): Option[FileSystem.Statistics] =
    FileSystem.getAllStatistics.find(s => s.getScheme.equals(scheme))

  private def registerFileSystemStat[T](
    scheme: String, name: String, f: FileSystem.Statistics => T, defaultValue: T) = {
    metricRegistry.register(MetricRegistry.name("filesystem", scheme, name), new Gauge[T] {
      override def getValue: T = fileStats(scheme).map(f).getOrElse(defaultValue)
    })
  }

  metricRegistry.register("loadThreadNum", new Gauge[Int] {
    override def getValue: Int = worker.loadServer.curThreadsNum
  })

  metricRegistry.register("loadBufferSize", new Gauge[Int] {
    override def getValue: Int = worker.netflowBuff.currSize
  })
  metricRegistry.register("loadBufferCapacity", new Gauge[Int] {
    override def getValue: Int = worker.netflowBuff.maxQueueNum
  })
  metricRegistry.register("loadBufferUsageRatio", new Gauge[Double] {
    override def getValue: Double = worker.netflowBuff.currUsageRate()
  })

  metricRegistry.register("dataReceptionRate", new Gauge[Double] {
    override def getValue: Double = worker.netflowBuff.enqueueRate
  })

  metricRegistry.register("dataProcessingRate", new Gauge[Double] {
    override def getValue: Double = worker.netflowBuff.dequeueRate
  })

  // Gauge for file system stats of this worker
  for (scheme <- Array("hdfs", "file")) {
    registerFileSystemStat(scheme, "write_bytes", _.getBytesWritten, 0L)
    registerFileSystemStat(scheme, "write_ops", _.getWriteOps, 0)
  }

  // Load characteristic of each thread ....

}
