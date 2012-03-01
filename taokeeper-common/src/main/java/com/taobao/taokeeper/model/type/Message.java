package com.taobao.taokeeper.model.type;

/**
 * Description: 消息实体
 * 
 * @author 银时 yinshi.nc@taobao.com
 * @Date Dec 26, 2011
 */
public class Message {

	/**
	 * 邮件
	 */
	public static final String MAIL_TYPE = "1";

	/**
	 * 短信
	 */
	public static final String SMS_TYPE = "2";

	/**
	 * 站内信
	 */
	public static final String SITE_TYPE = "4";

	/**
	 * 旺旺
	 */
	public static final String WANGWANG_TYPE = "8";

	/**
	 * 消息类型
	 */
	public enum MessageType {
		SMS(SMS_TYPE), EMAIL(MAIL_TYPE), WANGWANG(WANGWANG_TYPE);

		private String type;
		private MessageType( String type ) {
			this.type = type;
		}
		public String toString() {
			return this.type;
		}
	}

	private String targetAddresses;
	private String subject;
	private String content;
	private Message.MessageType type;

	public Message( String targetAddresses, String subject, String content, Message.MessageType type ) {
		this.targetAddresses = targetAddresses;
		this.subject = subject;
		this.content = content;
		this.type = type;
	}

	@Override
	public String toString() {
		return "[targetAddresses: " + this.targetAddresses + ", subject: " + this.subject + ", content: " + this.content + ", type: " + this.type
				+ "]";
	}

	public String getTargetAddresses() {
		return targetAddresses;
	}

	public void setTargetAddresses( String targetAddresses ) {
		this.targetAddresses = targetAddresses;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject( String subject ) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent( String content ) {
		this.content = content;
	}

	public Message.MessageType getType() {
		return type;
	}

	public void setType( Message.MessageType type ) {
		this.type = type;
	}

}
