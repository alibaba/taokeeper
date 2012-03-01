package com.taobao.taokeeper.dao;

import com.taobao.taokeeper.model.TaoKeeperSettings;
import common.toolkit.java.exception.DaoException;

/**
 * Description: Access DB for taokeeper settings
 * @author   yinshi.nc
 * @Date	 2011-11-13
 */
public interface SettingsDAO {
	
	/** 添加taokeeper配置信息 */
	public boolean addTaoKeeperSettings( TaoKeeperSettings taoKeeperSettings )throws DaoException;
	/** 获取指定 settingsId的 taokeeper配置信息 */
	public TaoKeeperSettings getTaoKeeperSettingsBySettingsId( int settingsId )throws DaoException;
	/** 更新taokeeper信息 */
	public boolean updateTaoKeeperSettingsBySettingsId( TaoKeeperSettings taoKeeperSettings ) throws DaoException;
}
