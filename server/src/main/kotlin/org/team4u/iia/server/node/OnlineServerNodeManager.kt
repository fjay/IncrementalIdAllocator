package org.team4u.iia.server.node

import cn.hutool.core.io.IoUtil
import org.apache.curator.framework.recipes.cache.PathChildrenCache
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener
import org.apache.zookeeper.CreateMode
import org.team4u.iia.server.ApplicationContext
import org.team4u.iia.server.model.ServerNode
import java.io.Closeable
import java.util.*

/**
 * @author Jay Wu
 */
@IocBean
class OnlineServerNodeManager : Closeable {

    companion object {
        private val ONLINE_SERVER_NODE_PATH = "/online_server_nodes"
    }

    private var onlinePathCache: PathChildrenCache? = null

    fun registerAndWatch(node: ServerNode, listener: PathChildrenCacheListener) {
        // 完整路径为 /namespace/path/node 例如: /IIA/onlineServerNodes/127.0.0.1:7000
        val nodePath = "${ONLINE_SERVER_NODE_PATH}/$node"

        ApplicationContext.zkClient.create()
            .creatingParentsIfNeeded()
            .withMode(CreateMode.EPHEMERAL)
            .forPath(nodePath)

        onlinePathCache = PathChildrenCache(
            ApplicationContext.zkClient,
            ONLINE_SERVER_NODE_PATH, false
        )
        onlinePathCache?.listenable?.addListener(listener)
        onlinePathCache?.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE)
    }

    fun loadOnlineServerNodes(): List<ServerNode> {
        return onlinePathCache?.currentData?.map {
            val (ip, port) = it.path.split("/").last().split(":")
            ServerNode(ip, port.toInt())
        }?.distinct() ?: Collections.emptyList()
    }

    override fun close() {
        IoUtil.close(onlinePathCache)
    }
}