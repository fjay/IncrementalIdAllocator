package com.asiainfo.iia.server.config

import com.asiainfo.iia.server.model.ServerNode

interface ServerConfig {

    val namespace: String

    val serverNode: ServerNode

    val zkNode: String

    val maxSegmentSize: Int

    val idAllocatorPoolSize: Int

    val nodeSessionTimeoutMs: Int
}