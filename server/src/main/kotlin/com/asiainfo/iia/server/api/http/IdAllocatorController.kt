package com.asiainfo.iia.server.api.http;

import com.asiainfo.common.kotlin.extension.isNotEmpty
import com.asiainfo.common.util.log.LogMessage
import com.asiainfo.common.util.log.Logs
import com.asiainfo.iia.common.model.AllocResponse
import com.asiainfo.iia.common.model.ServerNodeRoute
import com.asiainfo.iia.server.ApplicationErrorCode
import com.asiainfo.iia.server.id.IdAllocatorManager
import com.asiainfo.iia.server.node.ServerNodeRouter
import org.nutz.ioc.loader.annotation.Inject
import org.nutz.ioc.loader.annotation.IocBean
import org.team4u.fhs.web.handler.method.annotation.Controller
import org.team4u.fhs.web.handler.method.annotation.RequestMapping
import org.team4u.fhs.web.handler.method.annotation.RequestParam
import org.team4u.fhs.web.handler.method.annotation.ResponseView

/**
 *
 * @author Jay Wu
 */
@IocBean
@Controller("/id")
class IdAllocatorController {

    private val log = Logs.get()

    @Inject
    private lateinit var serverNodeRouter: ServerNodeRouter

    @RequestMapping
    @ResponseView(success = "json")
    fun alloc(@RequestParam("key") key: Int?, @RequestParam("version") version: String?): AllocResponse {
        ApplicationErrorCode.ILLEGAL_PARAM.isNotEmpty(key, "key")

        if (log.isDebugEnabled) {
            log.debug(LogMessage("IdAllocatorController", "alloc")
                    .processing()
                    .append("key", key)
                    .append("version", version))
        }

        val id = IdAllocatorManager.alloc(key!!)

        if (log.isDebugEnabled) {
            log.debug(LogMessage("IdAllocatorController", "alloc")
                    .success()
                    .append("key", key)
                    .append("version", version)
                    .append("id", id))
        }

        return AllocResponse().apply {
            this.id = id
            this.route = route(version)
        }
    }

    @RequestMapping
    @ResponseView(success = "json")
    fun route(@RequestParam("version") version: String?): ServerNodeRoute? {
        return if (serverNodeRouter.serverNodeRoute.version != version) {
            serverNodeRouter.serverNodeRoute
        } else {
            null
        }
    }
}