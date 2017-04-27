package com.asiainfo.test

import com.asiainfo.common.util.ThreadUtil
import com.asiainfo.common.util.http.HttpRequester
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

    @Test
    fun alloc() {
        val response = HttpRequester.create()
                .setUrl("http://127.0.0.1:7000/id/alloc")
                .putUrlParam("key", "1")
                .get()

        println(response.code)
        println(response.content)
    }
}