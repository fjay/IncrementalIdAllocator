package org.team4u.iia.client;

import java.io.IOException;
import java.util.List;

/**
 * @author Jay Wu
 */
public class SimpleClientConfig implements ClientConfig {

    private List<String> serverHosts;

    private int requestTimeoutMs = DEFAULT_REQUEST_TIMEOUT_MS;

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

    @Override
    public void close() throws IOException {
    }
}