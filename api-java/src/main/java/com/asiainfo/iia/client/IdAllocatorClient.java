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

    private List<String> hosts;

    private ServerNodeRoute route = new ServerNodeRoute();

    private RoundRobinPolicy<String> roundRobinPolicy = new RoundRobinPolicy<String>();

    public IdAllocatorClient(List<String> hosts) {
        this.hosts = hosts;

        initServerNodeRoute();
    }

    public Long alloc(String key) {
        try {
            return tryAllocOnce(key);
        } catch (Exception e) {
            log.debug(new LogMessage("IdAllocatorClient", "firstAlloc")
                    .append("key", key)
                    .fail());

            ThreadUtil.safeSleep(3500);
            initServerNodeRoute();
            return tryAllocOnce(key);
        }
    }

    private Long tryAllocOnce(String key) {
        int intKey = Math.abs(key.hashCode()) % route.getMaxNodeSize();
        String host = route.getServerNode(intKey);
        Response response = HttpRequester.create()
                .setUrl("http://" + host + "/id/alloc")
                .putUrlParam("key", intKey)
                .putUrlParam("version", route.getVersion())
                .get();

        AllocResponse allocResponse = handleResponse(response, AllocResponse.class);

        if (allocResponse != null) {
            if (allocResponse.getRoute() != null) {
                route = allocResponse.getRoute();
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

    public List<String> getHosts() {
        return hosts;
    }

    public IdAllocatorClient setHosts(List<String> hosts) {
        this.hosts = hosts;
        return this;
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
