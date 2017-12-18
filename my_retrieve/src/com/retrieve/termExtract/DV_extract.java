package com.retrieve.termExtract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.retrieve.util.ExtractUtils;
import com.retrieve.util.FileUtils;
import com.retrieve.util.SplitWordsUtils;
import com.retrieve.util.StringUtils;
import com.retrieve.util.WebExtractor;


/**
 * @Description: 通过termhood方法抽取术语
 * @author: DU 
 * @date: 2017-12-12  
 */
public class DV_extract {

	private static List<String> allPath_one;
	private static Map<String,Map<String,Double>> allwordsTF;
	private static Map<String,Double> allWordTermHood;//记录所有的词的termhood的值


	public static void main(String[] args) {
//		extractWeb("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\www.sinopecnews.com.cn\\news\\content");
//				splitWord("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\rawTxt","C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\splitWord");
//				extractbyRule("G:\\TermExtract\\dic\\split\\rule.txt","C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\splitWord","C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\wordSet");
//				fliterWord("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\wordSet","C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\filterwords");
//				delWord("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\filterwords","C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\delrepwords");
		//		System.out.println(calWordTF("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\filterwords","中国/ns 石化/n "));
		//		System.out.println(calWordDF("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\filterwords","中国/ns 石化/n "));
		//		System.out.println(calFileTF("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\filterwords","C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\filterwords\\content_1260973.txt","中国/ns 石化/n "));
		//		System.out.println(calFileAveTF("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\filterwords","天津/ns 石化/n "));
		//		calTermhood("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\filterwords");
				writePageTermhood(sort_hood(calTermhood("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\filterwords")),"C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\wordsTermhood");
		//		sortAllTermHood("C:\\resource\\行业网站\\crawlData\\oilGas\\中石化新闻网\\1\\mirror\\wordsTermhood\\alltermhood1.txt");
	}




