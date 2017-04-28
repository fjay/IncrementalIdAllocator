package com.asiainfo.iia.server.node

import com.asiainfo.iia.server.model.ServerNode
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache.PathChildrenCache
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener
import org.apache.zookeeper.CreateMode
import java.util.*

/**
 * @author Jay Wu
 */
class OnlineServerNodeManager(val client: CuratorFramework) {

    companion object {
        private val ONLINE_SERVER_NODE_PATH = "/onlineServerNodes"
    }

    private lateinit var pathCache: PathChildrenCache

    fun registerAndWatch(node: ServerNode, listener: PathChildrenCacheListener) {
        // 完整路径为 /namespace/path/node:seq 例如: /IIA/onlineServerNodes/IIA_0:127.0.0.1:7000:0000000001
        val nodePath = "$ONLINE_SERVER_NODE_PATH/$node:"

        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(nodePath)

        pathCache = PathChildrenCache(client, ONLINE_SERVER_NODE_PATH, false)
        pathCache.listenable.addListener(listener)
        pathCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE)
    }

    fun loadOnlineServerNodes(): List<ServerNode> {
        return pathCache.currentData?.map {
            val (id, ip, port) = it.path.split("/").last().split(":")
            ServerNode(id, ip, port.toInt())
        }?.distinct() ?: Collections.emptyList()
    }
}