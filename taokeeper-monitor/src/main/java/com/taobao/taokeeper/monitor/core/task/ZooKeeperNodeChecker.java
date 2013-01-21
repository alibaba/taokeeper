package com.taobao.taokeeper.monitor.core.task;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.common.type.NodePathCheckRule;
import com.taobao.taokeeper.common.util.AlarmSettingUtil;
import com.taobao.taokeeper.dao.AlarmSettingsDAO;
import com.taobao.taokeeper.dao.ZooKeeperClusterDAO;
import com.taobao.taokeeper.model.AlarmSettings;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.model.type.Message;
import com.taobao.taokeeper.monitor.core.ThreadPoolManager;
import com.taobao.taokeeper.reporter.alarm.TbMessageSender;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.ObjectUtil;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.collection.CollectionUtil;
import common.toolkit.java.util.collection.ListUtil;
/**
 * Description: ZK节点的Path检查<br>
 * 啊！！！添加这个类的时候，距离taokeeper开始开发已经一年时间了，哎。时间过得真快，又要大一岁了，悲剧！
 * @author 银时 yinshi.nc@taobao.com
 * @date 2012-10-22
 */
public class ZooKeeperNodeChecker extends TimerTask{

	private static final Logger LOG = LoggerFactory.getLogger( ZooKeeperNodeChecker.class );
	private WebApplicationContext wac;
	private AlarmSettingsDAO alarmSettingsDAO;
	
	public ZooKeeperNodeChecker(){
		wac = ContextLoader.getCurrentWebApplicationContext();
		alarmSettingsDAO = ( AlarmSettingsDAO ) wac.getBean( "alarmSettingsDAO" );
	}
	
