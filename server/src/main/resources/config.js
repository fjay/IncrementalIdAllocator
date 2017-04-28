var conf = {
    nodeId: "IIA_1",
    // 数据库配置
    dataSource: {
        url: '${database.url}',
        username: '${database.username}',
        password: '${database.password}',
        initialSize: 1,
        maxActive: 1,
        minIdle: 1,
        minEvictableIdleTimeMillis: 600000,
        validationQuery: "${database.validationQuery}"
    }
};