package com.taobao.taokeeper.model;
import static common.toolkit.java.constant.HtmlTagConstant.BR;

import java.util.List;
import java.util.Map;

import common.toolkit.java.entity.io.Connection;
import common.toolkit.java.util.StringUtil;

/**
 * Description: Model: ZooKeeperStat
 * 
 * @author yinshi.nc
 * @Date 2011-10-28
 */
public class ZooKeeperStatus {
	
	private String ip;
	private List< String > clientConnectionList;
	private Map< String,Connection > connections;
	@SuppressWarnings("unused")
	private String connectionsContent;
	/** watch nums */
	private int watches;
	private int watchedPaths;
	private boolean isLeader;
	private String mode;
	private long nodeCount;
	private String statContent;
	/** 0:不确定 1:OK 2: ERROR*/
	private int statusType = 0;

	private String sent;
	private String Received;
	
	private Map<String/**session id*/, List<String> /** watched path list */ > watchedPathMap;
	private String watchedPathMapContent;

	
	public String getIp() {
		return ip;
	}

	public void setIp( String ip ) {
		this.ip = ip;
	}

	public boolean isLeader() {
		return isLeader;
	}

	public void setLeader( boolean isLeader ) {
		this.isLeader = isLeader;
	}

	public String getStatContent() {
		return statContent;
	}

	public void setStatContent( String statContent ) {
		this.statContent = statContent;
	}

	public List< String > getClientConnectionList() {
		return clientConnectionList;
	}

	public void setClientConnectionList( List< String > clientConnectionList ) {
		this.clientConnectionList = clientConnectionList;
	}

	public long getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount( long nodeCount ) {
		this.nodeCount = nodeCount;
	}

	public String getMode() {
		return mode;
	}

	public void setMode( String mode ) {
		this.mode = mode;
	}

	public int getWatches() {
		return watches;
	}

	public void setWatches( int watches ) {
		this.watches = watches;
	}
	public Map< String,Connection > getConnections() {
		return connections;
	}

	public void setConnections( Map< String,Connection > connections ) {
		this.connections = connections;
	}

	public int getWatchedPaths() {
		return watchedPaths;
	}

	public void setWatchedPaths( int watchedPaths ) {
		this.watchedPaths = watchedPaths;
	}

	public int getStatusType() {
		return statusType;
	}

	public void setStatusType( int statusType ) {
		this.statusType = statusType;
	}
	
	public Map<String, List<String> > getWatchedPathMap() {
		return watchedPathMap;
	}

	public void setWatchedPathMap( Map<String, List<String> > watchedPathMap ) {
		this.watchedPathMap = watchedPathMap;
	}

	public String getWatchedPathMapContent() {
		return watchedPathMapContent;
	}

	public void setWatchedPathMapContent( String watchedPathMapContent ) {
		this.watchedPathMapContent = watchedPathMapContent;
	}
	
	public String getSent() {
		return sent;
	}

	public void setSent( String sent ) {
		this.sent = sent;
	}

	public String getReceived() {
		return Received;
	}

	public void setReceived( String received ) {
		Received = received;
	}
	
	@Override
	public String toString() {
		return "ZooKeeperStatus[ip: " + ip + ", isLeader: " + isLeader + ", nodeCount: " + nodeCount + ", "
				+ connections + " 个客户端连接" + "在" + watchedPaths + "个path上注册了" + watches + "个watch" + clientConnectionList;
	}
	
	public String getConnectionsContent() {
		return convertConnsToHtmlContent();
	}
	
	/** 把一个Map< String,Connection > connections 转换成html内容 */
	private String convertConnsToHtmlContent(){
		if( null == this.connections || connections.isEmpty() )
			return "No conn on this server!";
		StringBuffer sb = new StringBuffer();
		
//		这里可以统计连接数个数
		
		
		for( String sessionId : connections.keySet() ){
			Connection conn = null;
			if( StringUtil.isBlank( sessionId ) || null == ( conn = connections.get( sessionId ) ) )
				continue;
			sb.append( sessionId ).append( ": " ).append( conn.getClientIp() ).append( ", send to server: " ).append( conn.getReceiveTimes() ).append( ", receive from server: ").append( conn.getSendTimes() ).append( BR );
		}
		return sb.toString();
	}

	
	
	
	
	
	
	
	

}
