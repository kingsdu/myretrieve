package com.retrieve.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.retrieve.config.ConstantParams;

public class DataBaseUtils {
	private static String URL;
	private static String USER;
	private static String PASSWORD;
	
	static{
		URL = StringUtils.getConfigParam(ConstantParams.DATABASE_URL, "", ConstantParams.SEARCH_CONFIG);
		USER = StringUtils.getConfigParam(ConstantParams.DATABASE_USER, "", ConstantParams.SEARCH_CONFIG);
		PASSWORD = StringUtils.getConfigParam(ConstantParams.DATABASE_PASSWORD, "", ConstantParams.SEARCH_CONFIG);
	}

	public static Connection getConnection(){
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static void closeConnection(Connection conn,PreparedStatement pstmt,Statement stmt,ResultSet rs){
		try {
			if(rs != null){
				rs.close();
			}
			if(stmt != null){
				stmt.close();
			}
			if(pstmt != null){
				pstmt.close();
			}
			if(conn != null){
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
