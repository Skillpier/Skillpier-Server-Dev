/**
 * 
 */
package com.quark.app.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.upload.UploadFile;
import com.quark.api.annotation.Author;
import com.quark.api.annotation.DataType;
import com.quark.api.annotation.Explaination;
import com.quark.api.annotation.ReturnDBParam;
import com.quark.api.annotation.ReturnOutlet;
import com.quark.api.annotation.Rp;
import com.quark.api.annotation.Type;
import com.quark.api.annotation.URLParam;
import com.quark.api.annotation.UpdateLog;
import com.quark.api.annotation.Value;
import com.quark.api.auto.bean.ResponseValues;
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.RongToken;
import com.quark.common.Storage;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.model.extend.Course;
import com.quark.model.extend.IndexBanner;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quark.utils.MessageUtils;

/**
 * @author C罗
 * 引导页
 *
 */
@Before(Tx.class)
public class IndexBannerManage extends Controller {
	@Author("cluo")
	@Rp("引导页")
	@Explaination(info = "闪屏")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "SplashResponse{Splash:$}", column = IndexBanner.cover)
	@ReturnOutlet(name = "SplashResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SplashResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SplashResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	@Before(CacheInterceptor.class)
	@CacheName("indexbanner")
	public void splash() {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			IndexBanner inBanners = IndexBanner.dao.findFirst("select cover from index_banner where status=1 and type=3 order by post_time desc");
			responseValues.put("Splash", inBanners);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("SplashResponse", responseValues);
			renderMultiJson("SplashResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("IndexBannerManage/splash", "闪屏", this);
		}
	}

	@Author("cluo")
	@Rp("引导页")
	@Explaination(info = "引导页列表")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "GuideBannerResponse{GuideBanners:list[GuideBanner:$]}", column = IndexBanner.cover)
	@ReturnDBParam(name = "GuideBannerResponse{GuideBanners:list[GuideBanner:$]}", column = IndexBanner.big_cover)
	@ReturnDBParam(name = "GuideBannerResponse{GuideBanners:list[GuideBanner:$]}", column = IndexBanner.index_banner_id)
	@ReturnOutlet(name = "GuideBannerResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "GuideBannerResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "GuideBannerResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	@Before(CacheInterceptor.class)
	@CacheName("indexbanner")
	public void guideBanner() {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			List<IndexBanner> inBanners = IndexBanner.dao.find("select index_banner_id,cover,big_cover from index_banner where status=1 and type=1 order by post_time desc");
			responseValues.put("GuideBanners", inBanners);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("GuideBannerResponse", responseValues);
			renderMultiJson("GuideBannerResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("IndexBannerManage/guideBanner", "引导页列表", this);
		}
	}
	@Author("cluo")
	@Rp("主页")
	@Explaination(info = "轮播列表")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "HomeBannerResponse{homeBannerResult:list[homeBanner:$]}", column = IndexBanner.cover)
	@ReturnDBParam(name = "HomeBannerResponse{homeBannerResult:list[homeBanner:$]}", column = IndexBanner.big_cover)
	@ReturnDBParam(name = "HomeBannerResponse{homeBannerResult:list[homeBanner:$]}", column = IndexBanner.index_banner_id)
	@ReturnOutlet(name = "HomeBannerResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "HomeBannerResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "HomeBannerResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	@Before(CacheInterceptor.class)
	@CacheName("indexbanner")
	public void homeBanner() {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<IndexBanner> inBanners = IndexBanner.dao.find("select index_banner_id,cover,big_cover from index_banner where status=1 and type=2 order by post_time desc");
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("homeBanner", inBanners);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("HomeBannerResponse", responseValues);
			renderMultiJson("HomeBannerResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("IndexBannerManage/homeBanner", "轮播列表", this);
		}
	}
	@Author("cluo")
	@Rp("主页")
	@Explaination(info = "轮播详情--h5")
	@URLParam(defaultValue = "{select index_banner_id from index_banner where status=1 and type=2 }", explain = Value.Infer, type = Type.String, name = IndexBanner.index_banner_id)
	public void indexBannerDetail() {
		try {
			int index_banner_id = getParaToInt("index_banner_id");
			IndexBanner indexBanner = IndexBanner.dao.findById(index_banner_id);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String content = "",title="",post_date="";
			if (indexBanner!=null) {
				title  = indexBanner.get("title");
				content  = indexBanner.get("content");
				post_date  = indexBanner.getTimestamp("post_time").toString();
			}
			setAttr("title", title);
			setAttr("post_date", post_date);
			setAttr("content", content);
			render("/webview/indexBanner.html");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("IndexBannerManage/indexBannerDetail", "轮播详情", this);
		}
	}
}
