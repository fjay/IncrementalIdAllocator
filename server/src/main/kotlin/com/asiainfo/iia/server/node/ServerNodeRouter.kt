package com.asiainfo.iia.server.node

import com.asiainfo.common.util.EncryptUtil
import com.asiainfo.common.util.log.LogMessage
import com.asiainfo.common.util.log.Logs
import com.asiainfo.common.util.policy.ConsistentHashPolicy
import com.asiainfo.iia.common.model.ServerNodeRoute
import com.asiainfo.iia.server.ApplicationContext
import com.asiainfo.iia.server.id.IdAllocatorManager
import com.asiainfo.iia.server.model.ServerNode
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener
import org.nutz.ioc.loader.annotation.Inject
import org.nutz.ioc.loader.annotation.IocBean

/**
 * @author Jay Wu
 */
@IocBean(create = "init")
class ServerNodeRouter {

    var serverNodeRoute: ServerNodeRoute = ServerNodeRoute()
        get

    private val log = Logs.get()

    @Inject
    private lateinit var onlineServerNodeManager: OnlineServerNodeManager

    fun init() {
        onlineServerNodeManager.registerAndWatch(
                ApplicationContext.config.serverNode,
                OnlinePathChildrenChangedListener()
        )
        onServerNodeChanged()
    }

    fun buildRoute(nodes: List<ServerNode>, maxSegmentSize: Int, nodeSessionTimeoutMs: Int): ServerNodeRoute {
        val policy = ConsistentHashPolicy(nodes.map {
            ConsistentHashPolicy.PhysicalNode("", it.ip, it.port)
        }, maxSegmentSize)

        val keyAndServerNodes = HashMap<Int, String>()
        (0..maxSegmentSize - 1).forEach {
            val node = policy.getNode(it.toString())
            keyAndServerNodes[it] = node.ip + ":" + node.port
        }

        return ServerNodeRoute().apply {
            this.nodeSessionTimeoutMs = nodeSessionTimeoutMs
            this.version = calculateVersion(nodes)
            this.keyAndServerNodes = keyAndServerNodes
        }
    }

    private fun calculateVersion(nodes: List<ServerNode>): String {
        return EncryptUtil.encryptWithMD5(nodes.map {
            it.toString()
        }.joinToString(","))
    }

    private fun onServerNodeChanged() {
        val oldServerNodeRoute = serverNodeRoute;

        serverNodeRoute = buildRoute(
                onlineServerNodeManager.loadOnlineServerNodes(),
                ApplicationContext.config.maxSegmentSize,
                ApplicationContext.config.nodeSessionTimeoutMs
        )

        IdAllocatorManager.buildIdAllocators(oldServerNodeRoute, serverNodeRoute)

        log.info(LogMessage("ServerNodeRouter", "onServerNodeChanged")
                .append("serverNodeRoute", serverNodeRoute.serverNodeAndKeys.keys)
                .success())
    }

    private inner class OnlinePathChildrenChangedListener : PathChildrenCacheListener {
        override fun childEvent(client: CuratorFramework, event: PathChildrenCacheEvent) {
            log.info(LogMessage("NodeChangedListener", "changed").append("event", event).success())

            when (event.type) {
                PathChildrenCacheEvent.Type.CHILD_UPDATED,
                PathChildrenCacheEvent.Type.CHILD_ADDED,
                PathChildrenCacheEvent.Type.CHILD_REMOVED -> onServerNodeChanged()

                PathChildrenCacheEvent.Type.CONNECTION_LOST -> IdAllocatorManager.enabled = false

                PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED -> IdAllocatorManager.enabled = true

                else -> return
            }
        }
    }
}