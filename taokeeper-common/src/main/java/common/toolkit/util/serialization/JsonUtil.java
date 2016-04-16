package common.toolkit.util.serialization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author 银时 yinshi.nc@taobao.com
 */
public class JsonUtil<T extends Object> {
	
	/**
	 * 获取一个 TypeToken< T > 对象
	 * @param t
	 * @return
	 */
	public TypeToken< T > getGsonTypeToken() {
		TypeToken< T > type = new TypeToken< T >() {
		};
		return type;
	}
	
	/**
	 * 反序列化JSON对象
	 * @param content
	 * @return
	 * @throws Exception 
	 */
	public T fromJsonString(String jsonString) throws Exception{
		try {
			TypeToken< T > type = new TypeToken< T >() {
			};
			return (T)new Gson().fromJson( jsonString, type.getType() );
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new Exception( "反序列化Json对象出错，jsonString：" + jsonString + ", Error: " + e.getMessage() );
		}
	}

}
