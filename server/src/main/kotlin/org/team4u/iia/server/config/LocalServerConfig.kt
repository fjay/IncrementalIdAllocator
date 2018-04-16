package org.team4u.iia.server.config

import org.team4u.iia.server.model.ServerNode

/**
 * @author Jay Wu
 */
class LocalServerConfig : ServerConfig {

    override lateinit var namespace: String

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