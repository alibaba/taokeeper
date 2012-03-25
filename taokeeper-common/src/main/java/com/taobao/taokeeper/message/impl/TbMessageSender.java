package com.taobao.taokeeper.message.impl;

import static common.toolkit.java.constant.SymbolConstant.COMMA;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.jm.msgcenter.MsgManager;
import com.taobao.jm.msgcenter.common.MsgConstants;
import com.taobao.jm.msgcenter.common.Result;
import com.taobao.taokeeper.common.SystemInfo;
import com.taobao.taokeeper.common.constant.SystemConstant;
import com.taobao.taokeeper.message.MessageSender;
import com.taobao.taokeeper.model.type.EnvType;
import com.taobao.taokeeper.model.type.Message;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.collection.ListUtil;

/**
 * Description: 淘宝内部使用：旺旺消息与手机短信
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

		if ( null == messages || 0 == messages.length || StringUtil.isBlank( SystemConstant.configOfMsgCenter ) )
			return;

		for ( Message message : this.messages ) {
			try {
				this.sendMessage( StringUtil.trimToEmpty( message.getTargetAddresses() ), StringUtil.trimToEmpty( message.getSubject() ),
						StringUtil.trimToEmpty( message.getContent() ), StringUtil.trimToEmpty( message.getType().toString() ) );
			} catch ( Exception e ) {
				e.printStackTrace();
				LOG.error( "Message send error: " + message + e.getMessage() );
			}
		}

	}

	/**
	 * 发送消息
	 * @param targetAddresses 
	 * @param subject 
	 * @param content message content
	 * @param channel messate tyep:sms,email,wangwang
	 * @return
	 */
	private Result sendMessage( String targetAddresses, String subject, String content, String channel ) {

		if ( StringUtil.isBlank( targetAddresses ) || StringUtil.isBlank( channel ) )
			return null;
		Result result = null;
		String sendType = MsgConstants.CUSTOM_SERVER_LIST_TYPE;
		try {

			MsgManager mcm = new MsgManager();

			mcm.setServiceType( MsgConstants.CUSTOM_SERVER_LIST_TYPE );
			mcm.setCustomServerHosts( SystemConstant.serverOfMsgCenter );
			mcm.init();

			if ( EnvType.DAILY.toString().equals( SystemInfo.envName ) ) {
				mcm.setServiceType( MsgConstants.CUSTOM_SERVER_LIST_TYPE );
				mcm.setCustomServerHosts( SystemConstant.serverOfMsgCenter );
				mcm.init();
			} else if ( EnvType.ONLINE.toString().equals( SystemInfo.envName ) || EnvType.PREPARE.toString().equals( SystemInfo.envName ) ) {
				sendType = "HSF";
				mcm.setServiceGroup( "HSF" );
				mcm.setServiceVersion( "1.0.0" );
				mcm.init();
			}

			String sourceId = SystemConstant.sourceIdOfMsgCenter;
			String templateId = SystemConstant.templateIdOfMsgCenter;
			String messageTypeId = SystemConstant.messageTypeIdOfMsgCenter;

			List< String > targetAddressList = ListUtil.parseList( StringUtil.trimToEmpty( targetAddresses ), COMMA );
			for ( String targetAddress : targetAddressList ) {
				result = mcm.sendMsg( targetAddress, "title: " + subject, "content: " + content, channel, sourceId, templateId, messageTypeId );
			}

			if ( result.isSuccess() ) {
				LOG.warn( "Send " + channel + " message success, send type is " + sendType + ", targetAddress: " + targetAddresses + ", subject: "
						+ subject + ", content: " + content );
			} else {
				LOG.warn( "Send " + channel + " message failure, send type is " + sendType + ", targetAddress: " + targetAddresses + ", subject: "
						+ subject + ", content: " + content );
			}

		} catch ( Exception e ) {
			LOG.warn( "Send " + channel + " message failure, send type is " + sendType + ", targetAddress: " + targetAddresses + ", subject: "
					+ subject + ", content: " + content );
			LOG.error( e.getMessage() );
		}
		return result;
	}

}
