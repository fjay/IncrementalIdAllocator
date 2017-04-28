package com.asiainfo.iia.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.asiainfo.common.util.MapUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author Jay Wu
 */
public class ServerNodeRoute implements Serializable {

    @JSONField(serialize = false)
    private transient Map<Integer, String> keyAndServerNodes = new HashMap<Integer, String>();

    private Map<String, Set<Integer>> serverNodeAndKeys = new HashMap<String, Set<Integer>>();

    private String version;

    private int nodeSessionTimeoutMs;

    public int getMaxSegmentSize() {
        return keyAndServerNodes.size();
    }

    public String getServerNode(Integer key) {
        return keyAndServerNodes.get(key);
    }

    public Map<String, Set<Integer>> getServerNodeAndKeys() {
        return serverNodeAndKeys;
    }

    public ServerNodeRoute setServerNodeAndKeys(Map<String, Set<Integer>> serverNodeAndKeys) {
        this.serverNodeAndKeys = serverNodeAndKeys;

        keyAndServerNodes.clear();
        for (Map.Entry<String, Set<Integer>> sk : serverNodeAndKeys.entrySet()) {
            for (Integer key : sk.getValue()) {
                keyAndServerNodes.put(key, sk.getKey());
            }
        }

        return this;
    }

    public Map<Integer, String> getKeyAndServerNodes() {
        return keyAndServerNodes;
    }

    public ServerNodeRoute setKeyAndServerNodes(Map<Integer, String> keyAndServerNodes) {
        this.keyAndServerNodes = keyAndServerNodes;

        serverNodeAndKeys.clear();
        for (Map.Entry<Integer, String> sk : keyAndServerNodes.entrySet()) {
            Set<Integer> keys = MapUtil.putIfAbsent(serverNodeAndKeys, sk.getValue(), new Callable<Set<Integer>>() {
                @Override
                public Set<Integer> call() throws Exception {
                    return new HashSet<Integer>();
                }
            });

            keys.add(sk.getKey());
        }

        return this;
    }

    public String getVersion() {
        return version;
    }

    public ServerNodeRoute setVersion(String version) {
        this.version = version;
        return this;
    }

    public int getNodeSessionTimeoutMs() {
        return nodeSessionTimeoutMs;
    }

    public ServerNodeRoute setNodeSessionTimeoutMs(int nodeSessionTimeoutMs) {
        this.nodeSessionTimeoutMs = nodeSessionTimeoutMs;
        return this;
    }
}