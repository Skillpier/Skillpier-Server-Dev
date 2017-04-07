/**
 * Copyright (c) 2011-2013, quark.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.plugin.bonecp;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.jfinal.kit.StringKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

/**
 * The c3p0 datasource plugin.
 */
public class BoneCpPlugin implements IPlugin, IDataSourceProvider {

	private String jdbcUrl;
	private String user;
	private String password;
	private String driverClass = "com.mysql.jdbc.Driver";
	private int maxConnectionsPerPartition = 10;
	private int minConnectionsPerPartition = 5;
	private int IdleConnetionTestPeriod = 60;
	private int acquireIncrement = 5;
	private int releaseHelperThreads = 3;
	private int maxAge = 240;
	private BoneCPConfig config = null;
	private BoneCP boneCP = null;
	private Connection connection = null;
	private BoneCPDataSource dataSource = null;

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		if (maxAge < 1)
			throw new IllegalArgumentException("maxAge must more than 0.");
		this.maxAge = maxAge;
	}

	public BoneCpPlugin setDriverClass(String driverClass) {
		if (StringKit.isBlank(driverClass))
			throw new IllegalArgumentException("driverClass can not be blank.");
		this.driverClass = driverClass;
		return this;
	}

	public BoneCpPlugin setMaxPoolSize(int maxPoolSize) {
		if (maxPoolSize < 1)
			throw new IllegalArgumentException("maxPoolSize must more than 0.");
		this.maxConnectionsPerPartition = maxPoolSize;
		return this;
	}

	public BoneCpPlugin setMinPoolSize(int minPoolSize) {
		if (minPoolSize < 1)
			throw new IllegalArgumentException("minPoolSize must more than 0.");
		this.minConnectionsPerPartition = minPoolSize;
		return this;
	}

	public BoneCpPlugin setInitialPoolSize(int initialPoolSize) {
		if (initialPoolSize < 1)
			throw new IllegalArgumentException(
					"initialPoolSize must more than 0.");
		this.IdleConnetionTestPeriod = initialPoolSize;
		return this;
	}

	public BoneCpPlugin setMaxIdleTime(int maxIdleTime) {
		if (maxIdleTime < 1)
			throw new IllegalArgumentException("maxIdleTime must more than 0.");
		this.acquireIncrement = maxIdleTime;
		return this;
	}

	public BoneCpPlugin setAcquireIncrement(int acquireIncrement) {
		if (acquireIncrement < 1)
			throw new IllegalArgumentException(
					"acquireIncrement must more than 0.");
		this.releaseHelperThreads = acquireIncrement;
		return this;
	}

	public BoneCpPlugin(String jdbcUrl, String user, String password) {
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
	}

	public BoneCpPlugin(String jdbcUrl, String user, String password,
			String driverClass) {
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.driverClass = driverClass != null ? driverClass : this.driverClass;
	}

	public BoneCpPlugin(String jdbcUrl, String user, String password,
			String driverClass, Integer maxPoolSize, Integer minPoolSize,
			Integer initialPoolSize, Integer maxIdleTime,
			Integer acquireIncrement) {
		initBoneCpProperties(jdbcUrl, user, password, driverClass, maxPoolSize,
				minPoolSize, initialPoolSize, maxIdleTime, acquireIncrement);
	}

	private void initBoneCpProperties(String jdbcUrl, String user,
			String password, String driverClass, Integer maxPoolSize,
			Integer minPoolSize, Integer initialPoolSize, Integer maxIdleTime,
			Integer acquireIncrement) {
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.driverClass = driverClass != null ? driverClass : this.driverClass;
		this.maxConnectionsPerPartition = maxPoolSize != null ? maxPoolSize
				: this.maxConnectionsPerPartition;
		this.minConnectionsPerPartition = minPoolSize != null ? minPoolSize
				: this.minConnectionsPerPartition;
		this.IdleConnetionTestPeriod = initialPoolSize != null ? initialPoolSize
				: this.IdleConnetionTestPeriod;
		this.acquireIncrement = maxIdleTime != null ? maxIdleTime
				: this.acquireIncrement;
		this.releaseHelperThreads = acquireIncrement != null ? acquireIncrement
				: this.releaseHelperThreads;
	}

	public BoneCpPlugin(File propertyfile) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propertyfile);
			Properties ps = new Properties();
			ps.load(fis);

			initBoneCpProperties(ps.getProperty("jdbcUrl"),
					ps.getProperty("user"), ps.getProperty("password"),
					ps.getProperty("driverClass"),
					toInt(ps.getProperty("maxPoolSize")),
					toInt(ps.getProperty("minPoolSize")),
					toInt(ps.getProperty("initialPoolSize")),
					toInt(ps.getProperty("maxIdleTime")),
					toInt(ps.getProperty("acquireIncrement")));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public BoneCpPlugin(Properties properties) {
		Properties ps = properties;
		initBoneCpProperties(ps.getProperty("jdbcUrl"), ps.getProperty("user"),
				ps.getProperty("password"), ps.getProperty("driverClass"),
				toInt(ps.getProperty("maxPoolSize")),
				toInt(ps.getProperty("minPoolSize")),
				toInt(ps.getProperty("initialPoolSize")),
				toInt(ps.getProperty("maxIdleTime")),
				toInt(ps.getProperty("acquireIncrement")));
	}

	public boolean start() {
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (dataSource == null) {
			dataSource = new BoneCPDataSource();
			dataSource.setUsername(user);
			dataSource.setPassword(password);
			dataSource.setJdbcUrl(jdbcUrl);
			dataSource
					.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
			dataSource
					.setMinConnectionsPerPartition(minConnectionsPerPartition);
			dataSource.setIdleConnectionTestPeriodInMinutes(IdleConnetionTestPeriod);
			dataSource.setIdleMaxAgeInMinutes(maxAge);
			dataSource.setAcquireIncrement(acquireIncrement);
		}
		return true;
	}

	private Integer toInt(String str) {
		return Integer.parseInt(str);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public boolean stop() {
		if (boneCP != null) {
			try {
				connection.close();
				boneCP.shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
