package com.asiainfo.iia.client;

import com.asiainfo.common.ServiceException;

/**
 * @author Jay Wu
 */
public class IdAllocatorClientException extends ServiceException {
    public IdAllocatorClientException(String message) {
        super(message);
    }

    public IdAllocatorClientException(String code, String message) {
        super(code, message);
    }

    public IdAllocatorClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdAllocatorClientException(Throwable cause) {
        super(cause);
    }
}