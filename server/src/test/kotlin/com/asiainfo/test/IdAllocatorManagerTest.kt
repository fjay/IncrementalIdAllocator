package com.asiainfo.test

import com.asiainfo.iia.server.ApplicationContext
import com.asiainfo.iia.server.id.IdAllocatorManager
import org.junit.Test

/**
 * @author Jay Wu
 */
class IdAllocatorManagerTest {

    @Test
    fun alloc() {
        ApplicationContext.initialize()
        Thread.sleep(1000)
        println(IdAllocatorManager.alloc(1))
    }
}