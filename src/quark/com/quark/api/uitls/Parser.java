/**
 * 
 */
package com.quark.api.uitls;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.quark.api.annotation.ReturnDBParam;
import com.quark.api.annotation.ReturnDBParams;
import com.quark.api.annotation.ReturnOutlet;
import com.quark.api.annotation.ReturnOutlets;
import com.quark.api.bean.ApiDescription;
import com.quark.api.bean.Bean;
import com.quark.api.bean.BeanTree;
import com.quark.api.bean.Property;
import com.quark.api.bean.Returns;
import com.quark.utils.PackageUtils;
import com.quark.utils.StringUtils;

/**
 * @author kingsley
 *
 * @datetime 2014年12月5日 上午10:41:49
 */
public class Parser {

	private BeanTree tree = new BeanTree();

	/**
	 * 向BeanTree中插入bean
	 * 
	 * @param rootObj
	 * @param type
	 * @param comment
	 */
	public void insertBean(String rootObj, String nodeObj, String type,
			String comment) {
		List<Bean> beans = tree.getBeans();
		if (beans == null) {
			beans = new ArrayList<Bean>();
			tree.setBeans(beans);
		}
		Bean bean = null;
		for (Bean b : beans) {
			if (b.getClassName().equalsIgnoreCase(rootObj)) {
				bean = b;
				break;
			}
		}
		if (bean == null) {
			bean = new Bean();
			bean.setClassName(rootObj);
			beans.add(bean);
		}
		List<Property> properties = bean.getProperties();
		Property p = new Property(nodeObj, type, comment);
		if (properties == null) {
			properties = new ArrayList<Property>();
			bean.setProperties(properties);

		}
		boolean found = false;
		for (Property property : properties) {
			if (property.getName().equalsIgnoreCase(nodeObj)) {
				found = true;
			}
		}
		if (!found) {
			properties.add(p);
		}
	}

	public List<Bean> token(List<Returns> words) {
		for (int i = 0; i < words.size(); i++) {
			String type = words.get(i).getType();
			String name = words.get(i).getName();
			String comment = words.get(i).getExplaination();
			String rootObj = name.substring(0, name.indexOf("{"));
			String nodeObj = name.substring(1 + name.indexOf("{"),
					name.length() - 1);
			// 只有一层结构：TestResponse2{order_time}.etc
			if (!words.get(i).getName().contains(":")) {
				insertBean(rootObj, nodeObj, type, comment);
			} else {
				String[] tmp_map = nodeObj.split(":");
				String[] map = new String[tmp_map.length + 1];
				for (int j = 0; j < map.length; ++j) {
					if (j == 0) {
						map[j] = rootObj;
					} else {
						map[j] = tmp_map[j - 1];
					}
				}
				for (int k = 0; k < map.length - 1; ++k) {
					String className = map[k];
					String propetyName = map[k + 1];
					if (className.contains("[")) {
						className = StringUtils.UpperCaseFirstLatter(className
								.replace("[", ""));
					}
					// 到了最后，则使用自定义类型
					if (k == map.length - 2) {
						type = words.get(i).getType();
						while (propetyName.contains("[")) {
							propetyName = propetyName.replace("[", "");
						}
						while (propetyName.contains("]")) {
							propetyName = propetyName.replace("]", "");
						}
					} else {
						if (propetyName.startsWith("[")) {
							while (propetyName.contains("[")) {
								propetyName = propetyName.replace("[", "");
							}
							while (propetyName.contains("]")) {
								propetyName = propetyName.replace("]", "");
							}
							type = "List<"
									+ StringUtils
											.UpperCaseFirstLatter(propetyName)
									+ ">";
						} else {
							while (propetyName.contains("[")) {
								propetyName = propetyName.replace("[", "");
							}
							while (propetyName.contains("]")) {
								propetyName = propetyName.replace("]", "");
							}
							type = StringUtils
									.UpperCaseFirstLatter(propetyName);
						}
					}
					className = StringUtils.UpperCaseFirstLatter(className);
					insertBean(className, propetyName, type, comment);
				}
			}
		}

		return tree.getBeans();
	}

	public static void main(String[] args) {

		/**
		 * app功能
		 */

		List<ReturnOutlet> params_list = new ArrayList<ReturnOutlet>();
		List<ApiDescription> apis = new ArrayList<ApiDescription>();
		List<Class> app_controller = PackageUtils
				.getClasses("com.quark.app.tmpcontroller");
		for (Class class1 : app_controller) {
			Method[] methods = class1.getDeclaredMethods();

			for (Method method : methods) {
				String url = class1.getSimpleName() + "/" + method.getName();
				Annotation[] annotations = method.getDeclaredAnnotations();
				ApiDescription bean = new ApiDescription();
				bean.setUrl(url);
				for (Annotation annotation : annotations) {
					if (annotation instanceof ReturnOutlets) {
						ReturnOutlets returnParams = (ReturnOutlets) annotation;
						ReturnOutlet[] params = returnParams.value();
						params_list = Arrays.asList(params);
					}
					if (annotation instanceof ReturnOutlet) {
						ReturnOutlet returnParams = (ReturnOutlet) annotation;
						params_list = Arrays.asList(returnParams);
					}
				}
			}
		}

		List<Returns> rpb_list = new ArrayList<Returns>();
		for (ReturnOutlet outlet : params_list) {
			Returns returns = new Returns();
			returns.setName(outlet.name());
			returns.setExplaination(outlet.remarks());
			returns.setType(outlet.dataType());
			rpb_list.add(returns);
		}
		// end for
		Parser p = new Parser();
		p.token(rpb_list);
		for (Bean javaBean : p.tree.getBeans()) {
			//AndroidBeanBuilder.buildResponse(javaBean);
			;
		}

	}
}
