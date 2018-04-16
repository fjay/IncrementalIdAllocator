package org.team4u.iia.server.config

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.team4u.fhs.server.HttpServer
import org.team4u.fhs.server.impl.netty.NettyHttpServer
import org.team4u.fhs.server.impl.netty.NettyHttpServerConfig
import org.team4u.fhs.web.DefaultHttpRouter
import org.team4u.fhs.web.RoutingContext
import org.team4u.fhs.web.ext.view.FastJsonViewResolver
import org.team4u.fhs.web.handler.method.RequestMappingHandlerMapping
import org.team4u.fhs.web.handler.method.annotation.Controller

/**
 * @author Jay Wu
 */
@Configuration
open class BeanConfig : ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    @Bean
    open fun server(): HttpServer {
        val router = DefaultHttpRouter()
            .addLastHandlerMapping(RequestMappingHandlerMapping().apply {
                applicationContext.getBeansWithAnnotation(Controller::class.java).values.forEach {
                    addController(it)
                }
            })
            .addLastViewResolver(FastJsonViewResolver())

        return NettyHttpServer(NettyHttpServerConfig())
            .onRequest { request, response ->
                router.doRoute(
                    RoutingContext()
                        .setRequest(request)
                        .setResponse(response)
                )
            }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}