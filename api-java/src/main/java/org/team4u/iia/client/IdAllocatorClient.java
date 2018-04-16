package org.team4u.iia.client;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import org.team4u.iia.common.model.AllocResponse;
import org.team4u.iia.common.model.ServerNodeRoute;
import org.team4u.kit.core.action.Callback;
import org.team4u.kit.core.lb.RoundRobinPolicy;
import org.team4u.kit.core.log.LogMessage;

import java.io.Closeable;

/**
 * @author Jay Wu
 */
public class IdAllocatorClient implements Closeable {

    private Log log = LogFactory.get();

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
                    .fail(e.getMessage())
                    .toString());

            ThreadUtil.safeSleep(route.getNodeSessionTimeoutMs() + 1000);

            initServerNodeRoute();
            return tryAllocOnce(type);
        }
    }

    private Long tryAllocOnce(String type) {
        int key = Math.abs(type.hashCode()) % route.getMaxSegmentSize();
        String host = route.getServerNode(key);
        HttpResponse response = HttpRequest.get("http://" + host + "/id/alloc")
                .timeout(clientConfig.getRequestTimeoutMs())
                .form("key", key)
                .form("version", route.getVersion())
                .execute();

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
                HttpResponse response = HttpRequest.get("http://" + host + "/id/route")
                        .timeout(clientConfig.getRequestTimeoutMs())
                        .form("version", route.getVersion())
                        .execute();

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
                .success()
                .toString());
    }

    private <T> T handleResponse(HttpResponse response, Class<T> clazz) {
        return JSON.parseObject(handleResponse(response), clazz);
    }

    private String handleResponse(HttpResponse response) {
        ClientErrorCode.NET_WORK_ERROR.assertNotEmpty(response);

        String content = response.body();

        if (response.getStatus() != HttpStatus.HTTP_OK) {
            ClientErrorCode.NET_WORK_ERROR.assertNotEmpty(content);
            Dict error = JSON.parseObject(content, Dict.class);
            throw new IdAllocatorClientException(error.getStr("errorCode"), error.getStr("errorMessage"));
        } else {
            if (StrUtil.isEmpty(content)) {
                return null;
            }

            return content;
        }
    }

    @Override
    public void close() {
        IoUtil.close(clientConfig);
    }
}