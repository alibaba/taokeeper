package com.taobao.taokeeper.dao;
import java.util.List;

import com.taobao.taokeeper.model.ZooKeeperCluster;
import common.toolkit.java.exception.DaoException;

/**
 * Description: Access DB for zookeeper cluster
 * @author   yinshi.nc
 * @version  
 * @since   
 * @Date	 2011-10-28
 */
public interface ZooKeeperClusterDAO {
	
	public ZooKeeperCluster getZooKeeperClusterByCulsterId( int clusterId )throws DaoException;
	
	public List<ZooKeeperCluster> getAllDetailZooKeeperCluster( )throws DaoException;
	
	/** 获取所有clusterid name*/
	public List<ZooKeeperCluster> getAllZooKeeperClusterIdAndName( )throws DaoException;
	
	
	public boolean updateZooKeeperSettingsByClusterId( ZooKeeperCluster zooKeeperCluster ) throws DaoException;
	
	/***
	 * 添加一个zookeeper集群
	 */
	public int addZooKeeper( ZooKeeperCluster zooKeeperCluster ) throws DaoException;
	
	
}
