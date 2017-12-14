package com.retrieve.util;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jws.soap.InitParam;

import com.retrieve.config.ConstantParams;

public class FileUtils {
	/**
	 * @Description: 获取最大的文件夹编号
	 * @return:
	 * @date: 2017-9-17  
	 */
	public static String getMaxFiles(String sFilepath){
		File Rootfile = new File(sFilepath);
		File[] files = Rootfile.listFiles();
		if(files.length<=1){
			return ConstantParams.FIRST;
		}
		Long MaxName = Long.valueOf(files[0].getName());
		for (int i = 1; i < files.length; i++) {
			if (MaxName < Long.valueOf(files[i].getName())) {
				MaxName = Long.valueOf(files[i].getName());
			}
		}
		return String.valueOf(MaxName);
	}

	//	public static String getMaxFiles(String sFilepath){
	//		File Rootfile = new File(sFilepath);
	//		File[] files = Rootfile.listFiles();
	//		ArrayList<Long> FileNameList = new ArrayList<Long>();
	//		int iFilesLen = files.length;
	//		for (int i = 0; i < iFilesLen; i++) {
	//			FileNameList.add(Long.valueOf(files[i].getName()));
	//		}
	//		Long MaxName = FileNameList.get(0);
	//		int iFileNameListSize = FileNameList.size();
	//		for (int i = 1; i < iFileNameListSize; i++) {
	//			if (MaxName < FileNameList.get(i)) {
	//				MaxName = FileNameList.get(i);
	//			}
	//		}
	//		return String.valueOf(MaxName);
	//	}



