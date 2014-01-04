package com.taobao.taokeeper.common;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.taobao.taokeeper.model.AlarmSettings;
import com.taobao.taokeeper.model.TaoKeeperSettings;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.model.ZooKeeperStatus;
import com.taobao.taokeeper.model.ZooKeeperStatusV2;

import common.toolkit.java.entity.HostPerformanceEntity;
import common.toolkit.java.entity.io.Connection;

/**
 * Description: 全局Instance
 * 
 * @author yinshi.nc
 * @Date 2011-10-31
 */
public class GlobalInstance {

	/** 上次更新ZooKeeper时间，毫秒 */
	private static long GlobalInstance_TIME_OF_UPDATE_ZOOKEEPER_NODE;
	
	/** 是否要进行节点自检 */
	public static boolean need_node_alive_check = true;
	/** 是否需要进行机器状态收集*/
	public static boolean need_host_performance_collect = true;
	/** 多余节点监控 */
	public static boolean need_node_checker = true;
	
	/** 多余节点监控 */
	public static boolean need_zk_status_collect = true;
	
	public static boolean need_client_throughput_stat = true;
	

	// ZooKeeper集群中每台机器状态信息
	private static Map< String/** IP */
	, ZooKeeperStatusV2 > zooKeeperStatusSet = new ConcurrentHashMap< String, ZooKeeperStatusV2 >();
	// 节点自检结果 0:不确定 1:OK 2: ERROR
	private static Map< String/** IP */
	, Integer > zooKeeperStatusTypeSet = new ConcurrentHashMap< String, Integer >();

	// ZooKeeper集群中每台机器状态信息-更新时间
	public static String timeOfUpdateZooKeeperStatusSet = EMPTY_STRING;

	// 机器HostPerformanceEntity
	private static Map< String/** IP */ , HostPerformanceEntity > hostPerformanceEntitySet = new ConcurrentHashMap< String, HostPerformanceEntity >();
	// ZooKeeper集群中每台机器状态信息-更新时间
	public static String timeOfUpdateHostPerformanceSet = EMPTY_STRING;

	/** 数据库中集群信息 */
	static Map< Integer/** clusterId */
	, ZooKeeperCluster > zooKeeperClusterMap = new ConcurrentHashMap< Integer, ZooKeeperCluster >();

	/** zookeeper 客户端连接情况 */
	public static Map< Integer/** clusterId */ , Map< String, Connection > > zooKeeperClientConnectionMapOfCluster = new ConcurrentHashMap< Integer/**clusterId */, Map< String, Connection > >();
	
	
	/** zookeeper 每个Server上的客户端连接情况 */
	public static Map< String/** clusterId-serverIp */ , Map< String, Connection > > zooKeeperClientConnectionMapOfServer = new ConcurrentHashMap< String/**clusterId */, Map< String, Connection > >();
	

	/** 报警信息配置 */
	static Map< Integer/** clusterId */
	, AlarmSettings > alarmSettingsMap = new ConcurrentHashMap< Integer, AlarmSettings >();

	/** 全局报警开关 */
	public static AtomicBoolean needAlarm = new AtomicBoolean( false );

	/** 当前正在检查的集群 */
	public static ConcurrentLinkedQueue< String > allCheckingCluster = new ConcurrentLinkedQueue< String >();
	private static Object lockOfAllCheckingCluster = new Object();

	public static TaoKeeperSettings taoKeeperSettings = new TaoKeeperSettings( 0, "不应该不出我，除非数据库出问题", 2, "不应该不出我，除非数据库出问题" );

	/** 设置为当前时间 */
	public static void setGlobalInstanceTimeOfUpdateZooKeeperNode( long time ) {
		GlobalInstance_TIME_OF_UPDATE_ZOOKEEPER_NODE = time;
	}

	/** 设置为当前时间 */
	public static long getGlobalInstanceTimeOfUpdateZooKeeperNode() {
		return GlobalInstance_TIME_OF_UPDATE_ZOOKEEPER_NODE;
	}

	/** 将机器的HostPerformanceEntity放置到全局变量中去 */
	public static void putHostPerformanceEntity( String ip, HostPerformanceEntity hostPerformanceEntity ) {
		hostPerformanceEntitySet.put( ip, hostPerformanceEntity );
	}

	/** 根据ip获取机器HostPerformanceEntity */
	public static HostPerformanceEntity getHostPerformanceEntity( String ip ) {
		return hostPerformanceEntitySet.get( ip );
	}

	/** 将所有机器的HostPerformanceEntity返回 */
	public static Map< String/** IP */
	, HostPerformanceEntity > getAllHostPerformanceEntity() {
		return hostPerformanceEntitySet;
	}

	/** 将机器的状态信息放置到全局变量中去 */
	public static void putZooKeeperStatus( String ip, ZooKeeperStatusV2 zooKeeperStatus ) {
		zooKeeperStatusSet.put( ip, zooKeeperStatus );
	}

	/** 根据ip获取机器状态信息 */
	public static ZooKeeperStatusV2 getZooKeeperStatus( String ip ) {
		return zooKeeperStatusSet.get( ip );
	}

	/** 将所有机器的状态信息返回 */
	public static Map< String/** IP */
	, ZooKeeperStatusV2 > getAllZooKeeperStatus() {
		return zooKeeperStatusSet;
	}

