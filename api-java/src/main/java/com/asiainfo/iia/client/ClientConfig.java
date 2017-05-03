package com.asiainfo.iia.client;

import java.util.List;

/**
 * @author Jay Wu
 */
public interface ClientConfig {

    List<String> getServerHosts();

    int getRequestTimeoutMs();
}
