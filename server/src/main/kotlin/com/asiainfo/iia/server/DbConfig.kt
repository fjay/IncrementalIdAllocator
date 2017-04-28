package com.asiainfo.iia.server

import com.asiainfo.common.util.Watchable
import com.asiainfo.conf.client.loader.DbConfigItemLoader
import com.asiainfo.conf.client.loader.InMemoryConfigItemLoader
import com.asiainfo.conf.common.entity.ConfigItem
import com.asiainfo.dao.core.SimpleDao
import java.util.concurrent.TimeUnit

/**
 * @author Jay Wu
 */
class DbConfig {

    lateinit var iiaNodes: List<ConfigItem>

    lateinit var zkNode: ConfigItem

    lateinit var maxIiaNodeSize: ConfigItem

    lateinit var idAllocatorPoolSize: ConfigItem

    lateinit var nodeSessionTimeoutMs: ConfigItem

    companion object {
        private val client = InMemoryConfigItemLoader(
                DbConfigItemLoader(Constant.APPLICATION_ID, "DEFAULT",
                        ApplicationContext.get(SimpleDao::class.java).getDataSource())
        )

        init {
            client.watch(TimeUnit.SECONDS, 30L, Watchable.ChangedCallback { _, _ ->
            })
        }

        fun get(): DbConfig {
            return client.load(DbConfig::class.java);
        }
    }
}