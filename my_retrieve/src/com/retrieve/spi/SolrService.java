package com.retrieve.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.retrieve.api.FullTextIndexParams;
import com.retrieve.api.FullTextResult;
import com.retrieve.api.FullTextSearchParams;
import com.retrieve.config.ConstantParams;
import com.retrieve.util.StringUtils;

public class SolrService extends FullTextServiceImpl {



//	private String genResultDic(){
//		//访问数据库获取根目录	
//		String initPath = getIndexPath();
//		//读取新闻，抽取关键词
//		String rawTxtInputPath = initPath+File.separator+ConstantParams.MIRROR+File.separator+"rawTxt";//初始文本
//		String splitTxtPath = initPath+File.separator+ConstantParams.MIRROR+File.separator+"splitWords";//分词文本
//		String ruleTxtPath = Thread.currentThread().getContextClassLoader().getResource("rules.txt").getPath();//规则文本
//		String wordsRuleSetsPath = initPath+File.separator+ConstantParams.MIRROR+File.separator+"wordsRuleSets";//通过规则生成文本
//		String filterwordsPath = initPath+File.separator+ConstantParams.MIRROR+File.separator+"filterwords";//去除单字词和其他无效词的文本
//		String delrepwordsPath = initPath+File.separator+ConstantParams.MIRROR+File.separator+"delrepwords";//去重文本
//		String integrationPath = initPath+File.separator+ConstantParams.MIRROR+File.separator+"integration";//完整性词文本
//		String resultWordsPath = initPath+File.separator+ConstantParams.MIRROR+File.separator+"resultWords"+File.separator+"result.dic";//完整性和稳定性双重匹配词典
//		String stablePath = initPath+File.separator+ConstantParams.MIRROR+File.separator+"stableWords";//稳定性词典
//		String filterDic = initPath+File.separator+ConstantParams.MIRROR+File.separator+"resultWords"+File.separator+"resultFilter.dic";//生成IK词典
//		String delDic = initPath+File.separator+ConstantParams.MIRROR+File.separator+"resultWords"+File.separator+"resultDel.dic";//IK词典去重后的最终结果
//		//1 分词,词性标注
//		StringUtils su = new StringUtils(rawTxtInputPath);
//		for(String path:su.allPathResult){
//			String news = StringUtils.getContent(path);
//			String splitRes = SplitWordsUtils.ITCTILS(news);
//			String fileName = StringUtils.getFileNameFromPath(path);
//			StringUtils.string2File(splitRes, splitTxtPath+"/"+fileName+".txt");
//		}	
//		//2 加载rules 抽取文本中的候选集
//	    su = new StringUtils(splitTxtPath);
//		List<String> splitTxtPaths = su.allPathResult;
//		for(String path:su.allPathResult){
//			String fileName = StringUtils.getFileNameFromPath(path);
//			ExtractUtils.singleTextWordsSet(ruleTxtPath, path, wordsRuleSetsPath+"/"+fileName+".txt");
//		}	
//		//3 过滤特殊词（单子动词，特殊符号... ...）
//	    su = new StringUtils(wordsRuleSetsPath);
//		for(String fileName : su.allPathResult){
//			String f = StringUtils.getFileNameFromPath(fileName);
//			ExtractUtils.filterWords(fileName, filterwordsPath+"/"+f+".txt");
//		}
//		//4 重复的候选词
//	    su = new StringUtils(filterwordsPath);
//		List<String> filterwordsPaths = su.allPathResult;
//		for(String fileName : su.allPathResult){
//			String f = StringUtils.getFileNameFromPath(fileName);
//			ExtractUtils.delRepWords(fileName, delrepwordsPath+"/"+f+".txt");
//		}
//		//5 生成完整性关键词
//	    su = new StringUtils(delrepwordsPath);
//		List<String> delrepwordsPaths = su.allPathResult;
//		for(int i=0;i<delrepwordsPaths.size();i++){
//			String fileName = StringUtils.getFileNameFromPath(delrepwordsPaths.get(i));
//			//6 生成完整性和稳定性过滤的词
//			ExtractUtils.getObject(filterwordsPaths.get(i), resultWordsPath, ExtractUtils.filterInteWords(delrepwordsPaths.get(i),splitTxtPaths.get(i),integrationPath+"/"+fileName+".txt"),stablePath+"/"+fileName+".txt");
//		}
//		//7 生成IK词典
//		genInteDic(resultWordsPath,filterDic);
//		//8 去重
//		ExtractUtils.delRepWords(filterDic,delDic);	
//		//最终返回处理完成之后的词典路径
//		return delDic;
//	}
	
	
	/**
	 * @Description: 增量IK词典
	 * @return:
	 * @date: 2017-10-16  
	 */
//	private static void IncrementalDic(String rawDicPath,String newDicPath){
//		BloomFilter_utils boom = new BloomFilter_utils(2000,24);
//		BufferedReader br = null;
//		StringBuilder result = new StringBuilder();
//		String word = null;	
//		try {
//			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(rawDicPath)),"utf-8"));
//			while((word = br.readLine())!=null){
//				//建立BloomFliter
//				if(!boom.contains(word)){
//					boom.add(word);	
//				}		
//			}
//			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(newDicPath)),"gbk"));
//			while((word = br.readLine())!=null){
//				//建立BloomFliter
//				if(!boom.contains(word)){
//					boom.add(word);	
//					result.append(word).append(ConstantParams.CHENG_LINE);
//				}
//			}
//			updateDic(result.toString());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			try {
//				if(br!=null){
//					br.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	
	
