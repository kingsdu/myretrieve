package com.retrieve.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.retrieve.util.DateUtils;

public class SolrDemo {

	public static void solrIndex(){
		try {
			String url = "http://localhost:8085/solr";
			HttpSolrServer server = new HttpSolrServer(url);
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("docfullid", "news_1");
//			doc.addField("newsId", "1");
			doc.addField("newsTitle", "中国天然气供需形势中国");
//			doc.addField("sourceNet", 25);
//			doc.addField("sourceUrl", "www.abc.com");	
//			doc.addField("filePath", "e://a/b/c");			
//			doc.addField("newsTime", DateUtils.getYear("2000"));
			doc.addField("content", "在搜索词前面天然气供需形势中国加入搜索域的优先级 > solrconfig.xml中配置的qf值 > schema.xml文件中配置的默认搜索域");
//			doc.addField("testik", "在程序中用setFields函数可以指定显示域，并且该指定方法的优先级 > solrconfing.xml中配置的fl值，schema.xml文件中配置的默认搜索域");
			
			
			SolrInputDocument doc1 = new SolrInputDocument();
			doc1.addField("docfullid", "news_2");
//			doc1.addField("newsId", "2");
			doc1.addField("newsTitle", "美国智能加油站");
//			doc1.addField("sourceNet", 25);
//			doc1.addField("sourceUrl", "www.asbc.com");	
//			doc1.addField("filePath", "e://a/ssb/c");			
//			doc1.addField("newsTime", DateUtils.getYear("2000"));
			doc1.addField("content", "在搜索词前面加入搜智能加油站索域中国石油测井的优先级 > solrconfig.xml中配置的qf值 > schema.xml文件中配中国石油测井置的默认搜索域");
			
			server.add(doc);
			server.add(doc1);
			server.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 1、在程序中设定的搜索域优先级 > schema.xml文件中配置的默认搜索域
	 * 2、在搜索词前面加入搜索域的优先级 > solrconfig.xml中配置的qf值 > schema.xml文件中配置的默认搜索域
	 * 3、在程序中用setFields函数可以指定显示域，并且该指定方法的优先级 > solrconfing.xml中配置的fl值
	 */
	public static void solrSearcher(){
		try {
			String url = "http://localhost:8085/solr";
			HttpSolrServer server = new HttpSolrServer(url);
			SolrQuery params = new SolrQuery("newsTitle:供需"); 
			
			//默认搜索域
//			params.setParam("df", "name");
			
			//显示域
			String[] fields = {"newsTitle","content"};
			params.setFields(fields);
			
			//高亮
//			params.addHighlightField("newsTitle");
//			params.setHighlight(true);
//			params.setHighlightSimplePre("<em class=\"highlight\" >");
//			params.setHighlightSimplePost("</em>");
			//显示的字数
			params.setHighlightFragsize(10);
			
			//排序
//			params.addSort("age", ORDER.desc);
			
			//过滤,对搜索结果进行过滤,达到精确搜索	
//			String[] fqs = {"name:中国"};
//			params.addFilterQuery(fqs);
			
			//分页
//			params.setStart(0);
//			params.setRows(10);
			
			//facet
//			String[] ftf = {"name","content"};
//			params.addFacetField(ftf);
			
//			params.addNumericRangeFacet("age", 1, 26, 26);
//			params.addDateRangeFacet("date", DateUtils.getYear("1999"), DateUtils.getYear("2015"), "+15YEAR");
			
			QueryResponse response = server.query(params);
			
			/* List<FacetField> listField = response.getFacetFields();
			 for(FacetField facetField : listField){
				 System.out.println(facetField.getName());
				 List<Count> counts = facetField.getValues();
				 for(Count c : counts){
					 System.out.println(c.getName()+":"+c.getCount());
				 }
			 }*/
			
//			 List<RangeFacet> listFacet = response.getFacetRanges();
//			 for(RangeFacet rf : listFacet){
//				 List<RangeFacet.Count> listCounts = rf.getCounts();
//				 for(RangeFacet.Count count : listCounts){
//					 System.out.println("facet:"+count.getValue()+":"+count.getCount());
//				 }
//			 }
			
			SolrDocumentList list = response.getResults();
			System.out.println("total hits:"+list.getNumFound());
			for(SolrDocument doc : list){
				System.out.println("content:"+doc.get("content"));
				System.out.println("newsTitle:"+doc.get("newsTitle"));
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void solrDelIndex(){
		try {
			String url = "http://localhost:8080/solr";
			HttpSolrServer server = new HttpSolrServer(url);
//			server.deleteById("1");
			List<String> ids = new ArrayList<String>();
			ids.add("1");
			ids.add("2");
			server.deleteById(ids);
			server.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String args[]){
//		solrIndex();
		solrSearcher();
	}
}
