package org.team4u.iia.server.config

import org.team4u.iia.server.model.ServerNode

interface ServerConfig {

    val namespace: String

    val serverNode: ServerNode

    val zkNode: String

    val maxSegmentSize: Int

    val idAllocatorPoolSize: Int

    val nodeSessionTimeoutMs: Int
}