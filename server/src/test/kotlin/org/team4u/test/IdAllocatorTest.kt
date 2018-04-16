package org.team4u.test

import org.apache.zookeeper.KeeperException
import org.junit.Assert
import org.junit.Test
import org.team4u.iia.server.ApplicationContext
import org.team4u.iia.server.id.IdAllocator

/**
 * @author Jay Wu
 */
class IdAllocatorTest {

    @Test
    fun alloc() {
        ApplicationContext.initialize()
        val segmentKey = -1
        clearNode("/id_segment/$segmentKey")

        val allocator =
            IdAllocator(segmentKey, 2, ApplicationContext.zkClient)
        Assert.assertEquals(allocator.alloc(), 1L)
        Assert.assertEquals(allocator.alloc(), 2L)
        Assert.assertEquals(allocator.alloc(), 3L)

        clearNode("/id_segment/$segmentKey")
    }

    private fun clearNode(path: String) {
        try {
            ApplicationContext.zkClient.delete().deletingChildrenIfNeeded().forPath(path)
        } catch (e: KeeperException.NoNodeException) {
            // Ignore error
        }
    }
}