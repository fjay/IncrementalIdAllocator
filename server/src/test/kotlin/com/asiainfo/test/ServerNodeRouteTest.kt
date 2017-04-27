package com.asiainfo.test

import com.asiainfo.iia.common.ServerNodeRoute
import org.junit.Assert
import org.junit.Test

/**
 * @author Jay Wu
 */
class ServerNodeRouteTest {

    @Test
    fun toKeyAndServerNodes() {
        val route = ServerNodeRoute()

        route.keyAndServerNodes = mapOf(1 to "a", 2 to "b", 3 to "a")
        Assert.assertEquals("{a=[1, 3], b=[2]}", route.serverNodeAndKeys.toString())

        route.serverNodeAndKeys = route.serverNodeAndKeys
        Assert.assertEquals("{1=a, 3=a, 2=b}", route.keyAndServerNodes.toString())
    }
}