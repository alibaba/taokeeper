package com.taobao.taokeeper.model;


/**
 * Description:	Model: TaoKeeper_Stat_DAO
 * @author   yinshi.nc
 */
public class TaoKeeperStat {

	private int clusterId;
	private String server;
	private String statDateTime;
	private String statDate;
	private int connections;
	private int watches;
	private long sendTimes;
	private long receiveTimes;
	private long nodeCount;
	
	
	
	
	public TaoKeeperStat(){}
	public TaoKeeperStat(int clusterId, String server, String statDateTime, String statDate, int connections, int watches, long sendTimes, long receiveTimes, long nodeCount ){
		this.clusterId    = clusterId;
		this.server       = server;
		this.statDateTime = statDateTime;
		this.statDate     = statDate;
		this.connections  = connections;
		this.watches      = watches;
		this.sendTimes    = sendTimes;
		this.receiveTimes = receiveTimes;
		this.nodeCount    = nodeCount;
	}
	

	public int getClusterId() {
		return clusterId;
	}
	public void setClusterId( int clusterId ) {
		this.clusterId = clusterId;
	}
	public String getServer() {
		return server;
	}
	public void setServer( String server ) {
		this.server = server;
	}
	public String getStatDateTime() {
		return statDateTime;
	}
	public void setStatDateTime( String statDateTime ) {
		this.statDateTime = statDateTime;
	}
	public String getStatDate() {
		return statDate;
	}
	public void setStatDate( String statDate ) {
		this.statDate = statDate;
	}
	public int getWatches() {
		return watches;
	}
	public void setWatches( int watches ) {
		this.watches = watches;
	}
	public long getSendTimes() {
		return sendTimes;
	}
	public void setSendTimes( int sendTimes ) {
		this.sendTimes = sendTimes;
	}
	public long getReceiveTimes() {
		return receiveTimes;
	}
	public void setReceiveTimes( int receiveTimes ) {
		this.receiveTimes = receiveTimes;
	}
	public long getNodeCount() {
		return nodeCount;
	}
	public void setNodeCount( int nodeCount ) {
		this.nodeCount = nodeCount;
	}
	public int getConnections() {
		return connections;
	}
	public void setConnections( int connections ) {
		this.connections = connections;
	}
	
}
