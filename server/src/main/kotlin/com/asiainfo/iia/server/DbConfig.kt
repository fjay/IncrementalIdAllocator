package com.asiainfo.iia.server

import com.asiainfo.common.util.ServiceProvider
import com.asiainfo.conf.client.loader.DbConfigItemLoader
import com.asiainfo.conf.client.loader.InMemoryConfigItemLoader
import com.asiainfo.conf.common.entity.ConfigItem
import com.asiainfo.dao.core.SimpleDao
import java.util.concurrent.TimeUnit

/**
 * @author Jay Wu
 */
class DbConfig {

    lateinit var serverNodes: List<ConfigItem>

    lateinit var zkNode: ConfigItem

    lateinit var maxServerNodeSize: ConfigItem

    lateinit var IdAllocatorPoolSize: ConfigItem

    private val client = InMemoryConfigItemLoader(
            DbConfigItemLoader(Constant.APPLICATION_ID, "DEFAULT",
                    ApplicationContext.get(SimpleDao::class.java).getDataSource())
    )

    init {
        client.watch(TimeUnit.SECONDS, 30L, null)
    }

    companion object {
        fun get(): DbConfig {
            return ServiceProvider.getInstance().get(DbConfig::class.java)
        }
    }
}