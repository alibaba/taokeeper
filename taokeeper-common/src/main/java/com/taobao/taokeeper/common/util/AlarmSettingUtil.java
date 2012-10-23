package com.taobao.taokeeper.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.taokeeper.common.type.NodePathCheckRule;

import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.collection.ListUtil;

/**
 * 
 * @author nileader/nileader@gmail.com
 * @date 2012-10-22 
 */
public class AlarmSettingUtil {

	/**
	 * 将字符串解析成NodePathCheckRule
	 * @param str 类似于的 [/:nileader,yinshi;/nileader:test]^{/:nileader,yinshi;/nileader:test}
	 * @return 可能会返回null
	 * @throws Exception 
	 */
	public static NodePathCheckRule parseNodePathCheckRuleFromString( String str ) throws Exception{
		if( StringUtil.isBlank( str ) ){
			return null;
		}
		
		String[] temp = str.split( "\\^" );
		if( 0 == temp.length ){
			//不合法的配置
			return null;
		}
		
		try {
			//可能是一个：/:nileader,yinshi;/nileader:test
			String strPathOnlyCanBeExist = StringUtil.replaceAll( temp[0], "", "\\|" );
			//可能是一个：/:nileader,yinshi;/nileader:test
			String strPathCanNotBeExist = StringUtil.replaceAll( temp[1], "", "\\|" );
			
			/** 只能够出现这些path */
			Map<String,List<String>> pathOnlyCanBeExist = new HashMap<String, List<String>>();
			/** 不能出现这些path */
			Map<String,List<String>> pathCanNotBeExist = new HashMap<String, List<String>>();
			
			if( !StringUtil.isBlank( strPathOnlyCanBeExist ) ){
				String[] ruleArray = strPathOnlyCanBeExist.split( ";" );
				for( String rule : ruleArray ){
					if( StringUtil.isBlank( rule ) ){
						continue;
					}
					String[] pathArray = StringUtil.trimToEmpty( rule ).split( ":" );
					String nodeName = StringUtil.trimToEmpty( pathArray[0] );
					List<String> pathList = ListUtil.parseList( pathArray[1] );
					pathOnlyCanBeExist.put( nodeName, pathList );
				}
			}
			
			if( !StringUtil.isBlank( strPathCanNotBeExist ) ){
				String[] ruleArray = strPathCanNotBeExist.split( ";" );
				for( String rule : ruleArray ){
					if( StringUtil.isBlank( rule ) ){
						continue;
					}
					String[] pathArray = StringUtil.trimToEmpty( rule ).split( ":" );
					String nodeName = pathArray[0];
					List<String> pathList = ListUtil.parseList( pathArray[1] );
					pathCanNotBeExist.put( nodeName, pathList );
				}
			}

			NodePathCheckRule nodePathCheckRule = new NodePathCheckRule();
			nodePathCheckRule.setPathCanNotBeExist( pathCanNotBeExist );
			nodePathCheckRule.setPathOnlyCanBeExist( pathOnlyCanBeExist );
			
			return nodePathCheckRule;
		} catch ( Exception e ) {
			throw new Exception( "Error when parseNodePathCheckRuleFromString, String: " + str + ", Error: " + e.getMessage(), e );
		}
		
		
		
		
	}
}
