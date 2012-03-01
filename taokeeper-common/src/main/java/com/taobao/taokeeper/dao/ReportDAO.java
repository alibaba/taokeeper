package com.taobao.taokeeper.dao;
import java.util.List;
import java.util.Map;

import com.taobao.taokeeper.model.TaoKeeperStat;
import common.toolkit.java.exception.DaoException;

/**
 * Description: Access DB for taokeeper_stat
 * @author   yinshi.nc
 * @since 2012-01-05
 */
public interface ReportDAO {
	
	/**
	 * 添加一条统计信息
	 * @param taoKeeperStat
	 * @throws DaoException
	 */
	public void addTaoKeeperStat( TaoKeeperStat taoKeeperStat ) throws DaoException;
	
	
	/**
	 * 根据cluster_id, server, stat_date 来查询一个统计信息
	 * @param clusterId
	 * @param server	ip
	 * @param statDate	2012-01-05
	 * @throws DaoException
	 */
	public List<TaoKeeperStat> queryTaoKeeperStatByClusterIdAndServerAndStatDate( int clusterId, String server, String statDate ) throws DaoException;
	
	
	/**
	 * 根据cluster_id, stat_date 来查询一个集群的统计信息
	 * @param clusterId
	 * @param statDate	2012-01-05
	 * @throws DaoException
	 */
	public Map<String, List< TaoKeeperStat > > queryStatByClusterIdAndStatDate( int clusterId, String statDate ) throws DaoException;
	
	
}
