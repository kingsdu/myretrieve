package com.retrieve.domain;

import java.util.ArrayList;

import org.jsoup.nodes.Element;

public class CountWebInfo {

	public int textCount = 0;
	public int linkTextCount = 0;
	public int tagCount = 0;
	public int linkTagCount = 0;
	public double density = 0;
	public double densitySum = 0;
	public double score = 0;
	public int pCount = 0;
	public int punctuation = 0;//标点符号的个数
	public int strongCount = 0;//strong标签
	public int imageCount = 0;//image个数
	public Element tag = null;
	
	public ArrayList<Integer> leafList = new ArrayList<Integer>();

}
