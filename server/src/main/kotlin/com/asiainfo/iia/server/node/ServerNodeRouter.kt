package com.asiainfo.iia.server.node

import com.asiainfo.common.util.EncryptUtil
import com.asiainfo.common.util.log.LogMessage
import com.asiainfo.common.util.log.Logs
import com.asiainfo.common.util.policy.ConsistentHashPolicy
import com.asiainfo.iia.common.ServerNodeRoute
import com.asiainfo.iia.server.ApplicationContext
import com.asiainfo.iia.server.Constant
import com.asiainfo.iia.server.DbConfig
import com.asiainfo.iia.server.id.IdAllocatorManager
import com.asiainfo.iia.server.model.ServerNode
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener

/**
 * @author Jay Wu
 */
class ServerNodeRouter {

    var serverNodeRoute: ServerNodeRoute = ServerNodeRoute()
        get

    private val log = Logs.get()

    private val onlineServerNodeManager = ApplicationContext.get(OnlineServerNodeManager::class.java)

    init {
        onlineServerNodeManager.registerAndWatch(ApplicationContext.currentServerNode, NodeChangedListener())
        onServerNodeChanged()
    }

    fun onServerNodeChanged() {
        val oldServerNodeRoute = serverNodeRoute;

        serverNodeRoute = buildRoute(
                onlineServerNodeManager.loadOnlineServerNodes(),
                DbConfig.get().maxSegmentSize.value.toInt()
        )

        IdAllocatorManager.register(oldServerNodeRoute, serverNodeRoute)

        log.info(LogMessage("ServerNodeRouter", "onServerNodeChanged")
                .append("serverNodeRoute", serverNodeRoute.serverNodeAndKeys.keys)
                .success())
    }

    fun buildRoute(nodes: List<ServerNode>, maxNodeSize: Int): ServerNodeRoute {
        val policy = ConsistentHashPolicy(nodes.map {
            ConsistentHashPolicy.PhysicalNode(Constant.APPLICATION_ID, it.ip, it.port)
        }, maxNodeSize)

        val keyAndServerNodes = HashMap<Int, String>()
        (0..maxNodeSize - 1).forEach {
            val node = policy.getNode(it.toString())
            keyAndServerNodes[it] = node.ip + ":" + node.port
        }

        return ServerNodeRoute().apply {
            this.nodeSessionTimeoutMs = DbConfig.get().nodeSessionTimeoutMs.value.toInt()
            this.version = calculateVersion(nodes)
            this.keyAndServerNodes = keyAndServerNodes
        }
    }

    fun calculateVersion(nodes: List<ServerNode>): String {
        return EncryptUtil.encryptWithMD5(nodes.map {
            it.toString()
        }.joinToString(","))
    }

    private inner class NodeChangedListener : PathChildrenCacheListener {
        override fun childEvent(client: CuratorFramework, event: PathChildrenCacheEvent) {
            log.info(LogMessage("NodeChangedListener", "changed").append("event", event).success())

            val eventType = event.type
            if (eventType == PathChildrenCacheEvent.Type.CHILD_UPDATED ||
                    eventType == PathChildrenCacheEvent.Type.CHILD_ADDED ||
                    eventType == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                onServerNodeChanged()
                return
            }

            if (eventType == PathChildrenCacheEvent.Type.CONNECTION_LOST) {
                IdAllocatorManager.enabled = false
                return
            }

            if (eventType == PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED) {
                IdAllocatorManager.enabled = true
                return
            }
        }
    }
}