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

    val nodeChangedListener: PathChildrenCacheListener = NodeChangedListener()

    private val log = Logs.get()

    private val onlineServerNodeManager = ApplicationContext.get(OnlineServerNodeManager::class.java)

    lateinit var serverNodeRoute: ServerNodeRoute
        get

    init {
        onServerNodeChanged()
    }

    fun onServerNodeChanged() {
        val oldServerNodeRoute = serverNodeRoute;

        serverNodeRoute = doRoute(
                onlineServerNodeManager.loadOnlineServerNodes(),
                DbConfig.get().maxServerNodeSize.value.toInt()
        )

        IdAllocatorManager.register(oldServerNodeRoute, serverNodeRoute)
    }

    fun doRoute(nodes: List<ServerNode>, maxNodeSize: Int): ServerNodeRoute {
        val policy = ConsistentHashPolicy(nodes.map {
            ConsistentHashPolicy.PhysicalNode(Constant.APPLICATION_ID, it.ip, it.port)
        }, maxNodeSize)

        val keyAndServerNodes = HashMap<Int, String>()
        (0..maxNodeSize).forEach {
            val node = policy.getNode(it.toString())
            keyAndServerNodes[it] = node.ip + ":" + node.port
        }

        return ServerNodeRoute().apply {
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
        override fun childEvent(client: CuratorFramework?, event: PathChildrenCacheEvent?) {
            if (event == PathChildrenCacheEvent.Type.CHILD_UPDATED ||
                    event == PathChildrenCacheEvent.Type.CHILD_ADDED ||
                    event == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                log.info(LogMessage("NodeChangedListener", "changed"))
                onServerNodeChanged()
                return
            }

            if (event == PathChildrenCacheEvent.Type.CONNECTION_LOST) {
                log.info(LogMessage("NodeChangedListener", "connectionLost"))
                IdAllocatorManager.online = false
                return
            }

            if (event == PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED) {
                log.info(LogMessage("NodeChangedListener", "connectionReconnected"))

                IdAllocatorManager.online = true
                return
            }
        }
    }
}