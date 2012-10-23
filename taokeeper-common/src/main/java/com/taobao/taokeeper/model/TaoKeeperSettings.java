package com.taobao.taokeeper.model;


/**
 * Description:	Model: TaoKeeper 系统设置
 * @author   yinshi.nc
 * @Date	 2011-11-13
 */
public class TaoKeeperSettings {

	
	private int 	settingsId;
	private String  envName;
	private int 	maxThreadsOfZooKeeperCheck;
	private String 	description;

	public TaoKeeperSettings(){}
	public TaoKeeperSettings(int settingsId, //
			                                String envName, //
			                                int maxThreadsOfZooKeeperCheck, //
			                                String description ){
		this.settingsId 				= settingsId;
		this.envName 					= envName;
		this.maxThreadsOfZooKeeperCheck	= maxThreadsOfZooKeeperCheck;
		this.description 				= description;
	}
	
	
	public int getSettingsId() {
		return settingsId;
	}
	public void setSettingsId( int settingsId ) {
		this.settingsId = settingsId;
	}
	public String getEnvName() {
		return envName;
	}
	public void setEnvName( String envName ) {
		this.envName = envName;
	}
	public int getMaxThreadsOfZooKeeperCheck() {
		return maxThreadsOfZooKeeperCheck;
	}
	public void setMaxThreadsOfZooKeeperCheck( int maxThreadsOfZooKeeperCheck ) {
		this.maxThreadsOfZooKeeperCheck = maxThreadsOfZooKeeperCheck;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription( String description ) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "TaoKeeperSettings:[settingsId=" + settingsId+ ", envName=" + envName + 
				    ", maxThreadsOfZooKeeperCheck="+ maxThreadsOfZooKeeperCheck + ", description=" + 
				    description + ", description=" + description;
	}
	
}
