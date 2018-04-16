package org.team4u.test

import org.junit.Test
import org.team4u.iia.server.ApplicationContext
import org.team4u.iia.server.id.IdAllocatorManager

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