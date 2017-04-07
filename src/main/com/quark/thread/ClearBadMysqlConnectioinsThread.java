package com.quark.thread;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jolbox.bonecp.BoneCPDataSource;
import com.quark.common.config;

public class ClearBadMysqlConnectioinsThread implements Runnable{

	@Override
	public void run() {
		while(true){
			//清除一次连接
			try {
				Thread.sleep(1000*5);
			    try {
					Connection connection = config.boneCpPlugin.getDataSource().getConnection();
					Statement statement = connection.createStatement();
					ResultSet rs = statement.executeQuery("SHOW full PROCESSLIST");
					while (rs.next()) {
						int id = rs.getInt("Id");
						int sleep_time = rs.getInt("Time");
						if(sleep_time > 120){
							//死链接
							Statement statement_kill = connection.createStatement();
							statement_kill.execute("KILL "+id);
							statement_kill.close();
							System.out.println("KILL MYSQL BAD CONNECTIONS:[Id="+id+",Time="+sleep_time+"]");
						}
					}
				rs.close();
				statement.close();
				connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
