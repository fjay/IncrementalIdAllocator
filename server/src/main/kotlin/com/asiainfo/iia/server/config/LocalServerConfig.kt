package com.asiainfo.iia.server.config

import com.asiainfo.iia.server.model.ServerNode

/**
 * @author Jay Wu
 */
class LocalServerConfig : ServerConfig {

    lateinit var node: String

    override val serverNode: ServerNode by lazy {
        val (ip, port) = node.split(":")
        ServerNode(ip, port.toInt())
    }

    override lateinit var zkNode: String

    override var maxSegmentSize: Int = 50000

    override var idAllocatorPoolSize: Int = 1000

    override var nodeSessionTimeoutMs: Int = 3000
}