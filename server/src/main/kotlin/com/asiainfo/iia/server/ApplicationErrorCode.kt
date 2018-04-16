package com.asiainfo.iia.server

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

        val ILLEGAL_PARAM = ApplicationErrorCode("20000", "Illegal Param:%s")

        val SYS_ERROR = ApplicationErrorCode("-1", "Internal Server Error")
    }
}