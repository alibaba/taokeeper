package common.toolkit.entity;

import java.util.Map;

/**
 * Info of system about performance such as: cup/memory usage, load,etc..
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class HostPerformanceEntity {

	private String ip;
	private String hostName;
	private String cpuUsage;
	private String load;
	private String memoryUsage;
	private Map< String/**挂载点*/, String/**使用百分比*/ > diskUsageMap;

	public String getIp() {
		return ip;
	}

	public void setIp( String ip ) {
		this.ip = ip;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName( String hostName ) {
		this.hostName = hostName;
	}

	/** 这里将会返回 30% */
	public String getCpuUsage() {
		return cpuUsage;
	}

	/** 按这样的格式赋值 30% */
	public void setCpuUsage( String cpuUsage ) {
		this.cpuUsage = cpuUsage;
	}

	public String getLoad() {
		return load;
	}

	public void setLoad( String load ) {
		this.load = load;
	}

	public String getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage( String memoryUsage ) {
		this.memoryUsage = memoryUsage;
	}

	public Map< String, String > getDiskUsageMap() {
		return diskUsageMap;
	}

	public void setDiskUsageMap( Map< String, String > diskUsageMap ) {
		this.diskUsageMap = diskUsageMap;
	}
	
	@Override
	public String toString() {
		return "SystemPerformanceEntity[ ip: " + this.ip + ", cpuUsage: " + this.cpuUsage + ", memoryUsage: " + this.memoryUsage + ", load: "
				+ this.load + ", diskUsage: "+ diskUsageMap +"]";
	}
	
	
	

}