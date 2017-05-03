package com.asiainfo.iia.client;

import com.asiainfo.common.util.*;
import com.asiainfo.common.util.http.HttpRequester;
import com.asiainfo.common.util.http.Response;
import com.asiainfo.common.util.log.Log;
import com.asiainfo.common.util.log.LogMessage;
import com.asiainfo.common.util.log.Logs;
import com.asiainfo.common.util.policy.RoundRobinPolicy;
import com.asiainfo.iia.common.model.AllocResponse;
import com.asiainfo.iia.common.model.ServerNodeRoute;

/**
 * @author Jay Wu
 */
public class IdAllocatorClient {

    private Log log = Logs.get();

    private ServerNodeRoute route = new ServerNodeRoute();
    private RoundRobinPolicy<String> roundRobinPolicy = new RoundRobinPolicy<String>();

    private ClientConfig clientConfig;

    public IdAllocatorClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;

        initServerNodeRoute();
    }

    public Long alloc(String type) {
        try {
            return tryAllocOnce(type);
        } catch (Exception e) {
            log.debug(new LogMessage("IdAllocatorClient", "tryAllocOnce")
                    .append("type", type)
                    .fail(e.getMessage()));

            ThreadUtil.safeSleep(route.getNodeSessionTimeoutMs() + 1000);

            initServerNodeRoute();
            return tryAllocOnce(type);
        }
    }

    private Long tryAllocOnce(String type) {
        int key = Math.abs(type.hashCode()) % route.getMaxSegmentSize();
        String host = route.getServerNode(key);
        Response response = HttpRequester.create()
                .setUrl("http://" + host + "/id/alloc")
                .setTimeout(clientConfig.getRequestTimeoutMs())
                .putUrlParam("key", key)
                .putUrlParam("version", route.getVersion())
                .get();

        AllocResponse allocResponse = handleResponse(response, AllocResponse.class);

        if (allocResponse != null) {
            setRoute(allocResponse.getRoute());
            return allocResponse.getId();
        }

        ClientErrorCode.REQUEST_ERROR.error("Alloc id fail");
        return null;
    }

    private synchronized void initServerNodeRoute() {
        roundRobinPolicy.request(clientConfig.getServerHosts(), new Callback<String>() {
            @Override
            public void invoke(String host) {
                Response response = HttpRequester.create()
                        .setUrl("http://" + host + "/id/route")
                        .setTimeout(clientConfig.getRequestTimeoutMs())
                        .putUrlParam("version", route.getVersion())
                        .get();

                setRoute(handleResponse(response, ServerNodeRoute.class));
            }
        });
    }

    private synchronized void setRoute(ServerNodeRoute route) {
        if (route == null || route.getMaxSegmentSize() == 0) {
            return;
        }

        this.route = route;

        log.info(new LogMessage("IdAllocatorClient", "setRoute")
                .append("nodes", route.getServerNodeAndKeys().keySet())
                .success());
    }

    private <T> T handleResponse(Response response, Class<T> clazz) {
        return JsonUtil.fromJson(clazz, handleResponse(response));
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