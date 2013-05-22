package com.taobao.taokeeper.research.test.performance.onlygetdata;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.ThreadUtil;

/**
 * 数据订阅者
 * @author yinshi.nc
 * @since 2011-11-22
 */
public class Subscriber implements Watcher {

	private static Log log = LogFactory.getLog( Subscriber.class );

	ZooKeeper zk = null;
	private String path;
	private String serverList;
	private int sessionTimeout;
	private boolean isFinish = false;

	/**
	 * @param path		znode的path
	 * @param getTimes	获取数据次数
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public Subscriber( String serverList, int sessionTimeout, String path ){
		this.serverList = serverList;
		this.sessionTimeout = sessionTimeout;
		this.path = path;
		this.createZk();
	}
	
	private boolean createZk(){
		if( null != this.zk ){
			try {
				this.zk.close();
			} catch ( InterruptedException e ) {
				//ignore
			}
			this.zk = null;
		}
		try {
			this.zk = new ZooKeeper( this.serverList, this.sessionTimeout, this );
			return true;
		} catch ( IOException e ) {
			return false;
		}
	}
	
	public void start() {
		if( null == this.zk )
			return;
		ThreadUtil.startThread( new Runnable() {
			@Override
			public void run() {
				// 获取数据
				while ( !isFinish ) {
					try {
						zk.getData( path, false, new Stat() );
						StatisticsUtil.totalTransactions.incrementAndGet();
					}catch(KeeperException keeperException ){
						 //如果过期，那么要重新连接一个了。
						keeperException.code().equals( Code.SESSIONEXPIRED );
						if( createZk() ) {
							PressDataGet.reConnectSuccessTimes.incrementAndGet();
						}else{
							PressDataGet.reConnectFailTimes.incrementAndGet();
						}
					}catch ( Exception e ) {
						PressDataGet.failTimes.incrementAndGet();
						log.error( "Fail get data: " + e.getMessage() );
					}
				}
				PressDataGet.finshedSub.incrementAndGet();
			}
		} );
	}

	@Override
	public void process( WatchedEvent event ) {
		// Do nothing
	}

}
