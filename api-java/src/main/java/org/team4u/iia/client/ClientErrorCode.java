package org.team4u.iia.client;


import org.team4u.kit.core.util.ValidatorUtil;
import org.team4u.kit.core.util.ValueUtil;

import java.util.Collection;

public enum ClientErrorCode {

    ILLEGAL_PARAM("20001", "非法的请求参数:%s"),
    REQUEST_ERROR("20002", "请求失败"),
    NET_WORK_ERROR("90001", "网络连接异常");

    private String message;
    private String code;

    ClientErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void error(Object... fmt) {
        throw new IdAllocatorClientException(code, fmt != null ? String.format(message, fmt) : message);
    }

    public void assertTrue(boolean cnd, Object... fmt) {
        if (!cnd) {
            error(fmt);
        }
    }

    public void assertFalse(boolean cnd, Object... fmt) {
        assertTrue(!cnd, fmt);
    }

    public void assertEmpty(Object cnd, Object... fmt) {
        if (!ValueUtil.isEmpty(cnd)) {
            error(fmt);
        }
    }

    public void assertNotEmpty(Object cnd, Object... fmt) {
        if (ValueUtil.isEmpty(cnd)) {
            error(fmt);
        }
    }

    public void assertUnique(Collection collection, Object... fmt) {
        assertNotEmpty(collection, fmt);
        assertTrue(collection.size() == 1, fmt);
    }

    public void validate(Object obj) {
        ValidatorUtil.Result result = ValidatorUtil.validate(obj);
        if (result.hasError()) {
            error(result.firstMessage());
        }
    }
}