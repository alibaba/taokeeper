package com.taobao.taokeeper.common.type;

import java.util.List;
import java.util.Map;

/**
 * ZooKeeper Node的path检查规则
 * @author nileader/nileader@gmail.com
 * @date 2012-10-22
 */
public class NodePathCheckRule {
	
	/** 只能够出现这些path */
	private Map<String,List<String>> pathOnlyCanBeExist;
	/** 不能出现这些path */
	private Map<String,List<String>> pathCanNotBeExist;
	
	
	public Map<String, List<String>> getPathOnlyCanBeExist() {
		return pathOnlyCanBeExist;
	}
	public void setPathOnlyCanBeExist( Map<String, List<String>> pathOnlyCanBeExist ) {
		this.pathOnlyCanBeExist = pathOnlyCanBeExist;
	}
	public Map<String, List<String>> getPathCanNotBeExist() {
		return pathCanNotBeExist;
	}
	public void setPathCanNotBeExist( Map<String, List<String>> pathCanNotBeExist ) {
		this.pathCanNotBeExist = pathCanNotBeExist;
	}
	
	@Override
	public String toString() {
		return "Node的path检查规则[只能出现的节点：" + this.pathOnlyCanBeExist + 
				   "不能出现的节点：" + this.pathCanNotBeExist + "]"; 
	}
	
	
	
}
