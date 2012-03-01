package com.taobao.taokeeper.model.type;

/**
 * 类说明: 各种环境
 * 
 * @author yinshi.nc
 */
public enum EnvType {

	TEST("TEST"), DAILY("DAILY"), ONLINE("ONLINE"), PREPARE("PREPARE"), SANDBOX("SANBOX");

	private String envName;

	private EnvType(String envName) {
		this.envName = envName;
	}

	@Override
	public String toString() {
		return this.envName;
	}

}
