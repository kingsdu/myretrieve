package com.retrieve.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullTextIndexParams {

	//需要索引的数据
	private List<Map<String,Object>> indexData = new ArrayList<Map<String,Object>>();

	//lucene 的索引路径
	private String indexPath = "";

	//删除索引时，传入id
	private List<String> ids = new ArrayList<String>();

	private String id;

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Map<String, Object>> getIndexData() {
		return indexData;
	}

	public void setIndexData(List<Map<String, Object>> indexData) {
		this.indexData = indexData;
	}

	public String getIndexPath() {
		return indexPath;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

}
