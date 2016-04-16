package common.toolkit.entity.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 邮件授权对象
 * @author 银时 yinshi.nc@taobao.com
 */
public class MyAuthenticator extends Authenticator{   
    String userName=null;   
    String password=null;   
        
    public MyAuthenticator(){   
    }   
    public MyAuthenticator(String username, String password) {    
        this.userName = username;    
        this.password = password;    
    }    
    protected PasswordAuthentication getPasswordAuthentication(){   
        return new PasswordAuthentication(userName, password);   
    }   
}   
