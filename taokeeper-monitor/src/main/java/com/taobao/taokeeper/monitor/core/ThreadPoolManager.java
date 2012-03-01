package com.taobao.taokeeper.monitor.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 线程池管理
 * 
 * @author 银时 yinshi.nc@taobao.com
 * @Date Dec 25, 2011
 */
public class ThreadPoolManager {

	private static Logger LOG = LoggerFactory.getLogger( ThreadPoolManager.class );

	private static int SIZE_OF_ZKNODEALIVECHECK_EXECUTOR = 5;
	private static int SIZE_OF_MESSAGESEND_EXECUTOR = 5;
	private static int SIZE_OF_ZKSERVERSTATUS_COLLECTOR_EXECUTOR = 3;
	private static int SIZE_OF_ZKSERVERPERFORMAN_CECOLLECTOR_EXECUTOR = 3;
	private static int SIZE_OF_ZKCLUSTERCONFIG_DUMPER_EXECUTOR = 2;
	
	
	public static void init(){
		if( null == zooKeeperNodeAliveCheckExecutor ){
			LOG.info( "Start init ThreadPoolManager..." );
			zooKeeperNodeAliveCheckExecutor 	 = Executors.newFixedThreadPool( SIZE_OF_ZKNODEALIVECHECK_EXECUTOR );
			messageSendExecutor             	 = Executors.newFixedThreadPool( SIZE_OF_MESSAGESEND_EXECUTOR );
			zkServerStatusCollectorExecutor 	 = Executors.newFixedThreadPool( SIZE_OF_ZKSERVERSTATUS_COLLECTOR_EXECUTOR );
			zkServerPerformanceCollectorExecutor = Executors.newFixedThreadPool( SIZE_OF_ZKSERVERPERFORMAN_CECOLLECTOR_EXECUTOR );
			zkClusterConfigDumperExecutor 		 = Executors.newFixedThreadPool( SIZE_OF_ZKCLUSTERCONFIG_DUMPER_EXECUTOR );
		}
	}
	
	
	
	/** 节点自检 线程池 */
	private static ExecutorService zooKeeperNodeAliveCheckExecutor;
	public static void addJobToZooKeeperNodeAliveCheckExecutor( Runnable command ){
		init();
		zooKeeperNodeAliveCheckExecutor.execute( command );
	}
	
	/** 消息发送 线程池 */
	private static ExecutorService messageSendExecutor;
	public static void addJobToMessageSendExecutor( Runnable command ){
		init();
		messageSendExecutor.execute( command );
	}
	
	/** 收集ZKServer状态信息 线程池 */
	private static ExecutorService zkServerStatusCollectorExecutor;
	public static void addJobToZKServerStatusCollectorExecutor( Runnable command ){
		init();
		zkServerStatusCollectorExecutor.execute( command );
	}
	
	
	/** 收集ZKServer机器信息 线程池 */
	private static ExecutorService zkServerPerformanceCollectorExecutor;
	public static void addJobToZKServerPerformanceCollectorExecutor( Runnable command ){
		init();
		zkServerPerformanceCollectorExecutor.execute( command );
	}
	
	/** Dump zk cluster config info to memeory*/
	private static ExecutorService zkClusterConfigDumperExecutor;
	public static void addJobToZKClusterDumperExecutor( Runnable command ){
		init();
		zkClusterConfigDumperExecutor.execute( command );
	}
	
	
	

}
