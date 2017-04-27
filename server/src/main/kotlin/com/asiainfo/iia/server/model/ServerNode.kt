package com.asiainfo.iia.server.model

/**
 * @author Jay Wu
 */
data class ServerNode(val id: String, val ip: String, val port: Int) {

    fun ipAndPort() = "$ip:$port"

    override fun toString(): String {
        return "$id:${ipAndPort()}"
    }
}