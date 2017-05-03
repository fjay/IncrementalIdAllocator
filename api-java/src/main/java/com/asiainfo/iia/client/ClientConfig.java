package com.asiainfo.iia.client;

import java.io.Closeable;
import java.util.List;

/**
 * @author Jay Wu
 */
public interface ClientConfig extends Closeable {

    List<String> getServerHosts();

    int getRequestTimeoutMs();
}
