package com.asiainfo.test

import com.asiainfo.iia.server.ApplicationContext
import com.asiainfo.iia.server.node.OnlineServerNodeManager
import com.asiainfo.iia.server.node.ServerNodeRouter
import org.junit.Assert
import org.junit.Test

/**
 * @author Jay Wu
 */
class ServerNodeRouterTest {

    @Test
    fun loadOnlineServerNodes() {
        ApplicationContext.initialize()

        val manager = ApplicationContext.get(OnlineServerNodeManager::class.java)
        Assert.assertEquals("[IIA_0:127.0.0.1:7000]", manager.loadOnlineServerNodes().toString())
    }

    @Test
    fun getServerNodeRouter() {
        ApplicationContext.initialize()

        val router = ApplicationContext.get(ServerNodeRouter::class.java)

        Assert.assertEquals("{127.0.0.1:7000=[0, 1, 2]}",
                router.serverNodeRoute.serverNodeAndKeys.toString())
        Assert.assertEquals("{0=127.0.0.1:7000, 1=127.0.0.1:7000, 2=127.0.0.1:7000}",
                router.serverNodeRoute.keyAndServerNodes.toString())
        Assert.assertEquals("9494E35D9467930D9134B9F1A43D6622", router.serverNodeRoute.version)
    }
}