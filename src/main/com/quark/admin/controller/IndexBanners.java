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
import com.quark.model.extend.IndexBanner;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quarkso.utils.DateUitls;

@Before(Login.class)
public class IndexBanners extends Controller {

	@Before(Privilege.class)
	public void list() throws ParseException {
		String message1 = getPara("message1", null);
		int currentPage = getParaToInt("pn", 1);
		String message = "list";
		Page<IndexBanner> bannPage = null;
		setAttr("action", message);
		bannPage = IndexBanner.dao.paginate(currentPage, PAGE_SIZE,
				"select * ",
				"from index_banner where type=2 order by post_time desc");
		setAttr("list", bannPage);
		setAttr("pn", currentPage);
		if (message1 != null) {
			if (message1.equals("1")) {
				setAttr("ok", "添加成功");
			}
			if (message1.equals("2")) {
				setAttr("ok", "添加失败");
			}
			if (message1.equals("3")) {
				setAttr("ok", "修改成功");
			}
			if (message1.equals("4")) {
				setAttr("ok", "修改失败");
			}
		}
		render("/admin/IndexBannerList.html");
	}

	public void add() {
		String message = getPara("message", null);
		if (message != null) {
			setAttr("message", "请上传图片");
		}
		render("/admin/IndexBannerAdd.html");
	}

	public void addCommit() {
		UploadFile upload_cover = getFile("cover", config.save_path);
		UploadFile upload_big_cover = getFile("big_cover", config.save_path);
		String content = getPara("content");
		IndexBanner indexBanner = new IndexBanner();
		if (upload_cover != null||upload_big_cover!=null) {
			indexBanner.set(indexBanner.cover,FileUtils.renameToFile(upload_cover, 352, 418));
			indexBanner.set(indexBanner.big_cover,FileUtils.renameToFile(upload_big_cover, 1024, 685));
			boolean save = indexBanner.set(indexBanner.type, 2)
					.set(indexBanner.content, content)
					.set(indexBanner.post_time, DateUtils.getCurrentDateTime())
					.save();
			if (save) {
				redirect("/admin/IndexBanners/list?message1=1");
			}
		} else {
			redirect("/admin/IndexBanners/add?message=0");
		}
	}

	public void bannerInfo() {
		String message = getPara("message", null);
		if (message != null) {
			setAttr("message", "请上传图片");
		}
		int currentPage = getParaToInt("pn", 1);
		int index_banner_id = getParaToInt("index_banner_id");
		IndexBanner indexBanner = IndexBanner.dao.findById(index_banner_id);
		setAttr("r", indexBanner);
		setAttr("pn", currentPage);
		render("/admin/IndexBannerModify.html");
	}

	public void addModify() {
		UploadFile upload_cover = getFile("cover", config.save_path);
		UploadFile upload_big_cover = getFile("big_cover", config.save_path);
		int currentPage = getParaToInt("pn", 1);
		int index_banner_id = getParaToInt("index_banner_id");
		String content = getPara("content");
		IndexBanner indexBanner = IndexBanner.dao.findById(index_banner_id);
		String old_cover = indexBanner.getStr(indexBanner.cover);
		String oldbig_cover_cover = indexBanner.getStr(indexBanner.big_cover);
		if (upload_cover != null||upload_big_cover!=null) {
			indexBanner.set(indexBanner.cover,FileUtils.renameToFile(upload_cover, 352, 418));
			indexBanner.set(indexBanner.big_cover,FileUtils.renameToFile(upload_big_cover, 1024, 685));
		}
		boolean update = indexBanner.set(indexBanner.content, content)
				.set(indexBanner.post_time, DateUtils.getCurrentDateTime())
				.update();
		if (update) {
			redirect("/admin/IndexBanners/list?message1=3");
		} else {
			redirect("/admin/IndexBanners/list?message1=4");
		}
	}

	public void delete() {
		int currentPage = getParaToInt("pn", 1);
		int index_banner_id = getParaToInt("index_banner_id");
		IndexBanner indexBanner = IndexBanner.dao.findById(index_banner_id);
		if (indexBanner != null) {
			indexBanner.delete();
		}
		redirect("/admin/IndexBanners/list");
	}
}
