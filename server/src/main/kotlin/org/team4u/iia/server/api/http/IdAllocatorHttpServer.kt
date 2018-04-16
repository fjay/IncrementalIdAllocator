package org.team4u.iia.server.api.http

import cn.hutool.core.io.IoUtil
import org.team4u.fhs.server.HttpServer
import org.team4u.fhs.server.impl.netty.NettyHttpServer
import org.team4u.fhs.server.impl.netty.NettyHttpServerConfig
import org.team4u.fhs.web.DefaultHttpRouter
import org.team4u.fhs.web.RoutingContext
import org.team4u.fhs.web.ext.view.FastJsonViewResolver
import org.team4u.fhs.web.handler.method.RequestMappingHandlerMapping
import org.team4u.iia.server.ApplicationContext
import java.io.Closeable

/**
 * @author Jay Wu
 */
class IdAllocatorHttpServer(val port: Int) : Closeable {

    private var server: HttpServer? = null

    fun start(): IdAllocatorHttpServer {
        val router = DefaultHttpRouter()
            .addLastHandlerMapping(
                RequestMappingHandlerMapping()
                    .addController(ApplicationContext.ioc.get(IdAllocatorController::class.java))
            )
            .addFirstViewResolver(FastJsonViewResolver())

        server = NettyHttpServer(NettyHttpServerConfig())
            .onRequest { request, response ->
                router.doRoute(RoutingContext().setRequest(request).setResponse(response))
            }.listen(port)

        return this
    }

    override fun close() {
        IoUtil.close(server)
    }
}