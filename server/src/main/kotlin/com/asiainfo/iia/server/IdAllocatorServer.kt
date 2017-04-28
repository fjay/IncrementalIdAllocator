package com.asiainfo.iia.server

import com.asiainfo.common.util.FileUtil
import com.asiainfo.common.util.IoUtil
import com.asiainfo.common.util.TimerUtil
import com.asiainfo.common.util.log.LogMessage
import com.asiainfo.common.util.log.Logs
import com.asiainfo.iia.server.api.web.IdAllocatorHttpServer
import org.apache.log4j.PropertyConfigurator
import java.io.Closeable

/**
 *
 *
 * @author Jay Wu
 */
object IdAllocatorServer : Closeable {

    private val log = Logs.get()

    private var httpServer: IdAllocatorHttpServer? = null

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

        httpServer = IdAllocatorHttpServer(ApplicationContext.currentServerNode.port).start();

        log.info(logMessage
                .append("node", ApplicationContext.currentServerNode)
                .success())
    }

    override fun close() {
        log.info(LogMessage("IdAllocatorServer", "close").processing())

        IoUtil.safeClose(httpServer)
        TimerUtil.depose()
        ApplicationContext.close()

        log.info(LogMessage("IdAllocatorServer", "close").success())
    }
}