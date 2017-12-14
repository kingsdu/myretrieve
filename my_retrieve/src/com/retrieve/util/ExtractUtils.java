package com.retrieve.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.retrieve.config.ConstantParams;

public class ExtractUtils {

	private static Map<String,Integer> keyMap = null;//某词出现次数
	private static Map<String,Double> wordCount_txts = new HashMap<>();//某词在所有文章中出现的次数
	private static Map<String,Double> wordCount_txt = new HashMap<>();//某词在某篇文章中出现的次数
	private static List<String> contained = new ArrayList<>();//去除所有文本中的重复词语
	private static Map<String,Double> noave_Object = new HashMap<>();//每个词未平均的object值


	/**
	 * 读取评价对象抽取的规则
	 * @param inputPath 规则的完整路径
	 * @return
	 * xx/n
	 */
	public static List<String> getRules(String inputPath){
		List<String> result = new ArrayList<String>();
		File file = new File(inputPath); 
		FileReader fr = null;
		BufferedReader br = null;
		String regex = "[\u4e00-\u9fa5a-zA-Z0-9]*";
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String temp = "";
			while((temp=br.readLine()) != null){
				String[] temps = temp.split(ConstantParams.SINGLE_BLANK);
				String str = "";
				for(String rule : temps){
					str += (regex+"/"+rule+"(.?)"+ConstantParams.SINGLE_BLANK);
				}
				result.add(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(br != null){
					br.close();
				}
				if(fr != null){
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}


	/**
	 * 按照规则，抽取单个文件的候选集
	 * @param rulesPath 规则的路径
	 * @param sourcePath 分词之后的文件路径
	 * @param outputPath 抽取之后的保存路径
	 * @return
	 */
	public static void singleTextWordsSet(String rulesPath,String sourcePath,String outputPath){
		try {
			String result = "";
			List<String> rulesList = getRules(rulesPath);
			String sourceString = StringUtils.getContent(sourcePath);
			for(String rule : rulesList){
				result += StringUtils.getContentUseRegex(rule, sourceString, "");
			}
			StringUtils.string2File(result, outputPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 过滤特殊词（单子动词，特殊符号... ...）
	 * @param intputPath
	 * @param outputPath
	 */
	public static void filterWords(String inputPath,String outputPath){
		try {
			String result = "";
			File file = new File(inputPath);
			InputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is,FileUtils.getFileEncode(inputPath));
			BufferedReader br = new BufferedReader(isr);
			String temp = "";
			while((temp=br.readLine()) != null){
				String[] temps = temp.split(ConstantParams.SINGLE_BLANK);
				if(temps.length > 1){
					result += temp+ConstantParams.CHENG_LINE;
				}else{
					String[] wordsTemps = temps[0].split("/");
					if(wordsTemps[0].length() != 1){
						result += temp+ConstantParams.CHENG_LINE;
					}
				}
			}
			br.close();
			StringUtils.string2File(result, outputPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	/**
	 * 去除txt中重复的候选词
	 * @param inputPath
	 * @param outputPath
	 */
	public static void delRepWords(String inputPath,String outputPath){
		try {
			List<String> list = new ArrayList<String>();
			String result = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath)),FileUtils.getFileEncode(inputPath)));
			String temp = "";
			while((temp=br.readLine()) != null){
				if(!list.contains(temp)){
					list.add(temp);
					result += temp+ConstantParams.CHENG_LINE;
				}
			}
			br.close();
			StringUtils.string2File(result, outputPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 非完整性过滤
	 * @param inputPath : 已去重完毕的词；   sourcePath：splitWords中的词；
	 * @param outputPath
	 * @return 满足条件的完整性词
	 */
	public static List<String> filterInteWords(String inputPath,String sourcePath,String outputPath){
		List<String> inteWords = new ArrayList<String>();
		try {
			String rawResult = "";
			List<String> wordsList = StringUtils.getContentFromPath(inputPath);
			for(String word : wordsList){					
				//读分词之后的源文件
				String sourceWord = StringUtils.getContent(sourcePath);
				String[] sourceWords = sourceWord.split(ConstantParams.SINGLE_BLANK);
				String[] words = word.split(ConstantParams.SINGLE_BLANK);
				Set<String> leftSet = new HashSet<String>();
				Set<String> rightSet = new HashSet<String>();

				//是0的时候，表示完整；1的时候表示不完整
				int left = 0;
				int right = 0;

				int leftFirst = 1;
				int rightLast = 1;
				if(words.length == 1){
					for(int i=0;i<sourceWords.length;i++){
						if(words[0].equals(sourceWords[i])){
							if(i == 0){
								leftFirst = 0;//左完整
								rightSet.add(sourceWords[i+1]);
							}else if(i == sourceWords.length-1){
								rightLast = 0;//右完整
								leftSet.add(sourceWords[i-1]);
							}else{
								leftSet.add(sourceWords[i-1]);
								rightSet.add(sourceWords[i+1]);
							}

						}
						if(leftFirst == 0){
							left = 0;
						}else{
							if(leftSet.size() == 1){
								left = 1;
							}else{
								left = 0;
							}
						}						
						if(rightLast == 0){
							right = 0;
						}else{
							if(rightSet.size() == 1){
								right = 1;
							}else{
								right = 0;
							}
						}
					}
				}else if(words.length == 2){					
					for(int i=0;i<sourceWords.length-1;i++){
						if(words[0].equals(sourceWords[i]) && words[1].equals(sourceWords[i+1])){
							if(i == 0){
								leftFirst = 0;//
								rightSet.add(sourceWords[i+2]);
							}else if(i == sourceWords.length-1){
								rightLast = 0;
								leftSet.add(sourceWords[i-1]);
							}else{
								leftSet.add(sourceWords[i-1]);
								rightSet.add(sourceWords[i+2]);
							}

						}
						if(leftFirst == 0){
							left = 0;
						}else{
							if(leftSet.size() == 1){
								left = 1;
							}else{
								left = 0;
							}
						}

						if(rightLast == 0){
							right = 0;
						}else{
							if(rightSet.size() == 1){
								right = 1;
							}else{
								right = 0;
							}
						}
					}
				}else if(words.length == 3){
					for(int i=0;i<sourceWords.length-2;i++){
						if(words[0].equals(sourceWords[i]) && words[1].equals(sourceWords[i+1]) && words[2].equals(sourceWords[i+2])){
							if(i == 0){
								leftFirst = 0;
								rightSet.add(sourceWords[i+3]);
							}else if(i == sourceWords.length-1){
								rightLast = 0;
								leftSet.add(sourceWords[i-1]);
							}else{
								leftSet.add(sourceWords[i-1]);
								rightSet.add(sourceWords[i+3]);
							}

						}
						if(leftFirst == 0){
							left = 0;
						}else{
							if(leftSet.size() == 1){
								left = 1;
							}else{
								left = 0;
							}
						}
						if(rightLast == 0){
							right = 0;
						}else{
							if(rightSet.size() == 1){
								right = 1;
							}else{
								right = 0;
							}
						}
					}
				}
				rawResult += word+ConstantParams.TABLE+left+ConstantParams.TABLE+right+ConstantParams.CHENG_LINE;
				if(left == 0 && right == 0){
					inteWords.add(word);
					//					result += word+ConstantParams.TABLE+left+ConstantParams.TABLE+right+ConstantParams.CHENG_LINE;
				}
			}
			StringUtils.string2File(rawResult, outputPath);//所有完整性词的展示
			//			StringUtils.string2File(result, outputPath);//仅是完整性词的展示
			return inteWords;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inteWords;	
	}



	/**
	 * @Description: 非稳定评价对象的过滤，并生成去除词性的stableWords,为后面计算做准备
	 * @param: inputPath : fliterWords（未去重）   outPath:输出路径     inteWords:符合规则完整性词     stablePath
	 * @return:
	 * @date: 2017-10-13  
	 */
	public static void getObject(String inputPath,String outPath,List inteWords,String stablePath){
		Map<String, Double> wordsObject = new HashMap<String, Double>();
		List<String> wordsList = StringUtils.getContentFromPath(inputPath);
		getKeyTimes(inputPath);
		try {
			double objectIndex = 0;
			for(String word : wordsList){
				if(!inteWords.contains(word)){
					continue;
				}
				String[] words = word.split(ConstantParams.SINGLE_BLANK);
				if(words.length == 1){
					wordsObject.put(word, 1.0);
				}
				else if(words.length == 2){
					double aWordTimes = 0;
					double bWordTimes = 0;
					double objectTimes = 0;
					double wordsNumber = 2;
					if(keyMap.containsKey(word)){
						objectTimes = keyMap.get(word);	
					}
					if(keyMap.containsKey(words[0]+ConstantParams.SINGLE_BLANK)){
						aWordTimes = keyMap.get(words[0]+ConstantParams.SINGLE_BLANK);	
					}
					if(keyMap.containsKey(words[1]+ConstantParams.SINGLE_BLANK)){	
						bWordTimes = keyMap.get(words[1]+ConstantParams.SINGLE_BLANK);	
					}
					objectIndex = objectTimes/((aWordTimes+bWordTimes)-(wordsNumber-1)*objectTimes);
					if(Double.isInfinite(objectIndex)){
						objectIndex = 0;
					}
					wordsObject.put(word, objectIndex);
				}
				else if(words.length == 3){
					double aWordTimes = 0;
					double bWordTimes = 0;
					double cWordTimes = 0;
					double objectTimes = 0;
					double wordsNumber = 3;
					if(keyMap.containsKey(word)){
						objectTimes = keyMap.get(word);	
					}		
					if(keyMap.containsKey(words[0]+ConstantParams.SINGLE_BLANK)){
						aWordTimes = keyMap.get(words[0]+ConstantParams.SINGLE_BLANK);	
					}
					if(keyMap.containsKey(words[1]+ConstantParams.SINGLE_BLANK)){	
						bWordTimes = keyMap.get(words[1]+ConstantParams.SINGLE_BLANK);	
					}
					if(keyMap.containsKey(words[2]+ConstantParams.SINGLE_BLANK)){	
						cWordTimes = keyMap.get(words[2]+ConstantParams.SINGLE_BLANK);	
					}
					objectIndex = objectTimes/((aWordTimes+bWordTimes+cWordTimes)-(wordsNumber-1)*objectTimes);
					if(Double.isInfinite(objectIndex)){
						objectIndex = 0;
					}
					wordsObject.put(word, objectIndex);
				}
			}
			Map resultMap = SortUtils.sortByValue(wordsObject);
			String str = "";
			//			String stable = "";
			Set<String> set = resultMap.keySet();
			Iterator<String> iter = set.iterator();
			while(iter.hasNext()){
				String key = iter.next();
				Double value = (Double)resultMap.get(key);		
				//				stable+=key+ConstantParams.SINGLE_BLANK+value+ConstantParams.CHENG_LINE;
				//稳定性>=0的数都可以存入最终的词典中
				if(value>=0){
					str+=key+ConstantParams.CHENG_LINE;	
				}			
			}
			//stable
			clearSpeech(str, stablePath);
			//			StringUtils.string2File(str, stablePath);//稳定性词（删除）
			StringUtils.string2FileTrue(str, outPath);//生成第一层词典
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * @Description: 计算TF,输入的词要对应其所在的文章
	 * @return:Map<String,Double> tf
	 * @param: word 词;inputPath 读取文本分词地址;outputPath:TF输出路径
	 * @date: 2017-10-30  
	 */
	public static double calTf(String wordPath,String inputPath,String outputPath){
		double wordNum = 0;
		String result = "";//写出文件结果
		boolean flag = true;
		List<String> wordList = new ArrayList<>();
		File file = new File(wordPath);
		try{
			//读取stableWords文件中生成的词，存入wordList
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),FileUtils.getFileEncode(inputPath)));
			String temp = "";
			while((temp=br.readLine()) != null){
				wordList.add(temp);
			}
			//计算wordList中的词频
			for(int i=0;i<wordList.size();i++){
				double wordCount = 0;
				file = new File(inputPath);
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file),FileUtils.getFileEncode(inputPath)));
				temp = "";
				while((temp=br.readLine()) != null){
					String[]tfTemp = temp.split(ConstantParams.SINGLE_BLANK);//切分成词
					if(flag) wordNum += tfTemp.length;//词总数
					for(int j=0; j<tfTemp.length; j++){
						if(wordList.get(i).equals(tfTemp[j])){
							wordCount++;
						}
					}
				}
				flag = false;
				wordCount_txt.put(wordList.get(i), wordCount);//存入词出现次数
				//一篇文章所有的词频
				result+=wordList.get(i)+ConstantParams.SINGLE_BLANK+(wordCount/wordNum)+ConstantParams.CHENG_LINE;
			}
			//写出词频文件
			StringUtils.string2File(result, outputPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}



	/**
	 * @Description: 计算文档频率df
	 * @param: word 词;inputPath 读取文本分词地址;txtNum 文章总数
	 * @return:
	 * @date: 2017-10-30  
	 */
	public static double calDF(String word,String inputPath,int txtNum){
		double txtCount = 0;
		for (int i = 0; i < new StringUtils(inputPath).allPathResult.size(); i++){
			try {
				String path = new StringUtils(inputPath).allPathResult.get(i);
				File file = new File(path);
				InputStream is = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(is,FileUtils.getFileEncode(path));
				BufferedReader br = new BufferedReader(isr);
				String temp = "";
				while((temp=br.readLine()) != null){
					if(temp.contains(word)){
						txtCount++;
					}
				}
				wordCount_txts.put(word, txtCount);
				//			if (txtCount>1 && txtNum>1) {  
				//				df.put(word, txtCount/txtNum);
				//			} 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return txtCount/txtNum;
	}



	/**
	 * @Description: 计算某词出现次数
	 * @return:
	 * @date: 2017-10-29  
	 */
	public static void getKeyTimes(String inputPath){
		File file = new File(inputPath);
		try {
			InputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is,FileUtils.getFileEncode(inputPath));
			BufferedReader br = new BufferedReader(isr);
			String temp = "";
			keyMap = new HashMap<String,Integer>();
			while((temp=br.readLine()) != null){
				if(keyMap.containsKey(temp)){
					Integer value = keyMap.get(temp);
					keyMap.remove(temp);
					keyMap.put(temp, value+1);
				}else{
					keyMap.put(temp, 1);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 计算候选词，在文章中的出现的次数
	 * @param inputPath
	 * @param outputPath
	 * @return 
	 */
	public static void getNumFromFile(String inputPath,String outputPath){
		try {
			String result = "";
			File file = new File(inputPath);
			InputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is,FileUtils.getFileEncode(inputPath));
			BufferedReader br = new BufferedReader(isr);
			String temp = "";
			Map<String,Integer> map = new HashMap<String,Integer>();
			while((temp=br.readLine()) != null){
				if(map.containsKey(temp)){
					Integer value = map.get(temp);
					map.remove(temp);
					map.put(temp, value+1);
				}else{
					map.put(temp, 1);
				}
			}
			br.close();
			Set<String> set = map.keySet();
			Iterator<String> iter = set.iterator();
			while(iter.hasNext()){
				String key = iter.next();
				Integer value = map.get(key);
				result += key+ConstantParams.TABLE+value+ConstantParams.CHENG_LINE;
			}
			StringUtils.string2File(result, outputPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 给统计之后的文件，进行排序
	 * @param inputPath
	 * @param outputPath
	 */
	public static void getSortResultForStatistics(String inputPath,String outputPath){
		try {
			String result = "";
			File file = new File(inputPath);
			InputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is,FileUtils.getFileEncode(inputPath));
			BufferedReader br = new BufferedReader(isr);
			String temp = "";
			Map<String,Integer> map = new HashMap<String,Integer>();
			while((temp=br.readLine()) != null){
				String[] temps = temp.split(ConstantParams.TABLE);
				map.put(temps[0], Integer.parseInt(temps[1]));
			}
			Map resultMap = SortUtils.sortByValue(map);
			br.close();

			Set<String> set = resultMap.keySet();
			Iterator<String> iter = set.iterator();
			while(iter.hasNext()){
				String key = iter.next();
				Integer value = (Integer)resultMap.get(key);
				result += key+ConstantParams.TABLE+value+ConstantParams.CHENG_LINE;
			}
			StringUtils.string2File(result, outputPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * @Description: 计算评价对象出现的概率:f-object
	 * @return:
	 * @date: 2017-10-30  
	 */
	public static double calObject(String word){
		Double d = wordCount_txt.get(word);
		Double objectD = wordCount_txts.get(word);
		double result = 0.0;
		double X = 0.5;
		if(objectD == 1){
			result = X;
			return result;
		}
		else{
			result = d/objectD+1;
		}
		if(Double.isNaN(result)){
			result = 0.0;
		}
		return result;
	}


	/**
	 * @Description: 词典处理：去除词性标注，抽取关键词之和
	 * @return:
	 * @date: 2017-10-15  
	 */
	public static void genInteDic(String sourcePath,String outputPath){
		BufferedReader br = null;
		String result = "";
		String line = "";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourcePath)),FileUtils.getFileEncode(sourcePath)));
			while((line = br.readLine())!=null){
				String []bStr = line.split(ConstantParams.SINGLE_BLANK);
				String s = "";
				for(String w : bStr){
					if(w.contains("/")){
						s+= w.substring(0, w.indexOf("/"));
					}
				}
				result+=s+ConstantParams.CHENG_LINE;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		StringUtils.string2File(result, outputPath);
	}




	/**
	 * @Description: 去除词性
	 * @param:content 去除的内容，outputPath 输出的路径
	 * @return:
	 * @date: 2017-10-29  
	 */
	public static void clearSpeech(String content,String outputPath){
		String result = "";
		String []aStr = content.split(ConstantParams.CHENG_LINE);
		String s = "";
		for(String m : aStr){
			s = "";
			String []bStr = m.split(ConstantParams.SINGLE_BLANK);
			for(String w : bStr){
				if(w.contains("/")){
					s+= w.substring(0, w.indexOf("/"));
				}	
			}
			result+=s+ConstantParams.CHENG_LINE;
		}
		StringUtils.string2File(result, outputPath);
	}


	/**
	 * @Description: 更新IK词典（词典不需要累加）
	 * @param: newDicPath:新词典文件的路径
	 * @return:
	 * @date: 2017-10-31  
	 */
	public static void updateIKDic(String newDicPath){
		String path = Thread.currentThread().getContextClassLoader().getResource("chinesewords.dic").getPath();
		String result = "";
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(newDicPath)),FileUtils.getFileEncode(newDicPath)));
			String temp = "";
			while((temp = br.readLine())!=null){
				result+=temp+ConstantParams.CHENG_LINE;
			}
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path)),"utf-8"));
			bw.write(result);
			bw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @Description: 调用更新后的IK重新分词
	 * @param: newDicPath 新文件的路径;inputRawPath 进行分词的原始数据;outputIkPath 新生成的分词数据文件夹
	 * @return:
	 * @date: 2017-10-31  
	 */
	public static void splitWordsByIK(String inputRawPath,String outputIkPath) {
		//调用IK词典二次分词
		BufferedReader br = null;
		String reString = "";
		String returnStr = "";  
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputRawPath)),FileUtils.getFileEncode(inputRawPath)));
			String temp = "";
			while((temp = br.readLine())!=null){
				reString+=temp;
			}
			IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(reString), true);//使用ik智能分词
			Lexeme lexeme;
			while ((lexeme = ikSegmenter.next()) != null) {
				returnStr += lexeme.getLexemeText()+ConstantParams.SINGLE_BLANK;
			}
			//写出结果
			StringUtils.string2File(returnStr, outputIkPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}



	/**
	 * @Description: 过滤TF词
	 * @param: inputPath 初始读入的TF词地址；outputPath 生成的.txt文件地址
	 * @return:
	 * @date: 2017-10-31  
	 */
	public static void fliterTFWordDic(String inputPath,String outputPath) {
		BufferedReader br = null;
		String result = "";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath)),FileUtils.getFileEncode(inputPath)));
			String temp = "";
			while((temp = br.readLine())!=null){
				String[] splitTemp = temp.split(ConstantParams.SINGLE_BLANK);
				if(!contained.contains(splitTemp[0])){
					contained.add(splitTemp[0]);
					result += splitTemp[0] + ConstantParams.SINGLE_BLANK + splitTemp[1] + ConstantParams.CHENG_LINE;
				}
			}
			StringUtils.string2FileTrue(result, outputPath);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public static void main(String args[]) throws UnsupportedEncodingException, FileNotFoundException{	
		String rawtxtInputPath = "G:/data/e430/hetritrix/output/sina/20/mirror/rawTxt";
		String splitTxtPath = "G:/data/e430/hetritrix/output/sina/20/mirror/splitWords";
		String ruleTxtPath = Thread.currentThread().getContextClassLoader().getResource("rules.txt").getPath();
		String wordsRuleSetsPath = "G:/data/e430/hetritrix/output/sina/20/mirror/wordsRuleSets";
		String filterwordsPath = "G:/data/e430/hetritrix/output/sina/20/mirror/filterwords";
		String delrepwordsPath = "G:/data/e430/hetritrix/output/sina/20/mirror/delrepwords";
		String integrationPath = "G:/data/e430/hetritrix/output/sina/20/mirror/integration";
		String resultWordsPath = "G:/data/e430/hetritrix/output/sina/20/mirror/resultWords/result.dic";
		String stablePath = "G:/data/e430/hetritrix/output/sina/20/mirror/stableWords";
		String filterDic = "G:/data/e430/hetritrix/output/sina/20/mirror/resultWords/resultFilter.dic";
		String delDic = "G:/data/e430/hetritrix/output/sina/20/mirror/resultWords/resultDel.dic";
		String fliterTF = "G:/data/e430/hetritrix/output/sina/20/mirror/resultWords/fliterTF.dic";
		String wordsTFPath = "G:/data/e430/hetritrix/output/sina/20/mirror/wordsTF";
		String splitIKPath = "G:/data/e430/hetritrix/output/sina/20/mirror/splitIK";
		String objectPath = "G:/data/e430/hetritrix/output/sina/20/mirror/resultWords/resultWords.dic";
		String evaluationPath = "G:/data/e430/hetritrix/output/sina/20/mirror/resultWords/evalution_del.txt";
		String sortObjectPath = "G:/data/e430/hetritrix/output/sina/20/mirror/resultWords/sortObject_result.txt";

		//		String rawtxtInputPath = "G:/data/e430/hetritrix/output/sina_News/1/mirror/rawTxt";
		//		String splitTxtPath = "G:/data/e430/hetritrix/output/sina_News/1/mirror/splitWords";
		//		String ruleTxtPath = Thread.currentThread().getContextClassLoader().getResource("rules.txt").getPath();
		//		String wordsRuleSetsPath = "G:/data/e430/hetritrix/output/sina_News/1/mirror/wordsRuleSets";
		//		String filterwordsPath = "G:/data/e430/hetritrix/output/sina_News/1/mirror/filterwords";
		//		String delrepwordsPath = "G:/data/e430/hetritrix/output/sina_News/1/mirror/delrepwords";
		//		String integrationPath = "G:/data/e430/hetritrix/output/sina_News/1/mirror/integration";
		//		String resultWordsPath = "G:/data/e430/hetritrix/output/sina_News/1/mirror/resultWords/result.dic";
		//		String stablePath = "G:/data/e430/hetritrix/output/sina_News/1/mirror/stableWords";
		//		String filterDic = "G:/data/e430/hetritrix/output/sina_News/1/mirror/resultWords/resultFilter.dic";
		//		String delDic = "G:/data/e430/hetritrix/output/sina_News/1/mirror/resultWords/resultDel.dic";
		//1 分词,词性标注
		StringUtils su = new StringUtils(rawtxtInputPath);
		for(String path:su.allPathResult){
			String news = StringUtils.getContent(path);
			String splitRes = SplitWordsUtils.ITCTILS(news);
			String fileName = StringUtils.getFileNameFromPath(path);
			StringUtils.string2File(splitRes, splitTxtPath+"/"+fileName+".txt");
		}	
		//2 加载rules 抽取文本中的候选集
		su = new StringUtils(splitTxtPath);
		List<String> splitTxtPaths = su.allPathResult;
		for(String path:su.allPathResult){
			String fileName = StringUtils.getFileNameFromPath(path);
			ExtractUtils.singleTextWordsSet(ruleTxtPath, path, wordsRuleSetsPath+"/"+fileName+".txt");
		}	
		//3 过滤特殊词（单子动词，特殊符号... ...）
		su = new StringUtils(wordsRuleSetsPath);
		for(String fileName : su.allPathResult){
			String f = StringUtils.getFileNameFromPath(fileName);
			ExtractUtils.filterWords(fileName, filterwordsPath+"/"+f+".txt");
		}
		//4 重复的候选词
		su = new StringUtils(filterwordsPath);
		List<String> filterwordsPaths = su.allPathResult;
		for(String fileName : su.allPathResult){
			String f = StringUtils.getFileNameFromPath(fileName);
			ExtractUtils.delRepWords(fileName, delrepwordsPath+"/"+f+".txt");
		}
		//5 生成完整性关键词
		su = new StringUtils(delrepwordsPath);
		List<String> delrepwordsPaths = su.allPathResult;
		for(int i=0;i<delrepwordsPaths.size();i++){
			String fileName = StringUtils.getFileNameFromPath(delrepwordsPaths.get(i));
			//6 生成完整性和稳定性过滤的词
			ExtractUtils.getObject(filterwordsPaths.get(i), resultWordsPath, ExtractUtils.filterInteWords(delrepwordsPaths.get(i),splitTxtPaths.get(i),integrationPath+"/"+fileName+".txt"),stablePath+"/"+fileName+".txt");
		}
		//7 生成IK词典
		genInteDic(resultWordsPath,filterDic);
		//8 去重
		ExtractUtils.delRepWords(filterDic,delDic);

		//9将去重后的词导入到IK词典中，并再次调用IK进行二次分词
		updateIKDic(delDic);//更新IK词典
		for(int i=0;i<new StringUtils(rawtxtInputPath).allPathResult.size();i++){
			String fileName = StringUtils.getFileNameFromPath(new StringUtils(rawtxtInputPath).allPathResult.get(i));
			splitWordsByIK(new StringUtils(rawtxtInputPath).allPathResult.get(i),splitIKPath+ConstantParams.SLASH+fileName+ConstantParams.TXTSUFFIX);	
		}

		//10 计算TF
		for (int i = 0; i < new StringUtils(stablePath).allPathResult.size(); i++) {
			String fileName = StringUtils.getFileNameFromPath(new StringUtils(stablePath).allPathResult.get(i));
			calTf(new StringUtils(stablePath).allPathResult.get(i),new StringUtils(splitIKPath).allPathResult.get(i),wordsTFPath+ConstantParams.SLASH+fileName+ConstantParams.TXTSUFFIX);
		}

		//11 去除TF重复词，生成过滤后的TF词典 
		for (int i = 0; i < new StringUtils(wordsTFPath).allPathResult.size(); i++) {
			fliterTFWordDic(new StringUtils(wordsTFPath).allPathResult.get(i),fliterTF);
		}


		//12 计算f-object
		List<String> words = StringUtils.getContentFromPath(fliterTF);
		double sum_object = 0;
		for (int i = 0; i < words.size(); i++) {
			String[] tempWords = words.get(i).split(ConstantParams.SINGLE_BLANK);
			//			String fileName = StringUtils.getFileNameFromPath(new StringUtils(stablePath).allPathResult.get(i));
			double tf = Double.parseDouble(tempWords[1]);
			double df = calDF(tempWords[0],splitIKPath,new StringUtils(splitIKPath).allPathResult.size());
			double object = calObject(tempWords[0]);
			double a_Object = tf*df*object;	
			sum_object+=a_Object;//object值之和
			noave_Object.put(tempWords[0], a_Object);
		}

		//13 计算每个词的平均object值
		Map<String, Double>preSortMap = new TreeMap<>();
		for (Entry<String, Double> object_entry : noave_Object.entrySet()) {
			String key = object_entry.getKey();
			Double value = object_entry.getValue()/sum_object;
			preSortMap.put(key, value);
		}

		//Map排序
		List<Map.Entry<String,Double>> sortList = StringUtils.sortMapByValue(preSortMap);
		String final_object = "";
		for(Map.Entry<String,Double> object_entry:sortList){ 
			final_object+=object_entry.getKey()+ConstantParams.SINGLE_BLANK+object_entry.getValue()+ConstantParams.CHENG_LINE;	
		} 

		//写出排序结果
		StringUtils.string2File(final_object, objectPath);

		//14 分别计算1和0的object中的最大值
		String evalution = StringUtils.getContent(evaluationPath);
		String[] evalutionSplit = evalution.split(ConstantParams.CHENG_LINE);
		Map<String,Double> ev0_word = new TreeMap<>();
		Map<String,Double> ev1_word = new TreeMap<>();
		//将object中的0和1分开
		for(int i=0;i<evalutionSplit.length;i++){
			String[] ev_word = evalutionSplit[i].split(ConstantParams.SINGLE_BLANK);
			if(ev_word[1].equals("1")){
				ev1_word.put(ev_word[0], 1.0);
			}else{
				ev0_word.put(ev_word[0], 0.0);
			}
		}
		//根据分开的0和1，在对概率的分布值进行处理，求出其中的0和1的最大值
		String content = StringUtils.getContent(objectPath);//f-object总排名
		String[] content_split = content.split(ConstantParams.CHENG_LINE);
		double ev0_max = 0, ev1_max = 0;
		for(int i=0;i<content_split.length;i++){
			String[] word = content_split[i].split(ConstantParams.SINGLE_BLANK);
			//计算出1和0  f-object的最大值
			/*
			 * 求0部分 f-object的分布范围，即为其置信区间
			 * */
			if(ev0_word.containsKey(word[0])){			
				if(ev0_max<Double.valueOf(word[1])){
					ev0_max = Double.valueOf(word[1]);
				}	
			}else if(ev1_word.containsKey(word[0])){
				if(ev1_max<Double.valueOf(word[1])){
					ev1_max = Double.valueOf(word[1]);
				}
			}
		}

		//		Entry<String, Double> ev0_List = StringUtils.getMapMaxValue(ev0_word);
		//		Entry<String, Double> ev1_List = StringUtils.getMapMaxValue(ev1_word);

		//		double ev0_max = ev0_List.getValue();	
		//		double ev1_max = ev1_List.getValue();

		double K = (ev1_max-ev0_max)/(1-0);

		change0_1(K,1,ev0_word,objectPath,sortObjectPath);
	}



	/**
	 * @Description: 根据K值，求平行线另一点坐标
	 * @param: K:斜率;X:X坐标值;ev0_word 评价值为0的词
	 * @return:
	 * @date: 2017-11-1  
	 */
	public static void change0_1(double K,double X,Map<String,Double> ev0_word,String objectPath,String outputPath){
		Map<String,Double> change_ev1 = new TreeMap<>();
		//读取objectPath中的频率值
		String content = StringUtils.getContent(objectPath);
		String[] content_split = content.split(ConstantParams.CHENG_LINE);
		for(int i=0;i<content_split.length;i++){
			String[] word = content_split[i].split(ConstantParams.SINGLE_BLANK);
			String key = word[0];
			if(ev0_word.containsKey(key)){
				double Y0 = Double.valueOf(word[1]);
				double Y =  K*(X-0)-Y0;
				change_ev1.put(key, Y);	
			}
		}

		List<Entry<String,Double>> sortMapByValue = StringUtils.sortMapByValue(change_ev1);	
		String fin_result = "";
		for(Map.Entry<String, Double> entry : sortMapByValue){
			fin_result+=entry.getKey() + ConstantParams.SINGLE_BLANK + entry.getValue() + ConstantParams.CHENG_LINE;
		}
		StringUtils.string2File(fin_result, outputPath);
	}



}