	/**
	 * @Description: 创建初始的文件夹
	 * @return:
	 * @date: 2017-9-20  
	 */
	public static void createFirstFile(String path){
		(new File(path)).mkdirs();
		//读取项目根目录下的默认配置文件
		String orderPath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
		orderPath+= "profiles"+ File.separator + "default";
		File[] file = (new File(orderPath)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].getName().equalsIgnoreCase("order.xml")|| 
					file[i].getName().equalsIgnoreCase("seeds.txt")||
					file[i].getName().equalsIgnoreCase("recover.gz")||
					file[i].getName().equalsIgnoreCase("recoverAll.txt")) {
				copyFile(file[i],new File(path + File.separator + file[i].getName()));
			}
		}
	}


	/**
	 * @Description: 复制order和seeds文件
	 * @return:
	 * @date: 2017-9-21  
	 */
	public static void copySameFiles(String inPath,String outPath){
		File[] file = (new File(inPath)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].getName().equalsIgnoreCase("order.xml")|| 
					file[i].getName().equalsIgnoreCase("seeds.txt")||
					file[i].getName().equalsIgnoreCase("recover.gz")||
					file[i].getName().equalsIgnoreCase("recoverAll.txt")) {
				copyFile(file[i],new File(outPath + File.separator + file[i].getName()));
			}
		}
	}


	/**
	 * @Description: 判断是否含有order和seeds文件
	 * @return:
	 * @date: 2017-9-21  
	 */
	public static boolean judgeOrderOrSeeds(String inPath){
		int count = 0;
		boolean flag = true;
		File[] file = (new File(inPath)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (flag && file[i].getName().equalsIgnoreCase("order.xml")){
				count++;
				flag = false;
			}
		}
		flag = true;
		for(int i = 0; i < file.length; i++){
			if(flag && file[i].getName().equalsIgnoreCase("seeds.txt")){
				count++;
				flag = false;
			}
		}
		flag = true;
		for(int i = 0; i < file.length; i++){
			if(flag && file[i].getName().equalsIgnoreCase("recover.gz")){
				count++;
				flag = false;
			}
		}
		flag = true;
		for(int i = 0; i < file.length; i++){
			if(flag && file[i].getName().equalsIgnoreCase("recoverAll.txt")){
				count++;
				flag = false;
			}
		}
		if(count == 4){
			return true;
		}
		return false;
	}




	/**
	 * @Description: 复制文件
	 * @return:
	 * @date: 2017-9-20  
	 */
	public static void copyFile(File fileIn,File fileOut){
		int length=2097152;
		try {
			FileInputStream in= new FileInputStream(fileIn);
			FileOutputStream out= new FileOutputStream(fileOut);

			FileChannel inC=in.getChannel();
			FileChannel outC=out.getChannel();
			ByteBuffer b=null;
			while(true){
				if(inC.position()==inC.size()){
					inC.close();
					outC.close();
				}else{
					if((inC.size()-inC.position())<length){
						length=(int)(inC.size()-inC.position());
					}else
						length=2097152;
					b=ByteBuffer.allocateDirect(length);
					inC.read(b);
					b.flip();
					outC.write(b);
					outC.force(false);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	/**
	 * @Description: 写入字符串至文件
	 * @return:
	 * @date: 2017-9-21  
	 */
	public static void writeStr(String filePath,String url) {
		BufferedWriter bw = null;
		try {
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			bw.append(url);
			bw.newLine();//换行
			bw.flush();//需要及时清
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



	/**
	 * 递归获取文件夹下所有的文件绝对路径
	 * @param inputPath
	 * @return
	 */
	public static List<String> getAllPath(String inputPath){
		List<String> allPathResult = new ArrayList<String>();
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
			return allPathResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 从文件的完整路径中，抽取其文件名称
	 * @param path
	 * @return
	 */
	public static String getFileNameFromPath(String path){
		String result = "";
		if(path==null){
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
	 * 利用第三方开源包cpdetector获取文件编码格式 
	 *  
	 * @param path 
	 *            要判断文件编码格式的源文件的路径 
	 * @author huanglei 
	 * @version 2012-7-12 14:05 
	 */  
	public static String getFileEncode(String path) {  
	    /* 
	     * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。 
	     * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、 
	     * JChardetFacade、ASCIIDetector、UnicodeDetector。 
	     * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的 
	     * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar 
	     * cpDetector是基于统计学原理的，不保证完全正确。 
	     */  
	    CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();  
	    /* 
	     * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于 
	     * 指示是否显示探测过程的详细信息，为false不显示。 
	     */  
	    detector.add(new ParsingDetector(false));  
	    /* 
	     * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码 
	     * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以 
	     * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。 
	     */  
	    detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar  
	    // ASCIIDetector用于ASCII编码测定  
	    detector.add(ASCIIDetector.getInstance());  
	    // UnicodeDetector用于Unicode家族编码的测定  
	    detector.add(UnicodeDetector.getInstance());  
	    java.nio.charset.Charset charset = null;  
	    File f = new File(path);  
	    try {  
	        charset = detector.detectCodepage(f.toURI().toURL());  
	    } catch (Exception ex) {  
	        ex.printStackTrace();  
	    }  
	    if (charset != null)  
	        return charset.name();  
	    else  
	        return null;  
	}  
	
	
	/**
	 * @Description: 读取整个文件
	 * @return:
	 * @date: 2017-9-30  
	 */
	public static StringBuilder readHtml(String path){
		File file = new File(path);
		BufferedReader br = null;
		String line = null;
		StringBuilder sb = null;
		String charset = getFileEncode(path);//获取字符编码格式
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
			sb = new StringBuilder();
			while((line = br.readLine())!=null){
				sb.append(line);
				sb.append("\r\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return sb;  
	}

	
	/**
	 * @Description: 根据路径生成文件夹
	 * @return:
	 * @date: 2017-10-21  
	 */
	public static void genDirs(String path){
		File file = new File(path);
		if(!file.isDirectory()){
			file.mkdirs();
		}
	}
	
	

	
	
	
	

//	public static void main(String[] args){
//		String path = "E:\\test\\hetritrix\\output\\sina\\15";
//		String inputPath = path + File.separator + "mirror";
//		StringUtils su = new StringUtils(inputPath);
//		List<String> allPath = su.allPathResult;
//		for(String p : allPath){
//			String html = FileUtils.readHtml(p);	
//			System.out.println(html);
//		}
//	}







}
