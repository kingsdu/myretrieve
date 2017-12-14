package com.retrieve.util;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HtmlUtils {

	
	public String getSingleTxt(String htmlContent, String outputPath){
		String title;
		String time;
		String content;
		String source;
		String finalResult;
		title = null;
		time = null;
		content = null;
		source = null;
		finalResult = null;
		try {
			Parser parser = Parser.createParser(htmlContent, "gb2312");
			NodeFilter title_Filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "sj-title"));
			NodeList title_nodes = parser.parse(title_Filter);
			if (title_nodes.size() != 0)
			{
				NodeList title_child = title_nodes.elementAt(0).getChildren();
				if (title_child.size() != 0)
				{
					NodeFilter h2_filter = new TagNameFilter("h2");
					NodeList title_h = title_child.extractAllNodesThatMatch(h2_filter);
					if (title_h.size() != 0)
						title = title_h.elementAt(0).toPlainTextString();
				}
			}
			parser.reset();
			NodeFilter time_Filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "sj-time"));
			NodeList time_nodes = parser.parse(time_Filter);
			if (time_nodes.size() != 0)
			{
				NodeList time_child = time_nodes.elementAt(0).getChildren();
				if (time_child.size() != 0)
				{
					NodeFilter sjn_filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "sj-n"));
					NodeList source_h = time_child.extractAllNodesThatMatch(sjn_filter);
					if (source_h.size() != 0)
						source = source_h.elementAt(0).toPlainTextString();
					NodeFilter sjt_filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "sj-t"));
					NodeList sjt_time = time_child.extractAllNodesThatMatch(sjt_filter);
					if (sjt_time.size() != 0)
						time = sjt_time.elementAt(0).toPlainTextString();
				}
			}
			parser.reset();
			NodeFilter content_Filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "as04"));
			NodeList content_nodes = parser.parse(content_Filter);
			if (content_nodes.size() != 0)
				content = content_nodes.elementAt(0).toPlainTextString();
			finalResult = (new StringBuilder(String.valueOf(title))).append("\r\n").append(source).append("\r\n").append(time).append("\r\n").append(content).append("\r\n").toString();
			return finalResult;
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

}
