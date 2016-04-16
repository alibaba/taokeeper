package common.toolkit.util;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Description:一个统计程序，用于统计压力测试时候的TPS计算
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class StatisticsUtil {

	private static Log log = LogFactory.getLog( "commonStat" );
	

	public static boolean NEED_STAT = false;
	public static int SECOND_OF_STAT_DELAY = 10;

	// 上次统计时间（这个值是通过 System.currentTimeMillis() 获取）
	public static long lastedTime = System.currentTimeMillis();
	// 上次统计之后累计执行的次数
	public static AtomicLong totalTransactions = new AtomicLong();

	/**
	 * 开始统计
	 */
	public static void start( int secondOfStatDelay ) {

		SECOND_OF_STAT_DELAY = secondOfStatDelay;
		StatisticsUtil.NEED_STAT = true;
		ThreadUtil.startThread( new Runnable() {
			@Override
			public void run() {
				while ( StatisticsUtil.NEED_STAT ) {
					try {
						Thread.sleep( SECOND_OF_STAT_DELAY * 1000 );
					} catch ( InterruptedException e ) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					log.warn( ( StatisticsUtil.totalTransactions.get() * 1.0 ) / ( ( System.currentTimeMillis() - StatisticsUtil.lastedTime ) / 1000 ) );

					StatisticsUtil.totalTransactions.set( 0 );
					StatisticsUtil.lastedTime = System.currentTimeMillis();
				}
			}
		} );
	}

	/**
	 * 停止统计
	 */
	public static void stop() {
		StatisticsUtil.NEED_STAT = false;
	}

	public static void main( String[] args ) {
		StatisticsUtil.start( 10 );
		while ( true ) {
			StatisticsUtil.totalTransactions.addAndGet( 1 );
		}
	}

}
