package com.asiainfo.test

import com.asiainfo.common.util.ThreadUtil
import com.asiainfo.iia.server.IdAllocatorServer
import org.junit.Test

/**
 *
 *
 * @author Jay Wu
 */
class ServerTest {

    @Test
    fun start() {
        IdAllocatorServer.start()
        ThreadUtil.sleep(Long.MAX_VALUE)
    }
}