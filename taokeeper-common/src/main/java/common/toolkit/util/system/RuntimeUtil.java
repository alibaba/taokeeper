package common.toolkit.util.system;

/**
 * Runtime 工具
 * 
 * @author 银时 yinshi.nc yinshi.nc@taobao.com
 * @Date Dec 25, 2011
 */
public class RuntimeUtil {
	
	/**
	 * 增加JVM停止时要做处理事件
	 */
	public static void addShutdownHook( Runnable runnable ) {
		Runtime.getRuntime().addShutdownHook( new Thread( runnable ) );
	}
}
