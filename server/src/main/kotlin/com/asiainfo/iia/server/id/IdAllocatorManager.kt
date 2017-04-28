package com.asiainfo.iia.server.id

import com.asiainfo.common.util.Registrar
import com.asiainfo.common.util.log.LogMessage
import com.asiainfo.common.util.log.Logs
import com.asiainfo.iia.common.ServerNodeRoute
import com.asiainfo.iia.server.ApplicationContext
import com.asiainfo.iia.server.DbConfig
import com.asiainfo.iia.server.node.ServerNodeRouter
import org.apache.curator.framework.CuratorFramework
import java.util.*

/**
 * @author Jay Wu
 */
object IdAllocatorManager : Registrar<Int, IdAllocator>() {

    private val log = Logs.get()

    var online = true

    fun register(oldOne: ServerNodeRoute, newOne: ServerNodeRoute) {
        val oldKeys = oldOne.serverNodeAndKeys[ApplicationContext.currentServerNode.ipAndPort()] ?: Collections.emptySet<Int>()
        val newKeys = newOne.serverNodeAndKeys[ApplicationContext.currentServerNode.ipAndPort()] ?: Collections.emptySet<Int>()

        val addKeys = newKeys - oldKeys
        addKeys.forEach {
            register(IdAllocator(it,
                    DbConfig.get().idAllocatorPoolSize.value.toInt(),
                    ApplicationContext.get(CuratorFramework::class.java)))
        }

        val removeKeys = oldKeys - newKeys
        removeKeys.forEach {
            unregister(it)
        }

        if (log.isDebugEnabled) {
            log.debug(LogMessage("IdAllocatorManager", "register")
                    .append("addKeys", addKeys)
                    .append("removeKeys", removeKeys)
                    .success())
        }
    }

    fun accept(key: Int): Boolean {
        return online && ApplicationContext.get(ServerNodeRouter::class.java).serverNodeRoute.getServerNode(key) ==
                ApplicationContext.currentServerNode.ipAndPort()
    }

    fun alloc(key: Int): Long? {
        if (!accept(key)) {
            return null
        }

        return get(key).alloc()
    }
}