	/**
	 * @Description: 网页正文抽取
	 * @param:
	 * @return:
	 * @date: 2017-12-12  
	 */
	public static void extractWeb(String inputPath){	
		boolean flag = false;
		StringUtils su = new StringUtils(inputPath);
		List<String> allPath = su.allPathResult;
		//		for(String p : allPath){
		for(int i=0;i<500;i++){
			String p = allPath.get(i);
			//判断是否为Index.html
			if(!flag){
				String index = p.substring(p.lastIndexOf("\\")+1,p.lastIndexOf("."));
				if(index.equals("index")){
					flag = true;
					continue;
				}
			}
			StringBuilder html = FileUtils.readHtml(p);	
			int position = p.indexOf("mirror")+6;
			String sourceUrl = p.substring(position);
			String rawTxtPath = p.substring(0,position);
			File f = new File(rawTxtPath + File.separator + "rawTxt");
			if(!f.isDirectory()){
				f.mkdirs();
			}
			String fileName = StringUtils.getFileNameFromPath(p)+".txt";
			//生成rawTxt文件夹
			String Extract_Term_path = rawTxtPath + File.separator + "rawTxt" + File.separator + fileName;
			Extract_Term_path = Extract_Term_path.replace("\\", "/");//转换，否则无法存入数据"/"
			//生成sourceUrl	
			sourceUrl = "http:/"+sourceUrl.replace("\\", "/");
			Document doc = Jsoup.parse(html.toString(), sourceUrl);
			WebExtractor exct = new WebExtractor(doc);
			Element contentElement = null;
			try {
				contentElement = exct.getContentElement();
				String content = contentElement.text();
				String title = exct.getTitle(contentElement);
				StringUtils.string2File(title+"\r\t"+content, Extract_Term_path);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
	}


	/**
	 * @Description: 分词，词性标注
	 * @param:
	 * @return:
	 * @date: 2017-12-12  
	 */
	public static void splitWord(String inputpath,String targetPath){
		StringUtils su = new StringUtils(inputpath);
		for(String path:su.allPathResult){
			String news = StringUtils.getContent(path);
			String splitRes = SplitWordsUtils.ITCTILS(news);
			String fileName = StringUtils.getFileNameFromPath(path);
			StringUtils.string2File(splitRes, targetPath+"/"+fileName+".txt");
		}
	}


	/**
	 * @Description: 通过词性规则抽取候选术语
	 * @param:
	 * @return:
	 * @date: 2017-12-12  
	 */
	public static void extractbyRule(String rulePath,String filePath,String targetPath){
		List<String> paths = getAllPath(filePath);
		for (int i = 0; i < paths.size(); i++) {
			String fileName = StringUtils.getFileNameFromPath(paths.get(i));
			ExtractUtils.singleTextWordsSet(rulePath,paths.get(i),targetPath+"/"+fileName+".txt");
		}
	}



	/**
	 * @Description: 过滤特殊词（单子动词，特殊符号... ...）
	 * @param:
	 * @return:
	 * @date: 2017-12-12  
	 */
	public static void fliterWord(String inputpath,String targetPath){
		StringUtils su = new StringUtils(inputpath);
		for(String fileName : su.allPathResult){
			String f = StringUtils.getFileNameFromPath(fileName);
			ExtractUtils.filterWords(fileName, targetPath+"/"+f+".txt");
		}
	}


	public static void delWord(String inputpath,String targetPath){
		StringUtils su = new StringUtils(inputpath);
		for(String fileName : su.allPathResult){
			String f = StringUtils.getFileNameFromPath(fileName);
			ExtractUtils.delRepWords(fileName, targetPath+"/"+f+".txt");
		}
		deleteZeroSize(new File(targetPath));
	}


	/**
	 * @Description: 计算一个词word 在所有文章中的总TF
	 * 所有文档总词数、某词在所有文档中出现的频率 
	 * @param: inputpath:所有已过滤、去重词的文档路径   word 参与计算的词
	 * @return:word的TF
	 * @date: 2017-12-12  
	 */
	public static double calWordTF(String inputpath,String word){
		Map<String,Integer> wordTF = new HashMap<>();
		double wordCount = 0;//记录文档总词数
		try {
			for(String path : allPath_one){
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"));
				String calWord = "";
				while((calWord = br.readLine())!=null){
					wordCount++;
					if(word.equals(calWord)){
						if(wordTF.get(word) == null){
							wordTF.put(word, 1);
						}
						else{
							wordTF.put(word, wordTF.get(word)+1);
						}
					}
				}
			}		
			//计算tf
			return wordTF.get(word)/wordCount;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}



	/**
	 * @Description: 计算一个词word 在所有文章中的总DF
	 * @param:
	 * @return:word的DF
	 * @date: 2017-12-13  
	 */
	public static double calWordDF(String inputpath,String word){
		Map<String, Set<String>> passageWords = new HashMap<String, Set<String>>();  
		try {
			//读取所有文件信息
			for(String path : allPath_one){
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"));
				Set<String> words = new HashSet<String>();
				String calWord = "";
				while((calWord = br.readLine())!=null){
					words.add(calWord);					
				}
				passageWords.put(path, words);
			}
			//计算某词df
			HashMap<String, Integer> wordPassageNum = new HashMap<String, Integer>();
			for(String filePath : allPath_one){
				Set<String> wordSet = new HashSet<String>();
				wordSet = passageWords.get(filePath); 
				if(wordSet.contains(word)){
					if(wordPassageNum.get(word) == null){
						wordPassageNum.put(word,1);
					}
					else{             
						wordPassageNum.put(word, wordPassageNum.get(word) + 1);           
					}
				}
			}
			return wordPassageNum.get(word)/Double.valueOf(getAllPath(inputpath).size());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}




	/**
	 * @Description: 计算某篇文章中某词的TF
	 * @param:  inputPath所有文章的父路径        pagePath 计算的文章路径        word计算的词
	 * @return: word的TF
	 * @date: 2017-12-13  
	 */
	public static double calFileTF(String inputPath,String pagePath,String word){
		for(String path : allwordsTF.keySet()){
			if(path.equals(pagePath)){
				Map<String,Double> pageTF = allwordsTF.get(pagePath);//某篇文章Tf
				if(pageTF.containsKey(word)){//某词的Tf
					return pageTF.get(word);
				}
			}
		}		
		return 0;
	}


	/**
	 * @Description: 计算平均TF
	 * @param:inputPath 所有文章的父路径     word 计算的词
	 * @return:word的TF
	 * @date: 2017-12-13  
	 */
	public static double calFileAveTF(String inputPath,String word){
		double tf = 0;
		double count = 0;
		for (String path : allwordsTF.keySet()) {
			Map<String,Double> pageTF = allwordsTF.get(path);
			if(pageTF.containsKey(word)){
				tf+=pageTF.get(word);//所有出现过的tf累加
				count++;
			}
		}
		return tf/count;//tf平均值
	}



	/**
	 * @Description: 计算所有文章中的词的TF
	 * 所有文档总词数、某词在所有文档中出现的频率 
	 * @param: inputpath:文档的路径    
	 * @return:Map<文档路径,Map<词,频率>>
	 * @date: 2017-12-13  
	 * 
	 */
	public static Map<String,Map<String,Double>> calAllFileTF(String inputpath){	
		allwordsTF = new HashMap<>();
		try {
			//遍历一篇文档的路径
			for(String path : allPath_one){
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),FileUtils.getFileEncode(path)));	
				List<String> words = new ArrayList<>();//一篇文档中所有词
				Map<String,Integer> wordCount = new HashMap<>();//一篇文档所有<词，词数>
				Map<String,Double> wordTF = new HashMap<>();//<词,tf>
				String calWord = "";
				while((calWord = br.readLine())!=null){
					words.add(calWord);//一篇文档所有词
				}
				//计算单个词的词频
				for(String word : words){
					if(wordCount.get(word) == null){
						wordCount.put(word, 1);
					}else{
						wordCount.put(word, wordCount.get(word)+1);
					}
				}
				//计算单个词的tf
				for(String word : words){
					wordTF.put(word, wordCount.get(word)/Double.valueOf(words.size()));
				}			
				allwordsTF.put(path, wordTF);//所有文档中所有词的tf<path,pageTF>
			}				
			return allwordsTF;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allwordsTF;
	}




	/**
	 * @Description: 计算TF 并将其写出
	 * @param:
	 * @return:
	 * @date: 2017-12-13  
	 */
	public static void calAllFileTFWrite(String inputpath,String outputPath){	
		try {
			//遍历一篇文档的路径
			for(String path : getAllPath(inputpath)){
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"));	
				String name = StringUtils.getFileNameFromPath(path);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath+"/"+name+".txt"), "utf-8"));
				List<String> words = new ArrayList<>();//一篇文档中所有词
				Map<String,Integer> wordCount = new HashMap<>();//一篇文档所有<词，词数>
				Map<String,Double> wordTF = new HashMap<>();//<词,tf>
				String calWord = "";
				while((calWord = br.readLine())!=null){
					words.add(calWord);//一篇文档所有词
				}
				//计算单个词的词频
				for(String word : words){
					if(wordCount.get(word) == null){
						wordCount.put(word, 1);
					}else{
						wordCount.put(word, wordCount.get(word)+1);
					}
				}
				//计算单个词的tf
				for(String word : words){
					wordTF.put(word, wordCount.get(word)/Double.valueOf(words.size()));
					bw.write(word +" "+wordTF.get(word).toString());
				}			
			}				
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}





