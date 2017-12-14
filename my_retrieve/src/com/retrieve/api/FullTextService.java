package com.retrieve.api;


public interface FullTextService {

	//启动服务
	public int beginService(String serverName);

	//启动服务，带启动地址
	public int beginService(String serverName,String url);

	//flag: 0:IndexWriter 1:IndexSearcher
	public int beginService(String serverName,String flag,String indexPath);

    //设置服务name
	public void setServerName(String serverName);

	//关闭服务
	public int endService(String serverName);

	//索引
	public void doIndex(FullTextIndexParams fullTextIndexParams);

	//索引之前要做的事情
	public void preIndexMethod();

	//索引之后要做的事情
	public void afterIndexMethod();

	//修改索引
	public void updateIndex(FullTextIndexParams fullTextIndexParams);

	//修改之前要做的事情
	public void preUpdateIndexMethod();

	//修改之后要做的事情
	public void afterUpdateIndexMethod();

	//删除索引
	public void deleteIndex(FullTextIndexParams fullTextIndexParams);

	//删除之前要做的事情
	public void preDeleteIndexMethod();

	//删除之后要做的事情
	public void afterDeleteIndexMethod();

	//搜索
	public FullTextResult doQuery(FullTextSearchParams fullTextSearchParams);
}
