package com.taobao.taokeeper.dao.impl;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.taokeeper.common.constant.SqlTemplate;
import com.taobao.taokeeper.dao.ReportDAO;
import com.taobao.taokeeper.model.TaoKeeperStat;
import common.toolkit.java.entity.DateFormat;
import common.toolkit.java.entity.db.DBConnectionResource;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.DateUtil;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.collection.CollectionUtil;
import common.toolkit.java.util.db.DbcpUtil;

/**
 * Description: Access DB for taokeeper_stat
 * @author yinshi.nc
 * @since 2012-01-05
 */
public class ReportDAOImpl implements ReportDAO {

	@Override
	public void addTaoKeeperStat( TaoKeeperStat taoKeeperStat ) throws DaoException {

		if ( null == taoKeeperStat ) {
			return;
		}
		try {
			String insertSql = StringUtil.replaceSequenced( SqlTemplate.SQL_INSERT_TAOKEEPER_STAT, taoKeeperStat.getClusterId() + "",
					StringUtil.trimToEmpty( taoKeeperStat.getServer() ),
					StringUtil.defaultIfBlank( taoKeeperStat.getStatDateTime(), DateUtil.getNowTime( DateFormat.DateTime ) ),
					StringUtil.defaultIfBlank( taoKeeperStat.getStatDate(), DateUtil.getNowTime( DateFormat.Date ) ), taoKeeperStat.getConnections()
							+ "", taoKeeperStat.getWatches() + "", taoKeeperStat.getSendTimes() + "", taoKeeperStat.getReceiveTimes() + "",
					taoKeeperStat.getNodeCount() + "" );

			DbcpUtil.executeInsert( insertSql );
		} catch ( Exception e ) {
			throw new DaoException( "Error when add taoKeeperStat" + taoKeeperStat + ", Error: " + e.getMessage(), e );
		}
	}

	@Override
	public List< TaoKeeperStat > queryTaoKeeperStatByClusterIdAndServerAndStatDate( int clusterId, String server, String statDate )
			throws DaoException {

		if ( 0 == clusterId || StringUtil.isBlank( server ) || StringUtil.isBlank( statDate ) ) {
			return new ArrayList< TaoKeeperStat >();
		}

		List< TaoKeeperStat > taoKeeperStatList = new ArrayList< TaoKeeperStat >();

		ResultSet rs = null;
		DBConnectionResource myResultSet = null;
		try {
			myResultSet = DbcpUtil.executeQuery( StringUtil.replaceSequenced( SqlTemplate.SQL_QUERY_TAOKEEPER_STAT_BY_CLUSTERID_SERVER_DATE,
					clusterId, server, statDate ) );
			if ( null == myResultSet ) {
				return new ArrayList< TaoKeeperStat >();
			}
			rs = myResultSet.resultSet;
			if ( null == rs ) {
				return new ArrayList< TaoKeeperStat >();
			}
			while ( rs.next() ) {

				server = StringUtil.trimToEmpty( rs.getString( "server" ) );
				String statDateTime = StringUtil.trimToEmpty( StringUtil.trimToEmpty( rs.getString( "stat_date_time" ) ).replaceFirst( statDate, EMPTY_STRING ) );
				int connections 	= rs.getInt( "connections" );
				int watches 		= rs.getInt( "watches" );
				long sendTimes 		= rs.getLong( "send_times" );
				long receiveTimes 	= rs.getLong( "receive_times" );
				int nodeCount 		= rs.getInt( "node_count" );
				taoKeeperStatList.add( new TaoKeeperStat( clusterId, server, statDateTime, statDate, connections, watches, sendTimes, receiveTimes,
						nodeCount ) );
			}
			return taoKeeperStatList;
		} catch ( Exception e ) {
			throw new DaoException( "Error when queryTaoKeeperStatByClusterIdAndServerAndStatDate: clusterId:" + clusterId + " server:" + server
					+ "statDate:" + statDate + ", Error: " + e.getMessage(), e );
		} finally {
			if ( null != myResultSet ) {
				DbcpUtil.closeResultSetAndStatement( rs, myResultSet.statement );
				DbcpUtil.returnBackConnectionToPool( myResultSet.connection );
			}
		}

	}

	@Override
	public Map< String, List< TaoKeeperStat > > queryStatByClusterIdAndStatDate( int clusterId, String statDate ) throws DaoException {

		if ( 0 == clusterId || StringUtil.isBlank( statDate ) ) {
			return CollectionUtil.emptyMap();
		}

		Map< String, List< TaoKeeperStat > > taoKeeperStatMap = new HashMap< String, List< TaoKeeperStat > >();

		ResultSet rs = null;
		DBConnectionResource myResultSet = null;
		try {
			myResultSet = DbcpUtil.executeQuery( StringUtil.replaceSequenced( SqlTemplate.SQL_QUERY_TAOKEEPER_STAT_BY_CLUSTERID_DATE, clusterId,
					statDate ) );
			if ( null == myResultSet ) {
				return CollectionUtil.emptyMap();
			}
			rs = myResultSet.resultSet;
			if ( null == rs ) {
				return CollectionUtil.emptyMap();
			}
			while ( rs.next() ) {
				String server = StringUtil.trimToEmpty( rs.getString( "server" ) );
				String statDateTime = StringUtil.trimToEmpty( rs.getString( "stat_date_time" ) );
				int connections = rs.getInt( "connections" );
				int watches = rs.getInt( "watches" );
				int sendTimes = rs.getInt( "send_times" );
				int receiveTimes = rs.getInt( "receive_times" );
				int nodeCount = rs.getInt( "node_count" );
				this.addStatToTaokeeperStatMap( taoKeeperStatMap, new TaoKeeperStat( clusterId, server, statDateTime, statDate, connections, watches,
						sendTimes, receiveTimes, nodeCount ) );
			}

			return taoKeeperStatMap;

		} catch ( Exception e ) {
			throw new DaoException( "Error when queryTaoKeeperStatByClusterIdAndServerAndStatDate: clusterId:" + clusterId + "statDate:" + statDate
					+ ", Error: " + e.getMessage(), e );
		} finally {
			if ( null != myResultSet ) {
				DbcpUtil.closeResultSetAndStatement( rs, myResultSet.statement );
				DbcpUtil.returnBackConnectionToPool( myResultSet.connection );
			}
		}
	}

	/**
	 * Tool: Add taoKeeperStat to Map<String, List< TaoKeeperStat > >
	 * taoKeeperStatMap
	 * */
	private void addStatToTaokeeperStatMap( Map< String, List< TaoKeeperStat > > taoKeeperStatMap, TaoKeeperStat taoKeeperStat ) {

		if ( null == taoKeeperStatMap ) {
			taoKeeperStatMap = new HashMap< String, List< TaoKeeperStat > >();
		}

		String server = StringUtil.trimToEmpty( taoKeeperStat.getServer() );
		if ( StringUtil.isBlank( server ) )
			return;

		List< TaoKeeperStat > taoKeeperStatList = null;
		// The server not in keySet
		if ( null == ( taoKeeperStatList = taoKeeperStatMap.get( server ) ) ) {
			taoKeeperStatList = new ArrayList< TaoKeeperStat >();
			taoKeeperStatList.add( taoKeeperStat );
			taoKeeperStatMap.put( server, taoKeeperStatList );
		}
		// The server in keySet
		else {
			taoKeeperStatList.add( taoKeeperStat );
			taoKeeperStatMap.put( server, taoKeeperStatList );
		}

	}

}
