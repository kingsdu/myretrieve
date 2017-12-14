package com.retrieve.util;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import sun.org.mozilla.javascript.internal.ast.ForInLoop;

/**
 * @Description: 油气行业词库词性规则抽取
 * @author: DU 
 * @date: 2017-12-11  
 */
public class OilRuleExtract {

	public static void main(String[] args) {
		//		read_WriteDic("G:\\TermExtract\\dic\\word03.txt","G:\\TermExtract\\dic\\split\\split_word.txt",true);
		//		statisticsRule("G:\\TermExtract\\dic\\split\\split_word.txt","G:\\TermExtract\\dic\\split\\word_rule.txt",true);
//		statisWordLength("G:\\TermExtract\\dic\\split\\word_rule.txt","G:\\TermExtract\\dic\\split\\statis_len.txt",true);
//		staticsRuleFre("G:\\TermExtract\\dic\\split\\word_rule.txt","G:\\TermExtract\\dic\\split\\rule_statics.txt",true);
		showRuleTimes("G:\\TermExtract\\dic\\split_word.txt","G:\\TermExtract\\dic\\split\\show_word.txt","n u ");
	}

	/**
	 * @Description: 读词典，分词，统计词长度，发现词性规则
	 * @param:
	 * @return:
	 * @date: 2017-12-11  
	 */
	public static void read_WriteDic(String filePath,String targetPath,boolean isAppend){
		BufferedReader in = null;
		BufferedWriter bw = null;	
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPath),isAppend), "UTF-8"));
			String str = null;
			while((str = in.readLine()) != null){
				String splitRes = SplitWordsUtils.ITCTILS(str);//ITCTILS分词
				bw.write(splitRes);
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
			try {
				if(bw!=null){
					bw.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * @Description: 统计词性规则出现频率
	 * @param:
	 * @return:
	 * @date: 2017-12-11  
	 */
	public static void statisticsRule(String filePath,String targetPath,boolean isAppend){
		BufferedReader in = null;
		BufferedWriter bw = null;		
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPath),isAppend), "UTF-8"));
			String str = "";
			StringBuilder sb = new StringBuilder();
			while((str = in.readLine()) != null){
				String[]temp_01 = str.split(" ");
				for(int i=0;i<temp_01.length;i++){
					String[] temp_02 = temp_01[i].split("/");
					sb.append(temp_02[1]).append(" ");//词性
				}
				sb.append("\r\n");
			}
			bw.write(sb.toString());
			bw.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw!=null){
					bw.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}



	/**
	 * @Description: 统计词长
	 * @param:
	 * @return:
	 * @date: 2017-12-11  
	 */
	public static void statisWordLength(String filePath,String targetPath,boolean isAppend){
		BufferedReader in = null;
		BufferedWriter bw = null;		
		Map<String,Integer> lenCount = new HashMap<>();
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPath),isAppend), "UTF-8"));
			String str = "";
			StringBuilder sb = new StringBuilder();
			while((str = in.readLine()) != null){
				String[]temp_01 = str.split(" ");
				String key = String.valueOf(temp_01.length);
				//首次
				if(!lenCount.containsKey(key)){
					lenCount.put(key, 1);
				}else{
					lenCount.put(key, lenCount.get(key)+1);
				}	
			}
			for (Map.Entry<String,Integer> entry : lenCount.entrySet()) {
				sb.append(entry.getKey()+" "+entry.getValue()).append("\r\t");
			}
			bw.write(sb.toString());
			bw.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw!=null){
					bw.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}



	/**
	 * @Description: 统计词性规则出现频率
	 * @param:
	 * @return:
	 * @date: 2017-12-11  
	 */
	public static void staticsRuleFre(String filePath,String targetPath,boolean isAppend){
		BufferedReader in = null;
		BufferedWriter bw = null;		
		Map<String,Integer> lenCount = new HashMap<>();
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPath),isAppend), "UTF-8"));
			String str = "";
			StringBuilder sb = new StringBuilder();
			while((str = in.readLine()) != null){
				//首次
				if(!lenCount.containsKey(str)){
					lenCount.put(str, 1);
				}else{
					lenCount.put(str, lenCount.get(str)+1);
				}	
			}
			//排序
			List<Map.Entry<String,Integer>> rule_statics = new ArrayList<Map.Entry<String,Integer>>(lenCount.entrySet());
			Collections.sort(rule_statics, new Comparator<Map.Entry<String,Integer>>(){
				@Override
				public int compare(Entry<String, Integer> o1,
						Entry<String, Integer> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}

			});
			for(Map.Entry<String,Integer> entry:rule_statics){ 
				sb.append(entry.getKey()+" "+entry.getValue()).append("\r\t");
			} 
			bw.write(sb.toString());
			bw.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw!=null){
					bw.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	
	/**
	 * @Description: 展示某种词性规则的词，输入某词性，展示属于该词性的词
	 * @param:
	 * @return:
	 * @date: 2017-12-11  
	 */
	public static void showRuleTimes(String filePath,String targetPath,String rule){
		BufferedReader in = null;	
		BufferedWriter bw = null;		
		List<String> rule_word = new ArrayList<>();
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPath),true), "UTF-8"));
			String str = "";
			while((str = in.readLine()) != null){
				String[] temp_01 = str.split(" ");
				String temp_rule = "";
				for(int i=0;i<temp_01.length;i++){				
					String[] temp_02 = temp_01[i].split("/");
				    temp_rule+=temp_02[1]+" ";
				}
				if(temp_rule.equals(rule)){
					rule_word.add(str);
				}	
			}
			bw.write("\r\t");
			bw.write("\r\t");
			bw.write("\r\t");
			bw.write(rule);
			bw.write("\r\t");
			for (int i = 0; i < rule_word.size(); i++) {
				bw.write(rule_word.get(i));
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
			try {
				if(bw!=null){
					bw.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
}
