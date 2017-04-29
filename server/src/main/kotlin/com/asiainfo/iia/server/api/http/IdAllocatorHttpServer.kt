package com.asiainfo.iia.server.api.http

import com.asiainfo.common.util.IoUtil
import org.team4u.fhs.server.HttpServer
import org.team4u.fhs.server.impl.netty.NettyHttpServer
import org.team4u.fhs.server.impl.netty.NettyHttpServerConfig
import org.team4u.fhs.web.DefaultHttpRouter
import org.team4u.fhs.web.RoutingContext
import org.team4u.fhs.web.ext.view.FastJsonViewResolver
import org.team4u.fhs.web.handler.method.RequestMappingHandlerMapping
import java.io.Closeable
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author Jay Wu
 */
class IdAllocatorHttpServer(val port: Int) : Closeable {

    private var server: HttpServer? = null

    fun start(): IdAllocatorHttpServer {
        val pool = ThreadPoolExecutor(5, 50, 0L, TimeUnit.MILLISECONDS, LinkedTransferQueue<Runnable>());

        val router = DefaultHttpRouter()
                .addLastHandlerMapping(RequestMappingHandlerMapping().addController(IdAllocatorController()))
                .addFirstViewResolver(FastJsonViewResolver())

        server = NettyHttpServer(NettyHttpServerConfig().setRequestThreadPool(pool))
                .onRequest { request, response ->
                    router.doRoute(RoutingContext().setRequest(request).setResponse(response));
                }.listen(port);

        return this;
    }

    override fun close() {
        IoUtil.safeClose(server)
    }
}