	/**
	 * @Description: 更新IK的词典
	 * @return:
	 * @date: 2017-10-15  
	 */
//	private static void updateDic(String str){
//		String path = Thread.currentThread().getContextClassLoader().getResource("chinesewords.dic").getPath();
//		try {
//			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path),true),"utf-8"));
//			bw.write(str);
//			bw.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * @Description: 词典处理：去除词性标注，抽取关键词之和
	 * @return:
	 * @date: 2017-10-15  
	 */
//	private static void genInteDic(String sourcePath,String outputPath){
//		BufferedReader br = null;
//		String result = "";
//		String line = "";
//		try {
//			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourcePath)),"gbk"));
//			while((line = br.readLine())!=null){
//				String []bStr = line.split(ConstantParams.SINGLE_BLANK);
//				String s = "";
//				for(String w : bStr){
//					if(w.contains("/")){
//						s+= w.substring(0, w.indexOf("/"));
//					}
//				}
//				result+=s+ConstantParams.CHENG_LINE;
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
//		StringUtils.string2File(result, outputPath);
//	}


//	public static void main(String args[]){
//		SolrService s = new SolrService();
//		s.preIndexMethod();
//	}

//	/**
//	 * @Description: 获取存储新闻的地址
//	 * @return:
//	 * @date: 2017-10-14  
//	 */
//	private String getIndexPath(){
//		Connection conn = DataBaseUtils.getConnection();
//		String sql = "select * from s_webinfo";
//		String path = null;
//		try {
//			PreparedStatement pst = conn.prepareStatement(sql);
//			ResultSet ret = pst.executeQuery();//执行语句，得到结果集  
//			path = null;
//			while (ret.next()) { 
//				path = ret.getString("resultUrl");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return path;
//	}

	@Override
	public void afterIndexMethod() {
	}

