package common.toolkit.exception;



/**
 * API接口层面的异常类型
 * @author  nileader / nileader@gmail.com
 * @date 2012-11-09
 */
public class ApiException extends Exception {
    public ApiException() {
	super();
    }

    public ApiException( String message ) {
	super( message );
    }

    public ApiException( String message, Throwable cause ) {
        super(message, cause);
    }
 
    public ApiException(Throwable cause) {
        super(cause);
    }
    private static final long serialVersionUID = -5365630128856068164L;
}

