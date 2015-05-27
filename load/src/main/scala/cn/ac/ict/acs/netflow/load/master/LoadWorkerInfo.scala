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
package cn.ac.ict.acs.netflow.load.master

import akka.actor.ActorRef

import cn.ac.ict.acs.netflow.util.Utils

class LoadWorkerInfo(
    val id: String,
    val host: String,
    val port: Int,
    val cores: Int,
    val memory: Int,
    val actor: ActorRef,
    val webUiPort: Int,
    val udpPort: Int)
  extends Serializable {

  Utils.checkHost(host, "Expected hostname")
  assert(port > 0 && udpPort > 0)

  @transient var state: WorkerState.Value = _
  @transient var coresUsed: Int = _
  @transient var memoryUsed: Int = _
  @transient var lastHeartbeat: Long = _

  def coresFree: Int = cores - coresUsed
  def memoryFree: Int = memory - memoryUsed

  protected def init(): Unit = {
    coresUsed = 0
    memoryUsed = 0
    lastHeartbeat = System.currentTimeMillis()
  }

  protected def hostPort: String = {
    assert(port > 0)
    host + ":" + port
  }

  def setState(state: WorkerState.Value): Unit = {
    this.state = state
  }
}
