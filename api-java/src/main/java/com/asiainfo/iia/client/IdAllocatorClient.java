package com.asiainfo.iia.client;

import com.asiainfo.common.util.*;
import com.asiainfo.common.util.http.HttpRequester;
import com.asiainfo.common.util.http.Response;
import com.asiainfo.common.util.log.Log;
import com.asiainfo.common.util.log.LogMessage;
import com.asiainfo.common.util.log.Logs;
import com.asiainfo.common.util.policy.RoundRobinPolicy;
import com.asiainfo.iia.common.AllocResponse;
import com.asiainfo.iia.common.ServerNodeRoute;

import java.util.List;

/**
 * @author Jay Wu
 */
public class IdAllocatorClient {

    private Log log = Logs.get();

    private ServerNodeRoute route = new ServerNodeRoute();
    private RoundRobinPolicy<String> roundRobinPolicy = new RoundRobinPolicy<String>();

    private List<String> hosts;
    private int requestTimeoutMs;

    public IdAllocatorClient(List<String> hosts) {
        this(hosts, 5000);
    }

    public IdAllocatorClient(List<String> hosts, int requestTimeoutMs) {
        this.hosts = hosts;
        this.requestTimeoutMs = requestTimeoutMs;

        initServerNodeRoute();
    }

    public Long alloc(String type) {
        try {
            return tryAllocOnce(type);
        } catch (Exception e) {
            log.debug(new LogMessage("IdAllocatorClient", "firstAlloc")
                    .append("type", type)
                    .fail());

            ThreadUtil.safeSleep(route.getNodeSessionTimeoutMs() + 1000);

            initServerNodeRoute();

            return tryAllocOnce(type);
        }
    }

    public List<String> getHosts() {
        return hosts;
    }

    public IdAllocatorClient setHosts(List<String> hosts) {
        this.hosts = hosts;
        return this;
    }

    private Long tryAllocOnce(String type) {
        int key = Math.abs(type.hashCode()) % route.getMaxKeySize();
        String host = route.getServerNode(key);
        Response response = HttpRequester.create()
                .setUrl("http://" + host + "/id/alloc")
                .setTimeout(requestTimeoutMs)
                .putUrlParam("key", key)
                .putUrlParam("version", route.getVersion())
                .get();

        AllocResponse allocResponse = handleResponse(response, AllocResponse.class);

        if (allocResponse != null) {
            if (allocResponse.getRoute() != null) {
                route = allocResponse.getRoute();

                log.info(new LogMessage("IdAllocatorClient", "initServerNodeRoute")
                        .append("nodes", route.getServerNodeAndKeys().keySet())
                        .success());
            }

            return allocResponse.getId();
        }


        ClientErrorCode.REQUEST_ERROR.error("Alloc id fail");
        return null;
    }

    private synchronized void initServerNodeRoute() {
        roundRobinPolicy.request(hosts, new Callback<String>() {
            @Override
            public void invoke(String host) {
                Response response = HttpRequester.create()
                        .setUrl("http://" + host + "/id/route")
                        .setTimeout(requestTimeoutMs)
                        .putUrlParam("version", route.getVersion())
                        .get();

                ServerNodeRoute newRoute = handleResponse(response, ServerNodeRoute.class);

                if (newRoute != null) {
                    route = newRoute;

                    log.info(new LogMessage("IdAllocatorClient", "initServerNodeRoute")
                            .append("nodes", route.getServerNodeAndKeys().keySet())
                            .success());
                }
            }
        });
    }

    private <T> T handleResponse(Response response, Class<T> clazz) {
        return JsonUtil.fromJson(clazz, handleResponse(response));
    }

    private <T> T handleResponse(Response response, TypeReference<T> typeRef) {
        return JsonUtil.fromJson(typeRef.getType(), handleResponse(response));
    }

    private String handleResponse(Response response) {
        ClientErrorCode.NET_WORK_ERROR.assertNotEmpty(response);

        String content = response.getContent();

        if (!response.isOK()) {
            ClientErrorCode.NET_WORK_ERROR.assertNotEmpty(content);
            SimpleMap error = JsonUtil.fromJson(SimpleMap.class, content);
            throw new IdAllocatorClientException(error.getString("errorCode"), error.getString("errorMessage"));
        } else {
            if (StringUtil.isEmpty(content)) {
                return null;
            }

            return content;

        }
    }
}
