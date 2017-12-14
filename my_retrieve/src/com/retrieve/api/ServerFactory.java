package com.retrieve.api;

import java.util.Map;

import com.retrieve.config.ConstantParams;
import com.retrieve.util.StringUtils;

public class ServerFactory {

	/**
	 * @Description: 启动服务的工厂类
	 * @return:
	 * @date: 2017-8-30  
	 */
	public FullTextService beginService(Map<String,String> configParams){
		FullTextService fullTextService = null;
		try {
			String serverName = configParams.get("serverName");
			String type = configParams.get("type");
			String className = configParams.get("className");
			Class<?> c = Class.forName(className);
			if("solr".equals(type)){
				String url = configParams.get("url");			
				if(StringUtils.isEmpty(url)){
					url = StringUtils.getConfigParam(ConstantParams.SOLR_URL, "", ConstantParams.SEARCH_CONFIG);
				}
				fullTextService = (FullTextService)c.newInstance();
				fullTextService.beginService(serverName,url);
			}
			if("lucene".equals(type)){
				String flag = configParams.get("flag");
				String indexPath = configParams.get("indexPath");
				fullTextService = (FullTextService)c.newInstance();
				if(StringUtils.isEmpty(indexPath)){
					fullTextService.beginService(serverName, flag);
				}else{
					fullTextService.beginService(serverName, flag, indexPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fullTextService;
	}
}
