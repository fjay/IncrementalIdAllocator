package com.asiainfo.iia.server

import org.team4u.kit.core.error.ErrorCode

enum class ApplicationErrorCode(private val code: String,
                                private val message: String) : ErrorCode {
    ILLEGAL_PARAM("20000", "Illegal Param:%s"),
    INIT_REMOTE_VALUE_TIMEOUT("30000", "Init remote value timeout"),

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