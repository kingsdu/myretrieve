package com.retrieve.api;

import java.util.List;

public interface FullTextResult {

	//返回搜索集合
	public List getResultList();
	
	public void setResultList(List list);
	
	//返回统计搜索集合
	public List getFacetList();
	
	public void setFacetList(List list);
	
	//返回总记录数
	public long getNumFound();
	
	public void setNumFound(long numFound);
}
