package com.taobao.taokeeper.dao.impl;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_ADD_ZOOKEEPER_CLUSTER;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_QUERY_ALL_CLUSTER_ID_NAME;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_QUERY_ALL_DETAIL_CLUSTER;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_QUERY_CLUSTER_BY_ID;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_UPDATE_ZOOKEEPER_CLUSTER_SETTINGS_BY_ID;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.SymbolConstant.COMMA;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.taobao.taokeeper.dao.ZooKeeperClusterDAO;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import common.toolkit.java.entity.db.DBConnectionResource;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.collection.ArrayUtil;
import common.toolkit.java.util.collection.CollectionUtil;
import common.toolkit.java.util.db.DbcpUtil;
/**
 * Description: Access DB for zookeeper cluster
 * @author   yinshi.nc
 * @Date	 2011-10-28
 */
public class ZooKeeperClusterDAOImpl implements ZooKeeperClusterDAO{
	
	public ZooKeeperCluster getZooKeeperClusterByCulsterId( int clusterId )throws DaoException{
		
		ZooKeeperCluster zookeeperCluster = null;
    	//从数据库中获取指定zookeeper集群中所有机器
		ResultSet rs = null;
		DBConnectionResource myResultSet = null;
    	try {
    		String querySQL = StringUtil.replaceSequenced( SQL_QUERY_CLUSTER_BY_ID, clusterId + EMPTY_STRING );
    		myResultSet = DbcpUtil.executeQuery( querySQL );
    		if( null == myResultSet )
    			throw new DaoException( "没有返回结果" );
    		rs = myResultSet.resultSet;
			if( rs.next() ){
				
				String clusterName = rs.getString( "cluster_name" );
				String serverListStr  = rs.getString( "server_list" );
				String description    = rs.getString( "description" );
				
				List<String> serverList  = null;
				if( !StringUtil.isBlank( serverListStr ) ){
					String[] serverListArray = serverListStr.split( COMMA );
					serverList = ArrayUtil.toArrayList( serverListArray );
				}
				zookeeperCluster = new ZooKeeperCluster();
				zookeeperCluster.setClusterId( clusterId );
				zookeeperCluster.setClusterName(clusterName);
				zookeeperCluster.setServerList( serverList );
				zookeeperCluster.setDescription(description);
			}
			return zookeeperCluster;
		} catch ( Exception e ) {
			throw new DaoException( "Error when query zookeeper cluster by cluster_id: " + clusterId + ", Error: " + e.getMessage(), e );
		}finally{
			if( null != myResultSet ){
				DbcpUtil.closeResultSetAndStatement( rs, myResultSet.statement );
				DbcpUtil.returnBackConnectionToPool( myResultSet.connection );
			}
		}
	}

	@Override
	public List< ZooKeeperCluster > getAllDetailZooKeeperCluster() throws DaoException {
		
		List<ZooKeeperCluster> zookeeperClusterList = new ArrayList< ZooKeeperCluster >();
    	//从数据库中获取指定zookeeper集群中所有机器
		ResultSet rs = null;
		DBConnectionResource myResultSet = null;
    	try {
    		myResultSet = DbcpUtil.executeQuery( SQL_QUERY_ALL_DETAIL_CLUSTER );
    		if( null == myResultSet )
    			throw new DaoException( "没有返回结果" );
    		rs = myResultSet.resultSet;
			while( rs.next() ){

				int      clusterId		   = rs.getInt( "cluster_id" );
				String clusterName = rs.getString( "cluster_name" );
				String serverListStr  = rs.getString( "server_list" );
				String description    = rs.getString( "description" );
				
				List<String> serverList  = null;
				if( !StringUtil.isBlank( serverListStr ) ){
					String[] serverListArray = serverListStr.split( COMMA );
					serverList = ArrayUtil.toArrayList( serverListArray );
				}
				ZooKeeperCluster zookeeperCluster = new ZooKeeperCluster();
				zookeeperCluster.setClusterId( clusterId );
				zookeeperCluster.setClusterName(clusterName);
				zookeeperCluster.setServerList( serverList );
				zookeeperCluster.setDescription(description);
				
				zookeeperClusterList.add( zookeeperCluster );
				
			}
			return zookeeperClusterList;
		} catch ( Exception e ) {
			throw new DaoException( "Error when query all zookeeper cluster, Error: " + e.getMessage(), e );
		}finally{
			if( null != myResultSet ){
				DbcpUtil.closeResultSetAndStatement( rs, myResultSet.statement );
				DbcpUtil.returnBackConnectionToPool( myResultSet.connection );
			}
		}
	}

