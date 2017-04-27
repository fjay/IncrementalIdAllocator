package com.asiainfo.test;

import com.asiainfo.common.util.RandomUtil;
import com.asiainfo.common.util.ThreadUtil;
import com.asiainfo.common.util.debug.Benchmark;
import com.asiainfo.dao.core.SimpleDataSource;
import com.asiainfo.db.executor.client.DbExecutorClient;
import com.asiainfo.mq.client.DatabaseMQClusterConfiguration;
import com.asiainfo.mq.client.MQClusterClient;
import com.asiainfo.mq.client.connection.ssdb.SSDBConnectionFactory;
import com.asiainfo.sql.builder.builder.TableSqlBuilder;
import com.asiainfo.sql.builder.util.SqlBuilders;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Date;

/**
 * @author Jay Wu
 */
public class ClientTest {

    private DataSource createDataSource() {
        SimpleDataSource ds = new SimpleDataSource();
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/womail_notification_center?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&autoReconnectForPools=true");
        ds.setUsername("root");
        ds.setPassword("1234");

        return ds;
    }

    private TableSqlBuilder createInsertRequest() {
        return createInsertRequest(RandomUtil.UUID());
    }

    private TableSqlBuilder createInsertRequest(String id) {
        return SqlBuilders.insert("sms_template")
                .setValue("template_id", id)
                .setValue("channel_id", id)
                .setValue("status_id", 1)
                .setValue("sender", "1")
                .setValue("content", "1")
                .setValue("created_date", new Date());
    }

    private DbExecutorClient createClient() {
        return new DbExecutorClient(new MQClusterClient("WM_NC",
                new DatabaseMQClusterConfiguration(createDataSource()),
                new SSDBConnectionFactory()));
    }

    private String insertData(DbExecutorClient client) {
        String id = RandomUtil.UUID();
        client.execute(createInsertRequest(id));
        return id;
    }

    @Test
    public void insert() {
        insertData(createClient());
        ThreadUtil.safeSleep(5000);
    }

    @Test
    public void update() {
        DbExecutorClient client = createClient();

        String id = insertData(client);

        client.execute(SqlBuilders.update("sms_template")
                .setValue("content", 2)
                .where("template_id", "=", id));

        ThreadUtil.safeSleep(5000);
    }

    @Test
    public void delete() {
        DbExecutorClient client = createClient();

        String id = insertData(client);

        client.execute(SqlBuilders.delete("sms_template")
                .where("template_id", "=", id));

        ThreadUtil.safeSleep(5000);
    }

    @Test
    public void insertBenchmark() {
        final DbExecutorClient client = createClient();

        new Benchmark().start(new Runnable() {
            public void run() {
                client.execute(createInsertRequest());
            }
        });
    }
}