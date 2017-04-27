package com.asiainfo.iia.server.api.web;

import com.asiainfo.common.kotlin.extension.isNotEmpty
import com.asiainfo.common.util.log.Logs
import com.asiainfo.iia.server.ApplicationContext
import com.asiainfo.iia.server.ApplicationErrorCode
import com.asiainfo.iia.server.id.IdAllocatorManager
import com.asiainfo.iia.server.node.ServerNodeRouter
import org.team4u.fhs.web.handler.method.annotation.Controller
import org.team4u.fhs.web.handler.method.annotation.RequestMapping
import org.team4u.fhs.web.handler.method.annotation.RequestParam
import org.team4u.fhs.web.handler.method.annotation.ResponseView

/**
 *
 * @author Jay Wu
 */
@Controller("/id")
class IdAllocatorController {

    private val log = Logs.get()

    private val serverNodeRouter = ApplicationContext.get(ServerNodeRouter::class.java)

    @RequestMapping
    @ResponseView(success = "json")
    fun alloc(@RequestParam("key") key: Int?, @RequestParam("version") version: String?): Map<String, Any?> {
        ApplicationErrorCode.ILLEGAL_PARAM.isNotEmpty(key, "key")
        ApplicationErrorCode.ILLEGAL_PARAM.isNotEmpty(key, "version")

        val id = IdAllocatorManager.alloc(key!!)
        val serverNodeRoute = if (serverNodeRouter.serverNodeRoute.version != version) {
            serverNodeRouter.serverNodeRoute
        } else {
            null
        }

        return mapOf<String, Any?>(
                "id" to id,
                "serverNodeRoute" to serverNodeRoute
        )
    }
}