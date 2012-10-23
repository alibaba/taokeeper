package com.taobao.taokeeper.monitor.web;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.model.AlarmSettings;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.io.ServletUtil;

/**
 * @author yinshi.nc@taobao.com
 * @since 2011-08-10
 */
@Controller
@RequestMapping("/alarmSettings.do")
public class AlarmSettingsController extends BaseController {
	
	@RequestMapping(params = "method=alarmSettingsPAGE")
	public ModelAndView alarmSettingsPAGE(HttpServletRequest request, HttpServletResponse response, String clusterId, String handleMessage ) {

		clusterId = StringUtil.defaultIfBlank( clusterId, 1 + EMPTY_STRING );
		
		try {
			Map<Integer, ZooKeeperCluster > zooKeeperClusterMap = GlobalInstance.getAllZooKeeperCluster();
			Map<Integer, AlarmSettings > alarmSettingsMap = GlobalInstance.getAllAlarmSettings();
			AlarmSettings alarmSettings = GlobalInstance.getAlarmSettingsByClusterId( Integer.parseInt( clusterId ) );
			if( null == alarmSettings ){
				alarmSettings = alarmSettingsDAO.getAlarmSettingsByCulsterId( Integer.parseInt( clusterId) );
			}
			if( null ==  alarmSettings ){
				ServletUtil.writeToResponse( response, "目前还没有这样的ZK集群<a href='zooKeeper.do?method=zooKeeperRegisterPAGE'><font color='red'> 加入监控</font></a>" );
				return null;
			}
			
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put( "alarmSettings", alarmSettings );
			model.put( "alarmSettingsMap", alarmSettingsMap );
			model.put("clusterId", clusterId );
			model.put( "zooKeeperClusterMap", zooKeeperClusterMap );
			model.put( "handleMessage", StringUtil.trimToEmpty( handleMessage ) );
			return new ModelAndView("monitor/alarmSettingsPAGE", model );
		} catch ( NumberFormatException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( DaoException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}

	
	@RequestMapping(params = "method=updateAlarmSettingsHandle")
	public String updateAlarmSettingsHandle( HttpServletRequest request, HttpServletResponse response, 
			String clusterId,
			String maxDelayOfCheck,
			String maxCpuUsage,
			String maxMemoryUsage,
			String maxLoad,
			String wangwangList,
			String phoneList,
			String emailList,
			String maxConnectionPerIp,
			String maxWatchPerIp,
			String dataDir,
			String dataLogDir,
			String maxDiskUsage,
			String nodePathCheckRule ) {
		
		try {
			if( StringUtil.isBlank( clusterId ) )
				throw new Exception( "clusterId 不能为空" );
			
			AlarmSettings alarmSettings = new AlarmSettings();
			alarmSettings.setClusterId( Integer.parseInt( clusterId) );
			alarmSettings.setMaxDelayOfCheck( StringUtil.trimToEmpty( maxDelayOfCheck ) );
			alarmSettings.setMaxCpuUsage( StringUtil.trimToEmpty( maxCpuUsage ) );
			alarmSettings.setMaxMemoryUsage( StringUtil.trimToEmpty( maxMemoryUsage ) );
			alarmSettings.setMaxLoad( StringUtil.trimToEmpty( maxLoad ) );
			alarmSettings.setWangwangList( StringUtil.trimToEmpty( wangwangList ) );
			alarmSettings.setPhoneList( StringUtil.trimToEmpty( phoneList ) );
			alarmSettings.setEmailList( StringUtil.trimToEmpty( emailList ) );
			alarmSettings.setMaxConnectionPerIp( StringUtil.trimToEmpty( maxConnectionPerIp ) );
			alarmSettings.setMaxWatchPerIp( StringUtil.trimToEmpty( maxWatchPerIp ) );
			alarmSettings.setDataDir( StringUtil.trimToEmpty( dataDir ) );
			alarmSettings.setDataLogDir( StringUtil.trimToEmpty( dataLogDir ) );
			alarmSettings.setMaxDiskUsage( StringUtil.trimToEmpty( maxDiskUsage ) );
			alarmSettings.setNodePathCheckRule( StringUtil.trimToEmpty( nodePathCheckRule ) );
			//进行Update
			String handleMessage = null;
			if( alarmSettingsDAO.updateAlarmSettingsByClusterId( alarmSettings ) ){
				handleMessage = "Update Success";
			}else{
				handleMessage = "Update Fail";
			}
			return "redirect:/alarmSettings.do?method=alarmSettingsPAGE&clusterId=" + clusterId + "&handleMessage=" + handleMessage;
		} catch ( NumberFormatException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( DaoException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