	@Override 
	public void updateIndex(FullTextIndexParams fullTextIndexParams) {
		long preStart = System.currentTimeMillis();
		preUpdateIndexMethod();
		long preEnd = System.currentTimeMillis();
		System.out.println("Your preUpdateIndex spent on "+(preEnd-preStart)+" ms.");
		try {
			deleteIndex(fullTextIndexParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long afterStart = System.currentTimeMillis();
		afterUpdateIndexMethod();
		long afterEnd = System.currentTimeMillis();
		System.out.println("Your afterUpdateIndex spent on "+(afterEnd-afterStart)+" ms again.");
	}

	@Override
	public void preUpdateIndexMethod() {
	}

	@Override
	public void afterUpdateIndexMethod() {
	}

	@Override
	public void deleteIndex(FullTextIndexParams fullTextIndexParams) {
		long preStart = System.currentTimeMillis();
		preDeleteIndexMethod();
		long preEnd = System.currentTimeMillis();
		System.out.println("Your preDeleteIndex spent on "+(preEnd-preStart)+" ms.");
		try {
			if(StringUtils.isNotEmpty(fullTextIndexParams.getId())){
				//首先去删除一个id的情况
				this.solrServerMap.get(this.serverName).deleteById(fullTextIndexParams.getId());
			}else{
				this.solrServerMap.get(this.serverName).deleteById(fullTextIndexParams.getIds());
			}
			this.solrServerMap.get(this.serverName).commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long afterStart = System.currentTimeMillis();
		afterDeleteIndexMethod();
		long afterEnd = System.currentTimeMillis();
		System.out.println("Your afterDeleteIndex spent on "+(afterEnd-afterStart)+" ms again.");
	}

	@Override
	public void preDeleteIndexMethod() {
	}

	@Override
	public void afterDeleteIndexMethod() {
	}

	public Map<String,SolrServer> solrServerMap = new HashMap<String,SolrServer>();

	private String serverName;


	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	public int beginService(String serverName) {
		SolrServer solrServer = solrServerMap.get(serverName);
		if(solrServer == null){
			solrServer = beginServer();
			solrServerMap.put(serverName,solrServer);
			return 1;
		}
		return -1;
	}

	@Override
	public int beginService(String serverName, String url) {
		if(StringUtils.isEmpty(url)){
			return -1;
		}
		SolrServer solrServer = solrServerMap.get(serverName);
		if(solrServer == null){
			solrServer = beginServer(url);//启动solr服务
			solrServerMap.put(serverName,solrServer);//存储启动的服务
			return 1;
		}
		return -1;
	}

	@Override
	public int endService(String serverName) {
		// TODO Auto-generated method stub
		return super.endService(serverName);
	}

	@Override
	public void doIndex(FullTextIndexParams fullTextIndexParams) {
		try {
			List<Map<String,Object>> indexData = fullTextIndexParams.getIndexData();
			if(indexData != null && indexData.size() > 0){
				List<SolrInputDocument> documentList = new ArrayList<SolrInputDocument>();
				SolrInputDocument doc = null;
				//将Map中的内容取出，加入到doc，docList,然后建立索引
				for(Map<String,Object> map : indexData){
					doc = new SolrInputDocument();
					Set<String> set = map.keySet();
					Iterator<String> iter = set.iterator();
					while(iter.hasNext()){
						String key = iter.next();
						Object value = map.get(key);
						doc.addField(key, value);
					}
					documentList.add(doc);
				}
				System.out.println("======================"+this.solrServerMap.get(this.serverName));
				this.solrServerMap.get(this.serverName).add(documentList);
				this.solrServerMap.get(this.serverName).commit();
			}else{
				return;
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long afterStart = System.currentTimeMillis();
		afterIndexMethod();
		long afterEnd = System.currentTimeMillis();
		System.out.println("Your afterIndex spent on "+(afterEnd-afterStart)+" ms again.");
	}

	@Override
	public FullTextResult doQuery(FullTextSearchParams fullTextSearchParams) {
		FullTextResult result = new SolrResult();
		try {
			String queryWord = fullTextSearchParams.getQueryWord();
			if(StringUtils.isEmpty(queryWord)){
				return null;
			}
			List<String> assignmentFields = fullTextSearchParams.getAssignmentFields();
			List<Map<String,String>> assignFields = fullTextSearchParams.getAssignFields();
			String queryString = "";
			if(assignmentFields != null && assignmentFields.size()>0){
				for(String assignmentField : assignmentFields){
					queryString += assignmentField+":"+queryWord+" OR ";
				}
				int pos = queryString.lastIndexOf(" OR ");
				queryString = queryString.substring(0, pos);
			}else if(assignFields != null && assignFields.size()>0){
				//title:中国
				String lastValue = "";
				for(Map<String,String> assignField : assignFields){
					Set<String> set = assignField.keySet();
					Iterator<String> iter = set.iterator();
					while(iter.hasNext()){
						String key = iter.next();
						String value = assignField.get(key);
						queryString += key+":"+queryWord + ConstantParams.SINGLE_BLANK + value + ConstantParams.SINGLE_BLANK;
						lastValue = value;
					}
				}
				int pos = queryString.lastIndexOf(" "+lastValue+" ");
				queryString = queryString.substring(0, pos);
			}else{
				queryString = queryWord;
			}

			System.out.println("queryString:"+queryString);
			SolrQuery params = new SolrQuery(queryString);

			//设置显示域
			String[] viewFields = fullTextSearchParams.getViewFields();
			params.setFields(viewFields);

			//高亮参数
			boolean isHighlight = fullTextSearchParams.getIsHighlight();
			String[] highlightFields = fullTextSearchParams.getHighlightFields();
			if(isHighlight && highlightFields != null && highlightFields.length > 0){
				params.setHighlight(true);
				params.setHighlightSimplePre(fullTextSearchParams.getPreHighlight());
				params.setHighlightSimplePost(fullTextSearchParams.getPostHighlight());
				params.setHighlightFragsize(fullTextSearchParams.getViewNums());
			}
			//			Fragmenter fragmenter = new SimpleFragmenter(fullTextSearchParams.getViewNums());

			//排序域 String:需要排序的域名，Boolean：true 升序 false 降序
			Map<String,Boolean> sortField = fullTextSearchParams.getSortField();
			if(sortField != null){
				Set<String> set = sortField.keySet();
				Iterator<String> iter = set.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					Boolean value = sortField.get(key);
					if(value){
						params.addSort(key,ORDER.asc);
					}else{
						params.addSort(key,ORDER.desc);
					}

				}
			}
			//过滤域
			Map<String,String> filterField = fullTextSearchParams.getFilterField();
			if(filterField != null && filterField.size() > 0){
				StringBuilder str = new StringBuilder();
				Set<String> set = filterField.keySet();
				Iterator<String> iter = set.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					String value = filterField.get(key);
					str.append(key+":"+value);
					str.append("-vertical-");
				}
				String[] fieldFields = str.toString().split("-vertical-");
				params.addFilterQuery(fieldFields);
			}

			//开始行
			params.setStart(fullTextSearchParams.getStartNums());
			//返回的总结果数
			params.setRows(fullTextSearchParams.getReturnNums());
			//统计域
			boolean isFacet = fullTextSearchParams.getIsFacet();
			String[] facetFields = fullTextSearchParams.getFacetFields();
			if(isFacet && facetFields != null && facetFields.length>0){
				params.addFacetField(facetFields);
			}

			QueryResponse response = this.solrServerMap.get(this.serverName).query(params);
			SolrDocumentList list = response.getResults();
			result.setNumFound(list.getNumFound());

			SolrDocument document = new SolrDocument();
			SolrDocumentList hlList = new SolrDocumentList();

			//高亮结果
			if(isHighlight && highlightFields != null && highlightFields.length > 0){
				Map<String,Map<String,List<String>>> map = response.getHighlighting();
				for(int i=0;i<list.size();i++){
					for(int j=0;j<highlightFields.length;j++){
						document = list.get(i);
						if(map != null && map.get(document.getFieldValue("docfullid")) != null && map.get(document.getFieldValue("docfullid")).get(highlightFields[j]) != null){
							if("datetime".equals(map.get(document.getFieldValue("docfullid")).get(highlightFields[j]))){
								document.setField(highlightFields[j], map.get(document.getFieldValue("docfullid")).get(highlightFields[j]).get(0));	
							}else{
								document.setField(highlightFields[j], map.get(document.getFieldValue("docfullid")).get(highlightFields[j]).get(0));	
							}
						}else{
							//无高亮信息，控制字数
							String temp = (String)document.getFieldValue(highlightFields[j]);
							if(temp.length()>fullTextSearchParams.getViewNums()){
								String string = temp.substring(0,fullTextSearchParams.getViewNums());
								document.setField(highlightFields[j], string);
							}
						}
					}
					hlList.add(document);
				}
				result.setResultList(hlList);
			}else{	
				result.setResultList(list);
			}

			//统计结果
			if(isFacet && facetFields != null && facetFields.length>0){
				List<FacetField> listField = response.getFacetFields();
				result.setFacetList(listField);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}



	public SolrServer beginServer(){
		SolrServer solrServer = null;
		try {
			String url = StringUtils.getConfigParam(ConstantParams.SOLR_URL, "", ConstantParams.SEARCH_CONFIG);
			solrServer = new HttpSolrServer(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return solrServer;
	}

	public SolrServer beginServer(String url){
		SolrServer solrServer = null;
		try {
			solrServer = new HttpSolrServer(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return solrServer;
	}
}
