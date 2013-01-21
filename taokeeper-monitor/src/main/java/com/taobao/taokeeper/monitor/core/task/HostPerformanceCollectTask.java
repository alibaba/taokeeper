package com.taobao.taokeeper.monitor.core.task;

import static com.taobao.taokeeper.common.constant.SystemConstant.DELAY_MINS_OF_TWO_CYCLE_ALIVE_CHECK_ZOOKEEPER;
import static com.taobao.taokeeper.common.constant.SystemConstant.MINS_RATE_OF_COLLECT_HOST_PERFORMANCE;
import static common.toolkit.java.constant.SymbolConstant.COLON;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.dao.AlarmSettingsDAO;
import com.taobao.taokeeper.dao.ZooKeeperClusterDAO;
import com.taobao.taokeeper.model.AlarmSettings;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.monitor.core.ThreadPoolManager;
import com.taobao.taokeeper.monitor.core.task.runable.ZKServerPerformanceCollector;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.DateUtil;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.ThreadUtil;

/**
 * Description: Collect info of host performance
 * @author yinshi.nc
 * @Date 2011-10-28
 */
public class HostPerformanceCollectTask implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger( HostPerformanceCollectTask.class );

	@Override
	public void run() {

		while ( true ) {
			
			if( !GlobalInstance.need_host_performance_collect ){
				LOG.info( "No need to host_performance_collect, need_host_performance_collect=" + GlobalInstance.need_host_performance_collect );
				ThreadUtil.sleep( 1000 * 60 * MINS_RATE_OF_COLLECT_HOST_PERFORMANCE  );
				continue;
			}
			
			try {
				// 根据clusterId来获取一个zk集群
				WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
				ZooKeeperClusterDAO zooKeeperClusterDAO = ( ZooKeeperClusterDAO ) wac.getBean( "zooKeeperClusterDAO" );
				AlarmSettingsDAO alarmSettingsDAO = ( AlarmSettingsDAO ) wac.getBean( "alarmSettingsDAO" );
				try {
					List< ZooKeeperCluster > zooKeeperClusterSet = null;
					Map< Integer, ZooKeeperCluster > zooKeeperClusterMap = GlobalInstance.getAllZooKeeperCluster();
					if ( null == zooKeeperClusterMap ) {
						zooKeeperClusterSet = zooKeeperClusterDAO.getAllDetailZooKeeperCluster();
					} else {
						zooKeeperClusterSet = new ArrayList< ZooKeeperCluster >();
						zooKeeperClusterSet.addAll( zooKeeperClusterMap.values() );
					}

					if ( null == zooKeeperClusterSet || zooKeeperClusterSet.isEmpty() ) {
						LOG.warn( "No zookeeper cluster config" );
					} else {
						for ( ZooKeeperCluster zookeeperCluster : zooKeeperClusterSet ) { // 对每个cluster处理
							if ( null != zookeeperCluster && null != zookeeperCluster.getServerList() ) {
								AlarmSettings alarmSettings = alarmSettingsDAO.getAlarmSettingsByCulsterId( zookeeperCluster.getClusterId() );
								for ( String server : zookeeperCluster.getServerList() ) {
									server = StringUtil.trimToEmpty( server );
									if ( StringUtil.isBlank( server ) )
										continue;
									String ip = StringUtil.trimToEmpty( server.split( COLON )[0] );
									ThreadPoolManager.addJobToZKServerPerformanceCollectorExecutor( 
											new ZKServerPerformanceCollector( ip, alarmSettings, zookeeperCluster )
											);
								}// for each server
							}// for each cluster
						}
					}//Finish all cluster HostPerformanceEntity collect
					LOG.info( "Finish all cluster HostPerformanceEntity collect" );
					GlobalInstance.timeOfUpdateHostPerformanceSet = DateUtil.convertDate2String( new Date() );
				} catch ( DaoException daoException ) {
					LOG.warn( "Error when handle data base" + daoException.getMessage() );
				} catch ( Throwable e ) {
					LOG.error( "System Error：" + e.getMessage() );
					e.printStackTrace();
				}
				// collect per 2 mins
				Thread.sleep( 1000 * 60 * MINS_RATE_OF_COLLECT_HOST_PERFORMANCE );
			} catch ( Throwable e ) {
				LOG.error( "Error when HostPerformanceEntityCollectJob: " + e.getMessage() );
				e.printStackTrace();
			}
		}
	}
	
}
