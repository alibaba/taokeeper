package com.taobao.taokeeper.research.test.performance.setdataandwatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.ZooKeeper;

import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.system.SystemUtil;

/**
 * ��˵��: ���з������ѹ������
 * 
 * @author yinshi.nc
 */
public class PressDataSetAndWatcher implements Watcher {

	static Log log = LogFactory.getLog( PressDataSetAndWatcher.class );

	static String PATH = "/YINSHI.NC-PRESS-TEST";
	static String SERVER_LIST = "10.13.44.47:2181";
	final static int SESSION_TIMEOUT = 5000;
	static int TOTAL_PUBS = 6;
	static int N_BYTE = 1;
	static boolean isFinish = false;

	static ZooKeeper zk = null;

	static AtomicLong failTimes = new AtomicLong();

	/** 重连成功次数，这个表示这个session过期或断开后，重新建立连接并且成功的次数 */
	static AtomicLong reConnectSuccessTimes = new AtomicLong();
	/** 重连失败次数，这个表示这个session过期或断开后，重新建立连接，但是失败的次数 */
	static AtomicLong reConnectFailTimes = new AtomicLong();

	static List<WatchedPublisher> pubList = new ArrayList<WatchedPublisher>();
	static Set<String> pathList = new HashSet<String>();

	public static void main( String[] args ) throws IOException, KeeperException, InterruptedException {

		log.info( "读写压测程序开始" );

		if ( args.length != 3 ) {
			throw new IllegalArgumentException( "请指定：zk服务器列表，发布者数量, 数据大小(这里填写一个数字，表明是1字节的倍数)，如1024，那么表示数据大小是1K" );
		}
		PressDataSetAndWatcher.SERVER_LIST = StringUtil.defaultIfBlank( args[0], SERVER_LIST );
		PressDataSetAndWatcher.TOTAL_PUBS = Integer.parseInt( StringUtil.defaultIfBlank( args[1], TOTAL_PUBS + EmptyObjectConstant.EMPTY_STRING ) );
		PressDataSetAndWatcher.PATH += "-"
				+ StringUtil.defaultIfBlank( SystemUtil.getHostName(), System.currentTimeMillis() + EmptyObjectConstant.EMPTY_STRING );
		PressDataSetAndWatcher.N_BYTE = Integer.parseInt( StringUtil.defaultIfBlank( args[2], N_BYTE + EmptyObjectConstant.EMPTY_STRING ) );

		try {
			// 准备发布者
			for ( int i = 0; i < TOTAL_PUBS; i++ ) {
				String path = PATH + "-" + i;
				pubList.add( new WatchedPublisher( SERVER_LIST, SESSION_TIMEOUT, path, N_BYTE ) );
				pathList.add( path );
			}
			PressDataSetAndWatcher press = new PressDataSetAndWatcher();
			if ( press.createAndInitPaths() ) {
				// 启动统计程序
				StatisticsUtil.start( 20 );
				for ( WatchedPublisher publisher : pubList ) {
					publisher.start();
				}
			}
		} finally {
			while ( !isFinish ) {
				log.error( "错误次数: " + PressDataSetAndWatcher.failTimes );
				log.warn( "重连成功次数：" + PressDataSetAndWatcher.reConnectSuccessTimes );
				log.warn( "重连失败次数：" + PressDataSetAndWatcher.reConnectFailTimes );
				Thread.sleep( 10000 );
			}
			StatisticsUtil.stop();
			closeZk( zk );
		}
	}

	boolean createAndInitPaths() {

		try {
			zk = new ZooKeeper( SERVER_LIST, SESSION_TIMEOUT, this );
			log.info( "需要创建的PATH数是：" + pathList.size() );
			log.info( "等待path创建" );
			while ( null != zk && States.CONNECTED != zk.getState() ) {
				try {
					Thread.sleep( 20 );
				} catch ( Throwable t ) {
					t.printStackTrace();
				}
			}
			for ( String path : pathList ) {
				if ( null != zk.exists( path, false ) ) {
					zk.delete( path, -1 );
				}
				zk.create( path, EmptyObjectConstant.EMPTY_STRING.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
				log.info( "Create path: " + path );
				Thread.sleep( 20 );
				zk.setData( path, new byte[N_BYTE * 1], -1 );
			}
			log.info( "PATH创建完毕" );
			return true;
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}

	}

	static void closeZk( ZooKeeper zk ) {
		if ( null != zk ) {
			try {
				zk.close();
			} catch ( InterruptedException e ) {
			}finally{
				zk = null;
			}
		}
	}

	@Override
	public void process( WatchedEvent event ) {
	}

}
