package org.team4u.test

import cn.hutool.core.lang.Dict
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.http.HttpUtil
import org.junit.Test
import org.team4u.iia.server.IdAllocatorServer

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
        val response = HttpUtil.get(
            "http://127.0.0.1:7000/id/alloc",
            Dict.create()
                .set("key", "2")
                .set("version", "20C11BF63638AAFB8CD5D12979D961DC")
        )

        println(response)
    }
}