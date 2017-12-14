package com.retrieve.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import com.retrieve.domain.CountWebInfo;


/**
 * @Description: 正文抽取算法，优化版。      测试算法优劣，只将结果抽取到本地
 * @author: DU 
 * @date: 2017-12-12  
 */
public class WebExtractor {

	
	
	public static void main(String[] args) {
		
	}
	
	
	
	
	
	
	/**
	 * @Description: 删除指定后缀名称文件
	 * 遍历文件夹中所有文件，获取名称，删除指定名称或者指定后缀文件
	 * @param:
	 * @return:
	 * @date: 2017-11-12  
	 */
	public void filterFile(File file){
		//判断文件是否存在     
		if (file.exists()) {     
			//如果是目录则递归计算其内容的总大小    
			if (file.isDirectory()) {     
				File[] children = file.listFiles();        
				for (File f : children){
					filterFile(f);
				}
			}else {//如果是文件则直接返回其大小,以“兆”为单位   
				if(suffixDelFile(file)||calFileSize(file)<3000){
					file.delete();
				}    
			}      
		} else {     
			System.out.println("文件或者文件夹不存在，请检查路径是否正确！");       
		}  
	}

	
	
	
	/**
	 * @Description: 判断需要删除的文件类型
	 * @param:
	 * @return:
	 * @date: 2017-11-12  
	 */
	public boolean suffixDelFile(File file){
		if(file.getName().contains("index")||file.getName().contains("default")
				||file.getName().contains("gif") || file.getName().contains(".php") 
				||file.getName().endsWith(".jpeg") || file.getName().contains(".ico")
				|| file.getName().endsWith(".jpg") || file.getName().endsWith(".JPEG")
				|| file.getName().endsWith(".JPG") || file.getName().endsWith(".JSP")
				|| file.getName().endsWith(".jsp") || file.getName().endsWith(".ppt")
				|| file.getName().endsWith(".pptx") || file.getName().endsWith(".gif")
				|| file.getName().endsWith(".css") || file.getName().endsWith(".doc")
				|| file.getName().endsWith(".docx") || file.getName().endsWith(".zip")
				|| file.getName().endsWith(".png") || file.getName().endsWith(".js")
				|| file.getName().endsWith(".swf") || file.getName().endsWith(".xml")
				|| file.getName().endsWith(".xlsx") || file.getName().endsWith(".pdf")
				|| file.getName().endsWith(".xls") || file.getName().endsWith(".rar")
				|| file.getName().endsWith(".exe") || file.getName().endsWith(".txt")
				|| file.getName().endsWith(".mp4") || file.getName().endsWith(".wmv")
				|| file.getName().endsWith(".mp3") || file.getName().endsWith(".flv")
				|| file.getName().endsWith(".mpg") || file.getName().endsWith(".action")
				|| file.getName().endsWith(".aspx") || file.getName().endsWith(".bmp")
				|| file.getName().endsWith("4") || file.getName().endsWith("5")
				|| file.getName().endsWith("6") || file.getName().endsWith("7")
				|| file.getName().endsWith("8") || file.getName().endsWith("9")
				|| file.getName().endsWith("0") || file.getName().contains("mailto") 
				||file.getName().contains("b2b")||file.getName().contains("info")){
			return true;
		} 
		return false;		
	}
	
	
	
