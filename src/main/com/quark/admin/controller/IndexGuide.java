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
 * 引导列表
 * 
 * @author 小阳
 * 
 */
@Before(Login.class)
public class IndexGuide extends Controller {

	@Before(Privilege.class)
	public void list() throws ParseException {
		int currentPage = getParaToInt("pn", 1);
		String message = "list";
		Page<IndexBanner> bannPage = null;
		String message1 = getPara("message1", null);
		setAttr("action", message);
		bannPage = IndexBanner.dao.paginate(currentPage, PAGE_SIZE,
				"select * ",
				"from index_banner where type=1 order by post_time desc");
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
			if (message1.equals("5")) {
				setAttr("ok", "只能添加三张图片");
			}
		}
		render("/admin/IndexGuideList.html");
	}

	public void add() {
		UploadFile cover = getFile("cover", config.save_path);
		IndexBanner indexBanner2 = IndexBanner.dao.findFirst("select count(index_banner_id) as total_index_banner from index_banner where type=1");
		long total_index_banner = 0;
		if (indexBanner2!=null) {
			indexBanner2 = indexBanner2.get("total_index_banner");
		}
		if (total_index_banner > 2) {
			redirect("/admin/IndexGuide/list?message1=5");// 只能添加三张图片
		} else {
			IndexBanner indexBanner = new IndexBanner();
			if (cover != null) {
				String fileName = FileUtils.renameToFile(cover, 750, 1334);
				boolean save = indexBanner.set(indexBanner.cover, fileName)
						.set(indexBanner.post_time,DateUtils.getCurrentDateTime())
						.save();
				if (save) {
					redirect("/admin/IndexGuide/list?message1=1");// 添加成功
				}
			} else {
				redirect("/admin/IndexGuide/list?message1=2");// 失败
			}
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
		render("/admin/IndexGuideModify.html");
	}
	public void addModify() {
		UploadFile cover = getFile("cover", config.save_path);
		int currentPage = getParaToInt("pn", 1);
		int index_banner_id = getParaToInt("index_banner_id");
		IndexBanner indexBanner = IndexBanner.dao.findById(index_banner_id);
		String old_cover = indexBanner.getStr(indexBanner.cover);
		if (cover != null) {
			String fileName = FileUtils.renameToFile(cover, 750, 1334);
			indexBanner.set(indexBanner.cover, fileName);
		}
		boolean update = indexBanner.set(indexBanner.post_time,DateUtils.getCurrentDateTime())
					.update();
		if (update) {
			redirect("/admin/IndexGuide/list?message1=3");
		} else {
			redirect("/admin/IndexGuide/list?message1=4");
		}
	}

	public void delete() {
		int currentPage = getParaToInt("pn", 1);
		int index_banner_id = getParaToInt("index_banner_id");
		IndexBanner indexBanner = IndexBanner.dao.findById(index_banner_id);
		if (indexBanner != null) {
			indexBanner.delete();
		}
		redirect("/admin/IndexGuide/list");
	}
}
