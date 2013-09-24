package com.taobao.taokeeper.reporter.alarm;

import static common.toolkit.java.constant.SymbolConstant.COMMA;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.taokeeper.common.constant.SystemConstant;
import com.taobao.taokeeper.model.type.Message;
import common.toolkit.java.entity.Message.MessageType;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.collection.ListUtil;
import common.toolkit.java.util.io.NetUtil;

/**
 * Description: 淘宝内部使用：旺旺消息与手机短信
 * 
 * @author 银时 yinshi.nc@taobao.com
 * @Date Dec 26, 2011
 */
public class TbMessageSender implements MessageSender {

	private static final Logger LOG = LoggerFactory.getLogger( TbMessageSender.class );

	private Message[] messages;

	public TbMessageSender( Message... messages ) {
		this.messages = messages;
	}

	@Override
	public void run() {

		if ( null == messages || 0 == messages.length || StringUtil.isBlank( SystemConstant.IP_OF_MESSAGE_SEND ) ){
			LOG.info( "[TaoKeeper]No need to send message: messages.length: " + messages + ", IP_OF_MESSAGE_SEND=" + SystemConstant.IP_OF_MESSAGE_SEND );
			return;
		}

		for ( Message message : this.messages ) {
			try {
				this.sendMessage( StringUtil.trimToEmpty( message.getTargetAddresses() ), StringUtil.trimToEmpty( message.getSubject() ),
						StringUtil.trimToEmpty( message.getContent() ), StringUtil.trimToEmpty( message.getType().toString() ) );
				LOG.info( "[TaoKeeper]Message send success: " + message );
			} catch ( Exception e ) {
				e.printStackTrace();
				LOG.error( "Message send error: " + message + e.getMessage() );
			}
		}

	}

	/**
	 * 发送消息
	 * 
	 * @param targetAddresses
	 * @param subject
	 * @param content
	 *            message content
	 * @param channel
	 *            messate tyep:sms,email,wangwang
	 * @return
	 * @throws Exception 
	 */
	private boolean sendMessage( String targetAddresses, String subject, String content, String channel ) throws Exception {

		if ( StringUtil.isBlank( targetAddresses ) || StringUtil.isBlank( channel ) )
			return false;

		
		List<String> targetAddressList = ListUtil.parseList( StringUtil.trimToEmpty( targetAddresses ), COMMA );

		Map<String, String> map = new HashMap<String, String>();
		map.put( "ip", SystemConstant.IP_OF_MESSAGE_SEND );
		map.put( "subject", URLEncoder.encode( subject, "UTF-8" ) );
		map.put( "content", URLEncoder.encode( content, "UTF-8" ) );
		String url = "";
		if ( channel.equalsIgnoreCase( MessageType.WANGWANG.toString() ) ) {

			for ( String targetAddress : targetAddressList ) {

				map.put( "messageType", "sendWangWangMessage" );
				map.put( "targetAddress", URLEncoder.encode( targetAddress,"UTF-8" ) );
				url = StringUtil.replacePlaceholder( SystemConstant.URL_TEMPLEMENT_OF_MESSAG_SEND, map );
			}
		} else if ( channel.equalsIgnoreCase( MessageType.SMS.toString() ) ) {

			for ( String targetAddress : targetAddressList ) {
				map.put( "messageType", "sendWangWangMessage" );
				map.put( "targetAddress", URLEncoder.encode( targetAddress, "UTF-8" ) );
				url = StringUtil.replacePlaceholder( SystemConstant.URL_TEMPLEMENT_OF_MESSAG_SEND, map );
			}
		}
		LOG.info( "[Taokeeper]Send message: " + url );
		return "ok".equalsIgnoreCase( NetUtil.getContentOfUrl( url ) );

	}

}
