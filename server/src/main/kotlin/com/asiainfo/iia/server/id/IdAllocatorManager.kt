package com.asiainfo.iia.server.id

import com.asiainfo.common.util.Registrar
import com.asiainfo.iia.common.ServerNodeRoute
import com.asiainfo.iia.server.ApplicationContext
import com.asiainfo.iia.server.DbConfig
import com.asiainfo.iia.server.node.ServerNodeRouter
import java.util.*

/**
 * @author Jay Wu
 */
object IdAllocatorManager : Registrar<Int, IdAllocator>() {

    var online = true

    private val serverNodeRouter = ApplicationContext.get(ServerNodeRouter::class.java)

    fun register(oldOne: ServerNodeRoute, newOne: ServerNodeRoute) {
        val oldKeys = oldOne.serverNodeAndKeys[ApplicationContext.currentServerNode.id] ?: Collections.emptySet<Int>()
        val newKeys = newOne.serverNodeAndKeys[ApplicationContext.currentServerNode.id] ?: Collections.emptySet<Int>()

        val addKeys = newKeys - oldKeys
        addKeys.forEach {
            register(IdAllocator(it,
                    DbConfig.get().IdAllocatorPoolSize.value.toInt(),
                    ApplicationContext.zkClient))
        }

        val removeKeys = oldKeys - newKeys
        removeKeys.forEach {
            unregister(it)
        }
    }

    fun accept(key: Int): Boolean {
        return online && serverNodeRouter.serverNodeRoute.getServerNode(key) ==
                ApplicationContext.currentServerNode.ipAndPort()
    }

    fun alloc(key: Int): Long? {
        if (!accept(key)) {
            return null
        }

        return get(key).alloc()
    }
}