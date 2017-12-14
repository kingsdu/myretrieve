package com.retrieve.spi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.retrieve.api.FullTextIndexParams;
import com.retrieve.api.FullTextResult;
import com.retrieve.api.FullTextSearchParams;
import com.retrieve.config.ConstantParams;
import com.retrieve.util.StringUtils;

public class LuceneService extends FullTextServiceImpl {

	private String serverName;
	public static Map<String,IndexWriter> writerMap = new HashMap<String,IndexWriter>();
	public static Map<String,IndexSearcher> searchMap = new HashMap<String,IndexSearcher>();
	private static Analyzer analyzer = new IKAnalyzer();
	private static String indexPath = StringUtils.getConfigParam(ConstantParams.INDEXPATH, "", ConstantParams.SEARCH_CONFIG);
	
	
	public String getServerName() {
		return serverName;
	}

	@Override
	public int beginService(String serverName, String flag) {
		try {
			if("writer".equals(flag)){
				IndexWriter indexWriter = writerMap.get(serverName);
					if(indexWriter == null){
						Directory dir = FSDirectory.open(new File(indexPath));
						IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
						indexWriter = new IndexWriter(dir, config);
						writerMap.put(serverName, indexWriter);
					}
					if(indexWriter != null){
						return 1;
					}
			}
			if("search".equals(flag)){
				IndexSearcher indexSearcher = searchMap.get(serverName);
				if(indexSearcher == null){
					Directory dir = FSDirectory.open(new File(indexPath));
					IndexReader reader = DirectoryReader.open(dir);
					indexSearcher = new IndexSearcher(reader);
					searchMap.put(serverName, indexSearcher);
				}
				if(indexSearcher != null){
					return 1;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public int beginService(String serverName, String flag,String indexPath) {
		try {
			if("writer".equals(flag)){
				IndexWriter indexWriter = writerMap.get(serverName);
				if(indexWriter == null){
					Directory dir = FSDirectory.open(new File(indexPath));
					IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
					indexWriter = new IndexWriter(dir, config);
					writerMap.put(serverName, indexWriter);
				}
				if(indexWriter != null){
					return 1;
				}
			}
			if("search".equals(flag)){
				IndexSearcher indexSearcher = searchMap.get(serverName);
				if(indexSearcher == null){
					Directory dir = FSDirectory.open(new File(indexPath));
					IndexReader reader = DirectoryReader.open(dir);
					indexSearcher = new IndexSearcher(reader);
					searchMap.put(serverName, indexSearcher);
				}
				if(indexSearcher != null){
					return 1;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int beginService(String serverName) {
		return -1;
	}

	@Override
	public int endService(String serverName) {
		return -1;
	}

	@Override
	public void doIndex(FullTextIndexParams fullTextIndexParams) {
		long preStart = System.currentTimeMillis();
		preIndexMethod();
		long preEnd = System.currentTimeMillis();
		System.out.println("Your preIndex spent on "+(preEnd-preStart)+" ms.");
		
		try {
			List<Map<String,Object>> listData = fullTextIndexParams.getIndexData();
			for(Map<String,Object> map : listData){
				Document doc = new Document();
				Set<String> set = map.keySet();
				Iterator<String> iter = set.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					Object value = map.get(key);
					IndexableField field = null;
					if(value instanceof Integer){
						field = new IntField(key,(Integer)value,Field.Store.YES);
					}else if(value instanceof Long){
						field = new  LongField(key,(Long)value,Field.Store.YES);
					}else if(value instanceof Double){
						field = new  DoubleField(key,(Double)value,Field.Store.YES);
					}else if(value instanceof Float){
						field = new  FloatField(key,(Float)value,Field.Store.YES);
					}else{
						field = new TextField(key,value.toString(),Field.Store.YES);
					}
					doc.add(field);
				}
				writerMap.get(this.serverName).addDocument(doc);
			}
			writerMap.get(this.serverName).commit();
			writerMap.get(this.serverName).close();
			writerMap.put(this.serverName, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long afterStart = System.currentTimeMillis();
		afterIndexMethod();
		long afterEnd = System.currentTimeMillis();
		System.out.println("Your afterIndex spent on "+(afterEnd-afterStart)+" ms again.");
	}

	@Override
	public FullTextResult doQuery(FullTextSearchParams fullTextSearchParams) {
		FullTextResult fullTextResult = new LuceneResult();
		try {
			String queryWord = fullTextSearchParams.getQueryWord();
			if(StringUtils.isEmpty(queryWord)){
				return null;
			}
			
			//指定搜索域
			List<String> assignmentFields = fullTextSearchParams.getAssignmentFields();
			String[] fields = null;
			if(assignmentFields != null && assignmentFields.size() > 0){
				int size = assignmentFields.size();
				fields = new String[size];
				for(int i=0;i<size;i++){
					fields[i] = assignmentFields.get(i);
				}
			}else{
				fields = new String[]{};
			}
			
			//指定权重
			Map<String,Float> boost = fullTextSearchParams.getBoost();
			
			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_46,fields,analyzer,boost);
			Query query = queryParser.parse(queryWord);
			
			//返回结果数
			int returnNums = fullTextSearchParams.getReturnNums();
			
			//过滤
			Map<String,String> filterField = fullTextSearchParams.getFilterField();
			Filter filter = null;
			if(filterField != null && filterField.size() > 0){
				Set<String> set = filterField.keySet();
				Iterator<String> iter = set.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					String value = filterField.get(key);
					filter = new QueryWrapperFilter(new TermQuery(new Term(key,value)));
				}
			}
			
			boolean isHighlight = fullTextSearchParams.getIsHighlight();
			Highlighter highlighter = null;
			if(isHighlight){
				String preTag = fullTextSearchParams.getPreHighlight();
				String postTag = fullTextSearchParams.getPostHighlight();
				Formatter formatter = new SimpleHTMLFormatter(preTag,postTag);
				Scorer fragmentScorer = new QueryScorer(query);
				highlighter = new Highlighter(formatter, fragmentScorer);
				Fragmenter fragmenter = new SimpleFragmenter(fullTextSearchParams.getViewNums());
				highlighter.setTextFragmenter(fragmenter);
			}
			
			//显示域
			String[] viewFields = fullTextSearchParams.getViewFields();
			
			TopDocs topDocs = searchMap.get(this.serverName).search(query,filter, returnNums);
			int hits = topDocs.totalHits;
			fullTextResult.setNumFound(Long.valueOf(hits));
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			Map map = null;
			List list = new ArrayList();
			for(ScoreDoc sd : scoreDoc){
				map = new HashMap();
				int docID = sd.doc;
				Document doc = searchMap.get(this.serverName).doc(docID);
				
				String highlightResult = null;
				if(viewFields != null && viewFields.length > 0){
					for(String vf : viewFields){
						if(highlighter != null){
							highlightResult = highlighter.getBestFragment(analyzer, vf,doc.get(vf));
						}
						if(highlightResult != null){
							map.put(vf, highlightResult);
						}else{
							String value = doc.get(vf);
							int docLength = value.length();
							if(docLength!=0){
								if(docLength > fullTextSearchParams.getViewNums()){
									value = value.substring(0,fullTextSearchParams.getViewNums());
								}
								map.put(vf, value);
							}
							else{
								return null;
							}
						}
					}
					list.add(map);
				}else{
					for(IndexableField field : doc.getFields()){
						if(highlighter != null){
							highlightResult = highlighter.getBestFragment(analyzer, field.name(),field.stringValue());
						}
						if(highlightResult != null){
							map.put(field.name(), highlightResult);
						}else{
							String value = field.stringValue();
							int docLength = value.length();
							if(docLength > fullTextSearchParams.getViewNums()){
								value = value.substring(0,fullTextSearchParams.getViewNums());
							}
							map.put(field.name(), value);
						}
					}
					list.add(map);
				}
			}
			fullTextResult.setResultList(list);
			searchMap.put(this.serverName, null);//关掉之后会重新启动，达到实时
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fullTextResult;
	}

	@Override
	public void preIndexMethod() {
	}

	@Override
	public void afterIndexMethod() {
	}

	@Override
	public void updateIndex(FullTextIndexParams fullTextIndexParams) {
		// TODO Auto-generated method stub
		super.updateIndex(fullTextIndexParams);
	}

	@Override
	public void preUpdateIndexMethod() {
		// TODO Auto-generated method stub
		super.preUpdateIndexMethod();
	}

	@Override
	public void afterUpdateIndexMethod() {
		// TODO Auto-generated method stub
		super.afterUpdateIndexMethod();
	}

	@Override
	public void deleteIndex(FullTextIndexParams fullTextIndexParams) {
		preDeleteIndexMethod();
		try {
			String id = fullTextIndexParams.getId();
			writerMap.get(this.serverName).deleteDocuments(new Term("docfullid",id));
			writerMap.get(this.serverName).commit();
			writerMap.get(this.serverName).close();
			writerMap.put(this.serverName, null);//关掉之后会重新启动，达到实时
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		afterDeleteIndexMethod();
	}

	@Override
	public void preDeleteIndexMethod() {
		// TODO Auto-generated method stub
		super.preDeleteIndexMethod();
	}

	@Override
	public void afterDeleteIndexMethod() {
		// TODO Auto-generated method stub
		super.afterDeleteIndexMethod();
	}

	@Override
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	
	
}
