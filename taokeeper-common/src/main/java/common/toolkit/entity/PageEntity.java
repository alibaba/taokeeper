package common.toolkit.entity;

import java.util.List;

/**
 * 分页包装类
 * @author nileader
 */
public class PageEntity<T> {

	private List<T> results;
	private long totalResults;
	private int totalPages;
	private int pageNum;
	private int pageSize;
	public List< T > getResults() {
		return results;
	}
	public void setResults( List< T > results ) {
		this.results = results;
	}
	public long getTotalResults() {
		return totalResults;
	}
	public void setTotalResults( long totalResults ) {
		this.totalResults = totalResults;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages( int totalPages ) {
		this.totalPages = totalPages;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum( int pageNum ) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize( int pageSize ) {
		this.pageSize = pageSize;
	}
	
	
	
	
	
	
	
	
}
