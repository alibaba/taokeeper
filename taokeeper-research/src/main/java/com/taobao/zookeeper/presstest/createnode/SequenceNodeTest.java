package com.taobao.zookeeper.presstest.createnode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.StringUtil;

/**
 * 类说明: 进行SequenceNode建立压力测试
 * @author yinshi.nc
 */
public class SequenceNodeTest implements Watcher {

	static Log log = LogFactory.getLog( SequenceNodeTest.class );

	static String SERVER_LIST = "10.13.44.47:2181";
	final static int SESSION_TIMEOUT = 5000;
	static int SESSIONCREATORS = 10;
	static String PARENT_PATH = "/YINSHI_PRESS_TEST_SEQ_NODE";

	static AtomicLong failTimes = new AtomicLong();
	
	static List< SequenceNodeCreator > nodeCreatorList = new ArrayList< SequenceNodeCreator >();

	static boolean isWork = true;
	
	
	public static void main( String[] args ) throws IOException, KeeperException, InterruptedException {

		if ( args.length != 2 ) {
			throw new IllegalArgumentException( "请指定：zk服务器列表，SessionCreator数量" );
		}
		SequenceNodeTest.SERVER_LIST = StringUtil.defaultIfBlank( args[0], SERVER_LIST );
		SequenceNodeTest.SESSIONCREATORS = Integer.parseInt( StringUtil.defaultIfBlank( args[1], SESSIONCREATORS + EmptyObjectConstant.EMPTY_STRING ) );
		
		try {
			
			createParentNode();
			
			// 准备SessionCreator
			for ( int i = 0; i < SESSIONCREATORS; i++ ) {
				nodeCreatorList.add( new SequenceNodeCreator( SERVER_LIST, PARENT_PATH ) );
			}
			// 启动统计程序
			StatisticsUtil.start( 20 );
			for ( SequenceNodeCreator sessionCreator : nodeCreatorList ) {
				sessionCreator.start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			while ( isWork ) {
				log.error( "创建SequenceNode错误次数: " + SequenceNodeTest.failTimes );
				Thread.sleep( 10000 );
			}
			StatisticsUtil.stop();
		}
	}
	
	
	static void createParentNode() throws KeeperException, InterruptedException, IOException{
		ZooKeeper zk = new ZooKeeper( SERVER_LIST, 5000, new SequenceNodeTest() );
		Stat stat = zk.exists( PARENT_PATH, false );
		
		if( null == stat )
			zk.create( PARENT_PATH, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );
		
		zk.close();
	}
	
	
	
	public void process( WatchedEvent event ) {
		// TODO Auto-generated method stub
	}

}