	/**
	 * @Description: 计算根号里面的部分的值
	 * @param:
	 * @return:
	 * @date: 2017-12-14  
	 */
	public static double caltermIndex(String inputpath,String word){
		double N = allPath_one.size();
		double var_tf = 0;
		double ave_tf = 0;
		boolean flag = true;
		calAllFileTF(inputpath);//计算所有文章的tf
		for(String path : allPath_one){
			double per_tf = calFileTF(inputpath,path,word);//某词在某篇文章的tf
			if(flag){
				ave_tf = calFileAveTF(inputpath,word);//某词在所有文章的平均tf
				flag = false;
			}
			var_tf+= Math.pow((per_tf - ave_tf), 2);//tf方差
		}
		return Math.sqrt(var_tf * (1/N));//根号里面的计算结果
	}




	/**
	 * @Description: 计算最终termhood的值
	 * 1 读所有词
	 * 2 按公式计算
	 * 3 按分值高低排序
	 * 4 写出到文件中
	 * @param:
	 * @return:
	 * @date: 2017-12-13  
	 */
	public static Map<String,Map<String,Double>> calTermhood(String inputpath){
		double tf = 0;
		double df = 0;
		double index = 0;
		double finalTf = 0;
		int count = 1;//计数，可删去
		Map<String,Map<String,Double>> allWordsTF = new HashMap<>(); 
		allWordTermHood = new HashMap<>();//记录所有的termhood值，作为总的排序输出
		try {
			for(String path : getAllPath(inputpath)){
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"));		
				List<String> words = new ArrayList<>();//一篇文档中所有词
				Map<String,Double> wordTF = new HashMap<>();//<词,tf>
				String line = "";
				while((line = br.readLine())!=null){
					words.add(line);
				}
				//一篇文档词的TF
				for(String word : words){					
					tf = calWordTF(inputpath,word);
					df = calWordDF(inputpath,word);
					index = caltermIndex(inputpath,word);
					finalTf = tf * df * index;
					wordTF.put(word, finalTf);
					allWordTermHood.put(word, finalTf);//所有词
				}
				System.out.println("add "+(count++));
				//所有文章词的TF
				allWordsTF.put(path, wordTF);
			}
			return allWordsTF;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allWordsTF;
	}




	/**
	 * @Description: 对结果进行排序,并输出至txt
	 * @param:allWordsTF 所有词的tf
	 * @return: 所有词的tf排序后的值
	 * @date: 2017-12-14  
	 */
	public static Map<String,List<Map.Entry<String,Double>>> sort_hood(Map<String,Map<String,Double>> allWordsTF){
		Map<String,List<Map.Entry<String,Double>>> sort_termhood = new HashMap<>();
		for(String path : allWordsTF.keySet()){
			Map<String,Double> pageTermhood = allWordsTF.get(path);
			List<Map.Entry<String,Double>> page_list = new ArrayList<Map.Entry<String,Double>>(pageTermhood.entrySet());
			Collections.sort(page_list,new Comparator<Map.Entry<String,Double>>(){
				@Override
				public int compare(Entry<String, Double> o1,
						Entry<String, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}				
			});		
			sort_termhood.put(path, page_list);
		}	
		return sort_termhood;
	}


	/**
	 * @Description: 写出每篇文章的Termhood
	 * @param:
	 * @return:
	 * @date: 2017-12-14  
	 */
	public static void writePageTermhood(Map<String,List<Map.Entry<String,Double>>> page_list,String targetpath){
		BufferedWriter bw = null;
		int count = 1;
		try {
			for(String path : page_list.keySet()){
				String name = getFileNameFromPath(path);
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetpath +"/"+name+".txt")), "utf-8"));
				for(Map.Entry<String, Double> list : page_list.get(path)){
					bw.write(list.getKey() +":"+list.getValue());
					bw.newLine();
					bw.flush();
				}
				System.out.println("write"+ count++);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(bw!=null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}




