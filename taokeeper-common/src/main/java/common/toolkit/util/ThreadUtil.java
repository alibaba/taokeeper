package common.toolkit.util;

import java.util.Timer;
import java.util.TimerTask;

import common.toolkit.constant.SymbolConstant;
import common.toolkit.exception.IllegalParamException;

/**
 * 类说明: 线程相关工具类
 * @author 银时 yinshi.nc@taobao.com
 */
public class ThreadUtil {

	/**
	 * 重复开启 threadNum 个线程来执行 runnable
	 * @param runnable 可执行任务
	 * @param threadNum 重复开启的线程个数
	 * @param sleepTime 启动完所有线程后，休息 ms
	 */
	public static void startThread( Runnable runnable, String threadName, int threadNum, long sleepTime ) {

		for ( int i = 0; i < threadNum; i++ ) {
			Thread thread = new Thread( runnable, SymbolConstant.POUND + StringUtil.defaultIfBlank( threadName, "Thread" ) + SymbolConstant.MINUS_SIGN + i );
			thread.start();
		}
		try {
			Thread.sleep( sleepTime );
		} catch ( InterruptedException e ) {
		}
	}
	
	
	
	/**
	 * 重复开启 threadNum 个线程来执行 runnable
	 * @param runnable 可执行任务
	 * @param threadNum 重复开启的线程个数
	 * @param sleepTime 启动完所有线程后，休息 ms
	 */
	public static void startThread( Runnable runnable, int threadNum, long sleepTime ) {
		ThreadUtil.startThread( runnable, "Thread", threadNum, sleepTime );
	}
	
	

	/**
	 * 开启 1 个线程来执行 runnable
	 * @param runnable 可执行任务
	 */
	public static void startThread( Runnable runnable ) {
		startThread( runnable, 1, 0 );
	}
	
	/**
	 * 开启 1 个线程来执行 runnable
	 * @param runnable 可执行任务
	 */
	public static void startThread( Runnable runnable, String threadName) {
		startThread( runnable, StringUtil.trimToEmpty( threadName ), 1, 0 );
	}
	

	/**
	 * 重复开启 threadNum 个线程来执行 runnable
	 * @param runnable 可执行任务
	 * @param threadNum 重复开启的线程个数
	 */
	public static void startThread( Runnable runnable, long sleepTime ) {
		startThread( runnable, 1, sleepTime );
	}

	/**
	 * 定时执行一个任务
	 * @param task 实现TimerTask接口的任务实例
	 * @param delay 调用方法后要延时的毫秒数
	 * @param period 执行间隔
	 * @throws IllegalParamException 
	 */
	public static void scheduleAtFixedRateDelayTimeMillisDelay( TimerTask task, long delay, long period ) throws IllegalParamException {
		if( ObjectUtil.isBlank( task ) )
			throw new IllegalParamException( "task 为空" );
		new Timer().scheduleAtFixedRate( task, delay, period );
	}
	
	/**
	 * 定时执行一个任务
	 * @param task 实现TimerTask接口的任务实例
	 * @param hourOfTomorrow 第二天的几点开始
	 * @param period 执行间隔
	 * @throws Exception 
	 * @throws IllegalParamException 
	 */
	public static void scheduleAtFixedRateDelayDaysHour( TimerTask task, int delayDays, int hourOfTomorrow, long period ) throws IllegalParamException, Exception {
		ThreadUtil.scheduleAtFixedRateDelayTimeMillisDelay( task, DateUtil.getTimeMillisToAfterDaysHour( delayDays, hourOfTomorrow ), period );
	}
	
	/**
	 * Sleep thread without exception.
	 * @param millis
	 */
	public static void sleep( long millis ){
		try {
			Thread.sleep( millis );
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	

}