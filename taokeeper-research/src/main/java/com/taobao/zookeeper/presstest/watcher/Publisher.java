package com.taobao.zookeeper.presstest.watcher;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooKeeper;

import common.toolkit.java.util.ThreadUtil;

/**
 * ��ݷ�����
 * @author yinshi.nc
 * @Date 2011-11-10
 */
public class Publisher{

	private static Log log = LogFactory.getLog( Publisher.class );

	ZooKeeper zk = null;
	private String path;
	private String serverList;
	private int sessionTimeout;
	private long setTimes = 0;
	private int bytes = 256;

	public Publisher(String serverList, int sessionTimeout, String path, long setTimes, int bytes) throws IOException {
		this.serverList = serverList;
		this.sessionTimeout = sessionTimeout;
		this.path = path;
		this.setTimes = setTimes;
		this.bytes = bytes;
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
			this.zk = new ZooKeeper( this.serverList, this.sessionTimeout, null );
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
				//�������
				for ( int i = 0; i < setTimes; i++ ) {
					try {
						zk.setData( path, new byte[bytes], -1 );
					} catch(KeeperException keeperException ){
						 //�����ڣ���ôҪ��������һ���ˡ�
						keeperException.code().equals( Code.SESSIONEXPIRED );
						if( createZk() ) {
							PressWatcher.reConnectSuccessTimes.incrementAndGet();
						}else{
							PressWatcher.reConnectFailTimes.incrementAndGet();
						}
					}catch ( Exception e ) {
						PressWatcher.failTimes.incrementAndGet();
						log.error( "fail set data: " + e.getMessage() );
					}
				}
				PressWatcher.finshedPub.incrementAndGet();
			}
		} );
	}

}
