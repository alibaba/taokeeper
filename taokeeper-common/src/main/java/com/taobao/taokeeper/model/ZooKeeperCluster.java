package com.taobao.taokeeper.model;

import java.util.List;

/**
 * Description: Model: ZooKeeperCluster
 * 
 * @author yinshi.nc
 * @Date 2011-10-26
 */
public class ZooKeeperCluster {

	public List< String > getServerList() {
		return serverList;
	}

	public void setServerList( List< String > serverList ) {
		this.serverList = serverList;
	}

	private int clusterId;
	private String clusterName;
	/** ip:prot */
	private List< String > serverList;
	private String description;

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId( int clusterId ) {
		this.clusterId = clusterId;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName( String clusterName ) {
		this.clusterName = clusterName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "[clusterId: " + this.clusterId + ", clusterName: " + this.clusterName + ", serverList: " + this.serverList;
	}

}
