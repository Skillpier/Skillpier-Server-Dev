/**
 * 
 */
package com.quark.api.uitls;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.jfinal.plugin.activerecord.DbKit;
import com.quark.api.annotation.Type;
import com.quark.api.bean.ApiDescription;
import com.quark.api.bean.FormBean;
import com.quark.api.bean.Input;
import com.quark.api.bean.InputType;
import com.quark.api.bean.InputValue;
import com.quark.api.bean.Params;
import com.quark.api.controller.BuildAPI;

import freemarker.core._RegexBuiltins.replace_reBI;

/**
 * @author kingsley
 *
 * @datetime 2014年12月5日 上午12:27:34
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class RequestFormBuilder {

	public static String[] getColumName(String sql) {
		return sql.split(" ")[1].split(",");
	}

	public static List<InputValue> buildInputFromDB(Connection connection,
			String sql) {
		ArrayList<InputValue> inputValues = new ArrayList<InputValue>();
		try {
			System.out.println("executeSql:"+sql);
			PreparedStatement pst = connection.prepareStatement(sql);
			DbKit.dialect.fillStatement(pst);
			ResultSet rs = pst.executeQuery();
			String[] names = getColumName(sql);
			while (rs.next()) {
				InputValue inputValue = new InputValue();
				inputValue.setValue(rs.getObject(1).toString());
				if (names.length >= 2) {
					String info = "";
					for (int i = 2; i < names.length + 1; i++) {
						info = info + "," + rs.getObject(i);
					}
					inputValue.setInfo(info);
				}
				inputValues.add(inputValue);
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return inputValues;
	}

	/**
	 * {、} -单选样式 {sql} -数据源从数据库映射
	 * 
	 * [、] -多选样式 [sql] -数据源从数据库映射
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static FormBean buildForm(ApiDescription bean) {

		FormBean form = new FormBean();
		form.setAction(bean.getUrl());
		List<Input> inputs = new ArrayList<Input>();
		StringBuffer sb = new StringBuffer();
		for (Params p : bean.getParams()) {
			String type = p.getType();
			if (Type.File.equalsIgnoreCase(type)) {
				form.setMultipart(1);
				form.setMethod("post");
			}
			String defaultValue = p.getDefaultValue();
			String explain = p.getExplaination();
			String name = p.getName();
			Input input = new Input();
			input.setExplain(explain);
			input.setPrototype(type);
			if (name.contains("columnName=")) {
				String[] string = name.split(",");
				name = string[0].replace("columnName=", "");
			}
			input.setName(name);
			if(type.contains("_NotRequired")){
				input.setRequire(false);
			}
			if (type.equalsIgnoreCase(Type.Int) || type.equalsIgnoreCase(Type.Int_NotRequired)
					|| type.equalsIgnoreCase(Type.String) || type.equalsIgnoreCase(Type.String_NotRequired)) {
				if (defaultValue.startsWith("[") && defaultValue.endsWith("]")) {
					if (defaultValue.startsWith("[select")) {
						// 值从数据库取出
						try {
							String sql = defaultValue.replace("[", "").replace(
									"]", "");
							input.setType(InputType.SELECT);
							List<InputValue> inputValues = buildInputFromDB(
									DbKit.getConnection(), sql);
							input.setValues(inputValues);
							inputs.add(input);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						// [] - 多选样式
						String[] value = defaultValue.replace("[", "")
								.replace("]", "").split("、");
						// [] - 多选样式
						List<InputValue> inputValues = new ArrayList<InputValue>();
						for (String v : value) {
							InputValue inputValue = new InputValue();
							inputValue.setValue(v);
							inputValues.add(inputValue);
						}
						input.setType(InputType.SELECT);
						input.setValues(inputValues);
						inputs.add(input);
					}
				} else if (defaultValue.startsWith("{")
						&& defaultValue.endsWith("}")) {
					if (defaultValue.startsWith("{select")) {
						// 值从数据库取出
						try {
							String sql = defaultValue.replace("{", "").replace(
									"}", "");
							input.setType(InputType.RADIO);
							List<InputValue> inputValues = buildInputFromDB(
									DbKit.getConnection(), sql);
							input.setValues(inputValues);
							inputs.add(input);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						// {} - 单选样式
						String[] value = defaultValue.replace("{", "")
								.replace("}", "").split("、");
						sb.append("<tr>\n");
						List<InputValue> inputValues = new ArrayList<InputValue>();
						for (String v : value) {
							InputValue inputValue = new InputValue();
							inputValue.setValue(v);
							inputValues.add(inputValue);
						}
						input.setType(InputType.RADIO);
						input.setValues(inputValues);
						inputs.add(input);
					}
				} else {
					// input text
					final InputValue value = new InputValue();
					value.setValue(defaultValue);
					input.setType(InputType.TEXT);
					input.setValues(new ArrayList<InputValue>() {
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						{
							add(value);
						}
					});
					inputs.add(input);

				}
			} else if (type.equalsIgnoreCase(Type.File) || type.equalsIgnoreCase(Type.File_NotRequired)) {
				// file
				input.setType(InputType.FILE);
				inputs.add(input);

			} else if (type.equalsIgnoreCase(Type.Date) || type.equalsIgnoreCase(Type.Date_NotRequired)) {
				final InputValue value = new InputValue();
				value.setValue(defaultValue);
				input.setType(InputType.DATE);
				input.setValues(new ArrayList<InputValue>() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					{
						add(value);
					}
				});
				inputs.add(input);

			}
		}
		form.setInputs(inputs);
		return form;
	}

	public static void main(String[] args) {
		List<ApiDescription> list = BuildAPI.getApis();
		for (ApiDescription apiBean : list) {
			List<Params> params = apiBean.getParams();
		}

	}
}
