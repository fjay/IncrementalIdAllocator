CREATE TABLE config_item
(
  profile_id        VARCHAR(32) DEFAULT 'DEFAULT' NOT NULL
  COMMENT '环境标识',
  client_id         VARCHAR(32)                   NOT NULL
  COMMENT '客户端标识',
  config_item_id    VARCHAR(36) DEFAULT ''        NOT NULL
  COMMENT '配置明细项标识',
  parent_item_id    VARCHAR(32)                   NULL
  COMMENT '父明细项标识',
  config_type_id    VARCHAR(32)                   NOT NULL
  COMMENT '配置类型标识',
  enabled           VARCHAR(32)                   NOT NULL
  COMMENT '是否开启(0:关闭,1:开启)',
  value             TEXT                          NULL
  COMMENT '配置项值',
  description       VARCHAR(255)                  NULL
  COMMENT '描述',
  sequence_no       INT(32)                       NULL
  COMMENT '序号',
  created_date      DATE                          NULL
  COMMENT '创建时间',
  last_updated_date DATE                          NULL
  COMMENT '最后更新时间',
  CONSTRAINT `PRIMARY`
  PRIMARY KEY (profile_id, client_id, config_item_id)
)
  COMMENT '配置明细项';


INSERT INTO config_item (profile_id, client_id, config_item_id, parent_item_id, config_type_id, enabled, value, description, sequence_no, created_date, last_updated_date)
VALUES ('DEFAULT', 'IIA', 'ID_ALLOCATOR_POOL_SIZE', NULL, 'COMMON', '1', '1000', 'ID分配器缓存池', NULL, '2017-04-27', NULL);
INSERT INTO config_item (profile_id, client_id, config_item_id, parent_item_id, config_type_id, enabled, value, description, sequence_no, created_date, last_updated_date)
VALUES ('DEFAULT', 'IIA', 'IIA_0', NULL, 'IIA_NODES', '1', '127.0.0.1:7001', 'IIA节点服务器', NULL, '2017-04-27', NULL);
INSERT INTO config_item (profile_id, client_id, config_item_id, parent_item_id, config_type_id, enabled, value, description, sequence_no, created_date, last_updated_date)
VALUES ('DEFAULT', 'IIA', 'IIA_1', NULL, 'IIA_NODES', '1', '127.0.0.1:7000', 'IIA节点服务器', NULL, '2017-04-27', NULL);
INSERT INTO config_item (profile_id, client_id, config_item_id, parent_item_id, config_type_id, enabled, value, description, sequence_no, created_date, last_updated_date)
VALUES ('DEFAULT', 'IIA', 'MAX_SEGMENT_SIZE', NULL, 'COMMON', '1', '50000', '最大分段数量', NULL, '2017-04-27', NULL);
INSERT INTO config_item (profile_id, client_id, config_item_id, parent_item_id, config_type_id, enabled, value, description, sequence_no, created_date, last_updated_date)
VALUES
  ('DEFAULT', 'IIA', 'NODE_SESSION_TIMEOUT_MS', NULL, 'COMMON', '1', '3000', '节点Session有效期（毫秒）', NULL, '2017-04-27',
              NULL);
INSERT INTO config_item (profile_id, client_id, config_item_id, parent_item_id, config_type_id, enabled, value, description, sequence_no, created_date, last_updated_date)
VALUES ('DEFAULT', 'IIA', 'ZK_NODE', NULL, 'COMMON', '1', '127.0.0.1:2181', 'zookeeper地址', NULL, '2017-04-27', NULL);