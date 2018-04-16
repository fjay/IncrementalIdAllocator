package org.team4u.iia.server

import cn.hutool.log.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import org.team4u.fhs.server.HttpServer

@Component
class InitListener : ApplicationListener<ContextRefreshedEvent> {

    private val log = LogFactory.get()

    @Autowired
    private lateinit var httpServer: HttpServer

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (event.applicationContext.parent != null) {
            return
        }

        // TODO
        httpServer.listen(7001)
    }
}