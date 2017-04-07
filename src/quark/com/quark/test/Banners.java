package com.quark.test;

import java.util.ArrayList;
import java.util.List;
import com.quark.api.auto.bean.*;

/**
 * @author kingsley
 * @copyright quarktimes.com
 * @datetime 2015-03-27 16:26:41
 *
 */
public class Banners {
	// Banner ID
	public int banner_id;
	// banner图片
	public String cover;

	public void setBanner_id(int banner_id) {
		this.banner_id = banner_id;
	}

	public int getBanner_id() {
		return this.banner_id;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getCover() {
		return this.cover;
	}

	@Override
	public String toString() {
		return "Banners [banner_id=" + banner_id + ", cover=" + cover + "]";
	}

}