package common.toolkit.exception;


/**
 * Description: Exception of service level
 * @author  nileader / nileader@gmail.com
 * @Date	 2012-3-1
 */
public class ServiceException extends Exception {
    public ServiceException() {
	super();
    }

    public ServiceException( String message ) {
	super( message );
    }

    public ServiceException( String message, Throwable cause ) {
        super(message, cause);
    }
 
    public ServiceException(Throwable cause) {
        super(cause);
    }
    private static final long serialVersionUID = -5365630128856068164L;
}

