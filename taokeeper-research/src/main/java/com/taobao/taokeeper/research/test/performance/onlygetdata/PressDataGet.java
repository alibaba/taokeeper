package com.taobao.taokeeper.research.test.performance.onlygetdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
 * 类说明: 进行获取数据压力测试
 * 
 * @author yinshi.nc
 */
public class PressDataGet implements Watcher {

	static Log log = LogFactory.getLog( PressDataGet.class );

	static ZooKeeper zk = null;

	static String PATH = "/YINSHI.NC-PRESS-TEST";
	static String SERVER_LIST = "10.13.44.47:2181";
	final static int SESSION_TIMEOUT = 5000;
	static int TOTAL_SUBS = 6;
	static int N_BYTE = 1; // 1字节的倍数

	static AtomicLong failTimes = new AtomicLong();

	/** 重连成功次数，这个表示这个session过期或断开后，重新建立连接并且成功的次数 */
	static AtomicLong reConnectSuccessTimes = new AtomicLong();
	/** 重连失败次数，这个表示这个session过期或断开后，重新建立连接，但是失败的次数 */
	static AtomicLong reConnectFailTimes = new AtomicLong();

	static List<Subscriber> pubList = new ArrayList<Subscriber>();
	static Set<String> pathList = new HashSet<String>();

	// 已完成任务的订阅者数量
	static AtomicInteger finshedSub = new AtomicInteger();

	public static void main( String[] args ) throws IOException, KeeperException, InterruptedException {

		log.info( "全读压测程序开始" );

		if ( args.length != 3 ) {
			throw new IllegalArgumentException( "请指定：zk服务器列表，订阅者数量, 数据大小(这里填写一个数字，表明是1字节的倍数)，如1024，那么表示数据大小是1K" );
		}
		PressDataGet.SERVER_LIST = StringUtil.defaultIfBlank( args[0], SERVER_LIST );
		PressDataGet.TOTAL_SUBS = Integer.parseInt( StringUtil.defaultIfBlank( args[1], TOTAL_SUBS + EmptyObjectConstant.EMPTY_STRING ) );
		PressDataGet.PATH += "-"
				+ StringUtil.defaultIfBlank( SystemUtil.getHostName(), System.currentTimeMillis() + EmptyObjectConstant.EMPTY_STRING );
		PressDataGet.N_BYTE = Integer.parseInt( StringUtil.defaultIfBlank( args[2], N_BYTE + EmptyObjectConstant.EMPTY_STRING ) );

		try {
			// 准备订阅者
			for ( int i = 0; i < TOTAL_SUBS; i++ ) {
				String path = PATH + "-" + i;
				pubList.add( new Subscriber( SERVER_LIST, SESSION_TIMEOUT, path ) );
				pathList.add( path );
			}
			// 准备Path,并发布初始数据
			PressDataGet press = new PressDataGet();
			if ( press.createAndInitPaths() ) {
				// 启动统计程序
				StatisticsUtil.start( 10 );
				for ( Subscriber subscriber : pubList ) {
					subscriber.start();
				}
			}
		} finally {
			while ( finshedSub.get() < TOTAL_SUBS ) {
				log.error( "错误次数: " + PressDataGet.failTimes );
				log.warn( "重连成功次数：" + PressDataGet.reConnectSuccessTimes );
				log.warn( "重连失败次数：" + PressDataGet.reConnectFailTimes );
				Thread.sleep( 10000 );
			}
			finalDeleteNode();
			StatisticsUtil.stop();
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
				if( null != zk.exists( path, false ) ){
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

	/**
	 * 删除节点
	 */
	static void finalDeleteNode() {

		try {
			ZooKeeper zk = new ZooKeeper( SERVER_LIST, SESSION_TIMEOUT, null );
			for ( String path : pathList ) {
				try {
					int version = zk.exists( path, null ).getVersion();
					zk.delete( path, version );
				} catch ( Exception e ) {
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

	@Override
	public void process( WatchedEvent event ) {
		// TODO Auto-generated method stub
	}

}
