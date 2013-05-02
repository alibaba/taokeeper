CREATE DATABASE taokeeper;
USE taokeeper;

-- ----------------------------
-- Table: alarm_settings
-- ----------------------------
DROP TABLE IF EXISTS `alarm_settings`;
CREATE TABLE `alarm_settings` (
  `alarm_settings_id` int(11) NOT NULL auto_increment,
  `cluster_id` int(11) NOT NULL,
  `wangwang_list` varchar(255) default NULL,
  `phone_list` varchar(255) default NULL,
  `email_list` varchar(255) default NULL,
  `max_delay_of_check` varchar(255) default NULL,
  `max_cpu_usage` varchar(255) default NULL,
  `max_memory_usage` varchar(255) default NULL,
  `max_load` varchar(255) default NULL,
  `max_connection_per_ip` varchar(255) default NULL,
  `max_watch_per_ip` varchar(255) default NULL,
  `data_dir` varchar(255) default NULL,
  `data_log_dir` varchar(255) default NULL,
  `max_disk_usage` varchar(255) default NULL,
  PRIMARY KEY  (`alarm_settings_id`),
  UNIQUE KEY `uk_alarm_settings_cid` (`cluster_id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

-- ----------------------------
-- Table taokeeper_settings
-- ----------------------------
DROP TABLE IF EXISTS `taokeeper_settings`;
CREATE TABLE `taokeeper_settings` (
  `settings_id` int(11) NOT NULL auto_increment,
  `env_name` varchar(20) default NULL,
  `max_threads_of_zookeeper_check` int(5) default NULL,
  `description` varchar(255) default NULL,
  PRIMARY KEY  (`settings_id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

-- ----------------------------
-- Table: taokeeper_stat
-- ----------------------------
DROP TABLE IF EXISTS `taokeeper_stat`;
CREATE TABLE `taokeeper_stat` (
  `cluster_id` int(11) NOT NULL,
  `server` varchar(30) NOT NULL COMMENT '127.0.0.1:2181',
  `stat_date_time` datetime NOT NULL COMMENT '统计时间 2012-01-05 14:56:20',
  `stat_date` date NOT NULL,
  `connections` int(11) DEFAULT NULL,
  `watches` int(11) DEFAULT NULL COMMENT '订阅者数目',
  `send_times` bigint(20) unsigned DEFAULT 0,
  `receive_times` bigint(20) unsigned DEFAULT 0,
  `node_count` int(11) DEFAULT 0,
  PRIMARY KEY (`cluster_id`,`server`,`stat_date_time`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;


-- ----------------------------
-- Table: zookeeper_cluster
-- ----------------------------
DROP TABLE IF EXISTS `zookeeper_cluster`;
CREATE TABLE `zookeeper_cluster` (
  `cluster_id` int(11) NOT NULL auto_increment,
  `cluster_name` varchar(255) NOT NULL,
  `server_list` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  PRIMARY KEY  (`cluster_id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;