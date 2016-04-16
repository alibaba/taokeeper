package common.toolkit.constant;

import java.util.regex.Pattern;


/**
 * Description:
 * @author 银时 yinshi.nc@taobao.com
 */
public class RegExpConstant {
	
	public static final String   REG_EXP_OF_IP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-1]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
	/**Diff 文件中用于分隔不同文件的标志, 通过是这样: <br>
	 * Index: trunk/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-config/src/main/resources/common/page-cache.xml   <br>
	 * =================================================================== <br>
	 * */
	public final static String REGEX_OF_DIFF_INDEX = "===================================================================";
	
	
	/** 包含IP */
	public static final String REG_EXP_OF_CONTAINS_IP = "((?s).*?)([1-9]|[1-9]\\d|1\\d{2}|2[0-1]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}((?s).*?)";
	public static final Pattern PATTERN_OF_CONTAINS_IP = Pattern.compile( REG_EXP_OF_CONTAINS_IP );
	
}
