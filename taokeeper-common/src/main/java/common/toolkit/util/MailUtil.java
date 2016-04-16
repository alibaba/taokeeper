package common.toolkit.util;

import common.toolkit.entity.email.MailEntity;
import common.toolkit.entity.email.SimpleMailSender;


/**
 * 邮件相关工具类
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class MailUtil {

	/**
	 * 发送纯文本邮件
	 * @param serverHost = "mail.abc.com";
	 * @param serverPort  = "25";
	 * @param userName = "yinshi.nc";
	 * @param passWord = "123456";
	 * @param fromMail = "yinshi.nc@taobao.com";
	 * @param toMail = "yinshi.nc@taobao.com";
	 * @param subject = "这是一个测试标题";
	 * @param content = "这是一个测试内容";
	 * @throws Exception
	 */
	public static boolean sendTextMail( String serverHost, String serverPort, String userName, String passWord,
			String fromMail, String toMail, String subject, String content ) throws Exception {

		if ( StringUtil.isBlank( serverHost, userName, passWord, fromMail, toMail ) ) {
			throw new Exception( "参数不能为空" );
		}

		// 这个类主要是设置邮件
		MailEntity mailEntity = new MailEntity();
		mailEntity.setMailServerHost( serverHost );
		mailEntity.setMailServerPort( serverPort );
		mailEntity.setValidate( true );
		mailEntity.setUserName( userName );
		mailEntity.setPassword( passWord );
		mailEntity.setFromAddress( fromMail );
		mailEntity.setToAddress( toMail );
		mailEntity.setSubject( subject );
		mailEntity.setContent( content );

		SimpleMailSender sms = new SimpleMailSender();
		sms.sendTextMail( mailEntity );

		return true;
	}

}
