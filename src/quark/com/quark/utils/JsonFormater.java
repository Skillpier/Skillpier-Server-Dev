package com.quark.utils;

public class JsonFormater {

	public static void main(String[] args) {
	    String jsonStr = "{'ActivityResponse':{'totalRow':13,'pageNumber':1,'totalPage':2,'pageSize':10,'list':[{'start_time':'2015-01-11','publish_time':'2015-01-09','activity_id':2,'county':'罗湖','confirmed_count':2,'pay':120,'days':5,'title':'宝安招车迷','type':'服务员;','left_count':31},{'start_time':'2015-01-11','publish_time':'2015-01-06','activity_id':13,'county':'宝安','confirmed_count':0,'pay':120,'days':5,'title':'华清宝马拉松比赛','type':'其它','left_count':22},{'start_time':'2015-01-11','publish_time':'2015-01-06','activity_id':5,'county':'罗湖','confirmed_count':0,'pay':120,'days':5,'title':'华清宝马拉松比赛','type':'安保人员','left_count':67},{'start_time':'2015-01-11','publish_time':'2015-01-06','activity_id':6,'county':'宝安','confirmed_count':0,'pay':120,'days':5,'title':'华清宝马拉松比赛','type':'模特','left_count':25},{'start_time':'2015-01-11','publish_time':'2015-01-06','activity_id':7,'county':'罗湖','confirmed_count':0,'pay':120,'days':5,'title':'华清宝马拉松比赛','type':'派发','left_count':23},{'start_time':'2015-01-11','publish_time':'2015-01-06','activity_id':8,'county':'罗湖','confirmed_count':0,'pay':120,'days':5,'title':'华清宝马拉松比赛','type':'主持','left_count':10},{'start_time':'2015-01-13','publish_time':'2015-01-06','activity_id':11,'county':'宝安','confirmed_count':0,'pay':120,'days':3,'title':'华清宝马拉松比赛','type':'其它','left_count':10},{'start_time':'2015-01-01','publish_time':'2015-01-06','activity_id':1,'county':'宝安','confirmed_count':2,'pay':120,'days':1,'title':'京东网购用户访谈兼职','type':'派发','left_count':8},{'start_time':'2015-01-11','publish_time':'2015-01-06','activity_id':9,'county':'宝安','confirmed_count':0,'pay':120,'days':5,'title':'华清宝马拉松比赛','type':'翻译','left_count':100},{'start_time':'2015-01-11','publish_time':'2015-01-06','activity_id':10,'county':'罗湖','confirmed_count':0,'pay':120,'days':5,'title':'华清宝马拉松比赛','type':'其它','left_count':40}]}}";
	    String fotmatStr = JsonFormater.format(jsonStr);
//			fotmatStr = fotmatStr.replaceAll("\n", "<br/>");
//			fotmatStr = fotmatStr.replaceAll("\t", "    ");
	    System.out.println(fotmatStr);
	  }	/**
	 * 得到格式化json数据 退格用\t 换行用\r
	 */
	public static String format(String jsonStr) {
		int level = 0;
		StringBuffer jsonForMatStr = new StringBuffer();
		for (int i = 0; i < jsonStr.length(); i++) {
			char c = jsonStr.charAt(i);
			if (level > 0
					&& '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
				jsonForMatStr.append(getLevelStr(level));
			}
			switch (c) {
			case '{':
			case '[':
				jsonForMatStr.append(c + "\n");
				level++;
				break;
			case ',':
				jsonForMatStr.append(c + "\n");
				break;
			case '}':
			case ']':
				jsonForMatStr.append("\n");
				level--;
				jsonForMatStr.append(getLevelStr(level));
				jsonForMatStr.append(c);
				break;
			default:
				jsonForMatStr.append(c);
				break;
			}
		}

		return jsonForMatStr.toString();

	}

	private static String getLevelStr(int level) {
		StringBuffer levelStr = new StringBuffer();
		for (int levelI = 0; levelI < level; levelI++) {
			levelStr.append("\t");
		}
		return levelStr.toString();
	}

}