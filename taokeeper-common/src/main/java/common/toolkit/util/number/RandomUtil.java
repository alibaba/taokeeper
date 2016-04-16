package common.toolkit.util.number;

import java.util.Random;

/**
 * 类说明: 文件操作相关工具类
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class RandomUtil {

	/**
	 * 获取[0,endInt) 的一个随机整数，不包括endInt 
	 */
	public static int getInt( int endInt ){
		
		Random random = new Random();
		return random.nextInt( endInt );
		
	}
	public static void main( String[] args ) throws InterruptedException {
		while(true){
			System.out.println(getInt( 2 ));
			Thread.sleep( 500 );
		}
	}
	
	
	
	
}
