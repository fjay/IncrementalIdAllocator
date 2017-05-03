package com.asiainfo.iia.client;

import com.asiainfo.common.util.CollectionUtil;
import com.asiainfo.common.util.Function;
import com.asiainfo.common.util.IoUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * @author Jay Wu
 */
public class ZkClientConfig implements ClientConfig, Closeable {

    private static final String ONLINE_SERVER_NODE_PATH = "/onlineServerNodes";

    private PathChildrenCache onlinePathCache;

    private int requestTimeoutMs = 5000;

    public ZkClientConfig(CuratorFramework zkClient) {
        onlinePathCache = new PathChildrenCache(zkClient, ONLINE_SERVER_NODE_PATH, false);
        try {
            onlinePathCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            throw new IdAllocatorClientException(ClientErrorCode.REQUEST_ERROR.name(), e);
        }
    }

    private List<String> loadOnlineServerHosts() {
        return CollectionUtil.collect(onlinePathCache.getCurrentData(), new Function<ChildData, String>() {
            @Override
            public String invoke(ChildData data) {
                String[] ipAndPort = CollectionUtil.last(data.getPath().split("/")).split(":");
                return ipAndPort[0] + ":" + ipAndPort[1];
            }
        });
    }

    @Override
    public void close() throws IOException {
        IoUtil.safeClose(onlinePathCache);
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