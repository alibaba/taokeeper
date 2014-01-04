package com.taobao.taokeeper.common.constant;

/**
 * Sql template
 * Description: SQL Template
 * @author   yinshi.nc
 * @version  1.0.0
 * @since   	  1.0.0
 * @Date	 2011-10-26
 */
public class SqlTemplate {

	/** Query cluster by cluster_id */
	public static final String SQL_QUERY_CLUSTER_BY_ID 				= "SELECT cluster_id,cluster_name,server_list,description FROM zookeeper_cluster WHERE cluster_id= {0}";
	/** Query all cluster */
	public static final String SQL_QUERY_ALL_DETAIL_CLUSTER    		= "SELECT cluster_id,cluster_name,server_list,description FROM zookeeper_cluster";
	/** Query all cluster id and name*/
	public static final String SQL_QUERY_ALL_CLUSTER_ID_NAME     	= "SELECT cluster_id,cluster_name FROM zookeeper_cluster";
	
	
	/** Query alarm settings by cluster id*/
	public static final String SQL_QUERY_ALARM_SETTINGS_BY_ID    = "SELECT " +
			                                                                                                    "alarm_settings_id, " +
			                                                                                                    "cluster_id, " +
			                                                                                                    "wangwang_list, " +
			                                                                                                    "phone_list, " +
			                                                                                                    "email_list, " +
			                                                                                                    "max_delay_of_check, " +
			                                                                                                    "max_cpu_usage," +
			                                                                                                    "max_memory_usage, " +
			                                                                                                    "max_load, " +
			                                                                                                    "max_connection_per_ip, " +
			                                                                                                    "max_watch_per_ip, " +
			                                                                                                    "data_dir, " +
			                                                                                                    "data_log_dir, " +
			                                                                                                    "max_disk_usage, " +
			                                                                                                    "node_path_check_rule " +
			                                                                                                    "FROM " +
			                                                                                                    "alarm_settings " +
			                                                                                                    "WHERE " +
			                                                                                                    "cluster_id= {0}";
	/** Update alarm settings by cluster id  */
	public static final String SQL_UPDATE_ALARM_SETTINGS_BY_ID   = "UPDATE " +
																												"alarm_settings " +
																												"SET " +
																												"max_delay_of_check='{0}', " +
																												"max_cpu_usage='{1}', " +
																												"max_memory_usage='{2}', " +
																												"max_load='{3}', " +
																												"wangwang_list='{4}', " +
																												"phone_list='{5}', " +
																												"email_list='{6}', " +
																												"max_connection_per_ip='{7}', " +
																												"max_watch_per_ip='{8}', " +
																												"data_dir='{9}', " +
																												"data_log_dir='{10}', " +
																												"max_disk_usage='{11}', " +
																												"node_path_check_rule='{12}' " +
																												"WHERE " +
																												"cluster_id= {13}";
	
	/** Add alarm settings , cluster_id, max_delay_of_check, max_cpu_usage, max_memory_usage, max_load, wangwang_list, phone_list, email_list */
	public static final String SQL_ADD_ALARM_SETTINGS                   = "INSERT INTO alarm_settings ( cluster_id, max_delay_of_check, max_cpu_usage, max_memory_usage, max_load, wangwang_list, phone_list, email_list, max_connection_per_ip, max_watch_per_ip, data_dir, data_log_dir, max_disk_usage ) VALUES ( '{0}','{1}','{2}','{3}','{4}','{5}','{6}','{7}','{8}','{9}','{10}','{11}','{12}')";
	
	
	
	public static final String SQL_UPDATE_ZOOKEEPER_CLUSTER_SETTINGS_BY_ID   = "UPDATE zookeeper_cluster SET cluster_name='{0}', server_list='{1}', description='{2}' WHERE cluster_id= {3}";
	public static final String SQL_ADD_ZOOKEEPER_CLUSTER              = "INSERT INTO zookeeper_cluster ( cluster_name, server_list, description ) VALUES ('{0}','{1}','{2}')";
	
	
	
	
	
	/** TaoKeeper settings */
	public static final String SQL_INSERT_TAOKEEPER_SETTINGS_BY_ID = "INSERT into taokeeper_settings ( env_name,max_threads_of_zookeeper_check, description ) VALUES( '{0}', '{1}', '{2}' )";
	public static final String SQL_QUERY_TAOKEEPER_SETTINGS_BY_ID = "SELECT settings_id,env_name,max_threads_of_zookeeper_check,description FROM taokeeper_settings WHERE settings_id= {0}";
	public static final String SQL_UPDATE_TAOKEEPER_SETTINGS_BY_ID = "UPDATE taokeeper_settings SET env_name='{0}',max_threads_of_zookeeper_check='{1}',description='{2}' WHERE settings_id= {3}";
	
	
	/** zookeeper_stat */
	public static final String SQL_INSERT_TAOKEEPER_STAT = "INSERT INTO taokeeper_stat ( cluster_id, server, stat_date_time, stat_date, connections, watches, send_times, receive_times, node_count, rwps ) VALUES ( {0},'{1}', '{2}', '{3}', {4}, {5}, {6}, {7}, {8}, '{9}' )";
	public static final String SQL_QUERY_TAOKEEPER_STAT_BY_CLUSTERID_SERVER_DATE = "SELECT * FROM taokeeper_stat where cluster_id='{0}' AND server='{1}' AND stat_date='{2}'";
	public static final String SQL_QUERY_TAOKEEPER_STAT_BY_CLUSTERID_DATE = "SELECT * FROM taokeeper_stat where cluster_id='{0}' AND stat_date='{1}'";
	
	
	
}
