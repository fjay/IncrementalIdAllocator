package org.team4u.iia.server.api.http;

import cn.hutool.log.LogFactory
import org.team4u.common.kotlin.extension.isNotEmpty
import org.team4u.fhs.web.handler.method.annotation.Controller
import org.team4u.fhs.web.handler.method.annotation.RequestMapping
import org.team4u.fhs.web.handler.method.annotation.RequestParam
import org.team4u.fhs.web.handler.method.annotation.ResponseView
import org.team4u.iia.common.model.AllocResponse
import org.team4u.iia.common.model.ServerNodeRoute
import org.team4u.iia.server.ApplicationErrorCode
import org.team4u.iia.server.id.IdAllocatorManager
import org.team4u.iia.server.node.ServerNodeRouter
import org.team4u.kit.core.log.LogMessage

/**
 *
 * @author Jay Wu
 */
@IocBean
@Controller("/id")
class IdAllocatorController {

    private val log = LogFactory.get()

    @Inject
    private lateinit var serverNodeRouter: ServerNodeRouter

    @RequestMapping
    @ResponseView(success = "json")
    fun alloc(@RequestParam("key") key: Int?, @RequestParam("version") version: String?): AllocResponse {
        ApplicationErrorCode.ILLEGAL_PARAM.isNotEmpty(key, "key")

        if (log.isDebugEnabled) {
            log.debug(
                LogMessage("IdAllocatorController", "alloc")
                    .processing()
                    .append("key", key)
                    .append("version", version)
                    .toString()
            )
        }

        val id = IdAllocatorManager.alloc(key!!)

        if (log.isDebugEnabled) {
            log.debug(
                LogMessage("IdAllocatorController", "alloc")
                    .success()
                    .append("key", key)
                    .append("version", version)
                    .append("id", id)
                    .toString()
            )
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