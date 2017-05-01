package com.asiainfo.iia.server.id

import com.asiainfo.common.util.Registrar
import com.asiainfo.common.util.log.LogMessage
import com.asiainfo.common.util.log.Logs
import com.asiainfo.iia.common.model.ServerNodeRoute
import com.asiainfo.iia.server.ApplicationContext
import com.asiainfo.iia.server.node.ServerNodeRouter
import java.util.*

/**
 * @author Jay Wu
 */
object IdAllocatorManager : Registrar<Int, IdAllocator>() {

    var enabled = true
        set

    private val log = Logs.get()

    fun buildIdAllocators(oldOne: ServerNodeRoute, newOne: ServerNodeRoute) {
        val oldKeys = getCurrentServerNodeKeys(oldOne)
        val newKeys = getCurrentServerNodeKeys(newOne)

        val addKeys = newKeys - oldKeys
        addKeys.forEach {
            register(IdAllocator(it,
                    ApplicationContext.config.idAllocatorPoolSize,
                    ApplicationContext.zkClient))
        }

        val removeKeys = oldKeys - newKeys
        removeKeys.forEach {
            unregister(it)
        }

        log.info(LogMessage("IdAllocatorManager", "buildIdAllocators")
                .append("addKeys", addKeys.size)
                .append("removeKeys", removeKeys.size)
                .success())
    }

    fun accept(key: Int): Boolean {
        return enabled &&
                ApplicationContext.ioc.get(ServerNodeRouter::class.java).serverNodeRoute.getServerNode(key) ==
                        ApplicationContext.config.serverNode.toString()
    }

    fun alloc(key: Int): Long? {
        if (!accept(key)) {
            return null
        }

        return get(key).alloc()
    }

    private fun getCurrentServerNodeKeys(route: ServerNodeRoute): Set<Int> {
        return route.serverNodeAndKeys[ApplicationContext.config.serverNode.toString()] ?: Collections.emptySet()
    }
}