package com.retrieve.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullTextSearchParams {

	//搜索关键词
	private String queryWord = "";

	//指定搜索域，并且默认的关系是OR
	private List<String> assignmentFields = new ArrayList<String>();
	/**
	 * 指定搜索域与搜索域之间的关系
	 * Map<String,String> 第一个String是域名，比如：name
	 *                    第二个String是关系，写法：或者：OR 与：AND
	 */
	private List<Map<String,String>> assignFields = new ArrayList<Map<String,String>>();

	//显示域
	private String[] viewFields;

	//是否高亮
	private Boolean isHighlight = true;

	//高亮域
	private String[] highlightFields;

	//高亮前缀
	private String preHighlight = "<em class=\"highlight\">";

	//高亮后缀
	private String postHighlight = "</em>";

	//排序域 String:需要排序的域名，Boolean：true 升序 false 降序
	private Map<String,Boolean> sortField = new HashMap<String,Boolean>();

	//过滤域
	private Map<String,String> filterField = new HashMap<String,String>();

	//开始行
	private int startNums = 0;

	//一页显示多少行
	private int pageCount = 15;

	//是否统计
	private Boolean isFacet = false;

	//统计域
	private String[] facetFields;

	//返回的结果数
	private long numFound;

	//显示结果的字数
	private int viewNums = 200;

	//指定搜索权重
	private Map<String,Float> boost = new HashMap<String,Float>();

	//指定lucene中返回的结果数
	private int returnNums = 1000;


	public Map<String, Float> getBoost() {
		return boost;
	}

	public void setBoost(Map<String, Float> boost) {
		this.boost = boost;
	}

	public int getReturnNums() {
		return returnNums;
	}

	public void setReturnNums(int returnNums) {
		this.returnNums = returnNums;
	}


	public String getQueryWord() {
		return queryWord;
	}

	public void setQueryWord(String queryWord) {
		this.queryWord = queryWord;
	}

	public List<String> getAssignmentFields() {
		return assignmentFields;
	}

	public void setAssignmentFields(List<String> assignmentFields) {
		this.assignmentFields = assignmentFields;
	}

	public String[] getViewFields() {
		return viewFields;
	}

	public void setViewFields(String[] viewFields) {
		this.viewFields = viewFields;
	}

	public String[] getHighlightFields() {
		return highlightFields;
	}

	public void setHighlightFields(String[] highlightFields) {
		this.highlightFields = highlightFields;
	}

	public String getPreHighlight() {
		return preHighlight;
	}

	public void setPreHighlight(String preHighlight) {
		this.preHighlight = preHighlight;
	}

	public String getPostHighlight() {
		return postHighlight;
	}

	public void setPostHighlight(String postHighlight) {
		this.postHighlight = postHighlight;
	}

	public Map<String, Boolean> getSortField() {
		return sortField;
	}

	public void setSortField(Map<String, Boolean> sortField) {
		this.sortField = sortField;
	}

	public Map<String, String> getFilterField() {
		return filterField;
	}

	public void setFilterField(Map<String, String> filterField) {
		this.filterField = filterField;
	}

	public int getStartNums() {
		return startNums;
	}

	public void setStartNums(int startNums) {
		this.startNums = startNums;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public String[] getFacetFields() {
		return facetFields;
	}

	public void setFacetFields(String[] facetFields) {
		this.facetFields = facetFields;
	}

	public long getNumFound() {
		return numFound;
	}

	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}

	public int getViewNums() {
		return viewNums;
	}

	public void setViewNums(int viewNums) {
		this.viewNums = viewNums;
	}

	public List<Map<String, String>> getAssignFields() {
		return assignFields;
	}

	public void setAssignFields(List<Map<String, String>> assignFields) {
		this.assignFields = assignFields;
	}

	public Boolean getIsHighlight() {
		return isHighlight;
	}

	public void setIsHighlight(Boolean isHighlight) {
		this.isHighlight = isHighlight;
	}

	public Boolean getIsFacet() {
		return isFacet;
	}

	public void setIsFacet(Boolean isFacet) {
		this.isFacet = isFacet;
	}
}
