package com.asiainfo.iia.client;

import java.util.List;

/**
 * @author Jay Wu
 */
public class SimpleClientConfig implements ClientConfig {

    private List<String> serverHosts;

    private int requestTimeoutMs = 5000;

    @Override
    public List<String> getServerHosts() {
        return serverHosts;
    }

    public SimpleClientConfig setServerHosts(List<String> serverHosts) {
        this.serverHosts = serverHosts;
        return this;
    }

    @Override
    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public SimpleClientConfig setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
        return this;
    }
}