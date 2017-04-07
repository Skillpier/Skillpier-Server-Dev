package com.quark.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.eclipse.jetty.util.UrlEncoded;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.upload.UploadFile;
import com.quark.common.config;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Course;
import com.quark.model.extend.IndexBanner;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quarkso.utils.DateUitls;

/**
 * 闪屏列表
 * 
 * @author xxy
 * 
 */
@Before(Login.class)
public class IndexSplash extends Controller {

	@Before(Privilege.class)
	public void add() {
		IndexBanner index = IndexBanner.dao
				.findFirst("select * from index_banner where type=3");
		int message = getParaToInt("message", 0);
		if (message != 0) {
			if (message == 1) {
				setAttr("ok", "上传成功");
			}
			if (message == 2) {
				setAttr("ok", "上传失败");
			}
			if (message == 3) {
				setAttr("ok", "修改成功");
			}

			if (message == 4) {
				setAttr("ok", "修改失败");
			}

			if (message == 5) {
				setAttr("message", "请上传图片");
			}
		}
		setAttr("r", index);
		render("/admin/IndexSplashList.html");
	}

	public void addIndexSplash1() {
		UploadFile cover = getFile("cover", config.images_path);
		String index_banner_id = getPara("index_banner_id", null);
		if (index_banner_id == null) {
			IndexBanner indexBanner = new IndexBanner();
			if (cover != null) {
				String fileName = FileUtils.renameToFile(cover, 750, 1334);
				boolean save = indexBanner.set(indexBanner.cover, fileName)
						.set(indexBanner.post_time,DateUtils.getCurrentDateTime())
						.set(indexBanner.type, 3)
						.save();
				if (save) {
					redirect("/admin/IndexSplash/add?message=1");// 添加成功
				} else {
					redirect("/admin/IndexSplash/add?message=2");// 添加失败
				}
			} else {
				redirect("/admin/IndexSplash/add?message=5");// 请上传图片
			}
		} else {
			IndexBanner indexBanner = IndexBanner.dao.findById(index_banner_id);
			if (cover != null) {
				String fileName = FileUtils.renameToFile(cover, 750, 1334);
				boolean update = indexBanner.set(indexBanner.cover, fileName)
						.set(indexBanner.post_time,DateUtils.getCurrentDateTime())
						.set(indexBanner.type, 3)
						.update();
				if (update) {
					redirect("/admin/IndexSplash/add?message=3");// 修改成功
				} else {
					redirect("/admin/IndexSplash/add?message=4");// 修改失败
				}
			} else {
				redirect("/admin/IndexSplash/add?message=5");// 请上传图片
			}
		}
	}

}
