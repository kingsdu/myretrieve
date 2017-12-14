package com.retrieve.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class XMLHandleUtils {

	public static String getXML(String path,String key){		
		File xmlFile = new File(path);		
		String result = null;
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);

			//利用XPath查找所有节点中，属性名 id 值为参数中id值的节点元素
			Element currElement = (Element)doc.selectSingleNode("//*[@id='"+key+"']");
			result = currElement.attributeValue("name");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static boolean updateXML(String path,String key,String value){
		File xmlFile = new File(path);
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);

			//利用XPath查找所有节点中，属性名 id 值为参数中id值的节点元素
			Element currElement = (Element)doc.selectSingleNode("//*[@id='"+key+"']");	
			Attribute urlAttr = currElement.attribute("name");	
			urlAttr.setValue(value);

			OutputFormat format = OutputFormat.createPrettyPrint();

			//指定XML字符集编码，防止乱码
			format.setEncoding("UTF-8");

			//将Document中的内容写入文件中
			XMLWriter writer=new XMLWriter(new FileOutputStream(xmlFile),format); 
			writer.write(doc);  
			writer.close();  
			return true;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	
	/**
	 * @Description: 根据name修改xml属性
	 * @param: path xml路径   name 节点名称    value 要写入的值
	 * @date: 2017-9-27  
	 */
	public static Boolean updataXMLByName(String path,String name,String value){
		File xmlFile = new File(path);
		
		SAXReader reader = new SAXReader();
		reader.setEncoding("UTF-8");
		try {
			Document doc = reader.read(xmlFile);
			// 找到设置时间的xml位置
			List listModTime = doc.selectNodes("//controller/string/@name");
			Iterator iterModTime = listModTime.iterator();
			while (iterModTime.hasNext()) {
				Attribute attribute = (Attribute) iterModTime.next();
				if (attribute.getValue().equals(name)) {
					Element element = (Element) attribute.getParent();
					element.setText(value);
				}
			} 
			OutputFormat format = OutputFormat.createPrettyPrint();
			//指定XML字符集编码，防止乱码
			format.setEncoding("UTF-8");
			//将Document中的内容写入文件中
			XMLWriter writer=new XMLWriter(new FileOutputStream(xmlFile),format); 
			writer.write(doc);  
			writer.close();  
			return true;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	
	

}
