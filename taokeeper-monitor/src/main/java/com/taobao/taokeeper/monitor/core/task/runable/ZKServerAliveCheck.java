package com.taobao.taokeeper.monitor.core.task.runable;

import static com.taobao.taokeeper.common.constant.SystemConstant.DELAY_SECS_OF_TWO_SERVER_ALIVE_CHECK_ZOOKEEPER;
import static com.taobao.taokeeper.common.constant.SystemConstant.ZOOKEEPER_MONITOR_PATH;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.SymbolConstant.COLON;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.model.AlarmSettings;
import com.taobao.taokeeper.model.Subscriber;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.model.type.Message;
import com.taobao.taokeeper.monitor.core.ThreadPoolManager;
import com.taobao.taokeeper.reporter.alarm.TbMessageSender;
import common.toolkit.java.util.ObjectUtil;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.io.ServletUtil;

/**
 * Description: Check if zookeeper server alive.<br>
 * 节点自检 是指对集群中每个IP所在ZK节点上的PATH: /YINSHI.MONITOR.ALIVE.CHECK 定期进行三次如下流程 :<br>
 * 节点连接 - 数据发布 - 修改通知 - 获取数据 - 数据对比, 三次流程均成功视为该节点处于正常状态。<br>
 * 
 * @author 银时 yinshi.nc@taobao.com
 * @Date Dec 26, 2011
 */
public class ZKServerAliveCheck implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger( ZKServerAliveCheck.class );

	private ZooKeeperCluster zooKeeperCluster;
	private AlarmSettings alarmSettings;

	public ZKServerAliveCheck( ZooKeeperCluster zooKeeperCluster, AlarmSettings alarmSettings ) {
		this.zooKeeperCluster = zooKeeperCluster;
		this.alarmSettings    = alarmSettings;
	}

	@Override
	public void run() {
		try {
			checkAliveAndAlarm();
		} catch ( Exception e ) {
			LOG.error( "Exception when check zk server alive, Error: " + e.getMessage(), e );
		}
	}

	/**
	 * 检查存活性，并报警
	 * 
	 * @param server是一个包含ip的参数
	 */
	public void checkAliveAndAlarm() {

		try {
			if ( ObjectUtil.isBlank( this.zooKeeperCluster, this.alarmSettings ) || ObjectUtil.isBlank( this.zooKeeperCluster.getServerList() ) )
				return;
			List< String > serverList = this.zooKeeperCluster.getServerList();

			if ( null == serverList || serverList.isEmpty() ) {
				LOG.warn( "#- " + this.zooKeeperCluster.getClusterName() + " 集群没有配置任何Ip." );
				return;
			}
			// 每个ip依次检查
			for ( String server : serverList ) {
				if ( StringUtil.isBlank( server ) )
					continue;

				// TODO 这里的并发问题要考虑下。
				// 如果已经有线程在对其进行自检了。
				String zkIp = ServletUtil.paraseIpAndPortFromServer( server )[0] ;
				if ( 0 == GlobalInstance.getZooKeeperStatusType( zkIp ) ){
					LOG.info( zkIp + " is checking, no need to check." );
					continue;
				}
				// 如果要检查，标记为正在检查
				GlobalInstance.putZooKeeperStatusType( zkIp, 0 );
				LOG.info( zkIp + " not check, start to check now time..." );
				String ip = server.split( COLON )[0];

				String wangwangList = alarmSettings.getWangwangList();
				String phoneList = alarmSettings.getPhoneList();

				if ( !StringUtil.isBlank( alarmSettings.getMaxDelayOfCheck() ) ) {
					// 进行两次检查
					Subscriber sub = null;
					try {
						sub = new Subscriber( server, ZOOKEEPER_MONITOR_PATH, Integer.parseInt( alarmSettings.getMaxDelayOfCheck() ) );
						// 判断一个节点已经挂了：连续两次检测均失败。
						if ( !sub.checkIfAlive() ) {
							if ( !sub.checkIfAlive() ) { // 连续两次check失败
								GlobalInstance.putZooKeeperStatusType( ip, 2 );
								// 报警
								if ( GlobalInstance.needAlarm.get() ) {
									ThreadPoolManager.addJobToMessageSendExecutor( new TbMessageSender( new Message( wangwangList, "ZooKeeper所在机器存活性检测失败" + this.zooKeeperCluster.getClusterName(), "Zk node: "
											+ server + " 存活性检测失败", Message.MessageType.WANGWANG ) ) );
									
									ThreadPoolManager.addJobToMessageSendExecutor( new TbMessageSender( new Message( phoneList, "ZooKeeper所在机器存活性检测失败" + this.zooKeeperCluster.getClusterName(), "Zk node: " + server
											+ " 存活性检测失败", Message.MessageType.WANGWANG ) ) );
								}
								LOG.info( "#-" + this.zooKeeperCluster.getClusterName() + "-" + server + "自检结果ERROR" );
								continue;
							}
							GlobalInstance.putZooKeeperStatusType( ip, 1 );
							LOG.info( "#-" + this.zooKeeperCluster.getClusterName() + "-" + server + "自检结果OK" );
							continue;
						}
						GlobalInstance.putZooKeeperStatusType( ip, 1 );
						LOG.info( "#-" + this.zooKeeperCluster.getClusterName() + "-" + server + "自检结果OK" );
						continue;
					} catch ( Throwable e ) {
						// 报警
						if ( GlobalInstance.needAlarm.get() ) {
							
							ThreadPoolManager.addJobToMessageSendExecutor( new TbMessageSender( new Message( wangwangList, "ZooKeeper所在机器存活性检测失败" + this.zooKeeperCluster.getClusterName(), "Zk node: "
									+ server + " 存活性检测失败" + e.getMessage(), Message.MessageType.WANGWANG ) ) );
							
							
							ThreadPoolManager.addJobToMessageSendExecutor( new TbMessageSender( new Message( phoneList, "ZooKeeper所在机器存活性检测失败" + this.zooKeeperCluster.getClusterName(), "Zk node: " + server
									+ " 存活性检测失败" + e.getMessage(), Message.MessageType.WANGWANG ) ) );
							
						}
						GlobalInstance.putZooKeeperStatusType( ip, 2 );
						LOG.info( "Exception when check #-" + this.zooKeeperCluster.getClusterName() + "-" + server + ", Error: " + e.getMessage(), e );
					} finally {
						if ( null != sub ) {
							sub.close();
							sub = null;
						}
					}
				}// IF需要进行check
				try {
					Thread.sleep( 1000 * DELAY_SECS_OF_TWO_SERVER_ALIVE_CHECK_ZOOKEEPER );
				} catch ( InterruptedException e ) {
				}// ignore
			}// for each server
			return;
		} catch ( Exception e ) {
			LOG.error( "Exception when check zk server alive, Error: " + e.getMessage(), e );
		} finally {
			// 检查完一定要进行释放。
			GlobalInstance.removeFromAllCheckingCluster( this.zooKeeperCluster.getClusterId() + EMPTY_STRING );
		}
	}

}
