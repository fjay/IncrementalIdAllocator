package com.asiainfo.iia.client;

import java.io.Closeable;
import java.util.List;

/**
 * @author Jay Wu
 */
public interface ClientConfig extends Closeable {

    int DEFAULT_REQUEST_TIMEOUT_MS = 5000;

    List<String> getServerHosts();

    int getRequestTimeoutMs();
}
