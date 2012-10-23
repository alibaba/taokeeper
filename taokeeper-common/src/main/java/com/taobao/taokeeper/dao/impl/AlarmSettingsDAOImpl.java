package com.taobao.taokeeper.dao.impl;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_ADD_ALARM_SETTINGS;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_QUERY_ALARM_SETTINGS_BY_ID;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_UPDATE_ALARM_SETTINGS_BY_ID;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.sql.ResultSet;

import com.taobao.taokeeper.dao.AlarmSettingsDAO;
import com.taobao.taokeeper.model.AlarmSettings;
import common.toolkit.java.entity.db.DBConnectionResource;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.db.DbcpUtil;

/**
 * Description: Access DB for alarm settings
 * @author   yinshi.nc
 * @Date	 2011-10-28
 */
public class AlarmSettingsDAOImpl implements AlarmSettingsDAO{
	
	public AlarmSettings getAlarmSettingsByCulsterId( int clusterId )throws DaoException{
		
		AlarmSettings alarmSettings = null;
    	ResultSet rs = null;
    	DBConnectionResource dbConnectionResource = null;
    	try {
    		String querySQL = StringUtil.replaceSequenced( SQL_QUERY_ALARM_SETTINGS_BY_ID, //
    				 																	clusterId + EMPTY_STRING );
    		dbConnectionResource = DbcpUtil.executeQuery( querySQL );
    		if( null == dbConnectionResource )
    			throw new DaoException( "没有返回结果" );
    		rs = dbConnectionResource.resultSet;
			if( null != rs && rs.next() ){
				
				String wangwangList			= rs.getString( "wangwang_list" );
				String phoneList			= rs.getString( "phone_list" );
				String emailList        	= rs.getString( "email_list" );
				String maxDelayOfCheck		= rs.getString( "max_delay_of_check" );
				String maxCpuUsage			= rs.getString( "max_cpu_usage" );
				String maxMemoryUsage		= rs.getString( "max_memory_usage" );
				String maxLoad				= rs.getString( "max_load" );
				String maxConnectionPerIp	= rs.getString( "max_connection_per_ip" );
				String maxWatchPerIp		= rs.getString( "max_watch_per_ip" );
				String dataDir				= rs.getString( "data_dir" );
				String dataLogDir			= rs.getString( "data_log_dir" );
				String maxDiskUsage			= rs.getString( "max_disk_usage" );
				String nodePathCheckRule			= StringUtil.trimToEmpty( rs.getString( "node_path_check_rule" ) );
				
				alarmSettings = new AlarmSettings();
				alarmSettings.setClusterId( clusterId );
				alarmSettings.setWangwangList( wangwangList );
				alarmSettings.setPhoneList( phoneList );
				alarmSettings.setEmailList( emailList );
				alarmSettings.setMaxDelayOfCheck( maxDelayOfCheck );
				alarmSettings.setMaxCpuUsage( maxCpuUsage );
				alarmSettings.setMaxMemoryUsage( maxMemoryUsage );
				alarmSettings.setMaxLoad( maxLoad );
				alarmSettings.setMaxConnectionPerIp( maxConnectionPerIp );
				alarmSettings.setMaxWatchPerIp( maxWatchPerIp );
				alarmSettings.setDataDir( dataDir );
				alarmSettings.setDataLogDir( dataLogDir );
				alarmSettings.setMaxDiskUsage( maxDiskUsage );
				alarmSettings.setNodePathCheckRule( nodePathCheckRule );
			}
			return alarmSettings;
		} catch ( Exception e ) {
			throw new DaoException( "Error when query AlarmSettings by cluster_id: " + clusterId + 
					                                ", Error: " + e.getMessage(), e );
		}finally{
			if( null != dbConnectionResource ){
				DbcpUtil.closeResultSetAndStatement( rs, dbConnectionResource.statement );
				DbcpUtil.returnBackConnectionToPool( dbConnectionResource.connection );
			}
		}
	}
//
//	@Override
//	public Set< ZooKeeperCluster > getAllDetailZooKeeperCluster() throws DaoException {
//		
//		Set<ZooKeeperCluster> zookeeperClusterList = new HashSet< ZooKeeperCluster >();
//    	//从数据库中获取指定zookeeper集群中所有机器
//    	ResultSet rs = null;
//    	try {
//    		JDBCHelper jdbcHelper = new JDBCHelper( JDBC_URL_MYSQL, DB_USERNAME, DB_PASSWORD  );
//			rs = jdbcHelper.executeQuery( SQL_QUERY_ALL_DETAIL_CLUSTER );
//			while( rs.next() ){
//
//				int      clusterId		   = rs.getInt( "cluster_id" );
//				String clusterName = rs.getString( "cluster_name" );
//				String serverListStr  = rs.getString( "server_list" );
//				String description    = rs.getString( "description" );
//				
//				List<String> serverList  = null;
//				if( !StringUtil.isBlank( serverListStr ) ){
//					String[] serverListArray = serverListStr.split( COMMA );
//					serverList = ArrayUtil.toArrayList( serverListArray );
//				}
//				ZooKeeperCluster zookeeperCluster = new ZooKeeperCluster();
//				zookeeperCluster.setClusterId( clusterId );
//				zookeeperCluster.setClusterName(clusterName);
//				zookeeperCluster.setServerList( serverList );
//				zookeeperCluster.setDescription(description);
//				
//				zookeeperClusterList.add( zookeeperCluster );
//				
//			}
//			return zookeeperClusterList;
//		} catch ( Exception e ) {
//			throw new DaoException( "Error when query all zookeeper cluster, Error: " + e.getMessage(), e );
//		}finally{
//			JDBCHelper.closeResultSet( rs );
//		}
//	}
//
//	@Override
//	public List< ZooKeeperCluster > getAllZooKeeperClusterIdAndName() throws DaoException {
//		List<ZooKeeperCluster> zookeeperClusterList = new ArrayList< ZooKeeperCluster >();
//    	//从数据库中获取指定ZooKeeper集群中所有机器
//    	ResultSet rs = null;
//    	try {
//    		JDBCHelper jdbcHelper = new JDBCHelper( JDBC_URL_MYSQL, DB_USERNAME, DB_PASSWORD  );
//			rs = jdbcHelper.executeQuery( SQL_QUERY_ALL_CLUSTER_ID_NAME );
//			while( rs.next() ){
//
//				int      clusterId		   = rs.getInt( "cluster_id" );
//				String clusterName = rs.getString( "cluster_name" );
//				
//				ZooKeeperCluster zookeeperCluster = new ZooKeeperCluster();
//				zookeeperCluster.setClusterId( clusterId );
//				zookeeperCluster.setClusterName(clusterName);
//				zookeeperClusterList.add( zookeeperCluster );
//			}
//			return zookeeperClusterList;
//		} catch ( Exception e ) {
//			throw new DaoException( "Error when query all zookeeper cluster id and name, Error: " + e.getMessage(), e );
//		}finally{
//			JDBCHelper.closeResultSet( rs );
//		}
//	}

