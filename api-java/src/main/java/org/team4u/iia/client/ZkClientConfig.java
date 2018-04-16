package org.team4u.iia.client;

import com.asiainfo.common.util.CollectionUtil;
import com.asiainfo.common.util.Function;
import org.apache.curator.framework.CuratorFramework;

import java.io.IOException;
import java.util.List;

/**
 * @author Jay Wu
 */
public class ZkClientConfig implements ClientConfig {

    private static final String ONLINE_SERVER_NODE_PATH = "/online_server_nodes";

    private CuratorFramework zkClient;

    private int requestTimeoutMs = DEFAULT_REQUEST_TIMEOUT_MS;

    public ZkClientConfig(CuratorFramework zkClient) {
        this.zkClient = zkClient;
    }

    private List<String> loadOnlineServerHosts() {
        try {
            return CollectionUtil.collect(zkClient.getChildren().forPath(ONLINE_SERVER_NODE_PATH),
                    new Function<String, String>() {
                        @Override
                        public String invoke(String path) {
                            String[] ipAndPort = CollectionUtil.last(path.split("/")).split(":");
                            return ipAndPort[0] + ":" + ipAndPort[1];
                        }
                    });
        } catch (Exception e) {
            throw new IdAllocatorClientException(ClientErrorCode.REQUEST_ERROR.name(), e);
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public List<String> getServerHosts() {
        return loadOnlineServerHosts();
    }

    @Override
    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public ZkClientConfig setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
        return this;
    }
}