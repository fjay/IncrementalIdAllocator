@file:kotlin.jvm.JvmName("ErrorCodeKt")

package com.asiainfo.common.kotlin.extension

import com.asiainfo.common.ServiceException
import com.asiainfo.common.util.AssertUtil
import com.asiainfo.common.util.ErrorCode

fun ErrorCode.isTrue(cnd: Boolean, vararg fmt: Any?) {
    AssertUtil.isTrue(this, cnd, *fmt)
}

fun ErrorCode.error(vararg fmt: Any?) {
    AssertUtil.error(this, *fmt)
}

fun ErrorCode.isFalse(cnd: Boolean, vararg fmt: Any?) {
    AssertUtil.isFalse(this, cnd, *fmt)
}

fun ErrorCode.isEmpty(cnd: Any?, vararg fmt: Any?) {
    AssertUtil.isEmpty(this, cnd, *fmt)
}

fun ErrorCode.isNotEmpty(cnd: Any?, vararg fmt: Any?) {
    AssertUtil.isNotEmpty(this, cnd, *fmt)
}

fun ErrorCode.isUnique(obj: Collection<*>?, vararg fmt: Any?) {
    AssertUtil.isUnique(this, obj, *fmt)
}

fun ErrorCode.validate(obj: Any?) {
    AssertUtil.validate(this, obj)
}

fun ErrorCode.exception(vararg fmt: Any?): ServiceException {
    return this.createException(this.code, if (fmt.isNotEmpty()) {
        String.format(this.message, *fmt)
    } else {
        this.message;
    })
}