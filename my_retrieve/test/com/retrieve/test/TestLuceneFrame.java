package com.retrieve.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.retrieve.api.FullTextIndexParams;
import com.retrieve.api.FullTextResult;
import com.retrieve.api.FullTextSearchParams;
import com.retrieve.api.ServerFactory;
import com.retrieve.spi.LuceneService;
import com.retrieve.spi.SolrService;
import com.retrieve.util.StringUtils;

public class TestLuceneFrame extends TestCase{

	LuceneService luceneService = null;
	
	SolrService solrService = null;

	public void beginService(String flag){
		Map<String,String> params = new HashMap<String,String>();
		params.put("type", "lucene");
		params.put("serverName", "test");
		params.put("flag", flag);
		params.put("className", LuceneService.class.getName());
		params.put("indexPath", "G:/codeBackup/solr/solr_home_news/news/data/index");
		ServerFactory serverFactory = new ServerFactory();
		luceneService = (LuceneService)serverFactory.beginService(params);
		luceneService.setServerName("test");
	}
	
	
	public void beginService_solr(){
		Map<String,String> params = new HashMap<String,String>();
		params.put("type", "solr");
		params.put("serverName", "test");
		params.put("url", "http://localhost:8085/my_fullretrieve");
		params.put("className", SolrService.class.getName());
		ServerFactory serverFactory = new ServerFactory();
		solrService =  (SolrService)serverFactory.beginService(params);
		solrService.setServerName("test");
	}

	public void test1(){
		//启动服务
		beginService("writer");

		FullTextIndexParams fullTextIndexParams = new FullTextIndexParams();
		List<Map<String,Object>> indexData = new ArrayList<Map<String,Object>>();
		Map<String,Object> map1 = new HashMap<String,Object>();
		Map<String,Object> map2 = new HashMap<String,Object>();
		Map<String,Object> map3 = new HashMap<String,Object>();
		
		Date d = new Date();
		
		map1.put("id", 1);
		map1.put("name", "新浪");
		map1.put("time", d);
		map1.put("content", "广州的天气依然很闷热。");

		map2.put("id", 2);
		map2.put("name", "百度");
		map2.put("time", d);
		map2.put("content", "北京应该很冷了。天气还是不错的。");
		
		map3.put("id", 2);
		map3.put("name", "腾讯");
		map2.put("time", d);
		map3.put("content", "上海的天气不冷不热。");
		
		indexData.add(map1);
		indexData.add(map2);
		fullTextIndexParams.setIndexData(indexData);
		luceneService.doIndex(fullTextIndexParams);
	}


	public void test2(){
		//启动服务
		beginService("search");

		FullTextSearchParams fullTextSearchParams = new FullTextSearchParams();

		fullTextSearchParams.setQueryWord("腾讯");

		fullTextSearchParams.setReturnNums(10);

		List<String> assignmentFields = new ArrayList<String>();
		assignmentFields.add("name");
		assignmentFields.add("content");
		fullTextSearchParams.setAssignmentFields(assignmentFields);

		String[] viewFields = new String[]{"name","content","id","datetime"};
		fullTextSearchParams.setViewFields(viewFields);

		fullTextSearchParams.setViewNums(5);

		fullTextSearchParams.setIsHighlight(false);
//
//		Map<String,String> filterField = new HashMap<String,String>();
//		filterField.put("name", "百度");
		//				fullTextSearchParams.setFilterField(filterField);

//		Map<String,Float> boost = new HashMap<String,Float>();
//		boost.put("name", 1.0f);
//		boost.put("testik", 9.0f);
//		fullTextSearchParams.setBoost(boost);

		FullTextResult result = luceneService.doQuery(fullTextSearchParams);
		System.out.println("NumFound is "+result.getNumFound());
		List list = result.getResultList();
		System.out.println("list size:"+list.size());
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}

	}


	public void test3(){
		//启动服务
//		beginService("search");
		beginService_solr();

		FullTextSearchParams fullTextSearchParams = new FullTextSearchParams();

		fullTextSearchParams.setQueryWord("中国");

		fullTextSearchParams.setReturnNums(10);

		List<String> assignmentFields = new ArrayList<String>();
		assignmentFields.add("newsTitle");
		assignmentFields.add("content");
		fullTextSearchParams.setAssignmentFields(assignmentFields);

		String[] viewFields = new String[]{"newsTitle","content"};
		fullTextSearchParams.setViewFields(viewFields);

//		fullTextSearchParams.setViewNums(10);

		fullTextSearchParams.setIsHighlight(false);

//		Map<String,String> filterField = new HashMap<String,String>();
//		filterField.put("content", "中国");
		//				fullTextSearchParams.setFilterField(filterField);

		Map<String,Float> boost = new HashMap<String,Float>();
		boost.put("newsTitle", 1.0f);
		boost.put("content", 9.0f);
		fullTextSearchParams.setBoost(boost);

//		FullTextResult result = luceneService.doQuery(fullTextSearchParams);
		
		FullTextResult result = solrService.doQuery(fullTextSearchParams);
		System.out.println(result.getNumFound());
		List list = result.getResultList();
		System.out.println("list size:"+list.size());
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}

	}


	public void test4(){
		//启动服务
		beginService("writer");
		String sourcePath = "E:/test/hetritrix/rawTxt";
		FullTextIndexParams fullTextIndexParams = new FullTextIndexParams();
		List<Map<String,Object>> indexData = new ArrayList<Map<String,Object>>();
		
		StringUtils su = new StringUtils(sourcePath);
		List<String> pathList = su.allPathResult;
		Map<String,Object> map = null;
		for(String path : pathList){
			map = new HashMap<String,Object>();
			String fileName = StringUtils.getFileNameFromPath(path);
			String content = StringUtils.getContent(path);
			map.put("fileName", fileName);
			map.put("content", content);
			indexData.add(map);
		}
		fullTextIndexParams.setIndexData(indexData);
		luceneService.doIndex(fullTextIndexParams);
	}
	
	
    public void getUserDir(){
    	System.out.println(System.getProperty("user.dir"));
    }
}
