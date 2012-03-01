package com.taobao.taokeeper.model;
import static com.taobao.taokeeper.common.constant.SystemConstant.DELAY_SECS_OF_TWO_SERVER_ALIVE_CHECK_ZOOKEEPER;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import java.util.ArrayList;
import java.util.List;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
/**
 * 类说明: 
 * @author yinshi.nc
 */
public class Subscriber{

	private static Log log = LogFactory.getLog( Subscriber.class ); // 输出日志
	
	private int okTimes = 0;
	private String serverList;
	private String path;
	private ZkClient zkClient = null;
	private int maxDelaySecsForNotify = 1;
	//这里两个变量分别是最近一次向server提交的更新数据和这次拿到的数据，两个值一致说明存活
	private  String lastedUpdateToServer = EMPTY_STRING;
	public void setLastedUpdateToServer( String lastedUpdateToServer ) {
		this.lastedUpdateToServer = lastedUpdateToServer;
	}
	
	public Subscriber( String serverList, String path, int maxDelaySecsForNotify ){
		this.serverList = serverList;
		this.path = path;
		zkClient = new ZkClient( serverList, 5000 );
		this.maxDelaySecsForNotify = maxDelaySecsForNotify;
		//启动监听
		this.mointorData( path );
	}

	

	/**
	 * 监控 parentPath 目录下所有数据的变更
	 * 注意, 这里的监听不会监听父级变更, 只能监听自己.
	 * @param parentPath
	 */
	private void mointorData( String path) {
		zkClient.subscribeDataChanges( path, new IZkDataListener() {
			public void handleDataChange(String dataPath, Object data) throws Exception {
				if( lastedUpdateToServer.equalsIgnoreCase( data.toString() ) ){
					okTimes++;
					return;
				}
			}
			public void handleDataDeleted(String dataPath) throws Exception {
				//ingore
			}
		});
	}
	
	/**
	 * 检查是否存活，检查方法是：向一个ip发送数据，要求在指定时间内收到通知并获取正确数据。这个过程连续进行3次，其中有一次失败即失败。
	 */
	public boolean checkIfAlive(){
		createNodeNotExist( this.path );
		for( int i=0; i<3; i++ ){
			this.lastedUpdateToServer = this.serverList + System.currentTimeMillis();
			try {
				this.updateData( this.lastedUpdateToServer );
				Thread.sleep( 1000 * this.maxDelaySecsForNotify );//指定时间内
			} catch ( InterruptedException e ) {
				//ingore
			} catch ( Exception e ) {
				return false;
			}
		}
		
		return okTimes == 3;
	}
	
	public void close(){
		this.zkClient.close();
		this.zkClient = null;
	}
	

	/** 如果不存在，创建节点 */
	private void createNodeNotExist( String defaultData){
		if( !zkClient.exists( this.path ) ){
			zkClient.create( this.path, this.serverList + System.currentTimeMillis(), CreateMode.PERSISTENT );
		}
	}
	
	/**更新数据*/
	private  void updateData( String newData )throws Exception{
		zkClient.writeData( this.path, newData );
	}
	
	
	
	public static void main( String[] args ) throws InterruptedException {
		
		List<String> serverList = new ArrayList< String >();
		serverList.add( "10.232.37.129:2181" );
		serverList.add( "10.232.37.128:2181" );
		serverList.add( "10.232.37.126:2181" );
		serverList.add( "10.13.44.47:2181" );
		
		while( true ){
			for( String server : serverList ){
				
				Subscriber sub = null;
				try {
					sub = new Subscriber( server, "/yinshi.test", 1 );
					//判断一个节点已经挂了：连续两次检测均失败。
					if( !sub.checkIfAlive() ){
						if( !sub.checkIfAlive() ){
							log.error( server + "存活性检查失败" );
						}else{
							log.info( server + "存活性检查通过" );
						}
					}else{
						log.info( server + "存活性检查通过" );
					}
					Thread.sleep( 1000 * DELAY_SECS_OF_TWO_SERVER_ALIVE_CHECK_ZOOKEEPER );
				} catch ( Exception e ) {
					log.error( server + "存活性检测失败，原因是" + e.getMessage() );
				}finally{
					if( null != sub )
						sub.close();
				}
			}
			Thread.sleep( 5000 );
		}
		
	}
	
	
	
	
	
	
	
}
