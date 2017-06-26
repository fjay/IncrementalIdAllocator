package com.asiainfo.test

import com.asiainfo.iia.server.ApplicationContext
import com.asiainfo.iia.server.id.IdAllocator
import org.apache.zookeeper.KeeperException
import org.junit.Assert
import org.junit.Test

/**
 * @author Jay Wu
 */
class IdAllocatorTest {

    @Test
    fun alloc() {
        ApplicationContext.initialize()
        val segmentKey = -1
        try {
            ApplicationContext.zkClient.delete().deletingChildrenIfNeeded().forPath("/id_segment/$segmentKey")
        } catch (e: KeeperException.NoNodeException) {
            // Ignore error
        }

        val allocator = IdAllocator(segmentKey, 2, ApplicationContext.zkClient)
        Assert.assertEquals(allocator.alloc(), 1L)
        Assert.assertEquals(allocator.alloc(), 2L)
        Assert.assertEquals(allocator.alloc(), 3L)
    }
}