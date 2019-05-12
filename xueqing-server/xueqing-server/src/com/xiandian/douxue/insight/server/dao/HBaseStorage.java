/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.dao;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiandian.douxue.insight.server.utils.UtilTools;

/**
 * HBase存储操作类（保存RAWData、PerceptData）。 目前阶段只有互联网。
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team
 */
public class HBaseStorage {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	// 与HBase数据库的连接对象
	static Connection connection;
	// 数据库元数据操作对象
	private static Admin admin;
	
	private Configuration conf = HBaseConfiguration.create();
	/**
	 * 构造
	 */
	private static HBaseStorage instance;

	/**
	 * 获得单例。
	 * @return
	 */
	public static synchronized HBaseStorage getInstance() {
		if (instance == null) {
			instance = new HBaseStorage();
		}
		return instance;
	}

	/**
	 * 连接。
	 * @param url
	 * @param port
	 * @param path
	 */
	public void setUp(String url, String port,String path) {
		// 取得一个数据库连接的配置参数对象
		conf = HBaseConfiguration.create();
		// 设置连接参数：HBase数据库所在的主机IP
		conf.set("hbase.zookeeper.quorum", url);
		// 设置连接参数：HBase数据库使用的端口
		conf.set("hbase.zookeeper.property.clientPort", port);
//		conf.set("hbase.master", "192.168.137.25:9001"); 
		conf.set("zookeeper.znode.parent", path);
		// 取得一个数据库连接对象
		try {
			connection = ConnectionFactory.createConnection(conf);
			admin = connection.getAdmin();
			// 取得一个数据库元数据操作对象
			logger.info("HBase connection successfully!");
		} catch (Exception exc) {
			logger.error(exc.toString());
		}
	}

	public void closeHbase() {
		try {
			admin.close();
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}

	/**
	 * 创建表
	 */
	public void createTable(String tablename,String families) throws IOException {/////
		HBaseAdmin baseAdmin=new HBaseAdmin(conf);
		
		HTableDescriptor tableDescriptor = new HTableDescriptor(tablename);
		
		String[] familiesList=families.split(",");
		for(String family:familiesList) {
			HColumnDescriptor columnDescriptor=new HColumnDescriptor(family);
			tableDescriptor.addFamily(columnDescriptor);
		}
		
		baseAdmin.createTable(tableDescriptor);
	}
	
	public static void main(String[] args) {
		HBaseStorage baseStorage=HBaseStorage.getInstance();
		baseStorage.setUp("192.168.108.130", "2181","/hbase");
		try {
			baseStorage.createTable("job_internet", "RAW_DATA,TAG_DATA,PERCEPT_DATA");
			baseStorage.createTable("job_cloud", "cloud");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
