package org.team4u.iia.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties


@SpringBootApplication
@EnableConfigurationProperties
open class AppServer {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(AppServer::class.java).run(*args)
        }
    }
}