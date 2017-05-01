package com.asiainfo.iia.server.config

import com.asiainfo.common.kotlin.extension.isNotEmpty
import com.asiainfo.conf.client.loader.DbConfigItemLoader
import com.asiainfo.conf.client.loader.InMemoryConfigItemLoader
import com.asiainfo.conf.common.entity.ConfigItem
import com.asiainfo.iia.server.ApplicationErrorCode
import com.asiainfo.iia.server.model.ServerNode
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

/**
 * @author Jay Wu
 */
class DbServerConfig() : ServerConfig {

    override lateinit var namespace: String

    lateinit var nodeId: String

    lateinit var dataSource: DataSource

    private val client: InMemoryConfigItemLoader by lazy {
        val loader = InMemoryConfigItemLoader(
                DbConfigItemLoader(namespace, "DEFAULT", dataSource)
        )

        loader.watch(TimeUnit.SECONDS, 30L) { _, _ ->
        }

        loader
    }

    override val serverNode: ServerNode by lazy {
        val node = items().iiaNodes.find {
            it.configItemId == nodeId
        }

        ApplicationErrorCode.ILLEGAL_PARAM.isNotEmpty(node, "Can't find server node in db(nodeId=$nodeId)")

        val (ip, port) = node!!.value.split(":")
        ServerNode(ip, port.toInt())
    }

    override val zkNode: String
        get() = items().zkNode.value

    override val maxSegmentSize: Int
        get() = items().maxSegmentSize.value.toInt()

    override val idAllocatorPoolSize: Int
        get() = items().idAllocatorPoolSize.value.toInt()

    override val nodeSessionTimeoutMs: Int
        get() = items().nodeSessionTimeoutMs.value.toInt()

    private fun items() = client.load(DbConfigItem::class.java);

    class DbConfigItem {
        lateinit var iiaNodes: List<ConfigItem>

        lateinit var zkNode: ConfigItem

        lateinit var maxSegmentSize: ConfigItem

        lateinit var idAllocatorPoolSize: ConfigItem

        lateinit var nodeSessionTimeoutMs: ConfigItem
    }
}