	@Override
	public List< ZooKeeperCluster > getAllZooKeeperClusterIdAndName() throws DaoException {
		List<ZooKeeperCluster> zookeeperClusterList = new ArrayList< ZooKeeperCluster >();
		ResultSet rs = null;
		DBConnectionResource myResultSet = null;
    	try {
    		myResultSet = DbcpUtil.executeQuery( SQL_QUERY_ALL_CLUSTER_ID_NAME );
    		if( null == myResultSet )
    			throw new DaoException( "没有返回结果" );
    		rs = myResultSet.resultSet;
			while( rs.next() ){

				int      clusterId		   = rs.getInt( "cluster_id" );
				String clusterName = rs.getString( "cluster_name" );
				
				ZooKeeperCluster zookeeperCluster = new ZooKeeperCluster();
				zookeeperCluster.setClusterId( clusterId );
				zookeeperCluster.setClusterName(clusterName);
				zookeeperClusterList.add( zookeeperCluster );
			}
			return zookeeperClusterList;
		} catch ( Exception e ) {
			throw new DaoException( "Error when query all zookeeper cluster id and name, Error: " + e.getMessage(), e );
		}finally{
			if( null != myResultSet ){
				DbcpUtil.closeResultSetAndStatement( rs, myResultSet.statement );
				DbcpUtil.returnBackConnectionToPool( myResultSet.connection );
			}
		}
	}
	
	
	

	@Override
	public boolean updateZooKeeperSettingsByClusterId( ZooKeeperCluster zooKeeperCluster ) throws DaoException {

		if( null == zooKeeperCluster )
			return false;
		
		//从数据库中获取指定zookeeper集群中所有机器
    	try {
    		String serverListString = EMPTY_STRING;
    		List<String> serverList =  zooKeeperCluster.getServerList();
    		if( null != serverList && !serverList.isEmpty() ){
    			for( String server : serverList ){
    				serverListString += server + COMMA;
    			}
    			serverListString = StringUtil.replaceLast( serverListString, COMMA, EMPTY_STRING );
    		}
    		
    		String updateSql = StringUtil.replaceSequenced( SQL_UPDATE_ZOOKEEPER_CLUSTER_SETTINGS_BY_ID, zooKeeperCluster.getClusterName(), serverListString, zooKeeperCluster.getDescription(), zooKeeperCluster.getClusterId() + EMPTY_STRING );
			int num = DbcpUtil.executeUpdate( updateSql );
			if( 1 == num ){
				return true;
			}
			return false;
		} catch ( Exception e ) {
			throw new DaoException( "Error when update zooKeeperCluster by cluster_id: " + zooKeeperCluster + ", Error: " + e.getMessage(), e );
		}
	}

	@Override
	public int addZooKeeper( ZooKeeperCluster zooKeeperCluster ) throws DaoException {
		if( null == zooKeeperCluster )
			return -1;
		
    	try {
    		String serverListString = CollectionUtil.toString( zooKeeperCluster.getServerList() );
    		
    		String insertSql = StringUtil.replaceSequenced( SQL_ADD_ZOOKEEPER_CLUSTER, zooKeeperCluster.getClusterName(), serverListString, zooKeeperCluster.getDescription() );
			int key	= DbcpUtil.executeInsertAndReturnGeneratedKeys( insertSql );
			return key;
		} catch ( Exception e ) {
			throw new DaoException( "Error when add zooKeeperCluster" + zooKeeperCluster + ", Error: " + e.getMessage(), e );
		}
	}
}
