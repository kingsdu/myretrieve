package com.retrieve.spi;

import com.retrieve.api.FullTextIndexParams;
import com.retrieve.api.FullTextResult;
import com.retrieve.api.FullTextSearchParams;
import com.retrieve.api.FullTextService;

public class FullTextServiceImpl  implements FullTextService {

	@Override
	public int beginService(String serverName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int endService(String serverName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doIndex(FullTextIndexParams fullTextIndexParams) {
		// TODO Auto-generated method stub

	}

	@Override
	public FullTextResult doQuery(FullTextSearchParams fullTextSearchParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int beginService(String serverName, String url) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void preIndexMethod() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterIndexMethod() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateIndex(FullTextIndexParams fullTextIndexParams) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preUpdateIndexMethod() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdateIndexMethod() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteIndex(FullTextIndexParams fullTextIndexParams) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preDeleteIndexMethod() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterDeleteIndexMethod() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setServerName(String serverName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int beginService(String serverName,String flag,String indexPath) {
		// 	//flag: 0:IndexWriter 1:IndexSearcher
		return 0;
	}
}
