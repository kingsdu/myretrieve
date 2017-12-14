package com.retrieve.lucene;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class QueryDemo {

	private static Analyzer analyzer = new IKAnalyzer();
	private static String indexPath = "E:/test/hetritrix/indexTest";
	public static void queryIndex(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			IndexWriter iw = new IndexWriter(dir, config);
			
			Document doc = new Document();
			IndexableField idField = new IntField("id",1,Field.Store.YES);
			IndexableField dateField = new StringField("date","2011年9月",Field.Store.YES);
			IndexableField titleField = new StringField("title","中国北京",Field.Store.YES);
			IndexableField contentField = new TextField("content","天安门上太阳升，祖国真伟大！",Field.Store.YES);
			
			doc.add(idField);
			doc.add(dateField);
			doc.add(titleField);
			doc.add(contentField);
			
			Document doc1 = new Document();
			IndexableField idField1 = new IntField("id",2,Field.Store.YES);
			IndexableField dateField1 = new StringField("date","2012年3月",Field.Store.YES);
			IndexableField titleField1 = new StringField("title","中华人民共和国",Field.Store.YES);
			IndexableField contentField1 = new TextField("content","天安门在北京，明天凌晨北平要下大雪。美国没下雪。",Field.Store.YES);
			
			doc1.add(idField1);
			doc1.add(dateField1);
			doc1.add(titleField1);
			doc1.add(contentField1);
			
			Document doc2 = new Document();
			IndexableField idField2 = new IntField("id",3,Field.Store.YES);
			IndexableField dateField2 = new StringField("date","2013年12月",Field.Store.YES);
			IndexableField titleField2 = new StringField("title","美国",Field.Store.YES);
			IndexableField contentField2 = new TextField("content","华盛顿将要发生爆炸。",Field.Store.YES);
			
			doc2.add(idField2);
			doc2.add(dateField2);
			doc2.add(titleField2);
			doc2.add(contentField2);
			
			iw.addDocument(doc);
			iw.addDocument(doc1);
			iw.addDocument(doc2);
			
			iw.commit();
			iw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void termQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			Query query = new TermQuery(new Term("content","天安门"));
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id")+":"+doc.get("title")+":"+doc.get("content"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void booleanQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			Query query1 = new TermQuery(new Term("content","天安门"));
			Query query2 = new TermQuery(new Term("content","北平"));
			
			BooleanQuery query = new BooleanQuery();
			query.add(query1,BooleanClause.Occur.MUST);
			query.add(query2,BooleanClause.Occur.MUST_NOT);
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void wildcardQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			WildcardQuery query = new WildcardQuery(new Term("content","天安门*"));
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void phraseQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			PhraseQuery query = new PhraseQuery();
			// relative position
//			query.add(new Term("content","北京"));
//			query.add(new Term("content","北平"),3);
			
//			query.add(new Term("content","天安门"));
//			query.add(new Term("content","北京"),2);
			
			//slop
//			query.add(new Term("content","北京"));
//			query.add(new Term("content","北平"));
//			query.setSlop(2);
			
			query.add(new Term("content","天安门"));
			query.add(new Term("content","北京"));
			query.setSlop(1);
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void prefixQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			PrefixQuery query = new PrefixQuery(new Term("content","天安门"));
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void multiPhraseQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			MultiPhraseQuery query = new MultiPhraseQuery();
			Term term1 = new Term("content","祖国");
			Term term2 = new Term("content","北京");
			Term[] terms = {term1,term2};
			query.add(terms);
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void fuzzyQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			FuzzyQuery query = new FuzzyQuery(new Term("content","天安门"), 0, 1, 1, true);
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void regexpQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
//			RegexpQuery query = new RegexpQuery(new Term("title","中(.*)京"));
			RegexpQuery query = new RegexpQuery(new Term("content","天(.*)门"));
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void termRangeQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			BytesRef lowerTerm = new BytesRef("2011年9月");
			BytesRef upperTerm = new BytesRef("2013年11月");
			TermRangeQuery query = new TermRangeQuery("date", lowerTerm, upperTerm, false, true);
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("date"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void numericRangeQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			Query query = NumericRangeQuery.newIntRange("id", 1, 3, false, true);
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("date"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void constantScoreQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			Filter filter = new QueryWrapperFilter(new TermQuery(new Term("content","天安门")));
			ConstantScoreQuery query = new ConstantScoreQuery(filter);
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("date"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void disjunctionMaxQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			DisjunctionMaxQuery query = new DisjunctionMaxQuery(0.1f);
			query.add(new TermQuery(new Term("content","天安门")));
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("date"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void matchAllDocsQueryDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			MatchAllDocsQuery query = new MatchAllDocsQuery();
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("date"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void multiFieldQueryParserDemo(){
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher is = new IndexSearcher(reader);
			
			String[] fields = {"id","content"};
			Map<String,Float> boost = new HashMap<String,Float>();
			boost.put("title", 0.01f);
			boost.put("content", 10.0f);
			
			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_46,fields,analyzer,boost);
			Query query = queryParser.parse("美国");
			
			TopDocs topDocs = is.search(query, 10);
			
			int hits = topDocs.totalHits;
			System.out.println("hits:"+hits);
			ScoreDoc[] scoreDoc = topDocs.scoreDocs;
			for(ScoreDoc sd : scoreDoc){
				int docID = sd.doc;
				Document doc = is.doc(docID);
				System.out.println(doc.get("id"));
				System.out.println(doc.get("date"));
				System.out.println(doc.get("title"));
				System.out.println(doc.get("content"));
				System.out.println();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
