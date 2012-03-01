package com.taobao.taokeeper.monitor.web;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.model.TaoKeeperSettings;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.io.ServletUtil;

/**
 * Admin
 * 
 * @author yinshi.nc@taobao.com
 * @since 2011-08-10
 */
@Controller
@RequestMapping("/admin.do")
public class AdminController extends BaseController {

	
	private static final Logger LOG = LoggerFactory.getLogger( AdminController.class );
	
	/** PAGE 是否开启报警开关 */
	@RequestMapping(params = "method=switchOfNeedAlarmPAGE")
	public ModelAndView switchOfNeedAlarmPAGE( HttpServletRequest request, HttpServletResponse response, String handleMessage ) {

		Map< String, Object > model = new HashMap< String, Object >();
		model.put( "needAlarm", GlobalInstance.needAlarm.get() );
		model.put( "handleMessage", handleMessage );
		return new ModelAndView( "admin/switchOfNeedAlarmPAGE", model );
	}

	/** HANDLE 是否开启报警开关 */
	@RequestMapping(params = "method=updateSwitchOfNeedAlarmHandle")
	public String updateSwitchOfNeedAlarmHandle( HttpServletRequest request, HttpServletResponse response, String needAlarm ) {

		String handleMessage = EMPTY_STRING;
		if ( "true".equalsIgnoreCase( needAlarm ) ) {
			GlobalInstance.needAlarm.set( true );
			handleMessage = "Success, now open alarm!";
		} else {
			GlobalInstance.needAlarm.set( false );
			handleMessage = "Success, now close alarm!";
		}
		return "redirect:admin.do?method=switchOfNeedAlarmPAGE&handleMessage=" + handleMessage;
	}

	/** PAGE 系统设置 */
	@RequestMapping(params = "method=setSystemConfigPAGE")
	public ModelAndView setSystemConfigPAGE( HttpServletRequest request, HttpServletResponse response, String handleMessage ) {
		
		try {
			TaoKeeperSettings taoKeeperSettings = GlobalInstance.taoKeeperSettings;
			if( null == taoKeeperSettings ){
				taoKeeperSettings = taoKeeperSettingsDAO.getTaoKeeperSettingsBySettingsId( 1 );
				if( null == taoKeeperSettings ){
					ServletUtil.writeToResponse( response, "目前还没有TaoKeeper配置" );
					return null;
				}
			}
			Map< String, Object > model = new HashMap< String, Object >();
			model.put( "settingsId", taoKeeperSettings.getSettingsId() );
			model.put( "envName", taoKeeperSettings.getEnvName() );
			model.put( "maxThreadsOfZooKeeperCheck", taoKeeperSettings.getMaxThreadsOfZooKeeperCheck() );
			model.put( "description", taoKeeperSettings.getDescription() );
			model.put( "handleMessage", handleMessage );
			return new ModelAndView( "admin/setSystemConfigPAGE", model );
		} catch ( Exception e ) {
			ServletUtil.showSystemErrorToResponse( response, e );
			e.printStackTrace();
			return null;
		}
	}

	
	/** Handle 系统配置 */
	@RequestMapping(params = "method=setSystemConfigHANDLE")
	public String setSystemConfigHandle( HttpServletRequest request, HttpServletResponse response, 
			String handleMessage,
			String settingsId,
			String envName,
			String description,
			String maxThreadsOfZooKeeperCheck ) {
		maxThreadsOfZooKeeperCheck = StringUtil.defaultIfBlank( maxThreadsOfZooKeeperCheck, GlobalInstance.taoKeeperSettings.getMaxThreadsOfZooKeeperCheck() + EMPTY_STRING );
		try {
			
			TaoKeeperSettings taoKeeperSettings = new TaoKeeperSettings();
			int settingsIdInt = Integer.parseInt( settingsId ) != 0 ? Integer.parseInt( settingsId ) : 1;
			taoKeeperSettings.setSettingsId( settingsIdInt );
			taoKeeperSettings.setEnvName( envName );
			taoKeeperSettings.setDescription( description );
			taoKeeperSettings.setMaxThreadsOfZooKeeperCheck( Integer.parseInt( maxThreadsOfZooKeeperCheck ) );
			
			//先查询下是否存在这样的记录，如果不存在，先进行添加
			TaoKeeperSettings taoKeeperSettings_Old =  taoKeeperSettingsDAO.getTaoKeeperSettingsBySettingsId( taoKeeperSettings.getSettingsId() );
			if( null == taoKeeperSettings_Old ){
				//进行添加
				taoKeeperSettingsDAO.addTaoKeeperSettings( taoKeeperSettings );
			}else{
				//更新数据库
				taoKeeperSettingsDAO.updateTaoKeeperSettingsBySettingsId( taoKeeperSettings );
			}
			//更新缓存
			GlobalInstance.taoKeeperSettings = taoKeeperSettings;
			handleMessage = "Success to update system config.";
			LOG.info( "Success to update system config." );
		} catch ( Exception e ) {
			handleMessage = "Fail to update system config, error: " + e.getMessage();
			LOG.error( "Fail to update system config, error: " + e.getMessage() );
		}
		return "redirect:admin.do?method=setSystemConfigPAGE&handleMessage=" + handleMessage;
	}

}
