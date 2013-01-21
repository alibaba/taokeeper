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
 * ��˵��: 
 * @author yinshi.nc
 */
public class Subscriber{

	private static Log log = LogFactory.getLog( Subscriber.class ); // �����־
	
	private int okTimes = 0;
	private String serverList;
	private String path;
	private ZkClient zkClient = null;
	private int maxDelaySecsForNotify = 1;
	//�������������ֱ������һ����server�ύ�ĸ�����ݺ�����õ�����ݣ�����ֵһ��˵�����
	private  String lastedUpdateToServer = EMPTY_STRING;
	public void setLastedUpdateToServer( String lastedUpdateToServer ) {
		this.lastedUpdateToServer = lastedUpdateToServer;
	}
	
	public Subscriber( String serverList, String path, int maxDelaySecsForNotify ){
		this.serverList = serverList;
		this.path = path;
		zkClient = new ZkClient( serverList, 10000 );
		log.info( "====zk conn log====>start conn to zk server: " + this.serverList );
		this.maxDelaySecsForNotify = maxDelaySecsForNotify;
		//��������
		this.mointorData( path );
		
	}

	

	/**
	 * ��� parentPath Ŀ¼��������ݵı��
	 * ע��, ����ļ���������, ֻ�ܼ����Լ�.
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
	 * ����Ƿ����鷽���ǣ���һ��ip������ݣ�Ҫ����ָ��ʱ�����յ�֪ͨ����ȡ��ȷ��ݡ��������������3�Σ�������һ��ʧ�ܼ�ʧ�ܡ�
	 */
	public boolean checkIfAlive(){
		createNodeNotExist( this.path );
		for( int i=0; i<3; i++ ){
			this.lastedUpdateToServer = this.serverList + System.currentTimeMillis();
			try {
				this.updateData( this.lastedUpdateToServer );
				Thread.sleep( 1000 * this.maxDelaySecsForNotify );//ָ��ʱ����
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
		log.info( "====zk conn log====>start disconn to zk server: " + this.serverList );
	}
	

	/** �����ڣ������ڵ� */
	private void createNodeNotExist( String defaultData){
		if( !zkClient.exists( this.path ) ){
			zkClient.create( this.path, this.serverList + System.currentTimeMillis(), CreateMode.PERSISTENT );
		}
	}
	
	/**�������*/
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
					//�ж�һ���ڵ��Ѿ����ˣ��������μ���ʧ�ܡ�
					if( !sub.checkIfAlive() ){
						if( !sub.checkIfAlive() ){
							log.error( server + "����Լ��ʧ��" );
						}else{
							log.info( server + "����Լ��ͨ��" );
						}
					}else{
						log.info( server + "����Լ��ͨ��" );
					}
					Thread.sleep( 1000 * DELAY_SECS_OF_TWO_SERVER_ALIVE_CHECK_ZOOKEEPER );
				} catch ( Exception e ) {
					log.error( server + "����Լ��ʧ�ܣ�ԭ����" + e.getMessage() );
				}finally{
					if( null != sub )
						sub.close();
				}
			}
			Thread.sleep( 5000 );
		}
		
	}
	
	
	
	
	
	
	
}
