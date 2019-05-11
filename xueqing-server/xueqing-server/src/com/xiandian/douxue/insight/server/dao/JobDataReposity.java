/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.dao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.xiandian.douxue.insight.server.service.job.clean.JobCleanUtils;
import com.xiandian.douxue.insight.server.utils.UtilTools;

/**
 * HBase信息库（保存RAW_DATA、TAG_DATA标签数据 、PERCEPT_DATA感知数据）。 目前阶段只有互联网的招聘岗位。
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team
 */
public class JobDataReposity {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/** 单例 */
	private static JobDataReposity instance;
	private MongoDBStorage mongodbstorage = MongoDBStorage.getInstance();
	
	private Properties hbaseProperties = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/hbase.properties");

	/**
	 * 获得本对象。
	 * 
	 * @return
	 */
	public static synchronized JobDataReposity getInstance() {
		if (instance == null) {
			instance = new JobDataReposity();
		}
		return instance;
	}

	/**
	 * 插入数据到HBase中。
	 * 
	 * @throws Exception
	 */
	public void insertData(String tablename, Map<String, Object> map) throws Exception {
		logger.info("插入数据 START: " + tablename);
		// 取得一个数据表对象
		Table table = HBaseStorage.connection.getTable(TableName.valueOf(tablename));
		// 需要插入数据库的数据集合
		List<Put> putList = new ArrayList<Put>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int day = calendar.get(Calendar.DATE);
		calendar.set(Calendar.DATE, day + 1);
		// 结束日期，默认明天，有些网站的发布日期，没有结束日期，每天都是新发布
		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

		Put put;
		// 生成数据集合
		if (map.get("id") != null) {
			// rowid
			put = new Put(Bytes.toBytes((String) map.get("id")));

			// 1. RAW_DATA columnfamily 原始数据列族（名称、日期、来源和网页）
			// 职位名称
			put.addColumn(Bytes.toBytes("RAW_DATA"), Bytes.toBytes("JOB_NAME"),
					Bytes.toBytes((String) map.get("jobname")));
			// 发布时间
			put.addColumn(Bytes.toBytes("RAW_DATA"), Bytes.toBytes("RELEASEDATE"),
					Bytes.toBytes((String) map.get("date")));
			// 网站来源
			put.addColumn(Bytes.toBytes("RAW_DATA"), Bytes.toBytes("FROM"),
					Bytes.toBytes((String) map.get("resource")));
			// hdfs存储路径
			put.addColumn(Bytes.toBytes("RAW_DATA"), Bytes.toBytes("HDFS_PATH"),
					Bytes.toBytes((String) map.get("hdfs")));

			// 2. TAG_DATA columfamily 标签数据列族
			// 职位名称
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("JOB_NAME"),
					Bytes.toBytes((String) map.get("jobname")));
			// 发布时间
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("RELEASEDATE"),
					Bytes.toBytes((String) map.get("date")));
			// 是否洗数据
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("ISTAGED"), Bytes.toBytes("no"));
			// id
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("ID"), Bytes.toBytes((String) map.get("id")));
			// 工作地点
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("LOCATION"),
					Bytes.toBytes((String) map.get("location")));
			// 工作经验
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("EXPERIENCE"),
					Bytes.toBytes((String) map.get("experience")));
			// 薪资
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("SALARY"),
					Bytes.toBytes((String) map.get("salary")));
			// 招聘人数
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("AMOUNT"),
					Bytes.toBytes((String) map.get("amount")));
			// 学历
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("EDUCATION"),
					Bytes.toBytes((String) map.get("education")));
			// 岗位描述
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("DESCRIPTION"),
					Bytes.toBytes((String) map.get("description")));
			// 职能类别
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("CATEGORY"),
					Bytes.toBytes((String) map.get("category")));
			// 公司名称
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("COMPANY_NAME"),
					Bytes.toBytes((String) map.get("company")));
			// 公司性质
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("COMPANY_NATURE"),
					Bytes.toBytes((String) map.get("nature")));
			// 公司行业
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("COMPANY_INDUSTRY"),
					Bytes.toBytes((String) map.get("industry")));
			// 公司规模
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("COMPANY_SCALE"),
					Bytes.toBytes((String) map.get("scale")));

			// 3. PERCEPT_DATA columfamily 感知数据列族
			// 结束时间
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("ENDDATE"), Bytes.toBytes(dayAfter));
			putList.add(put);
		}

		// 将数据集合插入到数据库
		table.put(putList);
		table.close();
		logger.info("插入数据 END: " + tablename);
	}

	/**
	 * 插入结束日期，目的为了有一个精确的岗位招聘期的跨度。
	 * 
	 * @throws Exception
	 */
	public void insertEndTime(String tablename, Map<String, Object> map) throws Exception {
		logger.info("插入数据 START");
		// 取得一个数据表对象
		Table table = HBaseStorage.connection.getTable(TableName.valueOf(tablename));
		// 需要插入数据库的数据集合
		List<Put> putList = new ArrayList<Put>();
		Calendar c = Calendar.getInstance();
		Date date = new Date();
		// date = new SimpleDateFormat("yy-MM-dd").parse(date);
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);
		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		Put put;
		// 生成数据集合
		if (map.get("id") != null) {
			// rowid
			put = new Put(Bytes.toBytes((String) map.get("id")));
			// PERCEPT_DATA columfamily
			// 结束时间
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("ENDDATE"), Bytes.toBytes(dayAfter));
			putList.add(put);
		}
		// 将数据集合插入到数据库
		table.put(putList);
		table.close();
		logger.info("插入数据 END");
	}

	/**
	 * 按第二列簇ID查询表job_internet数据。
	 * 
	 * @param tablename
	 *            表名
	 * @param clustername
	 *            列族
	 * @param columname
	 *            列
	 * @param id
	 *            id
	 * @return
	 * @throws IOException
	 */
	public boolean queryDataById(String tablename, String clustername, String columname, String id) throws IOException {
		logger.info("按第二列簇ID查询表job_internet数据 START");
		// 取得数据表对象
		Table table = HBaseStorage.connection.getTable(TableName.valueOf(tablename));
		// 创建一个查询过滤器
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes(clustername), Bytes.toBytes(columname),
				CompareOp.EQUAL, Bytes.toBytes(id));
		// 创建一个数据表扫描器
		Scan scan = new Scan();
		scan.addColumn(clustername.getBytes(), columname.getBytes());
		// 将查询过滤器加入到数据表扫描器对象
		scan.setFilter(filter);
		// 执行查询操作，并取得查询结果
		ResultScanner scanner = table.getScanner(scan);
		Iterator<Result> iterator = scanner.iterator();
		logger.info(iterator.hasNext() + "按第二列簇ID查询表job_internet数据 END");
		return iterator.hasNext();
	}

	/**
	 * 插入第三列簇数据（感知数据）
	 * 
	 * @param tablename
	 * @param map
	 * @throws IOException
	 */
	private void insertPerceptData(String tablename, Map<String, Object> map) throws IOException {
		logger.info("插入第三列簇数据 START");
		// 取得一个数据表对象
		Table table = HBaseStorage.connection.getTable(TableName.valueOf(tablename));
		// 需要插入数据库的数据集合
		List<Put> putList = new ArrayList<Put>();
		Put put;
		Calendar c = Calendar.getInstance();
		Date date = new Date();
		// date = new SimpleDateFormat("yy-MM-dd").parse(date);
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);
		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		// 生成数据集合
		if (map.get("id") != null) {
			put = new Put(Bytes.toBytes((String) map.get("id")));
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("JOB_NAME"),
					Bytes.toBytes((String) map.get("jobname")));
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("RELEASEDATE"),
					Bytes.toBytes((String) map.get("date")));
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("ENDDATE"), Bytes.toBytes(dayAfter));
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("ISPERCEPTED"), Bytes.toBytes("no"));
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("ID"), Bytes.toBytes((String) map.get("id")));
			// 工作地点
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("LOCATION"),
					Bytes.toBytes((String) map.get("location")));
			// 工作经验
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("EXPERIENCE"),
					Bytes.toBytes((String) map.get("experience")));
			// 薪资
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("SALARY"),
					Bytes.toBytes((String) map.get("salary")));
			// 招聘人数
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("AMOUNT"),
					Bytes.toBytes((String) map.get("amount")));
			// 学历
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("EDUCATION"),
					Bytes.toBytes((String) map.get("education")));
			// 岗位描述
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("DESCRIPTION"),
					Bytes.toBytes((String) map.get("description")));
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("DESCRIPTION2"),
					Bytes.toBytes((String) map.get("description2")));
			// 职能类别
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("CATEGORY"),
					Bytes.toBytes((String) map.get("category")));
			// 公司名称
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("COMPANY_NAME"),
					Bytes.toBytes((String) map.get("company")));
			// 公司性质
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("COMPANY_NATURE"),
					Bytes.toBytes((String) map.get("nature")));
			// 公司行业
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("COMPANY_INDUSTRY"),
					Bytes.toBytes((String) map.get("industry")));
			// 公司规模
			put.addColumn(Bytes.toBytes("PERCEPT_DATA"), Bytes.toBytes("COMPANY_SCALE"),
					Bytes.toBytes((String) map.get("scale")));
			putList.add(put);
		}
		// 将数据集合插入到数据库
		table.put(putList);
		table.close();
		logger.info("插入第三列簇数据 END");
	}

	/**
	 * 修改第二列簇是否处理过的状态
	 * 
	 * @param tablename
	 * @param map
	 * @throws Exception
	 */
	private void updateTagged(String tablename, Map<String, Object> map) throws Exception {
		logger.info("修改第二列簇是否处理过的状态 START");
		// 取得一个数据表对象
		Table table = HBaseStorage.connection.getTable(TableName.valueOf(tablename));
		// 需要插入数据库的数据集合
		List<Put> putList = new ArrayList<Put>();
		Put put;
		// 生成数据集合
		if (map.get("id") != null) {
			// rowid
			put = new Put(Bytes.toBytes((String) map.get("id")));
			// 是否洗数据
			put.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("ISTAGED"), Bytes.toBytes("yes"));
			putList.add(put);
		}
		// 将数据集合插入到数据库
		table.put(putList);
		table.close();
		logger.info("修改第二列簇是否处理过的状态 END");
	}

	/**
	 * 查询数据，表，列族，列。
	 * 
	 * @param tableName
	 * @param family
	 * @param column
	 * @return
	 * @throws Exception
	 */
	public List<String> queryDataByColumn(String tableName, String family, String column) throws Exception {
		logger.info("查询表列数据 START");
		// 取得数据表对象
		Table table = HBaseStorage.connection.getTable(TableName.valueOf(tableName));
		Scan scan = new Scan();
		scan.setCaching(60);
		scan.setMaxResultSize(1 * 1024 * 100); //100k （MB1 * 1024 * 1024）
		scan.setFilter(new PageFilter(100));
		
		scan.addColumn(family.getBytes(), column.getBytes());
		// 取得表中所有数据
		ResultScanner scanner = table.getScanner(scan);
		List<String> stlist = new ArrayList<String>();
		List<List<String>> list = new ArrayList<List<String>>();
		for (Result result : scanner) {
			byte[] row = result.getRow();
			// logger.info("row key is:" + new String(row));
			List<Cell> listCells = result.listCells();
			for (Cell cell : listCells) {
				String familyArray = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(),
						cell.getQualifierLength());// 列簇
				String qualifierArray = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
						cell.getQualifierLength());// 列
				String valueArray = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());// 值
				// logger.info("列簇：" + familyArray + "列：" + qualifierArray + "值：" + valueArray);
				stlist.add(valueArray);
			}
		}
		list.add(stlist);
		logger.info("查询表列数据 END");
		return stlist;
	}

	/**
	 * 根据岗位名称进行分类展示。
	 * 
	 * @param tablename
	 * @param family
	 * @param map
	 * @throws IOException
	 */
	private void save(String tablename, String family, Map<String, Object> map) throws IOException {
		logger.info("插入数据 START");
		// 取得一个数据表对象
		Table table = HBaseStorage.connection.getTable(TableName.valueOf(tablename));
		// 需要插入数据库的数据集合
		List<Put> putList = new ArrayList<Put>();
		Put put;
		Calendar c = Calendar.getInstance();
		Date date = new Date();
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);
		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		// 生成数据集合
		if (map.get("id") != null) {
			put = new Put(Bytes.toBytes((String) map.get("id")));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("JOB_NAME"), Bytes.toBytes((String) map.get("jobname")));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("RELEASEDATE"), Bytes.toBytes((String) map.get("date")));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("ENDDATE"), Bytes.toBytes(dayAfter));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("ISPERCEPTED"), Bytes.toBytes("no"));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("ID"), Bytes.toBytes((String) map.get("id")));
			// 工作地点
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("LOCATION"),
					Bytes.toBytes((String) map.get("location")));
			// 工作经验
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("EXPERIENCE"),
					Bytes.toBytes((String) map.get("experience")));
			// 薪资
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("SALARY"), Bytes.toBytes((String) map.get("salary")));
			// 招聘人数
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("AMOUNT"), Bytes.toBytes((String) map.get("amount")));
			// 学历
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("EDUCATION"),
					Bytes.toBytes((String) map.get("education")));
			// 岗位描述
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("DESCRIPTION"),
					Bytes.toBytes((String) map.get("description")));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("DESCRIPTION2"),
					Bytes.toBytes((String) map.get("description2")));
			// 职能类别
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("CATEGORY"),
					Bytes.toBytes((String) map.get("category")));
			// 公司名称
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("COMPANY_NAME"),
					Bytes.toBytes((String) map.get("company")));
			// 公司性质
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("COMPANY_NATURE"),
					Bytes.toBytes((String) map.get("nature")));
			// 公司行业
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("COMPANY_INDUSTRY"),
					Bytes.toBytes((String) map.get("industry")));
			// 公司规模
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes("COMPANY_SCALE"),
					Bytes.toBytes((String) map.get("scale")));
			putList.add(put);
		}
		// 将数据集合插入到数据库
		table.put(putList);
		table.close();
		logger.info("插入数据 END");
	}

	/**
	 * 按条件查询表数据?
	 */
	public List<String> queryTableByCondition(List<String> ids, String tableName, String family, String id2,
			String column) throws IOException {
		logger.info("按条件查询表数据 START");
		// 取得数据表对象
		Table table = HBaseStorage.connection.getTable(TableName.valueOf(tableName));
		List<String> a = new ArrayList<>();
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			// 创建一个查询过滤器
			Filter filter = new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(id2), CompareOp.EQUAL,
					Bytes.toBytes(id));
			// 创建一个数据表扫描器
			Scan scan = new Scan();
			// 将查询过滤器加入到数据表扫描器对象
			scan.setFilter(filter);
			// 执行查询操作，并取得查询结果
			ResultScanner scanner = table.getScanner(scan);
			List wlist = new ArrayList<>();
			Map nmap = new HashMap<>();
			// 循环输出查询结果
			for (Result result : scanner) {
				byte[] row = result.getRow();
				List<Cell> listCells = result.listCells();
				for (Cell cell : listCells) {
					String familyArray = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(),
							cell.getQualifierLength());// 列簇
					String qualifierArray = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
							cell.getQualifierLength());// 列
					String valueArray = Bytes.toString(cell.getValueArray(), cell.getValueOffset(),
							cell.getValueLength());// 值
					if (qualifierArray.equals(column))
						a.add(valueArray);
				}
			}
		}
		logger.info("按条件查询表数据 END");
		return a;

	}

	/**
	 * 产生感知数据（分类，把job_Internet分成Job_cloud, Job_bigdata等具体类）。
	 * 
	 * @param jsonArray
	 * @param jobname
	 * @param family
	 * @throws Exception
	 */
	public void classify(JSONArray jsonArray, String jobname, String family) throws Exception {
		logger.info("查询表job_internet第三列簇数据 START");
		// 取得数据表对象
		Table table = HBaseStorage.connection.getTable(TableName.valueOf("job_internet"));
		Scan scan = new Scan();
		scan.addFamily("PERCEPT_DATA".getBytes());
		// 取得表中所有数据（查询太多，性能太差）
		ResultScanner scanner = table.getScanner(scan);
		for (Result result : scanner) {
			byte[] row = result.getRow();
			// logger.info("row key is:" + new String(row));
			Map<String, Object> map = new HashMap<String, Object>();
			List<Cell> listCells = result.listCells();
			for (Cell cell : listCells) {
				String familyArray = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(),
						cell.getQualifierLength());// 列簇
				String qualifierArray = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
						cell.getQualifierLength());// 列
				String valueArray = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());// 值
				switch (qualifierArray) {
				case "RELEASEDATE":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("date", valueArray);
					} else {
						map.put("date", "");
					}
					break;
				case "DATE":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("date", valueArray);
					} else {
						map.put("date", "");
					}
					break;
				case "JOB_NAME":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("jobname", valueArray);
					} else {
						map.put("jobname", "");
					}
					break;
				case "ID":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("id", valueArray);
					} else {
						map.put("id", "");
					}
					break;
				case "COMPANY_NAME":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("company", valueArray);
					} else {
						map.put("company", "");
					}
					break;
				case "COMPANY_NATURE":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("nature", valueArray);
					} else {
						map.put("nature", "");
					}
					break;
				case "COMPANY_SCALE":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("scale", valueArray);
					} else {
						map.put("scale", "");
					}
					break;
				case "COMPANY_INDUSTRY":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("industry", valueArray);
					} else {
						map.put("industry", "");
					}
					break;
				case "LOCATION":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("location", valueArray);
					} else {
						map.put("location", "");
					}
					break;
				case "EXPERIENCE":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("experience", valueArray);
					} else {
						map.put("experience", "");
					}
					break;
				case "SALARY":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("salary", valueArray);
					} else {
						map.put("salary", "");
					}
					break;
				case "AMOUNT":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("amount", valueArray);
					} else {
						map.put("amount", "");
					}
					break;
				case "EDUCATION":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("education", valueArray);
					} else {
						map.put("education", "");
					}
					break;
				case "DESCRIPTION":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("description", valueArray);
					} else {
						map.put("description", "");
					}
					break;
				case "DESCRIPTION2":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("description2", valueArray);
					} else {
						map.put("description2", "");
					}
					break;
				case "CATEGORY":
					if (valueArray != null && !valueArray.equals("")) {
						map.put("category", valueArray);
					} else {
						map.put("category", "");
					}
					break;
				default:
					break;
				}
			}
			String des = (String) map.get("description2");
			if (des == null) {
				map.put("description2", "");
			}
			int size = map.size();
			//判断是否包含关键词
			if (size >= 1) {
				String jname = (String) map.get("jobname");
				if(jname==null) {
					return;
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					String name = object.getString("name");
					//如果岗位名称包含
					if (name != null && name != null && jname.toLowerCase().contains(name)) {
						save(jobname, family, map);
					}
				}
			}
		}
		logger.info("查询表job_internet第三列簇数据 END");
	}
	

	
	/**
	 * 数据清洗。
	 * 
	 * @throws Exception
	 */
	public void cleanJobData() throws Exception {
		// TOOD: MongoDB保存了省份、市信息，用于定位，改成json配置信息
		MongoClient mongoClient = null;
		try {
			mongoClient = mongodbstorage.setUp();

			logger.info("查询表job_internet第二列簇数据 START");
			// 取得数据表对象
			Table table = HBaseStorage.connection.getTable(TableName.valueOf("job_internet"));
			Scan scan = new Scan();
			
			scan.addFamily("TAG_DATA".getBytes());
			// 取得表中所有数据
			ResultScanner scanner = table.getScanner(scan);// 非常慢（186245）
			// 循环输出查询结果
			Map<String, Object> map = new HashMap<String, Object>();
			String clscale = "";
			String am = "";
			for (Result result : scanner) {
				byte[] row = result.getRow();
				logger.info("row key is:" + new String(row));
				List<Cell> listCells = result.listCells();
				for (Cell cell : listCells) {
					String familyArray = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(),
							cell.getQualifierLength());// 列簇
					String qualifierArray = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
							cell.getQualifierLength());// 列
					String valueArray = Bytes.toString(cell.getValueArray(), cell.getValueOffset(),
							cell.getValueLength());// 值
					logger.info("列簇：" + familyArray + "列：" + qualifierArray + "值：" + valueArray);
					switch (qualifierArray) {
					// 日期
					case "DATE":
						if (valueArray != null && !valueArray.equals("")) {
							String date = JobCleanUtils.cleanDate(valueArray);
							map.put("date", date);
						} else {
							map.put("date", "");
						}
						break;
					case "RELEASEDATE":
						// 发布时间
						if (valueArray != null && !valueArray.equals("")) {
							String date = JobCleanUtils.cleanDate(valueArray);
							map.put("date", date);
						} else {
							map.put("date", "");
						}
						break;
					case "JOB_NAME":
						// 岗位名称
						if (valueArray != null && !valueArray.equals("")) {
							map.put("jobname", valueArray);
						} else {
							map.put("jobname", "");
						}
						break;
					case "ISTAGED":
						// 十分做过标签
						if (valueArray != null && !valueArray.equals("")) {
							map.put("istaged", valueArray);
						} else {
							map.put("istaged", "");
						}
						break;
					case "ID":
						// ID，网页ID
						if (valueArray != null && !valueArray.equals("")) {
							map.put("id", valueArray);
						} else {
							map.put("id", "");
						}
						break;
					case "COMPANY_NAME":
						// 公司名称
						if (valueArray != null && !valueArray.equals("")) {
							map.put("company", valueArray);
						} else {
							map.put("company", "");
						}
						break;
					case "COMPANY_NATURE":
						// 公司性质
						if (valueArray != null && !valueArray.equals("")) {
							String nature = JobCleanUtils.cleanNature(valueArray);
							map.put("nature", nature);
						} else {
							map.put("nature", "");
						}
						break;
					case "COMPANY_SCALE":
						// 公司规模
						if (valueArray != null && !valueArray.equals("")) {
							String scale = JobCleanUtils.cleanScale(valueArray);
							clscale = valueArray;
							map.put("scale", scale);
						} else {
							map.put("scale", "");
						}
						break;
					case "COMPANY_INDUSTRY":
						// 公司产业
						if (valueArray != null && !valueArray.equals("")) {
							String industry = JobCleanUtils.cleanIndustry(valueArray);
							map.put("industry", industry);
						} else {
							map.put("industry", "");
						}
						break;
					case "LOCATION":
						// 公司城市（数据在MongonDB中）
						if (valueArray != null && !valueArray.equals("")) {
							String location = JobCleanUtils.cleanLocation(valueArray, mongoClient);
							map.put("location", location);
						} else {
							map.put("location", "");
						}
						break;
					case "EXPERIENCE":
						// 工作经验
						if (valueArray != null && !valueArray.equals("")) {
							Integer experience = JobCleanUtils.cleanExperience(valueArray);
							map.put("experience", experience.toString());
						} else {
							map.put("experience", "");
						}
						break;
					case "SALARY":
						// 薪水
						if (valueArray != null && !valueArray.equals("")) {
							double salary = JobCleanUtils.cleanSalary(valueArray);
							map.put("salary", String.valueOf(salary));
						} else {
							map.put("salary", "");
						}
						break;
					case "AMOUNT":
						// 招聘
						if (valueArray != null && !valueArray.equals("")) {
							am = valueArray;
						} else {
							map.put("amount", "");
						}
						break;
					case "EDUCATION":
						// 教育
						if (valueArray != null && !valueArray.equals("")) {
							String education = JobCleanUtils.cleanEducation(valueArray);
							map.put("education", education);
						} else {
							map.put("education", "");
						}
						break;
					case "DESCRIPTION":
						// 描述（分词）
						if (valueArray != null && !valueArray.equals("")) {
							map.put("description2", valueArray);
							String description = JobCleanUtils.fenci(valueArray);
							map.put("description", description);
						} else {
							map.put("description", "");
							map.put("description2", "");
						}
						break;
					case "CATEGORY":
						// 分类
						if (valueArray != null && !valueArray.equals("")) {
							map.put("category", valueArray);
						} else {
							map.put("category", "");
						}
						break;
					}
				}
				// 没有招聘数量
				if (!am.equals("")) {
					int amount = JobCleanUtils.cleanAmount(am, clscale);
					map.put("amount", String.valueOf(amount));
				}
				updateTagged("job_internet", map);
				// 判断招聘人数大于100以及描述为空
				// TODO 公司黑名单（培训机构，招人上岗，实则培训等）
//				if (!map.get("description").equals("") && Integer.parseInt((String) map.get("amount")) < 100
//						&& Integer.parseInt((String) map.get("amount")) > 0) {
					insertPerceptData("job_internet", map);
//				}

			}
			logger.info("查询表job_internet第二列簇数据 END");
		} catch (Exception exp) {
			logger.error(exp.toString());
		} finally {
			if (mongoClient != null) {
				mongoClient.close();
			}
		}
	}
	
}
