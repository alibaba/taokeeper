package com.taobao.taokeeper.dao.impl;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_INSERT_TAOKEEPER_SETTINGS_BY_ID;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_QUERY_TAOKEEPER_SETTINGS_BY_ID;
import static com.taobao.taokeeper.common.constant.SqlTemplate.SQL_UPDATE_TAOKEEPER_SETTINGS_BY_ID;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.sql.ResultSet;

import com.taobao.taokeeper.dao.SettingsDAO;
import com.taobao.taokeeper.model.TaoKeeperSettings;
import common.toolkit.java.entity.db.DBConnectionResource;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.db.DbcpUtil;
/**
 * Description: Access DB for taokeeper settings
 * @author   yinshi.nc
 * @Date	 2011-10-28
 */
public class SettingsDAOImpl implements SettingsDAO{
	
	public TaoKeeperSettings getTaoKeeperSettingsBySettingsId( int settingsId )throws DaoException{
		
		TaoKeeperSettings taoKeeperSettings = null;
    	ResultSet rs = null;
    	DBConnectionResource dbConnectionResource = null;
    	try {
    		String querySQL = StringUtil.replaceSequenced( SQL_QUERY_TAOKEEPER_SETTINGS_BY_ID, settingsId + EMPTY_STRING );
    		dbConnectionResource = DbcpUtil.executeQuery( querySQL );
    		if( null == dbConnectionResource )
    			throw new DaoException( "没有返回结果" );
    		rs = dbConnectionResource.resultSet;
			if( null != rs && rs.next() ){
				
				String envName						= rs.getString( "env_name" );
				int maxThreadsOfZooKeeperCheck		= rs.getInt( "max_threads_of_zookeeper_check" );
				String description        			= rs.getString( "description" );
				
				taoKeeperSettings = new TaoKeeperSettings();
				taoKeeperSettings.setSettingsId( settingsId );
				taoKeeperSettings.setEnvName( envName );
				taoKeeperSettings.setMaxThreadsOfZooKeeperCheck( maxThreadsOfZooKeeperCheck );
				taoKeeperSettings.setDescription( description );
			}
			return taoKeeperSettings;
		} catch ( Exception e ) {
			throw new DaoException( "Error when query TaoKeeperSettings by settings_id: " + settingsId + ", Error: " + e.getMessage(), e );
		}finally{
			if( null != dbConnectionResource ){
				DbcpUtil.closeResultSetAndStatement( rs, dbConnectionResource.statement );
				DbcpUtil.returnBackConnectionToPool( dbConnectionResource.connection );
			}
		}
	}

	@Override
	public boolean updateTaoKeeperSettingsBySettingsId( TaoKeeperSettings taoKeeperSettings ) throws DaoException {

		if( null == taoKeeperSettings )
			return false;
		
		//从数据库中获取指定settings_id配置
    	try {
    		String updateSql = StringUtil.replaceSequenced( SQL_UPDATE_TAOKEEPER_SETTINGS_BY_ID, taoKeeperSettings.getEnvName(), taoKeeperSettings.getMaxThreadsOfZooKeeperCheck() + EMPTY_STRING, taoKeeperSettings.getDescription(), taoKeeperSettings.getSettingsId() + EMPTY_STRING );
			int num = DbcpUtil.executeUpdate( updateSql );
			if( 1 == num ){
				return true;
			}
			throw new DaoException( "Not exist such record" );
		} catch ( Exception e ) {
			throw new DaoException( "Error when update taoKeeperSettings by settingsId: " + taoKeeperSettings + ", Error: " + e.getMessage(), e );
		}
	}

	@Override
	public boolean addTaoKeeperSettings( TaoKeeperSettings taoKeeperSettings ) throws DaoException {
		
		if( null == taoKeeperSettings )
			return false;
		
    	try {
    		String insertSql = StringUtil.replaceSequenced( SQL_INSERT_TAOKEEPER_SETTINGS_BY_ID, taoKeeperSettings.getEnvName(), taoKeeperSettings.getMaxThreadsOfZooKeeperCheck() + EMPTY_STRING, taoKeeperSettings.getDescription() );
			int num = DbcpUtil.executeInsert( insertSql );
			if( 1 == num ){
				return true;
			}
			throw new DaoException( "Not exist such record" );
		} catch ( Exception e ) {
			throw new DaoException( "Error when insert taoKeeperSettings: " + taoKeeperSettings + ", Error: " + e.getMessage(), e );
		}
		
		
	}

	
}