	/**
	 * @Description: 1 对所有的termhood的值进行排序    2  输出到指定文件
	 * @param:
	 * @return:
	 * @date: 2017-12-14  
	 */
	public static void sortAllTermHood(String targetpath){
		BufferedWriter bw = null;
		try {
			//排序
			List<Map.Entry<String, Double>> sortTermhood = new ArrayList<>(allWordTermHood.entrySet());
			Collections.sort(sortTermhood,new Comparator<Map.Entry<String, Double>>() {
				@Override
				public int compare(Entry<String, Double> o1,
						Entry<String, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			//输出
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetpath), "UTF-8"));
			for(Map.Entry<String, Double> entry : sortTermhood){
				bw.write(entry.getKey() +" : "+entry.getValue());
				bw.newLine();
				bw.flush();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bw!=null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}



	/***************************辅助方法*******************************/
	/**
	 * @Description: 删除0kB的文件夹
	 * @param:
	 * @return:
	 * @date: 2017-11-13  
	 */
	public static void deleteZeroSize(File file){
		if (file.exists()) {     
			//如果是目录则递归计算其内容的总大小    
			if (file.isDirectory()) {     
				File[] children = file.listFiles();        
				for (File f : children){
					deleteZeroSize(f);
				}			
			}     
		}
		if (file.exists()&&file.length()==0) {
			file.delete();//删除文件夹 
		} 
	}




	/**
	 * @Description: 获取某个路径下所有的文件路径,没有递归统计
	 * @param:
	 * @return:
	 * @date: 2017-12-12  
	 */
	public static List<String> getAllPath(String path){
		allPath_one = new ArrayList<>();
		try (DirectoryStream<Path> entries = Files.newDirectoryStream(Paths.get(path))){
			for(Path entry : entries){
				allPath_one.add(entry.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allPath_one;
	}






	/**
	 * @Description: 删除非某个文件夹下的路径的所有文件
	 * @param:
	 * @return:
	 * @date: 2017-12-12  
	 */
	public static void delNotPathFile(String path,String allpath){
		StringUtils su = new StringUtils(allpath);
		for(String p : su.allPathResult){
		}
	}



	/**
	 * @Description: 抽取文件基础路径
	 * @param:
	 * @return:
	 * @date: 2017-12-14  
	 */
	public static String extractPath(String path){
		String result = "";
		if(path == null){
			return result;
		}
		if(path.contains("/")){
			result = path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
		}else if(path.contains("\\")){
			result = path.substring(path.lastIndexOf("\\")+1,path.lastIndexOf("."));
		}else{
			result = path;
		}
		return result;
	}



	/**
	 * 从文件的完整路径中，抽取其文件名称
	 * @param path
	 * @return
	 */
	public static String getFileNameFromPath(String path){
		String result = "";
		if(path == null){
			return result;
		}
		if(path.contains("/")){
			result = path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
		}else if(path.contains("\\")){
			result = path.substring(path.lastIndexOf("\\")+1,path.lastIndexOf("."));
		}else{
			result = path;
		}
		return result;
	}


}
