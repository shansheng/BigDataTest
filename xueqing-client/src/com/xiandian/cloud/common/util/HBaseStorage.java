/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.util;

import java.io.IOException;
import java.util.List;

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



/**
 * HBase存储操作类（保存RAWData、PerceptData）。 目前阶段只有互联网。
 * 
 * @since v1.0
 * @date 20170815
 * @author WangHuanghuang
 */
public class HBaseStorage {
	

	private Logger logger = LoggerFactory.getLogger(getClass());
	// 与HBase数据库的连接对象
	static Connection connection;
	// 数据库元数据操作对象
	private static Admin admin;
	/**
	 * 构造
	 */
	private static HBaseStorage instance;

	public static synchronized HBaseStorage getInstance() {
		if (instance == null) {
			instance = new HBaseStorage();
		}
		return instance;
	}

	/**
	 * 
	 * @param url
	 * @param port
	 * @param path
	 */
	public void setUp(String url, String port, String path) {
		// 取得一个数据库连接的配置参数对象
		Configuration conf = HBaseConfiguration.create();
		// 设置连接参数：HBase数据库所在的主机IP 10.10.4.35 server_port= parent_path=/hbase-unsecure
		conf.set("hbase.zookeeper.quorum", url);
		// 设置连接参数：HBase数据库使用的端口 2181 /hbase-unsecure
		conf.set("hbase.zookeeper.property.clientPort", port);
		conf.set("zookeeper.znode.parent", path);
		// 取得一个数据库连接对象
		try {
			connection = ConnectionFactory.createConnection(conf);
			admin = connection.getAdmin();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 取得一个数据库元数据操作对象
	}
	
	public Connection getconnection() {
		return this.connection;
	}
	
	
	public static void main(String[] args) {
		 HBaseStorage hBaseStorage = HBaseStorage.getInstance();
		 hBaseStorage .setUp("10.10.4.35", "2181", "/hbase-unsecure");
		 Connection connection=hBaseStorage.getconnection();
		 Admin admin=null;
		 try {
			 admin=connection.getAdmin();
			 System.out.println(admin);
			 TableName tableName = TableName.valueOf("guosai");
			 System.out.println(tableName);
			HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
			System.out.println(hTableDescriptor);
				// 列族描述对象
			HColumnDescriptor family = new HColumnDescriptor("ai");
			System.out.println(family);
				// 在数据表中新建一个列族
			hTableDescriptor.addFamily(family);
				// 新建数据表
			admin.createTable(hTableDescriptor);
			System.out.println("111");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 try {
			Table table = connection.getTable(TableName.valueOf("guosai"));
			System.out.println(table);
			ResultScanner scanner = table.getScanner(new Scan());
			// 循环输出表中的数据
			for (Result result : scanner) {
				byte[] row = result.getRow();
				System.out.println("row key is:" + new String(row));
				List<Cell> listCells = result.listCells();
				for (Cell cell : listCells) {
					byte[] familyArray = cell.getFamilyArray();
					byte[] qualifierArray = cell.getQualifierArray();
					byte[] valueArray = cell.getValueArray();
					System.out.println("row value is:" + new String(familyArray) + new String(qualifierArray)
							+ new String(valueArray));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("222");
			e.printStackTrace();
		}
	}
	

	public void closeHbase() {
		try {
			admin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
		}
	}

	/**
	 * 创建表
	 */
	public void createTable(String tablename) throws IOException {
		logger.info("---------------创建表 START-----------------");
		// 新建一个数据表表名对象
		TableName tableName = TableName.valueOf(tablename);
		// 如果需要新建的表已经存在
		if (admin.tableExists(tableName)) {
			logger.info("表已经存在！");
		} else {
			// 数据表描述对象
			HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
			// 列族描述对象
			HColumnDescriptor family = new HColumnDescriptor("ai");
			// 在数据表中新建一个列族
			hTableDescriptor.addFamily(family);
			// 新建数据表
			admin.createTable(hTableDescriptor);
		}
		logger.info("---------------创建表 END-----------------");
	}

	/**
	 * 查询整表数据
	 */
	public void queryTable() throws IOException {
		logger.info("---------------查询整表数据 START-----------------");
		// 取得数据表对象
		Table table = connection.getTable(TableName.valueOf("ambarismoketest"));
		// 取得表中所有数据
		ResultScanner scanner = table.getScanner(new Scan());
		// 循环输出表中的数据
		for (Result result : scanner) {
			byte[] row = result.getRow();
			logger.info("row key is:" + new String(row));
			List<Cell> listCells = result.listCells();
			for (Cell cell : listCells) {
				byte[] familyArray = cell.getFamilyArray();
				byte[] qualifierArray = cell.getQualifierArray();
				byte[] valueArray = cell.getValueArray();
				logger.info("row value is:" + new String(familyArray) + new String(qualifierArray)
						+ new String(valueArray));
			}
		}
		logger.info("---------------查询整表数据 END-----------------");

	}

	/**
	 * 按行键查询表数据
	 */
	public void queryTableByRowKey() throws IOException {
		logger.info("---------------按行键查询表数据 START-----------------");
		// 取得数据表对象
		Table table = connection.getTable(TableName.valueOf("t_book"));
		// 新建一个查询对象作为查询条件
		Get get = new Get("row8".getBytes());
		// 按行键查询数据
		Result result = table.get(get);
		byte[] row = result.getRow();
		logger.info("row key is:" + new String(row));
		List<Cell> listCells = result.listCells();
		for (Cell cell : listCells) {
			byte[] familyArray = cell.getFamilyArray();
			byte[] qualifierArray = cell.getQualifierArray();
			byte[] valueArray = cell.getValueArray();
			logger.info(
					"row value is:" + new String(familyArray) + new String(qualifierArray) + new String(valueArray));
		}
		logger.info("---------------按行键查询表数据 END-----------------");
	}

	/**
	 * 按条件查询表数据
	 */
	public void queryTableByCondition() throws IOException {
		logger.info("---------------按条件查询表数据 START-----------------");
		// 取得数据表对象
		Table table = connection.getTable(TableName.valueOf("t_book"));
		// 创建一个查询过滤器
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes("base"), Bytes.toBytes("name"), CompareOp.EQUAL,
				Bytes.toBytes("bookName6"));
		// 创建一个数据表扫描器
		Scan scan = new Scan();
		// 将查询过滤器加入到数据表扫描器对象
		scan.setFilter(filter);
		// 执行查询操作，并取得查询结果
		ResultScanner scanner = table.getScanner(scan);
		// 循环输出查询结果
		for (Result result : scanner) {
			byte[] row = result.getRow();
			logger.info("row key is:" + new String(row));
			List<Cell> listCells = result.listCells();
			for (Cell cell : listCells) {
				byte[] familyArray = cell.getFamilyArray();
				byte[] qualifierArray = cell.getQualifierArray();
				byte[] valueArray = cell.getValueArray();
				logger.info("row value is:" + new String(familyArray) + new String(qualifierArray)
						+ new String(valueArray));
			}
		}
		logger.info("---------------按条件查询表数据 END-----------------");
	}

	/**
	 * 清空表
	 */
	public void truncateTable() throws IOException {
		logger.info("---------------清空表 START-----------------");
		// 取得目标数据表的表名对象
		TableName tableName = TableName.valueOf("t_book");
		// 设置表状态为无效
		admin.disableTable(tableName);
		// 清空指定表的数据
		admin.truncateTable(tableName, true);
		logger.info("---------------清空表 End-----------------");
	}

	/**
	 * 删除表
	 */
	public void deleteTable() throws IOException {
		logger.info("---------------删除表 START-----------------");
		// 设置表状态为无效
		admin.disableTable(TableName.valueOf("t_book"));
		// 删除指定的数据表
		admin.deleteTable(TableName.valueOf("t_book"));
		logger.info("---------------删除表 End-----------------");
	}

	/**
	 * 删除行
	 */
	public void deleteByRowKey() throws IOException {
		logger.info("---------------删除行 START-----------------");
		// 取得待操作的数据表对象
		Table table = connection.getTable(TableName.valueOf("t_book"));
		// 创建删除条件对象
		Delete delete = new Delete(Bytes.toBytes("row2"));
		// 执行删除操作
		table.delete(delete);
		logger.info("---------------删除行 End-----------------");
	}

	/**
	 * 新建列族
	 */
	public void addColumnFamily() throws IOException {
		logger.info("---------------新建列族 START-----------------");
		// 取得目标数据表的表名对象
		TableName tableName = TableName.valueOf("t_internet");
		// 创建列族对象
		HColumnDescriptor columnDescriptor = new HColumnDescriptor("more");
		// 将新创建的列族添加到指定的数据表
		admin.addColumn(tableName, columnDescriptor);
		logger.info("---------------新建列族 END-----------------");
	}

	/**
	 * 删除列族
	 */
	public void deleteColumnFamily() throws IOException {
		logger.info("---------------删除列族 START-----------------");
		// 取得目标数据表的表名对象
		TableName tableName = TableName.valueOf("t_book");
		// 删除指定数据表中的指定列族
		admin.deleteColumn(tableName, "more".getBytes());
		logger.info("---------------删除列族 END-----------------");
	}

}
