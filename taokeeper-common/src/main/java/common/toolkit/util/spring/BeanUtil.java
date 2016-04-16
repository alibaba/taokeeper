package common.toolkit.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Spring bean相关工具类
 * @author nileader/nileader@gmail.com
 * 
 */
public class BeanUtil implements BeanFactoryAware {

	private static BeanFactory beanFactory;

	public static Object getBean( String beanName ) {
		return beanFactory.getBean( beanName );
	}

	public static < T > T getBean( String beanName, Class<T> clazs ) {
		return clazs.cast( getBean( beanName ) );
	}

	public void setBeanFactory( BeanFactory beanFactory ) throws BeansException {
		BeanUtil.beanFactory = beanFactory;
	}

}
