package common.toolkit.exception;

/**
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 4, 2012
 */
public class CanNotEncodeException extends Exception{
	private static final long serialVersionUID = 9129117998948166073L;

	public CanNotEncodeException() {
		super();
	}

	public CanNotEncodeException( String message ) {
		super( message );
	}

	public CanNotEncodeException( String message, Throwable cause ) {
		super( message, cause );
	}

	public CanNotEncodeException( Throwable cause ) {
		super( cause );
	}

}
