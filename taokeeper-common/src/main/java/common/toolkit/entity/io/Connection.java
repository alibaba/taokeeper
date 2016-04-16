package common.toolkit.entity.io;

import static common.toolkit.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.constant.NumberConstant.ZERO;

import common.toolkit.util.StringUtil;

/**
 * 连接信息
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class Connection {

	private String serverIp = EMPTY_STRING;
	private String clientIp = EMPTY_STRING;
	private String sessionId = EMPTY_STRING;
	/** 表示从这个连接上收到数据次数，例如345，表示从这个连接（的客户端）那里收到数据345次 */
	private String receiveTimes = ZERO;
	/** 表示向这个连接上发出数据次数，例如345，表示向这个连接（的客户端）发送了数据345次 */
	private String sendTimes = ZERO;

	public Connection(){}
	
	public Connection( String clientIp, String sessionId ) {
		this.setServerIp( serverIp );
		this.setClientIp( clientIp );
		this.setSessionId( sessionId );
	}

	public Connection( String serverIp, String clientIp, String sessionId ) {
		this.setServerIp( serverIp );
		this.setClientIp( clientIp );
		this.setSessionId( sessionId );
	}

	/**
	 * Creates a new instance of Connection.
	 * 
	 * @param clientIp
	 * @param receive
	 *            表示从这个连接上收到数据次数，例如345，表示从这个连接（的客户端）那里收到数据345次
	 * @param send
	 *            表示向这个连接上发出数据次数，例如345，表示向这个连接（的客户端）发送了数据345次
	 */
	public Connection( String clientIp, String sessionId, String receiveTimes, String sendTimes ) {
		this.setClientIp( clientIp );
		this.setSessionId( sessionId );
		this.setReceiveTimes( receiveTimes );
		this.setSendTimes( sendTimes );
	}

	/**
	 * Creates a new instance of Connection.
	 * 
	 * @param clientIp
	 * @param receive
	 *            表示从这个连接上收到数据次数，例如345，表示从这个连接（的客户端）那里收到数据345次
	 * @param send
	 *            表示向这个连接上发出数据次数，例如345，表示向这个连接（的客户端）发送了数据345次
	 */
	public Connection( String serverIp, String clientIp, String sessionId, String receiveTimes, String sendTimes ) {
		this.setServerIp( serverIp );
		this.setClientIp( clientIp );
		this.setSessionId( sessionId );
		this.setReceiveTimes( receiveTimes );
		this.setSendTimes( sendTimes );
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp( String clientIp ) {
		this.clientIp = clientIp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId( String sessionId ) {
		this.sessionId = sessionId;
	}

	public String getReceiveTimes() {
		return receiveTimes;
	}

	public void setReceiveTimes( String receiveTimes ) {
		this.receiveTimes = receiveTimes;
	}

	public String getSendTimes() {
		return sendTimes;
	}

	public void setSendTimes( String sendTimes ) {
		this.sendTimes = sendTimes;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp( String serverIp ) {
		this.serverIp = serverIp;
	}

	@Override
	public String toString() {
		return "[serverIp: " + serverIp + ", clientip: " + this.clientIp + ", sessionId: " + this.sessionId + ", receiveTimes: "
				+ this.getReceiveTimes() + ", sendTimes: " + this.getSendTimes() + " ]";
	}

	@Override
	public boolean equals( Object obj ) {
		if ( null == obj )
			return false;
		if ( !( obj instanceof Connection ) )
			return false;
		if ( this.getServerIp().equalsIgnoreCase( ( ( Connection ) obj ).getServerIp() )
				&& this.getClientIp().equalsIgnoreCase( ( ( Connection ) obj ).getClientIp() )
				&& this.getSessionId().equalsIgnoreCase( ( ( Connection ) obj ).getSessionId() ) ) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return StringUtil.trimToEmpty( this.getServerIp() ).hashCode() + StringUtil.trimToEmpty( this.getClientIp() ).hashCode()
				+ StringUtil.trimToEmpty( this.getSessionId() ).hashCode();
	}

	

}
