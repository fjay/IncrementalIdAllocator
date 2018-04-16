package com.asiainfo.iia.server

import cn.hutool.log.LogFactory
import com.asiainfo.iia.server.config.ServerConfig
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.team4u.kit.core.log.LogMessage

/**
 *
 *
 * @author Jay Wu
 */
object ApplicationContext {

    private val log = LogFactory.get()

    lateinit var config: ServerConfig

    lateinit var ioc: NutIoc

    lateinit var zkClient: CuratorFramework

    fun initialize() {
        initIoc()
        initServerConfig()
        initZkClient()
    }

    fun initIoc() {
        ioc = NutIoc(
            ComboIocLoader(
                "*org.nutz.ioc.loader.json.JsonLoader", "config.js",
                "*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
                "com.asiainfo.iia.server"
            )
        );
    }

    fun initServerConfig() {
        val logMessage = LogMessage("ApplicationContext", "initServerConfig")
            .processing()

        log.info(logMessage.toString())

        config = ioc.get(ServerConfig::class.java)

        log.info(logMessage.success().toString())
    }

    fun initZkClient() {
        val logMessage = LogMessage("ApplicationContext", "initZKClient")
            .append("host", config.zkNode)
            .processing()

        log.info(logMessage.toString())

        val nodeRefreshIntervalMs = config.nodeSessionTimeoutMs
        zkClient = CuratorFrameworkFactory.builder()
            .connectString(config.zkNode)
            .connectionTimeoutMs(nodeRefreshIntervalMs - 1000)
            .sessionTimeoutMs(nodeRefreshIntervalMs)
            .retryPolicy(ExponentialBackoffRetry(nodeRefreshIntervalMs, 10))
            .namespace(ApplicationContext.config.namespace)
            .build()

        zkClient.start()

        log.info(logMessage.success().toString())
    }
}