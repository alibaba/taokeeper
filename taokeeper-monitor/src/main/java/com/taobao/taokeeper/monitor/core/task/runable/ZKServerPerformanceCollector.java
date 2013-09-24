package com.taobao.taokeeper.monitor.core.task.runable;

import static com.taobao.taokeeper.common.constant.SystemConstant.passwordOfSSH;
import static com.taobao.taokeeper.common.constant.SystemConstant.userNameOfSSH;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.SymbolConstant.PERCENT;
import static common.toolkit.java.constant.SymbolConstant.SLASH;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.common.constant.SystemConstant;
import com.taobao.taokeeper.model.AlarmSettings;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.model.type.Message;
import com.taobao.taokeeper.monitor.core.ThreadPoolManager;
import com.taobao.taokeeper.reporter.alarm.TbMessageSender;
import common.toolkit.java.entity.HostPerformanceEntity;
import common.toolkit.java.exception.SSHException;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.io.SSHUtil;

/**
 * Description: Collect zooKeeper machine performance CPU/LOAD/MEM
 * 
 * @author 银时 yinshi.nc@taobao.com
 * @Date Dec 26, 2011
 */
public class ZKServerPerformanceCollector implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger( ZKServerPerformanceCollector.class );

	private String ip;
	private AlarmSettings alarmSettings;
	private ZooKeeperCluster zookeeperCluster;

	/**
	 * @param ip  
	 * @param alarmSettings  
	 * @param zookeeperCluster 
	 */
	public ZKServerPerformanceCollector( String ip, AlarmSettings alarmSettings, ZooKeeperCluster zookeeperCluster ) {
		this.ip = ip;
		this.alarmSettings = alarmSettings;
		this.zookeeperCluster = zookeeperCluster;
	}

	@Override
	public void run() {
		try {
			HostPerformanceEntity hostPerformanceEntity = SSHUtil.getHostPerformance( ip, SystemConstant.portOfSSH, userNameOfSSH, passwordOfSSH );
			sendAlarm( alarmSettings, hostPerformanceEntity, zookeeperCluster.getClusterName() );
			GlobalInstance.putHostPerformanceEntity( ip, hostPerformanceEntity );
			LOG.info( "HostPerformanceEntity collect of #" + zookeeperCluster.getClusterName() + "-" + ip );
		} catch ( SSHException e ) {
			LOG.warn( "HostPerformanceEntity collect of " + ip + " ：" + e.getMessage() );
		} catch ( Throwable exception ) {
			LOG.error( "程序出错: " + exception.getMessage() );
			exception.printStackTrace();
		}
	}

	/**
	 * check if alarm
	 */
	public void sendAlarm( AlarmSettings alarmSettings, HostPerformanceEntity hostPerformanceEntity, String clusterName ) {

		if ( null == alarmSettings )
			return;

		String wangwangList = alarmSettings.getWangwangList();
		String phoneList = alarmSettings.getPhoneList();

		String maxCpuUsage = alarmSettings.getMaxCpuUsage();
		String maxMemoryUsage = alarmSettings.getMaxMemoryUsage();
		String maxLoad = alarmSettings.getMaxLoad();

		String dataDir = alarmSettings.getDataDir();
		String dataLogDir = alarmSettings.getDataLogDir();
		String maxDiskUsage = alarmSettings.getMaxDiskUsage();

		if ( !StringUtil.isBlank( maxCpuUsage ) ) { // Cpu usage alarm
			String cpuUsage = hostPerformanceEntity.getCpuUsage();
			if ( !StringUtil.isBlank( cpuUsage ) && cpuUsage.endsWith( "%" ) ) {
				cpuUsage = cpuUsage.replaceAll( "%", "" );
				double difference = Double.parseDouble( cpuUsage ) - Double.parseDouble( maxCpuUsage );
				if ( 0 < difference ) {
					LOG.warn( "ZK Server " + hostPerformanceEntity.getIp() + " cpu Usage too high：" + cpuUsage + "-" + maxCpuUsage + "=" + difference );
					// Alarm
					if ( GlobalInstance.needAlarm.get() ) {
						ThreadPoolManager.addJobToMessageSendExecutor( new TbMessageSender( 
								new Message( wangwangList, "ZK Server cpu usage too high-" + clusterName, hostPerformanceEntity.getIp() + " cpu usage too high! " + cpuUsage + "-" + maxCpuUsage + "=" + difference,
								Message.MessageType.WANGWANG ),
								new Message( phoneList, "ZK Server cpu usage too high-" + clusterName, "ZK Server cpu usage too high-" + clusterName + hostPerformanceEntity.getIp() + " cpu usage too high! " + cpuUsage + "-"
										+ maxCpuUsage + "=" + difference, Message.MessageType.SMS )
								) );
						LOG.info( "WangWangList: " + wangwangList );
					}
				}
			}
		}

		if ( !StringUtil.isBlank( maxMemoryUsage ) ) { // Memory usage alarm
			String memoryUsage = hostPerformanceEntity.getMemoryUsage();
			if ( !StringUtil.isBlank( memoryUsage ) && memoryUsage.endsWith( "%" ) ) {
				memoryUsage = memoryUsage.replaceAll( "%", "" );
				double difference = Double.parseDouble( memoryUsage ) - Double.parseDouble( maxMemoryUsage );
				if ( 0 < difference ) {
					LOG.warn( "ZK Server "+ hostPerformanceEntity.getIp() +" memory usage too high: " + memoryUsage + "-" + maxMemoryUsage + "=" + difference );
					// Alarm
					if ( GlobalInstance.needAlarm.get() ) {
						ThreadPoolManager.addJobToMessageSendExecutor( new TbMessageSender( 
								
								new Message( wangwangList, "ZK Server memory usage too high:-" + clusterName, hostPerformanceEntity.getIp() + " memory too high：" + memoryUsage + "-" + maxMemoryUsage + "=" + difference,
								Message.MessageType.WANGWANG ),
								
								new Message( phoneList, "", "ZK Server memory usage too high-" + clusterName + hostPerformanceEntity.getIp() + memoryUsage + "-" + maxMemoryUsage + "=" + difference, Message.MessageType.SMS )
								
								) );
						LOG.info( "WangWangList: " + wangwangList );
					}
				}
			}
		}

		if ( !StringUtil.isBlank( maxLoad ) ) { // Load usage alarm
			String load = hostPerformanceEntity.getLoad();
			if ( !StringUtil.isBlank( load ) ) {
				double difference = Double.parseDouble( load ) - Double.parseDouble( maxLoad );
				if ( 0 < difference ) {
					LOG.warn( "ZK Server "+ hostPerformanceEntity.getIp() +" load usage too high: " + load + "-" + maxLoad + "=" + difference );
					if ( GlobalInstance.needAlarm.get() ) {
						
						ThreadPoolManager.addJobToMessageSendExecutor( new TbMessageSender( 
								
								new Message( wangwangList, " ZK Server load usage too high-"
								+ clusterName, hostPerformanceEntity.getIp() + "：" + load + "-" + maxLoad + "=" + difference,
								Message.MessageType.WANGWANG ),
								
								new Message( phoneList, "", "ZK Server load usage too high-" + clusterName + hostPerformanceEntity.getIp() + "：" + load + "-"
										+ maxLoad + "=" + difference, Message.MessageType.SMS )
								
								) );
						LOG.info( "WangWangList: " + wangwangList );
					}
				}
			}
		}

		try {
			if ( !StringUtil.isBlank( dataDir ) || !StringUtil.isBlank( dataLogDir ) ) { // 需要进行
																							// disk容量
																							// 报警

				dataDir = StringUtil.trimToEmpty( dataDir );
				dataLogDir = StringUtil.trimToEmpty( dataLogDir );

				if ( !StringUtil.isBlank( maxDiskUsage ) ) {

					Map< String, String > diskUsageMap = hostPerformanceEntity.getDiskUsageMap();
					if ( null != diskUsageMap ) {
						for ( String mountedOn : diskUsageMap.keySet() ) {

							if ( StringUtil.trimToEmpty( mountedOn ).equalsIgnoreCase( SLASH ) )
								continue;

							if ( dataDir.startsWith( StringUtil.trimToEmpty( mountedOn ) )
									|| dataLogDir.startsWith( StringUtil.trimToEmpty( mountedOn ) ) ) {
								int diskUsage = Integer.parseInt( StringUtil.trimToEmpty( diskUsageMap.get( mountedOn ) ).replace( PERCENT,
										EMPTY_STRING ) );
								if ( diskUsage > Integer.parseInt( maxDiskUsage ) ) {
									LOG.warn( "ZK Server " + hostPerformanceEntity.getIp() + " disk usage too high, " + mountedOn + ": " + diskUsage
											+ "%, max setting usage is: " + maxDiskUsage + "%" );
									if ( GlobalInstance.needAlarm.get() ) {
										ThreadPoolManager.addJobToMessageSendExecutor( new TbMessageSender( 
												
												new Message( wangwangList,
												"ZK Server disk usage too high-" + clusterName, hostPerformanceEntity.getIp() + " disk usage too high, "
														+ mountedOn + ":" + diskUsage + "%, max setting usage is: " + maxDiskUsage + "%",
												Message.MessageType.WANGWANG ),
										
												new Message( phoneList, "", "ZK disk usage too high-" + clusterName
														+ hostPerformanceEntity.getIp() + "," + mountedOn + ": " + diskUsage
														+ "%, max setting usage is: " + maxDiskUsage + "%", Message.MessageType.SMS )
												) );
										LOG.info( "WangWangList: " + wangwangList );
									}
								}
							}
						}
					}
				}

			}
		} catch ( Throwable e ) {
			LOG.error( "Error when ckeck disk usage：" + e.getMessage() );
			e.printStackTrace();
		}// disk alarm

	}

}
