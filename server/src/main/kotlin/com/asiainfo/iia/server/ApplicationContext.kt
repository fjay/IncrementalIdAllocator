package com.asiainfo.iia.server

import com.asiainfo.common.util.log.LogMessage
import com.asiainfo.common.util.log.Logs
import com.asiainfo.iia.server.config.ServerConfig
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.nutz.ioc.impl.NutIoc
import org.nutz.ioc.loader.combo.ComboIocLoader

/**
 *
 *
 * @author Jay Wu
 */
object ApplicationContext {

    private val log = Logs.get()

    lateinit var config: ServerConfig

    lateinit var ioc: NutIoc

    lateinit var zkClient: CuratorFramework

    fun initialize() {
        initIoc()
        initServerConfig()
        initZkClient()
    }

    fun initIoc() {
        ioc = NutIoc(ComboIocLoader(
                "*org.nutz.ioc.loader.json.JsonLoader", "config.js",
                "*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
                "com.asiainfo.iia.server"));
    }

    fun initServerConfig() {
        val logMessage = LogMessage("ApplicationContext", "initServerConfig")
                .processing()

        log.info(logMessage)

        config = ioc.get(ServerConfig::class.java)

        log.info(logMessage.success())
    }

    fun initZkClient() {
        val logMessage = LogMessage("ApplicationContext", "initZKClient")
                .append("host", config.zkNode)
                .processing()

        log.info(logMessage)

        val nodeRefreshIntervalMs = config.nodeSessionTimeoutMs
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(config.zkNode)
                .connectionTimeoutMs(nodeRefreshIntervalMs - 1000)
                .sessionTimeoutMs(nodeRefreshIntervalMs)
                .retryPolicy(ExponentialBackoffRetry(nodeRefreshIntervalMs, 10))
                .namespace(ApplicationContext.config.namespace)
                .build()

        zkClient.start()

        log.info(logMessage.success())
    }
}