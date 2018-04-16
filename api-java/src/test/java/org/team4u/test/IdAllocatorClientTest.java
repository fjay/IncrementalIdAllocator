package org.team4u.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.team4u.iia.client.IdAllocatorClient;
import org.team4u.iia.client.ZkClientConfig;
import org.team4u.kit.core.debug.Benchmark;

/**
 * @author Jay Wu
 */
public class IdAllocatorClientTest {

    private IdAllocatorClient client = new IdAllocatorClient(new ZkClientConfig(newZkClient()));

    @Test
    public void alloc() {
        System.out.println(client.alloc("b"));
    }

    @Test
    public void benchmarkAlloc() {
        new Benchmark().start(new Runnable() {
            @Override
            public void run() {
                client.alloc("b");
            }
        });
    }

    private CuratorFramework newZkClient() {
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .connectionTimeoutMs(2000)
                .sessionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(3000, 10))
                .namespace("IIA")
                .build();

        zkClient.start();
        return zkClient;
    }
}