package com.quark.utils.db;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.quark.common.config;

public class ActiveRecordModel {
	private static C3p0Plugin c3p0Plugin = null;
	public static ActiveRecordPlugin arp = null;

	public ActiveRecordModel(JdbcConfig jdbc) {
		c3p0Plugin = new C3p0Plugin(jdbc.url, jdbc.username, jdbc.password);
		arp = new ActiveRecordPlugin(c3p0Plugin);
		c3p0Plugin.start();
	}

	public void addModel(String tableName, Class<? extends Model<?>> t) {
		arp.addMapping(tableName, t);
	}

	public void addModel(String tableName, String tableId,
			Class<? extends Model<?>> t) {
		arp.addMapping(tableName, tableId, t);
	}

	public void start() {
		arp.start();
	}
}
