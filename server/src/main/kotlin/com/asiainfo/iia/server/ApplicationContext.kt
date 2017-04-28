package com.asiainfo.iia.server

import com.alibaba.druid.pool.DruidDataSource
import com.asiainfo.common.kotlin.extension.isNotEmpty
import com.asiainfo.common.util.ServiceProvider
import com.asiainfo.common.util.log.LogMessage
import com.asiainfo.common.util.log.Logs
import com.asiainfo.dao.core.SimpleDao
import com.asiainfo.iia.server.model.ServerNode
import com.asiainfo.iia.server.node.OnlineServerNodeManager
import com.asiainfo.iia.server.node.ServerNodeRouter
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import javax.sql.DataSource

/**
 *
 *
 * @author Jay Wu
 */
object ApplicationContext : ServiceProvider() {

    private val log = Logs.get()

    lateinit var currentServerNode: ServerNode

    fun initialize() {
        initDao()
        initCurrentServerNode()
        initZKClient()
        initManager()
    }

    fun initDao() {
        register(DataSource::class.java, object : ServiceProvider.Factory<DataSource>() {
            override fun create(): DataSource {
                val logMessage = LogMessage("ApplicationContext", "initDao")
                        .append("url", LocalConfig.get().dataSource.url)
                        .processing()

                log.info(logMessage)

                val dataSource = DruidDataSource().apply {
                    url = LocalConfig.get().dataSource.url
                    username = LocalConfig.get().dataSource.username
                    password = LocalConfig.get().dataSource.password
                    initialSize = LocalConfig.get().dataSource.initialSize
                    maxActive = LocalConfig.get().dataSource.maxActive
                    minIdle = LocalConfig.get().dataSource.minIdle
                    minEvictableIdleTimeMillis = LocalConfig.get().dataSource.minEvictableIdleTimeMillis
                    isDefaultAutoCommit = false
                    isTestWhileIdle = true
                    isTestOnBorrow = true
                    validationQuery = LocalConfig.get().dataSource.validationQuery
                    validationQueryTimeout = 5
                    timeBetweenEvictionRunsMillis = 30000
                }

                log.info(logMessage.success())
                return dataSource
            }
        })

        register(SimpleDao::class.java, object : ServiceProvider.Factory<SimpleDao>() {
            override fun create(): SimpleDao {
                return SimpleDao(get(DataSource::class.java))
            }
        })
    }

    fun initZKClient() {
        register(CuratorFramework::class.java, object : ServiceProvider.Factory<CuratorFramework>() {
            override fun create(): CuratorFramework {
                val logMessage = LogMessage("ApplicationContext", "initZKClient")
                        .append("host", DbConfig.get().zkNode.value())
                        .processing()

                log.info(logMessage)

                val nodeRefreshIntervalMs = DbConfig.get().nodeSessionTimeoutMs.value.toInt()
                val zkClient = CuratorFrameworkFactory.builder()
                        .connectString(DbConfig.get().zkNode.value())
                        .connectionTimeoutMs(nodeRefreshIntervalMs - 1000)
                        .sessionTimeoutMs(nodeRefreshIntervalMs)
                        .retryPolicy(ExponentialBackoffRetry(3000, 10))
                        .namespace(Constant.APPLICATION_ID)
                        .build()

                zkClient.start()

                log.info(logMessage.success())
                return zkClient
            }
        })
    }

    fun initCurrentServerNode() {
        val item = DbConfig.get().iiaNodes.find {
            it.configItemId == LocalConfig.get().nodeId
        }

        ApplicationErrorCode.ILLEGAL_PARAM.isNotEmpty(item,
                "Can't find server node in db(nodeId=${LocalConfig.get().nodeId})")

        val (ip, port) = item!!.value.split(":")
        currentServerNode = ServerNode(item.configItemId, ip, port.toInt())
    }

    fun initManager() {
        register(OnlineServerNodeManager(get(CuratorFramework::class.java)))
        register(ServerNodeRouter())
    }
}