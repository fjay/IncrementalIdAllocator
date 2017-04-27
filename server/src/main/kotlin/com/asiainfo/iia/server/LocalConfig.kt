package com.asiainfo.iia.server

import com.asiainfo.common.util.config.Configs
import com.asiainfo.common.util.config.Configurable

/**
 * @author Jay Wu
 */
class LocalConfig : Configurable {

    lateinit var id: String

    lateinit var dataSource: DataSource

    class DataSource {
        lateinit var url: String
        lateinit var username: String
        lateinit var password: String
        var initialSize: Int = 1
        var maxActive: Int = 1
        var minIdle: Int = 1
        var minEvictableIdleTimeMillis: Long = 1
        lateinit var validationQuery: String
    }

    override fun getKey(): String {
        return this.javaClass.name
    }

    companion object {
        fun get(): LocalConfig {
            return Configs.getInstance().getOrLoadWithFilePath(
                    LocalConfig::class.java,
                    "config.js"
            )
        }
    }
}