package com.asiainfo.test;

import com.asiainfo.common.util.CollectionUtil;
import com.asiainfo.iia.client.IdAllocatorClient;
import org.junit.Test;

/**
 * @author Jay Wu
 */
public class IdAllocatorClientTest {

    @Test
    public void alloc() {
        IdAllocatorClient client = new IdAllocatorClient(CollectionUtil.arrayListOf("127.0.0.1:7000", "127.0.0.1:7001"));
        System.out.println(client.alloc("fjayblue1"));
    }
}