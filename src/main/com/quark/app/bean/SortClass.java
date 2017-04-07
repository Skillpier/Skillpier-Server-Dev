package com.quark.app.bean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;

/**
 * 自定义排序
 * 
 * @author Administrator
 *
 */
public class SortClass  {
	/**
	 * 按bean的属性值对list集合进行排序
	 *
	 * @param list
	 *            要排序的集合
	 * @param propertyName
	 *            集合元素的属性名
	 * @param isAsc
	 *            排序方向,true--正向排序,false--逆向排序
	 * @return 排序后的集合
	 */
	public static List sortList(List list, String propertyName, boolean isAsc) {
		// 借助commons-collections包的ComparatorUtils
		// BeanComparator，ComparableComparator和ComparatorChain都是实现了Comparator这个接口
		Comparator mycmp = ComparableComparator.getInstance();
		mycmp = ComparatorUtils.nullLowComparator(mycmp); // 允许null
		if (isAsc) {
			mycmp = ComparatorUtils.reversedComparator(mycmp); // 逆序
		}
		Comparator cmp = new BeanComparator(propertyName, mycmp);
		Collections.sort(list, cmp);
		return list;
	}
}