	/**
	 * 将机器的自检结果放置到全局变量中去
	 * 
	 * @param statusType
	 *            : 0: CHECKING 1: OK other: ERROR
	 */
	public static void putZooKeeperStatusType( String ip, int statusType ) {
		zooKeeperStatusTypeSet.put( ip, statusType );
	}

	/**
	 * 根据ip获取机器自检结果
	 * 
	 * @return statusType: 0: CHECKING 1: OK other: ERROR
	 * */
	public static int getZooKeeperStatusType( String ip ) {
		int status = 0;
		try {
			status = zooKeeperStatusTypeSet.get( ip );
		} catch ( Exception e ) {
			return -1;
		}
		return status;
	}

	/** 将所有机器的自检结果 */
	public static Map< String/** IP */, Integer > getAllZooKeeperStatusType() {
		return zooKeeperStatusTypeSet;
	}

	
	
	
	
	/** 整个ZooKeeper集群的连接情况 zooKeeperClientConnectionMapOfCluster */
	
	/** 根据clusterId获取这个集群的所有连接 */
	public static Map< String, Connection > getZooKeeperClientConnectionMapByClusterId( int clusterId ) {
		return zooKeeperClientConnectionMapOfCluster.get( clusterId );
	}
	/** 获取所有集群的所有连接 */
	public static Map< Integer, Map< String, Connection >> getAllZooKeeperClientConnectionMap() {
		return zooKeeperClientConnectionMapOfCluster;
	}
	/** 按clusterId将连接信息放置到全局变量中去 */
	public static void putZooKeeperClientConnectionMapByClusterId( int clusterId, Map< String, Connection > connectionMap ) {
		zooKeeperClientConnectionMapOfCluster.put( clusterId, connectionMap );
	}
	/** 清空集群信息缓存 */
	public static void clearZooKeeperClientConnectionMap() {
		zooKeeperClientConnectionMapOfCluster.clear();
	}

	/** 整个ZooKeeper集群的连接情况 zooKeeperClientConnectionMapOfCluster */
	
	
	
	
	/** ZooKeeper集群中每台Server的连接情况 zooKeeperClientConnectionMapOfCluster */
	
	/** 根据clusterId-serverIp 获取这个Server的所有连接 */
	public static Map< String, Connection > getZooKeeperClientConnectionMapByClusterIdAndServerIp( String clusterIdAndServerIp ) {
		return zooKeeperClientConnectionMapOfServer.get( clusterIdAndServerIp );
	}
	/** 获取所有Server的连接 */
	public static Map< String, Map< String, Connection >> getAllZooKeeperClientConnectionMapOfServer() {
		return zooKeeperClientConnectionMapOfServer;
	}
	/** 按clusterId-serverIp将连接信息放置到全局变量zooKeeperClientConnectionMapOfServer中去 */
	public static void putZooKeeperClientConnectionMapByClusterIdAndServerIp( String clusterIdAndServerIp, Map< String, Connection > connectionMap ) {
		zooKeeperClientConnectionMapOfServer.put( clusterIdAndServerIp, connectionMap );
	}
	/** 清空集群信息缓存 */
	public static void clearZooKeeperClientConnectionMapOfServer() {
		zooKeeperClientConnectionMapOfServer.clear();
	}

	/** ZooKeeper集群中每台Server的连接情况 zooKeeperClientConnectionMapOfCluster */
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** 根据clusterId获取集群信息 */
	public static ZooKeeperCluster getZooKeeperClusterByClusterId( int clusterId ) {
		return zooKeeperClusterMap.get( clusterId );
	}

	/** 将所有集群的信息返回 */
	public static Map< Integer/** clusterId */
	, ZooKeeperCluster > getAllZooKeeperCluster() {
		return zooKeeperClusterMap;
	}

	/** 按clusterId将集群信息放置到全局变量中去 */
	public static void putZooKeeperCluster( int clusterId, ZooKeeperCluster zooKeeperCluster ) {
		zooKeeperClusterMap.put( clusterId, zooKeeperCluster );
	}

	/** 清空集群信息缓存 */
	public static void clearZooKeeperCluster() {
		zooKeeperClusterMap.clear();
	}

	/** 按clusterId将报警配置放置到全局变量中去 */
	public static void putAlarmSettings( int clusterId, AlarmSettings alarmSettings ) {
		alarmSettingsMap.put( clusterId, alarmSettings );
	}

	/** 根据clusterId获取报警配置信息 */
	public static AlarmSettings getAlarmSettingsByClusterId( int clusterId ) {
		return alarmSettingsMap.get( clusterId );
	}

	/** 将所有报警配置的信息返回 */
	public static Map< Integer/** clusterId */
	, AlarmSettings > getAllAlarmSettings() {
		return alarmSettingsMap;
	}

	/** 当前正在检查的集群 */
	public static boolean addToAllCheckingCluster( String clusterId ) {
		synchronized ( lockOfAllCheckingCluster ) {
			if ( GlobalInstance.allCheckingCluster.contains( clusterId ) )
				return false;
			GlobalInstance.allCheckingCluster.add( clusterId );
			return true;
		}
	}

	/** 当前正在检查的集群 */
	public static boolean removeFromAllCheckingCluster( String clusterId ) {
		synchronized ( lockOfAllCheckingCluster ) {
			if ( GlobalInstance.allCheckingCluster.contains( clusterId ) ) {
				return GlobalInstance.allCheckingCluster.remove( clusterId );
			}
		}
		return false;
	}

}