	/**
	 * @Description: 计算文件大小
	 * @param:
	 * @return:
	 * @date: 2017-11-12  
	 */
	public double calFileSize(File file){
		if (file.exists()) {  
			if (!file.isDirectory()) {     
				return (double) file.length();
			} 
		}
		return 0;		
	}
	
	
	
	
	/**
	 * @Description: 删除0kB的文件夹
	 * @param:
	 * @return:
	 * @date: 2017-11-13  
	 */
	public void deleteZeroSize(File file){
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
	
	
	
	
	/*******************正文抽取算法**********************/

	protected Document doc;

	protected HashMap<Element, CountWebInfo> infoMap = new HashMap<Element, CountWebInfo>();

	public static ArrayList<CountWebInfo> sortCount = null;

	public WebExtractor(Document doc) {
		this.doc = doc;
	}

	protected void clean() {
		doc.select("script,noscript,style,iframe,br").remove();
	}

	public static void writeHtml(Object node){
		String path = "E:\\test\\remove2.html";		
		try {
			FileOutputStream outStream = new FileOutputStream(new File(path));
			outStream.write(node.toString().getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public Element getContentElement() throws Exception {
		clean();
		computeInfo(doc.body());
		double maxScore = 0;
		Element content = null;
		sortCount = new ArrayList<CountWebInfo>();
		for (Map.Entry<Element, CountWebInfo> entry : infoMap.entrySet()) {
			Element tag = entry.getKey();
			if (tag.tagName().equals("a") || tag == doc.body()) {
				continue;
			}
			double score = computeScore(tag);
			if (score > maxScore) {
				maxScore = score;
				content = tag;
			}
		}
		sortCountInfo(false);
		//二次抽取
		if(!meetsRules()){
			for(int i=0;i<5;i++){
				if(sortCount.get(i).punctuation>2 && sortCount.get(i).linkTextCount<10){
					return sortCount.get(i).tag;
				}
			}
			return null;
		}
		return content;
	}


	/**
	 * 方法一抽取出来的内容是否符合要求
	 * 
	 * */
	public Boolean meetsRules(){
		//1、排名第一模块标点<3   2、a标签中无内容
		if((sortCount.get(0).punctuation>2) && (sortCount.get(0).linkTextCount<10)){
			return true;
		}
		return false;		
	}


	/**
	 * @Description: 根据score排序
	 * @return:
	 * @date: 2017-10-25  
	 */
	protected void sortCountInfo(boolean flag){
		Collections.sort(sortCount, new Comparator<CountWebInfo>() {
			public int compare(CountWebInfo o1, CountWebInfo o2) {
				Integer score1 = (int) o1.score;
				Integer score2 = (int) o2.score;
				return score2.compareTo(score1);// 正确的方式  
			}
		});
		
	}



	public Element computeScoreSecond(){
		Element content = null;
		double maxScore = 0;
		for (int i = 0; i < sortCount.size(); i++) {
			if(sortCount.get(i).textCount<200){
				double var = Math.sqrt(computeVar(sortCount.get(i).leafList) + 1);//计算叶子节点方差
				double score = Math.log(var) * sortCount.get(i).densitySum * Math.log(sortCount.get(i).textCount - sortCount.get(i).linkTextCount + 1) * Math.log10(sortCount.get(i).pCount + 2)*(sortCount.get(i).imageCount+1)*(sortCount.get(i).punctuation)*(sortCount.get(i).strongCount+1);
				if (score > maxScore) {
					maxScore = score;
					content = sortCount.get(i).tag;
				}
			}
		}
		if (content == null) {
			return null;
		}
		return content;
	}




	protected CountWebInfo computeInfo(Node node) {	
		//节点是元素
		if (node instanceof Element) {
			Element tag = (Element) node;

			CountWebInfo countInfo = new CountWebInfo();
			for (Node childNode : tag.childNodes()) {
				CountWebInfo childCountInfo = computeInfo(childNode);
				countInfo.textCount += childCountInfo.textCount;//文本信息的长度
				countInfo.linkTextCount += childCountInfo.linkTextCount;//超链接文本信息节点（1 a标签中文本信息长度 ）
				countInfo.tagCount += childCountInfo.tagCount;//所有标签节点个数
				countInfo.linkTagCount += childCountInfo.linkTagCount;//<a>链接的个数
				countInfo.leafList.addAll(childCountInfo.leafList);//叶子节点信息的长度
				countInfo.densitySum += childCountInfo.density;//文本的密度
				countInfo.pCount += childCountInfo.pCount;//p标签节点
				countInfo.punctuation += childCountInfo.punctuation;//标点个数
				countInfo.strongCount += childCountInfo.strongCount;//strong个数
				countInfo.imageCount += childCountInfo.imageCount;//image个数
			}
			countInfo.tagCount++;
			String tagName = tag.tagName();
			if (tagName.equals("a")) {
				countInfo.linkTextCount = countInfo.textCount;
				countInfo.linkTagCount++;
			} else if (tagName.equals("p")) {
				countInfo.pCount++;
			}else if (tagName.equals("strong")){
				countInfo.strongCount++;
			} else if(tagName.equals("img")){
				countInfo.imageCount++;
			}

			int pureLen = countInfo.textCount - countInfo.linkTextCount;//文本信息的长度-a标签中文本信息长度
			int len = countInfo.tagCount - countInfo.linkTagCount;//所有标签节点个数-<a>链接的个数
			if (pureLen == 0 || len == 0) {
				countInfo.density = 0;
			} else {
				countInfo.density = (pureLen + 0.0) / len;//文本字数/文本节点数
			}

			infoMap.put(tag, countInfo);

			return countInfo;
		} else if (node instanceof TextNode) {//节点是文本
			TextNode tn = (TextNode) node;
			CountWebInfo countInfo = new CountWebInfo();
			String text = tn.text();
			int pLen = calPunctuation(text);
			countInfo.punctuation = pLen;//标点个数
			int len = text.length();
			countInfo.textCount = len;
			countInfo.leafList.add(len);
			return countInfo;
		} else {
			return new CountWebInfo();
		}
	}



	protected double computeScore(Element tag) {
		CountWebInfo countInfo = infoMap.get(tag);
		double var = Math.sqrt(computeVar(countInfo.leafList) + 1);//计算叶子节点方差
		double score = Math.log(var) * countInfo.densitySum * Math.log(countInfo.textCount - countInfo.linkTextCount + 1) * Math.log10(countInfo.pCount + 2);
		countInfo.score = score;
		countInfo.tag = tag;
		sortCount.add(countInfo);
		//score = log(叶子节点方差) * 所有文本密度之和     * log(文本字数总和) * log10(<p>个数+2)
		//score越高 = 叶子节点文本数差异越大  * 所有文本密度之和越大 * 文本字数总和越大 * <p>个数越多
		return score;
	}



	/**
	 * @Description: 计算文本中标点符号个数
	 * @return:
	 * @date: 2017-10-25  
	 */
	protected int calPunctuation(String text){
		String str = text.replaceAll("[\\,，\\。\\？?\\!！\\、\\；;\\‘’]", "^");
		if(str.contains("^")){
			if(str.subSequence(str.lastIndexOf("^"), str.length()).equals("^")){
				String s [] = str.split("\\^");
				return s.length;
			}else{
				String s [] = str.split("\\^");
				return s.length-1;
			}
		}
		return 0;
	}


	protected double computeVar(ArrayList<Integer> data) {
		if (data.size() == 0) {
			return 0;
		}
		if (data.size() == 1) {
			return data.get(0) / 2;
		}
		double sum = 0;
		//总和
		for (Integer i : data) {
			sum += i;
		}
		double ave = sum / data.size();//平均值
		sum = 0;
		for (Integer i : data) {
			sum += (i - ave) * (i - ave);
		}
		sum = sum / data.size();
		return sum;
	}



	public String getTime(Element contentElement) throws Exception {
		String regex = "([1-2][0-9]{3})[^0-9]{1,5}?([0-1]?[0-9])[^0-9]{1,5}?([0-9]{1,2})[^0-9]{1,5}?([0-2]?[1-9])[^0-9]{1,5}?([0-9]{1,2})[^0-9]{1,5}?([0-9]{1,2})";
		Pattern pattern = Pattern.compile(regex);
		Element current = contentElement;
		for (int i = 0; i < 2; i++) {
			if (current != null && current != doc.body()) {
				Element parent = current.parent();
				if (parent != null) {
					current = parent;
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			if (current == null) {
				break;
			}
			String currentHtml = current.outerHtml();
			Matcher matcher = pattern.matcher(currentHtml);
			if (matcher.find()) {//年份和时间信息
				return matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3) + " " + matcher.group(4) + ":" + matcher.group(5) + ":" + matcher.group(6);
			}
			if (current != doc.body()) {
				current = current.parent();
			}
		}

		try {
			return getDate(contentElement);
		} catch (Exception ex) {
			throw new Exception("time not found");
		}

	}

	//仅仅含有年份信息
	protected String getDate(Element contentElement) throws Exception {
		String regex = "([1-2][0-9]{3})[^0-9]{1,5}?([0-1]?[0-9])[^0-9]{1,5}?([0-9]{1,2})";
		Pattern pattern = Pattern.compile(regex);
		Element current = contentElement;
		for (int i = 0; i < 2; i++) {
			if (current != null && current != doc.body()) {
				Element parent = current.parent();
				if (parent != null) {
					current = parent;
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			if (current == null) {
				break;
			}
			String currentHtml = current.outerHtml();
			Matcher matcher = pattern.matcher(currentHtml);
			if (matcher.find()) {
				return matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
			}
			if (current != doc.body()) {
				current = current.parent();
			}
		}
		return null;
		//		throw new Exception("date not found");
	}



	public String getTitle(final Element contentElement) throws Exception {
		final ArrayList<Element> titleList = new ArrayList<Element>();
		final ArrayList<Double> titleSim = new ArrayList<Double>();
		final AtomicInteger contentIndex = new AtomicInteger();
		final String metaTitle = doc.title().trim();
		if (!metaTitle.isEmpty()) {
			doc.body().traverse(new NodeVisitor() {

				public void head(Node node, int i) {
					if (node instanceof Element) {
						Element tag = (Element) node;
						if (tag == contentElement) {
							contentIndex.set(titleList.size());
							return;
						}
						String tagName = tag.tagName();
						//抽取出html中h1-h6的标签，计算其中文字和title之间的相似度
						if (Pattern.matches("h[1-6]", tagName)) {
							String title = tag.text().trim();
							double sim = strSim(title, metaTitle);//计算两个字符串之间的相似度
							titleSim.add(sim);
							titleList.add(tag);
						}
					}
				}

				public void tail(Node node, int i) {
				}
			});
			//取出html中h1-h6的标签中的相似度，并计算其中相似度最大的文本作为标题
			int index = contentIndex.get();
			if (index > 0) {
				double maxScore = 0;
				int maxIndex = -1;
				for (int i = 0; i < index; i++) {
					double score = (i + 1) * titleSim.get(i);
					if (score > maxScore) {
						maxScore = score;
						maxIndex = i;
					}
				}
				if (maxIndex != -1) {
					return titleList.get(maxIndex).text();
				}
			}
		}
		//若metaTitle无信息，抽取出html中的title几乎所有包含title的部分，将其中的第一个作为标题
		Elements titles = doc.body().select("*[id^=title],*[id$=title],*[class^=title],*[class$=title]");
		if (titles.size() > 0) {
			String title = titles.first().text();
			if (title.length() > 5 && title.length()<40) {
				return titles.first().text();
			}
		}
		return getTitleByEditDistance(contentElement);
	}


	/**
	 * @Description:将HTML中所有的文本和metaTitle计算字符串相似度，取出最相似的作为标题
	 * @return:
	 * @date: 2017-9-29  
	 */
	protected String getTitleByEditDistance(Element contentElement) throws Exception {
		final String metaTitle = doc.title();

		final ArrayList<Double> max = new ArrayList<Double>();
		max.add(0.0);
		final StringBuilder sb = new StringBuilder();
		doc.body().traverse(new NodeVisitor() {

			public void head(Node node, int i) {

				if (node instanceof TextNode) {
					TextNode tn = (TextNode) node;
					String text = tn.text().trim();
					double sim = strSim(text, metaTitle);
					if (sim > 0) {
						if (sim > max.get(0)) {
							max.set(0, sim);
							sb.setLength(0);
							sb.append(text);
						}
					}

				}
			}

			public void tail(Node node, int i) {
			}
		});
		if (sb.length() > 0) {
			return sb.toString();
		}
		return null;
	}




	protected double strSim(String a, String b) {
		int len1 = a.length();
		int len2 = b.length();
		if (len1 == 0 || len2 == 0) {
			return 0;
		}
		double ratio;
		if (len1 > len2) {
			ratio = (len1 + 0.0) / len2;
		} else {
			ratio = (len2 + 0.0) / len1;
		}
		if (ratio >= 3) {
			return 0;
		}
		return (lcs(a, b) + 0.0) / Math.max(len1, len2);
	}



	protected int lcs(String x, String y) {

		int M = x.length();
		int N = y.length();
		if (M == 0 || N == 0) {
			return 0;
		}
		int[][] opt = new int[M + 1][N + 1];

		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				if (x.charAt(i) == y.charAt(j)) {
					opt[i][j] = opt[i + 1][j + 1] + 1;
				} else {
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
				}
			}
		}

		return opt[0][0];
	}
}
