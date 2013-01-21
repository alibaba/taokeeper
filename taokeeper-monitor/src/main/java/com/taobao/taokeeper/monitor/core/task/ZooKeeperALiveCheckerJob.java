package com.taobao.taokeeper.monitor.core.task;
import static com.taobao.taokeeper.common.constant.SystemConstant.DELAY_MINS_OF_TWO_CYCLE_ALIVE_CHECK_ZOOKEEPER;
import static com.taobao.taokeeper.common.constant.SystemConstant.ZOOKEEPER_MONITOR_PATH;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.SymbolConstant.COLON;

import java.util.ArrayList;
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
import com.taobao.taokeeper.model.Subscriber;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.monitor.core.ThreadPoolManager;
import com.taobao.taokeeper.monitor.core.task.runable.ZKServerAliveCheck;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.ThreadUtil;
/**
 * Description: Check if alive one node(ip).
 * 
 * @author 银时 yinshi.nc@taobao.com
 * @Date 2011-10-28
 */
public class ZooKeeperALiveCheckerJob implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger( ZooKeeperALiveCheckerJob.class );

	@Override
	public void run() {

		while ( true ) {
			
			if( !GlobalInstance.need_node_alive_check ){
				LOG.info( "No need to node_alive_check, need_node_alive_check=" + GlobalInstance.need_node_alive_check );
				ThreadUtil.sleep( 1000 * 60 * DELAY_MINS_OF_TWO_CYCLE_ALIVE_CHECK_ZOOKEEPER  );
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
						LOG.warn( "No zookeeper cluster" );
					} else {
						for ( final ZooKeeperCluster zookeeperCluster : zooKeeperClusterSet ) { // 对每个cluster处理

							// 如果已经有人在检测了
							if ( !GlobalInstance.addToAllCheckingCluster( zookeeperCluster.getClusterId() + EMPTY_STRING ) ) {
								// 那么不检测了。
								continue;
							}

							if ( null != zookeeperCluster && null != zookeeperCluster.getServerList() ) {

								final AlarmSettings alarmSettings = alarmSettingsDAO.getAlarmSettingsByCulsterId( zookeeperCluster.getClusterId() );
								if ( null == alarmSettings ) {
									// 一定要进行释放。
									GlobalInstance.removeFromAllCheckingCluster( zookeeperCluster.getClusterId() + EMPTY_STRING );
									continue;
								}
								ThreadPoolManager.addJobToZooKeeperNodeAliveCheckExecutor( new ZKServerAliveCheck( zookeeperCluster, alarmSettings ) );
							}// if cluster not null
						}// for each cluster
					}// for each server
				} catch ( DaoException daoException ) {
					LOG.warn( "Error when handle data base" + daoException.getMessage() );
				} catch ( Exception e ) {
					LOG.error( "Exception when check zk server alive, Error: " + e.getMessage(), e );
				}
				// 每1分钟收集一次检测
				Thread.sleep( 1000 * 60 * DELAY_MINS_OF_TWO_CYCLE_ALIVE_CHECK_ZOOKEEPER );
				// Thread.sleep( 1000 * 20 *
				// DELAY_MINS_OF_TWO_CYCLE_ALIVE_CHECK_ZOOKEEPER );
			} catch ( Throwable e ) {
				LOG.error( "Exception when check zk server alive, Error: " + e.getMessage(), e );
			}
		}
	}

	/**
	 * 检查存活性，不报警. 这个方法通常是在添加新的zk集群后调用的.
	 * 
	 * @param server是一个包含ip的参数
	 */
	public boolean checkAliveNoAlarm( String server ) {

		if ( StringUtil.isBlank( server ) )
			return true;

		String ip = server.split( COLON )[0];

		// 进行两次检查
		Subscriber sub = null;
		try {
			sub = new Subscriber( server, ZOOKEEPER_MONITOR_PATH, 5 );
			// 判断一个节点已经挂了：连续两次检测均失败。
			if ( !sub.checkIfAlive() ) {
				if ( !sub.checkIfAlive() ) { // 连续两次check失败
					GlobalInstance.putZooKeeperStatusType( ip, 2 );
					LOG.info( "对 " + server + "进行节点自检ERROR" );
					return false;
				}
				GlobalInstance.putZooKeeperStatusType( ip, 1 );
				LOG.info( "对 " + server + "进行节点自检OK" );
				return true;
			}
			GlobalInstance.putZooKeeperStatusType( ip, 1 );
			LOG.info( "对 " + server + "进行节点自检OK" );
			return true;
		} catch ( Throwable e ) {
			// 报警
			GlobalInstance.putZooKeeperStatusType( ip, 2 );
			LOG.info( "对 " + server + "进行节点自检ERROR" );
		} finally {
			if ( null != sub )
				sub.close();
		}
		return true;
	}

	public static void main( String[] args ) {
		Thread thread = new Thread( new ZooKeeperALiveCheckerJob() );
		thread.start();
	}
}
