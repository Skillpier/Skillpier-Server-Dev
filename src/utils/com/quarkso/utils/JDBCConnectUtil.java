package com.quarkso.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCConnectUtil {
	public static Connection getConnect() {
		String url = "jdbc:mysql://114.215.102.155:3307/laizhedai?useUnicode=true&characterEncoding=utf8";
		String username = "root";
		String password = "root";
		Connection con = null;
		try {
			// 加载MySql的驱动类
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驱动程序类 ，加载驱动失败！");
			e.printStackTrace();
		}catch (SQLException se) {
			System.out.println("数据库连接失败！");
			se.printStackTrace();
		}
		return con;
	}
	
	public static void main(String[] args) {
		List<Integer> ints = new ArrayList<Integer>();
		List<String> strigss = new ArrayList<String>();
		Connection con = getConnect();
		 try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from touzilicai");
			while(rs.next()){
				int id = rs.getInt("Id");
				String qitou = rs.getString("nianhuashouyi");
				System.out.println(qitou);
				rs.updateFloat("nianhuashouyiint", Float.parseFloat(rs.getString("nianhuashouyi")));
				stmt.executeUpdate("update touzilicai set nianhuashouyiint="+Float.parseFloat(rs.getString("nianhuashouyi")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		 try {
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		 
	}

}
