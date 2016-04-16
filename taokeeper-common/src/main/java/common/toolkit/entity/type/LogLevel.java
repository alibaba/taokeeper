package common.toolkit.entity.type;

/**
 * 类说明: 各种环境
 * 
 * @author yinshi.nc
 */
public enum LogLevel {

	DEBUG("DEBUG"), INFO("INFO"), WARN("WARN"), ERROR("ERROR"), FATAL("FATAL");

	private String logLevel;

	private LogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	@Override
	public String toString() {
		return this.logLevel;
	}

}
