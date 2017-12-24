package com.retrieve.test;

import com.retrieve.util.SplitWordsUtils;
import com.retrieve.util.StringUtils;

public class SplitTest {

	public static void main(String[] args) {
		SplitByITCTLS();
	}


	/**
	 * @Description: ITCTLS分词效果测试
	 * @param:
	 * @return:
	 * @Author: Du
	 * @Date: 2017/12/21 10:52
	 */
	private static void SplitByITCTLS() {
		String content = StringUtils.getContent("D:\\testpackage\\hanLPTest\\content_1273583.txt");
		String splitRes = SplitWordsUtils.ITCTILS(content);
		StringUtils.string2FileTrue(splitRes,"D:\\testpackage\\hanLPTest\\splitITCTLS.txt");
	}
}
