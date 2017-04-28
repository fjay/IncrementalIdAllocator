package com.asiainfo.test;

import com.asiainfo.common.util.CollectionUtil;
import com.asiainfo.common.util.debug.Benchmark;
import com.asiainfo.iia.client.IdAllocatorClient;
import org.junit.Test;

/**
 * @author Jay Wu
 */
public class IdAllocatorClientTest {

    private IdAllocatorClient client = new IdAllocatorClient(CollectionUtil.arrayListOf(
            "127.0.0.1:17000", "127.0.0.1:7001"
    ));

    @Test
    public void alloc() {
        System.out.println(client.alloc("b"));
    }

    @Test
    public void benchmarkAlloc() {
        new Benchmark().start(new Runnable() {
            @Override
            public void run() {
                client.alloc("a");
            }
        });
    }
}