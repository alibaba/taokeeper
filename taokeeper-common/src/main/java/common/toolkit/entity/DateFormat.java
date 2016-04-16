package common.toolkit.entity;

/**
 * 时间类型
 * @author  nileader / nileader@gmail.com
 * @Date	 Feb 15, 2012
 */
public enum DateFormat {
	Date("yyyy-MM-dd"), DateTime("yyyy-MM-dd HH:mm:ss"), SolrDateTime("yyyy-MM-ddTHH:mm:ssZ") ;

	private final String format;
	private DateFormat( String format ) {
		this.format = format;
	}

	public String getFormat() {
		return this.format;
	}

}
