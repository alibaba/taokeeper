package com.taobao.zookeeper.presstest.watcher;

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
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.StringUtil;


/**
 * ��˵��: Watcher ����
 * 
 * @author yinshi.nc
 */
public class PressWatcher{

	static Log log = LogFactory.getLog( PressWatcher.class );

	static String PATH = "/YINSHI.NC-PRESS-TEST";
	static String SERVER_LIST = "10.13.44.47:2181";
	final static int SESSION_TIMEOUT = 5000;
	static int TOTAL_PUBS = 6;
	static long PUT_TIMES = 10000000;
	static int N_256 = 1; // 256�ֽڵı���

	static AtomicLong failTimes = new AtomicLong();
	
	/** �����ɹ����������ʾ���session���ڻ�Ͽ������½������Ӳ��ҳɹ��Ĵ��� */
	static AtomicLong reConnectSuccessTimes = new AtomicLong();
	/** ����ʧ�ܴ��������ʾ���session���ڻ�Ͽ������½������ӣ�����ʧ�ܵĴ��� */
	static AtomicLong reConnectFailTimes = new AtomicLong();
	
	static List< Publisher > pubList = new ArrayList< Publisher >();
	static List< Subscriber > subList = new ArrayList< Subscriber >();
	static Set< String > pathList = new HashSet< String >();

	// ���������ķ���������
	static AtomicInteger finshedPub = new AtomicInteger();

	public static void main( String[] args ) throws IOException, KeeperException, InterruptedException {

		if ( args.length != 5 ) {
			throw new IllegalArgumentException(
					"��ָ����zk�������б?����������, ÿ�������߷���, PATH��׺, ��ݴ�С(������дһ�����֣�������256�ֽڵı���)����4����ô��ʾ��ݴ�С��1K" );
		}
		PressWatcher.SERVER_LIST = StringUtil.defaultIfBlank( args[0], SERVER_LIST );
		PressWatcher.TOTAL_PUBS = Integer.parseInt( StringUtil.defaultIfBlank( args[1], TOTAL_PUBS + EmptyObjectConstant.EMPTY_STRING ) );
		PressWatcher.PUT_TIMES = Long.parseLong( StringUtil.defaultIfBlank( args[2], PUT_TIMES + EmptyObjectConstant.EMPTY_STRING ) );
		PressWatcher.PATH += StringUtil.defaultIfBlank( args[3], System.currentTimeMillis() + EmptyObjectConstant.EMPTY_STRING );
		PressWatcher.N_256 = Integer.parseInt( StringUtil.defaultIfBlank( args[4], N_256 + EmptyObjectConstant.EMPTY_STRING ) );

		try {
			
			
			// ׼��Path
			if ( !createAndInitPaths() ){
				log.error( "Path����ʧ��" );
				System.exit( 0 );
			}
			// ׼�������ߺͶ�����
			for ( String path : pathList ) {
				pubList.add( new Publisher( SERVER_LIST, SESSION_TIMEOUT, path, PUT_TIMES, N_256 * 256 ) );
				for( int j = 0; j < 3 ;j++){
					subList.add( new Subscriber( SERVER_LIST, SESSION_TIMEOUT, path ) );
				}
			}
			
			// ����ͳ�Ƴ���
			StatisticsUtil.start( 10 );
			for ( Publisher publisher : pubList ) {
				publisher.start();
			}
			log.info( "�ȴ���������" );
			Thread.sleep( 10000 );
			for ( Subscriber subscriber : subList ) {
				subscriber.start();
			}
			
		} finally {
			while ( finshedPub.get() < TOTAL_PUBS ) {
				log.warn( "setData/getData�������: " + failTimes + " | Session�����ɹ�����" + PressWatcher.reConnectSuccessTimes + ", ʧ�ܴ���" + PressWatcher.reConnectFailTimes);
				Thread.sleep( 10000 );
			}
			finalDeleteNode();
			StatisticsUtil.stop();
		}
	}

	static boolean createAndInitPaths() {

		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper( SERVER_LIST, SESSION_TIMEOUT, null );
			log.info( "��Ҫ������PATH���ǣ�" + TOTAL_PUBS );
			log.info( "�ȴ�path����" ); 
			Thread.sleep( 10000 );
			for ( int i = 0; i < TOTAL_PUBS; i++ ) {
				String path = PATH + System.currentTimeMillis();
				zk.create( path, EmptyObjectConstant.EMPTY_STRING.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );
				Thread.sleep( 20 );
				zk.setData( path, new byte[ N_256 * 256 ], -1 );
				pathList.add( path );
			}
			log.info( "PATH�������" );
			return true;
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				zk.close();
			} catch ( InterruptedException e ) {
			}
		}

	}

	/**
	 * ɾ��ڵ�
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

}
