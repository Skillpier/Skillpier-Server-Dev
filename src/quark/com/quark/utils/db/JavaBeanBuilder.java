package com.quark.utils.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.Model;
import com.quark.api.annotation.Type;
import com.quark.utils.DateUtils;

import java.sql.Connection;

public class JavaBeanBuilder {

	/**
	 * 获得表或视图中的所有列信息
	 */
	public static List<String[]> getTableColumns(DatabaseMetaData dbMetaData,
			String schemaName, String tableName) {
		List<String[]> list = new ArrayList<String[]>();
		try {
			ResultSet rs = dbMetaData.getColumns(null, schemaName, tableName,
					"%");
			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");// 列名
				String remarks = rs.getString("REMARKS");// 列描述
				String dataTypeName = rs.getString("TYPE_NAME");// java.sql.Types类型
                String columnDef = rs.getString("COLUMN_DEF");//默认值  
				String[] meta = new String[4];
				meta[0] = columnName;
				meta[1] = dataTypeName;
				meta[2] = remarks;
				meta[3] = columnDef;
				list.add(meta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获得该用户下面的所有表
	 */
	public static List<String[]> getAllTableList(DatabaseMetaData dbMetaData,
			String schemaName) {
		List<String[]> list = new ArrayList<String[]>();
		try {
			// table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE",
			// "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
			String[] types = { "TABLE", "VIEW" };
			ResultSet rs = dbMetaData.getTables(null, schemaName, "%", types);
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME"); // 表名
				String remarks = rs.getString("REMARKS"); // 表备注
				String[] meta = new String[2];
				meta[0] = tableName;
				meta[1] = remarks;
				list.add(meta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args) throws SQLException, IOException {
		String path = System.getProperty("user.dir")+"/src/main/com/quark/model/extend/";
		String schemaName = "so";
		ActiveRecordModel model = new ActiveRecordModel(new JdbcConfig());
		model.start();
		DataSource ds = model.arp.dataSource;
		Connection conn = ds.getConnection();
		DatabaseMetaData dbMetaData = conn.getMetaData();
		
		List<String[]> list = getAllTableList(dbMetaData, schemaName);
		for (String[] meta : list) {
			StringBuffer sb = new StringBuffer();
			String bean_name = meta[0];
			String tmp_bean_name = "";
			if(bean_name.contains("_")){
				//视图
				String [] tmp = bean_name.split("_");
				for (String string : tmp) {
					tmp_bean_name = tmp_bean_name + string.substring(0, 1).toUpperCase() + string.substring(1);
				}
			}else{
				tmp_bean_name = bean_name.substring(0, 1).toUpperCase() + bean_name.substring(1);
			}
			bean_name = tmp_bean_name;
			File file = new File(path+bean_name+".java");
			if(file.exists()){
				file.delete();
			}
			System.out.println(file.getAbsolutePath());
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			String tableName = meta[0]; // 表名
			String remarks = meta[1]; // 表备注
			System.out.println("tablename:"+tableName);
			List<String[]> columns = getTableColumns(dbMetaData, schemaName, tableName);
			//生成bean
            sb.append("package com.quark.model.extend;\n");
            sb.append("import com.jfinal.plugin.activerecord.Model;\n\n");
            /**
             * @author kingsley
             * 
             * @info 返回值类型
             *
             * @datetime 2014年12月3日 下午8:00:40
             */
            sb.append("/**\n");
            sb.append("* @author cluo\n");
            sb.append("* \n");
            sb.append("* @info "+remarks+"\n");
            sb.append("*\n");
            sb.append("* @datetime"+DateUtils.getCurrentDateTime()+"\n");
            sb.append("*/\n");
            //注解
			sb.append("public class "+bean_name+" extends Model<"+bean_name+">{\n\n");
			sb.append("    public static "+bean_name+" dao = new "+bean_name+"();\n\n");
			for (String[] colMeta : columns) {
				String columnName = colMeta[0];// 列名
				String colremarks = colMeta[2];// 列描述
				String dataTypeName = colMeta[1];// java.sql.Types类型
				String defaultValue = colMeta[3];//默认值
				if(dataTypeName.equals("INT")){
					dataTypeName = Type.Int;
				}else if(dataTypeName.equals("VARCHAR")){
					dataTypeName = Type.String;
				}else if(dataTypeName.equals("LONG")){
					dataTypeName = Type.Long;
				}else{
					dataTypeName = Type.String;
				}
				sb.append("    public static final String "+columnName+"=\"columnName="+columnName+",remarks="+colremarks+",dataType="+dataTypeName+",defaultValue="+defaultValue+"\";\n\n");
			}
			sb.append("}\n");
			raf.write(sb.toString().getBytes());
			raf.close();
			System.out.println(sb.toString());
		}
	}
}
