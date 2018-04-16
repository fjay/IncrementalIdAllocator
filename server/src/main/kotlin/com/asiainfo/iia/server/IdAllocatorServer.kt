package com.asiainfo.iia.server

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.log.LogFactory
import com.asiainfo.iia.server.api.http.IdAllocatorHttpServer
import com.asiainfo.iia.server.node.OnlineServerNodeManager
import org.apache.log4j.PropertyConfigurator
import org.team4u.kit.core.log.LogMessage
import org.team4u.kit.core.util.TimerUtil
import java.io.Closeable

/**
 *
 *
 * @author Jay Wu
 */
object IdAllocatorServer : Closeable {

    private val log = LogFactory.get()

    private var httpServer: IdAllocatorHttpServer? = null

    @JvmStatic
    fun main(args: Array<String>) {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                close()
            }
        })

        val logMessage = LogMessage("IdAllocatorServer", "start")
            .processing()

        log.info(logMessage.toString())

        try {
            start()

            log.info(
                logMessage
                    .append("node", ApplicationContext.config.serverNode)
                    .success()
                    .toString()
            )
        } catch (e: Throwable) {
            log.error(logMessage.fail(e.message).toString(), e)
            System.exit(1)
        }
    }

    fun start() {
        PropertyConfigurator.configureAndWatch(FileUtil.file("log4j.properties").absolutePath, 1000)

        ApplicationContext.initialize()

        httpServer = IdAllocatorHttpServer(ApplicationContext.config.serverNode.port).start();
    }

    override fun close() {
        log.info(LogMessage("IdAllocatorServer", "close").processing().toString())

        IoUtil.close(httpServer)
        TimerUtil.close()

        IoUtil.close(ApplicationContext.ioc.get(OnlineServerNodeManager::class.java))
        IoUtil.close(ApplicationContext.zkClient)

        ApplicationContext.ioc.depose()

        log.info(LogMessage("IdAllocatorServer", "close").success().toString())
    }
}