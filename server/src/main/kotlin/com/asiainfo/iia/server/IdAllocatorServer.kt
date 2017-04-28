package com.asiainfo.iia.server

import com.asiainfo.common.util.FileUtil
import com.asiainfo.common.util.IoUtil
import com.asiainfo.common.util.TimerUtil
import com.asiainfo.common.util.log.LogMessage
import com.asiainfo.common.util.log.Logs
import com.asiainfo.iia.server.api.web.IdAllocatorController
import org.apache.log4j.PropertyConfigurator
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
 *
 *
 * @author Jay Wu
 */
object IdAllocatorServer : Closeable {

    private val log = Logs.get()

    private var server: HttpServer? = null

    @JvmStatic
    fun main(args: Array<String>) {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                close()
            }
        })

        start()
    }

    fun start() {
        val logMessage = LogMessage("IdAllocatorServer", "start")
                .processing()

        log.info(logMessage)

        PropertyConfigurator.configureAndWatch(FileUtil.findFile("log4j.properties").absolutePath, 1000)

        ApplicationContext.initialize()

        val pool = ThreadPoolExecutor(50, 50, 0L, TimeUnit.MILLISECONDS, LinkedTransferQueue<Runnable>());

        val router = DefaultHttpRouter()
                .addLastHandlerMapping(RequestMappingHandlerMapping().addController(IdAllocatorController()))
                .addFirstViewResolver(FastJsonViewResolver())

        server = NettyHttpServer(NettyHttpServerConfig().setRequestThreadPool(pool))
                .onRequest { request, response ->
                    val context = RoutingContext().setRequest(request).setResponse(response)
                    router.doRoute(context);
                }.listen(ApplicationContext.currentServerNode.port);

        log.info(logMessage
                .append("node", ApplicationContext.currentServerNode)
                .success())
    }

    override fun close() {
        log.info(LogMessage("IdAllocatorServer", "close").processing())

        IoUtil.safeClose(server)
        TimerUtil.depose()
        ApplicationContext.close()

        log.info(LogMessage("IdAllocatorServer", "close").success())
    }
}