package com.retrieve.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.retrieve.config.ConstantParams;
import com.retrieve.stringhelper.string;

public class StringUtils {


	public static final String patternString1 = "(http://|https://)+(\\w|\\.|\\/|-|=|\\?|&)+";//抽取特征

	public static Pattern pattern1 = Pattern.compile(patternString1,
			Pattern.CASE_INSENSITIVE);

	public List<String> allPathResult = new ArrayList<String>();

	public StringUtils(String inputPath){
		getAllPath(inputPath);
	}

	/**
	 * 判断字符为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		boolean b = false;
		if(null == str || "".equals(str)){
			b = true;
		}
		return b;
		
	}

	/**
	 * 判断字符不为空
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str){
		boolean b = false;
		if(null != str && !"".equals(str)){
			b = true;
		}
		return b;
	}

	/**
	 * 利用正则表达式，获取可能匹配的单个内容
	 * @param regexString
	 * @param sourceString
	 * @return
	 */
	public static String getContentUseRegex(String regexString,String sourceString){
		String result = "";
		if(isEmpty(regexString) || isEmpty(sourceString)){
			return result;
		}
		try {
			Pattern pattern = Pattern.compile(regexString);
			Matcher matcher = pattern.matcher(sourceString);
			if(matcher.find()){
				result = matcher.group(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 利用正则表达式，获取可能匹配的多个内容
	 * @param regexString
	 * @param sourceString
	 * @param splitMark 分隔符
	 * @return
	 */
	public static String getContentUseRegex(String regexString,String sourceString,String splitMark){
		String result = "";
		if(isEmpty(regexString) || isEmpty(sourceString)){
			return result;
		}
		if(isEmpty(splitMark)){
			splitMark = ConstantParams.CHENG_LINE;
		}
		try {
			Pattern pattern = Pattern.compile(regexString);
			Matcher matcher = pattern.matcher(sourceString);
			while(matcher.find()){
				result += matcher.group()+splitMark;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	public static boolean isContentUseRegex(String regexString,String sourceString){
		if(isEmpty(regexString) || isEmpty(sourceString)){
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(regexString);
			Matcher matcher = pattern.matcher(sourceString);
			while(matcher.find()){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 递归获取文件夹下所有的文件绝对路径
	 * @param inputPath
	 * @return
	 */
	public void getAllPath(String inputPath){
		try {
			File file = new File(inputPath);
			File[] files = file.listFiles();
			for(File f : files){
				if(f.isDirectory()){
					getAllPath(f.getAbsolutePath());//递归
				}else{
					allPathResult.add(f.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 读取文件内容（读到内存里面）
	 * @param inputPath
	 * @return
	 */
	public static String getContent(String inputPath){
		String result = "";
		if(isEmpty(inputPath)){
			return result;
		}
		try {
			File file = new File(inputPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String temp = "";
			while((temp=br.readLine()) != null){
				result += (temp+ConstantParams.CHENG_LINE);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将内存中的字符串写入到磁盘文件中
	 * @param str 待写入的字符串
	 * @param outputPath 写入路径
	 * @return
	 */
	public static boolean string2File(String str,String outputPath){
		boolean b = false;
		if(isEmpty(outputPath)){
			return b;
		}
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File file = new File(outputPath);
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
			bw.write(str);
			b = true;
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}finally{
			try {
				if(bw != null){
					bw.close();
				}
				if(fw != null){
					fw.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return b;
	}


	/**
	 * @Description: append方式添加词
	 * @return:
	 * @date: 2017-10-22  
	 */
	public static boolean string2FileTrue(String str,String outputPath){
		boolean b = false;
		if(isEmpty(outputPath)){
			return b;
		}
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File file = new File(outputPath);
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"));
			bw.write(str);
			b = true;
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}finally{
			try {
				if(bw != null){
					bw.close();
				}
				if(fw != null){
					fw.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return b;
	}



	/**
	 * 从文件的完整路径中，抽取其文件名称
	 * @param path
	 * @return
	 */
	public static String getFileNameFromPath(String path){
		String result = "";
		if(isEmpty(path)){
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
	 * 将txt中的内容，每一行作为一个元素，读入到list中。
	 * @param inputPath
	 * @return
	 */
	public static List<String> getContentFromPath(String inputPath){
		List<String> result = new ArrayList<String>();
		try {
			File file = new File(inputPath);
			InputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is,FileUtils.getFileEncode(inputPath));
			BufferedReader br = new BufferedReader(isr);
			String temp = "";
			while((temp=br.readLine()) != null){
				result.add(temp);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	

	/**
	 * @Description: 获取properties中指定的的值
	 * @param:
	 * @return:
	 * @date: 2017-11-14  
	 */
	public static String getConfigParam(String params,String defaultValue,String fileName){
		String result = "";
		if(isEmpty(fileName) || isEmpty(params)){
			return result;
		}
		try {
			Properties properties = loadConfig(fileName);
			result = properties.getProperty(params);
			if(isEmpty(result)){
				result = defaultValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * @Description:  修改属性文件
	 * @param:
	 * @return:
	 * @date: 2017-11-14  
	 */
	public static void updateProperties(String key,String value,String fileName){
		 //获取绝对路径  
		fileName = StringUtils.class.getResource("/" + fileName).toString();  
        //截掉路径的”file:/“前缀  
		fileName = fileName.substring(6);  
        Properties prop = new Properties();  
        try {  
            File file = new File(fileName);  
            if (!file.exists())  
                file.createNewFile();  
            InputStream fis = new FileInputStream(file);  
            prop.load(fis);  
            //一定要在修改值之前关闭fis  
            fis.close();  
            OutputStream fos = new FileOutputStream(fileName);  
            prop.setProperty(key, value);  
            //保存，并加入注释  
            prop.store(fos, "Update '" + key + "' value");  
            fos.close();  
        } catch (IOException e) {  
            System.err.println("Visit " + fileName + " for updating " + value + " value error");  
        }  
	}
	

	/**
	 * 内部方法，获取Properties对象
	 * @param fileName
	 * @return
	 */
	public static Properties loadConfig(String fileName){
		Properties properties = new Properties();
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if(classLoader == null){
				classLoader = StringUtils.class.getClassLoader();
			}
			InputStream is = classLoader.getResourceAsStream(fileName);
			properties.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}



	/**
	 * @Description: 判断url是否符合要求
	 * @return:
	 * @date: 2017-9-21  
	 */
	public static boolean judgeUrls(String url){
		Matcher matcher = null;
		//http://w.a.b.c(至少)
		if (url != null && url.length() > 12){
			matcher = pattern1.matcher(url);
			if (matcher != null && matcher.matches()) {
				return true;
			}
		}
		return false;
	}


	public static List<Map.Entry<String,Double>> sortMapByValue(Map<String, Double> preSortMap){
		List<Map.Entry<String,Double>> list = new ArrayList<Map.Entry<String,Double>>(preSortMap.entrySet());
		//然后通过比较器来实现排序
		Collections.sort(list,new Comparator<Map.Entry<String,Double>>() {
			//升序排序
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		return list;
	}
	
	
	/**
	 * @Description: 返回Map中的最大值
	 * @param:
	 * @return:
	 * @date: 2017-11-1  
	 */
	public static Map.Entry<String,Double> getMapMaxValue(Map<String, Double> preSortMap){
		List<Map.Entry<String,Double>> list = new ArrayList<Map.Entry<String,Double>>(preSortMap.entrySet());
		//然后通过比较器来实现排序
		Collections.sort(list,new Comparator<Map.Entry<String,Double>>() {
			//升序排序
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		return list.get(0);
	}
	
	public static String getSheduleTag(String crawlUrl) {
		Matcher matcher = null;
		String pattern = "http://((\\w|\\.|\\/|-|=|\\?|&)+)+";
		Pattern pattern11 = Pattern.compile(pattern,
				Pattern.DOTALL);
		matcher = pattern11.matcher(crawlUrl);
		if (matcher != null && matcher.matches()) {
			String u = matcher.group(1);
			return u.substring(0, u.indexOf("."));
		}
		return null;
	}


	//	public static void main(String args[]){
	//		StringUtils st = new StringUtils("E:\\test\\hetritrix\\output\\sina\\13\\mirror");
	//		int count = 0;
	//		for(String p : allPathResult){
	//			count++;
	//			System.out.println(p+"===="+count);
	//		}
	//	}
}
