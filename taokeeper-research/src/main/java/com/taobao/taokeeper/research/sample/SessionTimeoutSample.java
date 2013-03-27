package com.taobao.taokeeper.research.sample;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import common.toolkit.java.util.ObjectUtil;
import common.toolkit.java.util.ThreadUtil;

/**
 * ZooKeeper Java Api 使用样例<br>
 * ZK Api Version: 3.4.3
 * 
 * @author nileader/nileader@gmail.com
 */
public class SessionTimeoutSample implements Watcher {

	private static final int SESSION_TIMEOUT = 10000;
	private static final String CONNECTION_STRING = "test.zookeeper.connection_string:2181," +
			                                                                           "test.zookeeper.connection_string2:2181," +
			                                                                           "test.zookeeper.connection_string3:2181";
	private static final String ZK_PATH = "/nileader";
	private ZooKeeper zk = null;
	
	private CountDownLatch connectedSemaphore = new CountDownLatch( 1 );

	/**
	 * 创建ZK连接
	 * @param connectString	 ZK服务器地址列表
	 * @param sessionTimeout   Session超时时间
	 */
	public void createConnection( String connectString, int sessionTimeout ) {
		this.releaseConnection();
		try {
			zk = new ZooKeeper( connectString, sessionTimeout, this );
			connectedSemaphore.await();
		} catch ( InterruptedException e ) {
			System.out.println( "连接创建失败，发生 InterruptedException" );
			e.printStackTrace();
		} catch ( IOException e ) {
			System.out.println( "连接创建失败，发生 IOException" );
			e.printStackTrace();
		}
	}

	/**
	 * 关闭ZK连接
	 */
	public void releaseConnection() {
		if ( !ObjectUtil.isBlank( this.zk ) ) {
			try {
				this.zk.close();
			} catch ( InterruptedException e ) {
				// ignore
				e.printStackTrace();
			}
		}
	}

	/**
	 *  创建节点
	 * @param path 节点path
	 * @param data 初始数据内容
	 * @return
	 */
	public boolean createPath( String path, String data ) {
		try {
			System.out.println( "节点创建成功, Path: "
					+ this.zk.create( path, //
							                  data.getBytes(), //
							                  Ids.OPEN_ACL_UNSAFE, //
							                  CreateMode.EPHEMERAL )
					+ ", content: " + data );
		} catch ( KeeperException e ) {
			System.out.println( "节点创建失败，发生KeeperException" );
			e.printStackTrace();
		} catch ( InterruptedException e ) {
			System.out.println( "节点创建失败，发生 InterruptedException" );
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 读取指定节点数据内容
	 * @param path 节点path
	 * @return
	 */
	public String readData( String path ) {
		try {
			System.out.println( "获取数据成功，path：" + path );
			return new String( this.zk.getData( path, false, null ) );
		} catch ( KeeperException e ) {
			System.out.println( "读取数据失败，发生KeeperException，path: " + path  );
			e.printStackTrace();
			return "";
		} catch ( InterruptedException e ) {
			System.out.println( "读取数据失败，发生 InterruptedException，path: " + path  );
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 更新指定节点数据内容
	 * @param path 节点path
	 * @param data  数据内容
	 * @return
	 */
	public boolean writeData( String path, String data ) {
		try {
			System.out.println( "更新数据成功，path：" + path + ", stat: " +
		                                                this.zk.setData( path, data.getBytes(), -1 ) );
		} catch ( KeeperException e ) {
			System.out.println( "更新数据失败，发生KeeperException，path: " + path  );
			e.printStackTrace();
		} catch ( InterruptedException e ) {
			System.out.println( "更新数据失败，发生 InterruptedException，path: " + path  );
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 删除指定节点
	 * @param path 节点path
	 */
	public void deleteNode( String path ) {
		try {
			this.zk.delete( path, -1 );
			System.out.println( "删除节点成功，path：" + path );
		} catch ( KeeperException e ) {
			System.out.println( "删除节点失败，发生KeeperException，path: " + path  );
			e.printStackTrace();
		} catch ( InterruptedException e ) {
			System.out.println( "删除节点失败，发生 InterruptedException，path: " + path  );
			e.printStackTrace();
		}
	}

	public static void main( String[] args ) {

		PropertyConfigurator.configure("src/main/resources/log4j.properties");
		
		SessionTimeoutSample sample = new SessionTimeoutSample();
		sample.createConnection( CONNECTION_STRING, SESSION_TIMEOUT );
		if ( sample.createPath( ZK_PATH, System.currentTimeMillis()+"" ) ) {
			
			while( true ){
				System.out.println( "数据内容: " + sample.readData( ZK_PATH ) + "\n" );
				sample.writeData( ZK_PATH, System.currentTimeMillis()+"" );
				ThreadUtil.sleep( 1500 );
			}
		}

		sample.releaseConnection();
	}

	/**
	 * 收到来自Server的Watcher通知后的处理。
	 */
	@Override
	public void process( WatchedEvent event ) {
		System.out.println( "收到事件通知：" + event.getState() +"\n"  );
		if ( KeeperState.SyncConnected == event.getState() ) {
			connectedSemaphore.countDown();
		}

	}

}
