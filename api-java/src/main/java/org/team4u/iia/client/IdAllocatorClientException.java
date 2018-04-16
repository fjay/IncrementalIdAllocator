package org.team4u.iia.client;


import org.team4u.kit.core.error.ServiceException;

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