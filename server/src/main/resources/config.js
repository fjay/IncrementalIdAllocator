var conf = {
    // serverConfig: {
    //     type: "com.asiainfo.iia.server.config.DbServerConfig",
    //     fields: {
    //         // 节点标识，需要与config_item中的config_item_id一致
    //         nodeId: "IIA_0",
    //         // 数据库配置
    //         dataSource: {
    //             type: "com.alibaba.druid.pool.DruidDataSource",
    //             events: {
    //                 depose: "close"
    //             },
    //             fields: {
    //                 url: '${database.url}',
    //                 username: '${database.username}',
    //                 password: '${database.password}',
    //                 initialSize: 1,
    //                 maxActive: 20,
    //                 minIdle: 5,
    //                 minEvictableIdleTimeMillis: 600000,
    //                 timeBetweenEvictionRunsMillis: 30000,
    //                 defaultAutoCommit: false,
    //                 testWhileIdle: true,
    //                 testOnBorrow: true,
    //                 removeAbandoned: true,
    //                 removeAbandonedTimeout: 3600,
    //                 validationQueryTimeout: 5,
    //                 validationQuery: "${database.validationQuery}"
    //             }
    //         }
    //     }
    // },
    serverConfig: {
        type: "com.asiainfo.iia.server.config.LocalServerConfig",
        fields: {
            // IIA本机节点信息
            node: "127.0.0.1:7000",
            // zookeeper节点信息，多个节点采用逗号隔开
            zkNode: "127.0.0.1:2181",
            // 最大分段数量
            maxSegmentSize: 2,
            // ID分配器缓存池
            idAllocatorPoolSize: 1,
            // 节点Session有效期（毫秒）
            nodeSessionTimeoutMs: 3000
        }
    }
};