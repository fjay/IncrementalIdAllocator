# 分布式增长ID生成器

IncrementalIdAllocator为轻量级的分布式增长ID生成器，包含以下特点：

* 支持长整型id生成，且保证id绝对趋势增长，即后生成的id永远大于早生成的id。
* 生成速度快。
* 支持水平扩展，新加入节点只需进行简单配置即可加入集群，已有数据将自动迁移，迁移期间不影响服务。
* 生成服务器高可用，少数节点故障不影响整体服务。
* 生成器采用标准Http协议对外提供服务，目前已实现Java版本客户端，其他语言可参考进行实现。

## HOW TO USE

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
// 传入所有节点服务器
IdAllocatorClient client = new IdAllocatorClient(CollectionUtil.arrayListOf(
            "127.0.0.1:7001", "127.0.0.1:7000"
    ));

// 传入分类标识
Long id = client.alloc("bizType1")
```

## 服务端

### ENVIRONMENT

* JDK 7+
* Mysql 5+
* ZooKeeper 3.4+

### 打包安装

```
# clone当前项目地址
git clone ${URL}
# -P可选参数:test(测试环境),production(生产环境)
mvn clean install -Pproduction -Dmaven.test.skip=true
```

将server/target/incremental-id-allocator-server-x.x.x-bin/incremental-id-allocator-server-x.x.x文件夹复制到目标路径

### 日志配置

log4j.properties

```
log4j.appender.logfile.File=${log4j.logfile}

log4j.logger.com.asiainfo.iia=${log4j.level}
```

### 节点配置

节点有两种配置模式，一种为数据库配置模式，即所有配置从数据库加载，另一种为本地配置模式，即通过本地配置文件方式加载。

本地模式适合部署单一节点，建议只用于调试开发，数据库模式适合同时部署多个节点，推荐生产环境使用。

注意，两种模式不能共存，只能二选一。

#### 数据库配置模式

创建数据库并执行以下脚本：

```
server/src/other/sql/mysql/all.sql
```

所有配置项位于数据库中的config_item表，请根据值备注进行实际调整。

调整config.js：

```
serverConfig: {
    fields: {
        // 命名空间
        namespace: "IIA",
        // 节点标识，需要与config_item中的config_item_id一致
        nodeId: "IIA_0",
        // 数据库配置
        dataSource: {
            fields: {
                url: '${database.url}',
                username: '${database.username}',
                password: '${database.password}',
                ...
            }
        }
    }
 }
```

#### 本地配置模式

调整config.js：

```
serverConfig: {
    fields: {
        // 命名空间
        namespace: "IIA",
        // IIA本机节点信息
        node: "127.0.0.1:7000",
        // zookeeper节点信息，多个节点采用逗号隔开
        zkNode: "127.0.0.1:2181",
        // 最大分段数量
        maxSegmentSize: 50000,
        // ID分配器缓存池
        idAllocatorPoolSize: 1000,
        // IIA节点Session有效期（毫秒）
        nodeSessionTimeoutMs: 3000
    }
}
```

### 运行服务

Linux下执行以下命令（后台运行）：

```shell
sh start.sh start
```

Windows或者Linux执行以下命令（前台运行）：

```shell
java -jar incremental-id-allocator-server-x.x.x.jar
```

END