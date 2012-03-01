package com.taobao.taokeeper.dao;

import com.taobao.taokeeper.model.AlarmSettings;
import common.toolkit.java.exception.DaoException;

/**
 * Description: Access DB for alarm settings
 * @author   yinshi.nc
 * @Date	 2011-10-31
 */
public interface AlarmSettingsDAO {
	
	public AlarmSettings getAlarmSettingsByCulsterId( int clusterId )throws DaoException;
	
	public boolean updateAlarmSettingsByClusterId( AlarmSettings alarmSettings ) throws DaoException;
	
	/** 添加一个报警设置 */
	public boolean addAlarmSettings( AlarmSettings alarmSettings ) throws DaoException;
}
