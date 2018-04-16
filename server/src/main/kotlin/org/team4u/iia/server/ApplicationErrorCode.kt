package org.team4u.iia.server

import org.team4u.kit.core.error.ErrorCode

class ApplicationErrorCode(
    private val code: String,
    private val message: String
) : ErrorCode("app", code, message) {

    override fun getMessage(): String {
        return message
    }

    override fun getCode(): String {
        return code
    }

    companion object {

        val INIT_REMOTE_VALUE_TIMEOUT = ApplicationErrorCode("30000", "Init remote value timeout")

        val ILLEGAL_PARAM = ApplicationErrorCode("20000", "Illegal Param:%s")

        val SYS_ERROR = ApplicationErrorCode("-1", "Internal Server Error")
    }
}