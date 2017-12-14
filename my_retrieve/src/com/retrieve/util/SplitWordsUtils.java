package com.retrieve.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import ICTCLAS.I3S.AC.ITCTCLAS;

import com.retrieve.config.ConstantParams;

public class SplitWordsUtils {

	public static String ikSplit(String str){
		return ikSplit(str,ConstantParams.BLANK);
	}
	public static String ikSplit(String str,String mark){
		String result = "";
		if(StringUtils.isEmpty(str)){
			return result;
		}
		try {
			byte[] bt = str.getBytes();
			InputStream ip = new ByteArrayInputStream(bt);
			Reader read = new InputStreamReader(ip);
			IKSegmenter iks = new IKSegmenter(read,true);
			Lexeme t;
			while((t=iks.next()) != null){
				result += t.getLexemeText()+mark;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param str
	 * @param mark
	 * @param b 若b=true是智能分词；b=false是细粒度分词
	 * @return
	 */
	public static String ikSplit(String str,String mark,boolean b){
		String result = "";
		if(StringUtils.isEmpty(str)){
			return result;
		}
		try {
			byte[] bt = str.getBytes();
			InputStream ip = new ByteArrayInputStream(bt);
			Reader read = new InputStreamReader(ip);
			IKSegmenter iks = new IKSegmenter(read,b);
			Lexeme t;
			while((t=iks.next()) != null){
				result += t.getLexemeText()+mark;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String ikSplit(String str,boolean b){
		return ikSplit(str,ConstantParams.BLANK,b);
	}

	public static String ITCTILS(String str){
		return ITSplit(str);
	}


	private static String ITSplit(String sInput){      
		return ITCTCLAS.ICTCLAS_ParagraphProcess(sInput);
	}

}
