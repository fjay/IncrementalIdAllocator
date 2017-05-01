package com.asiainfo.iia.server.model

/**
 * @author Jay Wu
 */
data class ServerNode(val ip: String, val port: Int) {

    override fun toString(): String {
        return "$ip:$port"
    }
}