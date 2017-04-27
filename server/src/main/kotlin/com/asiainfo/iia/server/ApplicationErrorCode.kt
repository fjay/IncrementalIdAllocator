package com.asiainfo.iia.server

import com.asiainfo.common.ServiceException
import com.asiainfo.common.util.ErrorCode

enum class ApplicationErrorCode(private val code: String,
                                private val message: String) : ErrorCode {
    ILLEGAL_PARAM("20000", "Illegal Param:%s"),

    SYS_ERROR("-1", "Internal Server Error");

    override fun getMessage(): String {
        return message
    }

    override fun getCode(): String {
        return code
    }

    override fun createException(code: String, message: String): ServiceException {
        return ServiceException(code, message)
    }
}