package common.toolkit.exception;

/**
 * 
 * Description: Exception of SSH handle
 * @author 银时 yinshi.nc@taobao.com
 */
public class SSHException extends Exception {
    public SSHException() {
	super();
    }

    public SSHException( String message ) {
	super( message );
    }

    public SSHException( String message, Throwable cause ) {
        super(message, cause);
    }
 
    public SSHException(Throwable cause) {
        super(cause);
    }
    private static final long serialVersionUID = -5365630128856068164L;
}

