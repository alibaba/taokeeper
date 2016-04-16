package common.toolkit.exception;

/**
 * Description: Exception of illegal param.
 * @author 银时 yinshi.nc@taobao.com
 */
public class IllegalParamException extends Exception {
    public IllegalParamException() {
	super();
    }

    public IllegalParamException( String message ) {
	super( message );
    }

    public IllegalParamException( String message, Throwable cause ) {
        super(message, cause);
    }
 
    public IllegalParamException(Throwable cause) {
        super(cause);
    }
    private static final long serialVersionUID = -5365630128856068164L;
}