	@Override
	public boolean updateAlarmSettingsByClusterId( AlarmSettings alarmSettings ) throws DaoException {

		if( null == alarmSettings )
			return false;
		
		//根据 clusterId 更新报警规则
    	try {
    		String updateSql = StringUtil.replaceSequenced( SQL_UPDATE_ALARM_SETTINGS_BY_ID, //
    				                                                                                   alarmSettings.getMaxDelayOfCheck(), //
    				                                                                                   alarmSettings.getMaxCpuUsage(), //
    				                                                                                   alarmSettings.getMaxMemoryUsage(), //
    				                                                                                   alarmSettings.getMaxLoad(), //
    				                                                                                   alarmSettings.getWangwangList(), //
    				                                                                                   alarmSettings.getPhoneList(), //
    				                                                                                   alarmSettings.getEmailList(), //
    				                                                                                   alarmSettings.getMaxConnectionPerIp(), //
    				                                                                                   alarmSettings.getMaxWatchPerIp(), //
    				                                                                                   alarmSettings.getDataDir(), //
    				                                                                                   alarmSettings.getDataLogDir(), //
    				                                                                                   alarmSettings.getMaxDiskUsage(), //
    				                                                                                   alarmSettings.getNodePathCheckRule(), //
    				                                                                                   alarmSettings.getClusterId() + EMPTY_STRING );
			int num = DbcpUtil.executeUpdate( updateSql );
			if( 1 == num ){
				return true;
			}
			return false;
		} catch ( Exception e ) {
			throw new DaoException( "Error when update AlarmSettings by cluster_id: " + alarmSettings + 
					                                  ", Error: " + e.getMessage(), e );
		}
	}

	@Override
	public boolean addAlarmSettings( AlarmSettings alarmSettings ) throws DaoException {
		if( null == alarmSettings )
			return false;
		
		//从数据库中获取指定zookeeper集群中所有机器
    	try {
    		String insertSql = StringUtil.replaceSequenced( SQL_ADD_ALARM_SETTINGS, alarmSettings.getClusterId()+EMPTY_STRING, alarmSettings.getMaxDelayOfCheck(), alarmSettings.getMaxCpuUsage(), alarmSettings.getMaxMemoryUsage(), alarmSettings.getMaxLoad(), alarmSettings.getWangwangList(), alarmSettings.getPhoneList(), alarmSettings.getEmailList(), alarmSettings.getMaxConnectionPerIp(), alarmSettings.getMaxWatchPerIp(), alarmSettings.getDataDir(), alarmSettings.getDataLogDir(), alarmSettings.getMaxDiskUsage() );
			int num = DbcpUtil.executeInsert( insertSql );
			if( 1 == num ){
				return true;
			}
			return false;
		} catch ( Exception e ) {
			throw new DaoException( "Error when add AlarmSettings: " + alarmSettings + ", Error: " + e.getMessage(), e );
		}
	}
	
}
