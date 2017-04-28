package com.asiainfo.iia.server.node

import com.asiainfo.common.util.IoUtil
import com.asiainfo.iia.server.model.ServerNode
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache.PathChildrenCache
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener
import org.apache.zookeeper.CreateMode
import java.io.Closeable
import java.util.*

/**
 * @author Jay Wu
 */
class OnlineServerNodeManager(val client: CuratorFramework) : Closeable {

    companion object {
        private val ONLINE_SERVER_NODE_PATH = "/onlineServerNodes"
    }

    private var onlinePathCache: PathChildrenCache? = null

    fun registerAndWatch(node: ServerNode, listener: PathChildrenCacheListener) {
        // 完整路径为 /namespace/path/node:seq 例如: /IIA/onlineServerNodes/IIA_0:127.0.0.1:7000
        val nodePath = "$ONLINE_SERVER_NODE_PATH/$node"

        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(nodePath)

        onlinePathCache = PathChildrenCache(client, ONLINE_SERVER_NODE_PATH, false)
        onlinePathCache!!.listenable.addListener(listener)
        onlinePathCache!!.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE)
    }

    fun loadOnlineServerNodes(): List<ServerNode> {
        return onlinePathCache!!.currentData?.map {
            val (id, ip, port) = it.path.split("/").last().split(":")
            ServerNode(id, ip, port.toInt())
        }?.distinct() ?: Collections.emptyList()
    }

    override fun close() {
        IoUtil.safeClose(onlinePathCache)
    }
}