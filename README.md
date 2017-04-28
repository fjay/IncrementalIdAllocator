# 分布式增长序列化生成器

IncrementalIdAllocator为轻量级的分布式ID生成器，包含以下特点：

* 支持长整型id生成
* 生成速度快，且支持水平扩展，仅需简单配置
* 保证整体趋势增长，即生成的id永远大于之前的id
* 生成服务器高可用，少数节点故障不影响整体服务

##HOW TO USE

## 客户端

添加包依赖(Maven:pom.xml)

```xml
<dependency>
    <groupId>com.asiainfo.iia</groupId>
    <artifactId>incremental-id-allocator-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

使用代码示例：

```java
IdAllocatorClient client = new IdAllocatorClient(CollectionUtil.arrayListOf(
            "127.0.0.1:7001", "127.0.0.1:7000"
    ));

Long id = client.alloc("bizType1")
```


## 服务端

### ENVIRONMENT

* JDK 7+
* Mysql 5+
* ZooKeeper 3.4+

### 初始化数据库

创建数据库并执行以下脚本：

```
server/src/other/sql/mysql/all.sql
```

### 打包安装

```
# clone当前项目地址
git clone ${URL}
# -P可选参数:test(测试环境),production(生产环境)
mvn clean install -Pproduction -Dmaven.test.skip=true
```

将server/target/incremental-id-allocator-server-xxx-bin/incremental-id-allocator-server-xxx文件夹复制到目标路径

### 调整日志与数据源

log4j.properties

```
log4j.appender.logfile.File=${log4j.logfile}

log4j.logger.com.asiainfo.iia=${log4j.level}
```

config.js：

```java
var conf = {
    // 节点标识，需要与config_item中的config_item_id一致
    nodeId: "IIA_0",
    // 数据库配置
    dataSource: {
        url: '${database.url}',
        username: '${database.username}',
        password: '${database.password}'
        ...
    }
};
```

### 调整配置项

所有配置项位于数据库中的config_item表，请根据值备注进行实际调整。

### 运行服务

执行以下命令：

```shell
start.sh start
```

END