	@Override
	public void run() {

		if( !GlobalInstance.need_node_checker ){
			LOG.info( "No need to check node name, need_node_checker= " + GlobalInstance.need_node_checker );
			return;
		}
		
		
			try {
				// 根据clusterId来获取一个zk集群
				ZooKeeperClusterDAO zooKeeperClusterDAO = ( ZooKeeperClusterDAO ) wac.getBean( "zooKeeperClusterDAO" );
				try {
					List< ZooKeeperCluster > zooKeeperClusterSet = null;
					Map< Integer, ZooKeeperCluster > zooKeeperClusterMap = GlobalInstance.getAllZooKeeperCluster();
					if ( null == zooKeeperClusterMap ) {
						zooKeeperClusterSet = zooKeeperClusterDAO.getAllDetailZooKeeperCluster();
					} else {
						zooKeeperClusterSet = new ArrayList< ZooKeeperCluster >();
						zooKeeperClusterSet.addAll( zooKeeperClusterMap.values() );
					}

					if ( null == zooKeeperClusterSet || zooKeeperClusterSet.isEmpty() ) {
						LOG.warn( "没事配置任何ZooKeeper集群信息，没有必要进行节点的Path检查" );
					} else {
						for ( final ZooKeeperCluster zookeeperCluster : zooKeeperClusterSet ) { // 对每个cluster处理

							this.checkNodePath( zookeeperCluster );
						}// for each cluster
					}
				} catch ( DaoException daoException ) {
					LOG.warn( "Error when handle data base" + daoException.getMessage() );
				} catch ( Exception e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch ( Throwable e ) {
				e.printStackTrace();
			}
		}

	/**
	 * 检查存活性，不报警. 这个方法通常是在添加新的zk集群后调用的.
	 * 
	 * @param server是一个包含ip的参数
	 */
	private void checkNodePath( final ZooKeeperCluster zookeeperCluster ) {
		
		if( ObjectUtil.isBlank( zookeeperCluster ) || CollectionUtil.isBlank( zookeeperCluster.getServerList()  ) ){
			return;
		}
		AlarmSettings alarmSettings = null;
		try {
			alarmSettings = alarmSettingsDAO.getAlarmSettingsByCulsterId( zookeeperCluster.getClusterId() );
		} catch ( DaoException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( ObjectUtil.isBlank( alarmSettings ) ) {
			return;
		}
		List<String> zkServerList = zookeeperCluster.getServerList(); 
		if( CollectionUtil.isBlank( zkServerList ) ){
			//没有配置任何ZooKeeper集群地址。
			//额。。。既然没有配置地址，为毛要配置这个集群呢~为什么捏？
			return;
		}
		String strNodePathCheckRule = StringUtil.trimToEmpty( alarmSettings.getNodePathCheckRule() );
		String wangwangList               = alarmSettings.getWangwangList();
		NodePathCheckRule nodePathCheckRule = null;
		try {
			nodePathCheckRule = AlarmSettingUtil.parseNodePathCheckRuleFromString( strNodePathCheckRule );
		} catch ( Exception e ) {
			LOG.warn( e.getMessage() );
			e.printStackTrace();
			return;
		}
		//没有配置节点检查规则，直接返回
		if( ObjectUtil.isBlank( nodePathCheckRule ) ){
			return;
		}
		//只能出现的节点
		Map<String,List<String>> pathOnlyCanBeExist = nodePathCheckRule.getPathOnlyCanBeExist();
		//不能出现这些path
		Map<String,List<String>> pathCanNotBeExist = nodePathCheckRule.getPathCanNotBeExist();
		if( CollectionUtil.isBlank( pathOnlyCanBeExist ) && CollectionUtil.isBlank( pathCanNotBeExist ) ){
			return;
		}
		
		LOG.info( "开始检查ZK集群，name: " + zookeeperCluster.getClusterName() + 
                ", serverList: " + zookeeperCluster.getServerList()  + " " +
                nodePathCheckRule );
//		基本逻辑已经ok，明天做一些测试就好了~
		
		ZkClient zkClient= new ZkClient( ListUtil.toString( zkServerList ), 5000, 5000 );
		
		
		if( !CollectionUtil.isBlank( pathOnlyCanBeExist )  ){
			
			for(  String path : pathOnlyCanBeExist.keySet() ){
				List<String> listConfig = pathOnlyCanBeExist.get( path );
				List<String> listReal = null;
				try {
					listReal = zkClient.getChildren( path );
				} catch ( Exception e ) {
					//ignore 当然这里也会报一些节点不存在的异常，这都没有问题。
					e.printStackTrace();
				}
				if( CollectionUtil.isBlank( listReal ) ){
					continue;
				}
				for( String pathReal : listReal ){
					pathReal = StringUtil.trimToEmpty( pathReal );
					if( !listConfig.contains( pathReal ) ){
						//规定path下面只能这些path，但是出现了额外的path
						// 报警
						if ( GlobalInstance.needAlarm.get() ) {
							ThreadPoolManager.addJobToMessageSendExecutor( 
									new TbMessageSender( 
											new Message( wangwangList,//
													"ZooKeeper Node的path检查结果,cluster: " + zookeeperCluster.getClusterName(),//
													 path + " 下存在多余的node："  + pathReal, //
													 Message.MessageType.WANGWANG ) ) );
						}
					}
				}
			}			
		}//Finish check pathOnlyCanBeExist
		
		if( !CollectionUtil.isBlank( pathCanNotBeExist )  ){
			
			for(  String path : pathCanNotBeExist.keySet() ){
				List<String> listConfig = pathCanNotBeExist.get( path );
				List<String> listReal = null;
				try {
					listReal = zkClient.getChildren( path );
				} catch ( Exception e ) {
					//ignore 当然这里也会报一些节点不存在的异常，这都没有问题。
				}
				if( CollectionUtil.isBlank( listReal ) ){
					continue;
				}
				for( String pathConfig : listConfig ){
					pathConfig = StringUtil.trimToEmpty( pathConfig );
					if( listReal.contains( pathConfig ) ){
						//规定path下面不能有这些path，但是出现了
						// 报警
						if ( GlobalInstance.needAlarm.get() ) {
							ThreadPoolManager.addJobToMessageSendExecutor( 
									new TbMessageSender( 
											new Message( wangwangList,//
												   "ZooKeeper Node的path检查结果,cluster: " + zookeeperCluster.getClusterName(),//
												    path + " 下存在多余的node："  + pathConfig, //
													Message.MessageType.WANGWANG ) ) );
						}
					}
				}
			}			
		}//Finish check pathOnlyCanBeExist
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		String zkServers = ListUtil.toString( zkServerList );
//		ZkClient zkClient = new ZkClient( zkServers, 5000, 5000 );
//		zkClient.getChildren( path );
//
//		if ( StringUtil.isBlank( server ) )
//			return true;
//
//		String ip = server.split( COLON )[0];
//
//		// 进行两次检查
//		Subscriber sub = null;
//		try {
//			sub = new Subscriber( server, ZOOKEEPER_MONITOR_PATH, 5 );
//			// 判断一个节点已经挂了：连续两次检测均失败。
//			if ( !sub.checkIfAlive() ) {
//				if ( !sub.checkIfAlive() ) { // 连续两次check失败
//					GlobalInstance.putZooKeeperStatusType( ip, 2 );
//					LOG.info( "对 " + server + "进行节点自检ERROR" );
//					return false;
//				}
//				GlobalInstance.putZooKeeperStatusType( ip, 1 );
//				LOG.info( "对 " + server + "进行节点自检OK" );
//				return true;
//			}
//			GlobalInstance.putZooKeeperStatusType( ip, 1 );
//			LOG.info( "对 " + server + "进行节点自检OK" );
//			return true;
//		} catch ( Throwable e ) {
//			// 报警
//			GlobalInstance.putZooKeeperStatusType( ip, 2 );
//			LOG.info( "对 " + server + "进行节点自检ERROR" );
//		} finally {
//			if ( null != sub )
//				sub.close();
//		}
//		return true;
	}

	public static void main( String[] args ) {
		Thread thread = new Thread( new ZooKeeperNodeChecker() );
		thread.start();
	}
}
