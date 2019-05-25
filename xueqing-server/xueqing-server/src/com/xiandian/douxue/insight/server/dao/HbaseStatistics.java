/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.xiandian.douxue.insight.server.utils.UtilTools;

/**
 * @author xuanhuidong E-mail: 1259023939@qq.com
 * @version 创建时间：2018年2月9日 下午4:18:18 类说明
 */
public class HbaseStatistics {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MongoDBStorage mongodbstorage = MongoDBStorage.getInstance();
	private static Properties cityprovice = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/cityprovice.properties");
	private static Properties education = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/job_education.properties");
	private Properties hbaseProperties = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/hbase.properties");
	private Properties hadoopProperties = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/hadoop.properties");
	private Map<String, String> cities = new HashMap<String, String>();
	private List<String> educations = new ArrayList<String>();
	private static Connection connection;

	public HbaseStatistics() {
		Configuration configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.quorum", hbaseProperties.getProperty("server_ip"));
		// 设置连接参数：HBase数据库使用的端口
		configuration.set("hbase.zookeeper.property.clientPort", hbaseProperties.getProperty("server_port"));
		configuration.set("hbase.master", hbaseProperties.getProperty("server_ip") + ":16000");
		configuration.set("zookeeper.znode.parent", hbaseProperties.getProperty("parent_path"));
		try {
			connection = ConnectionFactory.createConnection(configuration);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] provice = cityprovice.getProperty("provice").split(",");
		for (String p : provice) {
			String[] city = cityprovice.getProperty(p).split(",");
			for (String c : city) {
				cities.put(c, p);
			}
		}
		String[] educationStr= education.getProperty("education").split(",");
		for(String p:educationStr) {
			educations.add(p);
		}
	}

	public static void main(String[] args) throws IOException {
		HbaseStatistics hbaseStatistics = new HbaseStatistics();
		hbaseStatistics.doDataStatistics();
	}

	/**
	 * 岗位省份分布
	 * 
	 * @param stmt
	 * @param tableName
	 * @throws SQLException
	 * @throws IOException
	 */
	public void countProvinceDistribution(String tableName, String mongoTable, String family) throws SQLException, IOException {
		MongoClient mongoClient = mongodbstorage.setUp();
		// String sql = "select LOCATION,count(1) as count,sum(AMOUNT) from " +
		// tableName
		// + " where ISPERCEPTED = 'no' group by LOCATION order by count desc";
		// ResultSet results = stmt.executeQuery(sql);

		// 建立表的连接
		Table table = connection.getTable(TableName.valueOf(tableName));
		// 创建一个空的Scan实例
		Scan scan1 = new Scan();
		// 可以指定具体的列族列
		scan1.addColumn(Bytes.toBytes(family), Bytes.toBytes("LOCATION")).addColumn(Bytes.toBytes(family),
				Bytes.toBytes("AMOUNT"));
		scan1.setCaching(60);
		scan1.setMaxResultSize(1 * 1024 * 1024); // 100k （MB1 * 1024 * 1024）
		scan1.setFilter(new PageFilter(1000));

		// 在行上获取遍历器
		ResultScanner scanner1 = table.getScanner(scan1);

		Map map = mongodbstorage.create(mongoTable, "job_province_distribution", mongoClient);// mongodb集合,key,value
		MongoCollection<Document> collection = (MongoCollection<Document>) map.get("collection");
		Document document = (Document) map.get("document");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		String da = sdf.format(date);
		mongodbstorage.appendString(document, "date", da);
		int[] cu = new int[34];
		int[] nu = new int[34];
		Vector<Document> vec = new Vector<Document>();
		for (Result res : scanner1) {
			String loc = (new String(CellUtil.cloneValue(res.rawCells()[1]))).split("-")[0];
			int total = Integer.parseInt(new String(CellUtil.cloneValue(res.rawCells()[0])));
			int num = 1;
			if (!cities.containsKey(loc)) {
			} else {
				// String prov = jobanalysisreposity.appendProvinceDistribution(document, loc,
				// total, mongoClient);
				String prov = cities.get(loc);
				switch (prov) {
				case "北京":
					cu[0] += total;
					nu[0] += num;
					break;
				case "天津":
					cu[1] += total;
					nu[1] += num;
					break;
				case "河北":
					cu[2] += total;
					nu[2] += num;
					break;
				case "山西":
					cu[3] += total;
					nu[3] += num;
					break;
				case "内蒙古":
					cu[4] += total;
					nu[4] += num;
					break;
				case "辽宁":
					cu[5] += total;
					nu[5] += num;
					break;
				case "吉林":
					cu[6] += total;
					nu[6] += num;
					break;
				case "黑龙江":
					cu[7] += total;
					nu[7] += num;
					break;
				case "上海":
					cu[8] += total;
					nu[8] += num;
					break;
				case "江苏":
					cu[9] += total;
					nu[9] += num;
					break;
				case "浙江":
					cu[10] += total;
					nu[10] += num;
					break;
				case "安徽":
					cu[11] += total;
					nu[11] += num;
					break;
				case "福建":
					cu[12] += total;
					nu[12] += num;
					break;
				case "江西":
					cu[13] += total;
					nu[13] += num;
					break;
				case "山东":
					cu[14] += total;
					nu[14] += num;
					break;
				case "河南":
					cu[15] += total;
					nu[15] += num;
					break;
				case "湖北":
					cu[16] += total;
					nu[16] += num;
					break;
				case "湖南":
					cu[17] += total;
					nu[17] += num;
					break;
				case "广东":
					cu[18] += total;
					nu[18] += num;
					break;
				case "广西":
					cu[19] += total;
					nu[19] += num;
					break;
				case "海南":
					cu[20] += total;
					nu[20] += num;
					break;
				case "重庆":
					cu[21] += total;
					nu[21] += num;
					break;
				case "四川":
					cu[22] += total;
					nu[22] += num;
					break;
				case "贵州":
					cu[23] += total;
					nu[23] += num;
					break;
				case "云南":
					cu[24] += total;
					nu[24] += num;
					break;
				case "西藏":
					cu[25] += total;
					nu[25] += num;
					break;
				case "陕西省":
					cu[26] += total;
					nu[26] += num;
					break;
				case "甘肃":
					cu[27] += total;
					nu[27] += num;
					break;
				case "青海":
					cu[28] += total;
					nu[28] += num;
					break;
				case "宁夏":
					cu[29] += total;
					nu[29] += num;
					break;
				case "新疆":
					cu[30] += total;
					nu[30] += num;
					break;
				case "台湾":
					cu[31] += total;
					nu[31] += num;
					break;
				case "香港":
					cu[32] += total;
					nu[32] += num;
					break;
				case "澳门":
					cu[33] += total;
					nu[33] += num;
					break;
				}
			}
			logger.info(loc + ":" + total);
		}
		Document[] doc = new Document[34];
		for (int i = 0; i <= 33; i++) {
			doc[i] = new Document();
		}
		mongodbstorage.appendProvince(doc[0], "北京", cu[0], nu[0]);
		mongodbstorage.appendProvince(doc[1], "天津", cu[1], nu[1]);
		mongodbstorage.appendProvince(doc[2], "河北", cu[2], nu[2]);
		mongodbstorage.appendProvince(doc[3], "山西", cu[3], nu[3]);
		mongodbstorage.appendProvince(doc[4], "内蒙古", cu[4], nu[4]);
		mongodbstorage.appendProvince(doc[5], "辽宁", cu[5], nu[5]);
		mongodbstorage.appendProvince(doc[6], "吉林", cu[6], nu[6]);
		mongodbstorage.appendProvince(doc[7], "黑龙江", cu[7], nu[7]);
		mongodbstorage.appendProvince(doc[8], "上海", cu[8], nu[8]);
		mongodbstorage.appendProvince(doc[9], "江苏", cu[9], nu[9]);
		mongodbstorage.appendProvince(doc[10], "浙江", cu[10], nu[10]);
		mongodbstorage.appendProvince(doc[11], "安徽", cu[11], nu[11]);
		mongodbstorage.appendProvince(doc[12], "福建", cu[12], nu[12]);
		mongodbstorage.appendProvince(doc[13], "江西", cu[13], nu[13]);
		mongodbstorage.appendProvince(doc[14], "山东", cu[14], nu[14]);
		mongodbstorage.appendProvince(doc[15], "河南", cu[15], nu[15]);
		mongodbstorage.appendProvince(doc[16], "湖北", cu[16], nu[16]);
		mongodbstorage.appendProvince(doc[17], "湖南", cu[17], nu[17]);
		mongodbstorage.appendProvince(doc[18], "广东", cu[18], nu[18]);
		mongodbstorage.appendProvince(doc[19], "广西", cu[19], nu[19]);
		mongodbstorage.appendProvince(doc[20], "海南", cu[20], nu[20]);
		mongodbstorage.appendProvince(doc[21], "重庆", cu[21], nu[21]);
		mongodbstorage.appendProvince(doc[22], "四川", cu[22], nu[22]);
		mongodbstorage.appendProvince(doc[23], "贵州", cu[23], nu[23]);
		mongodbstorage.appendProvince(doc[24], "云南", cu[24], nu[24]);
		mongodbstorage.appendProvince(doc[25], "西藏", cu[25], nu[25]);
		mongodbstorage.appendProvince(doc[26], "陕西", cu[26], nu[26]);
		mongodbstorage.appendProvince(doc[27], "甘肃", cu[27], nu[27]);
		mongodbstorage.appendProvince(doc[28], "青海", cu[28], nu[28]);
		mongodbstorage.appendProvince(doc[29], "宁夏", cu[29], nu[29]);
		mongodbstorage.appendProvince(doc[30], "新疆", cu[30], nu[30]);
		mongodbstorage.appendProvince(doc[31], "台湾", cu[31], nu[31]);
		mongodbstorage.appendProvince(doc[32], "香港", cu[32], nu[32]);
		mongodbstorage.appendProvince(doc[33], "澳门", cu[33], nu[33]);
		for (int i = 0; i <= 33; i++) {
			vec.add(doc[i]);
		}
		mongodbstorage.appendArray(document, "category", vec);
		mongodbstorage.insertOne(collection, document);
	}

	/**
	 * 学历分布
	 * 
	 * @param stmt
	 * @param tableName
	 * @throws SQLException
	 * @throws IOException
	 */
	public void countEducationDistribution(String tableName, String mongoTable, String family) throws SQLException, IOException {/////
		MongoClient mongoClient = mongodbstorage.setUp();
		// String sql = "select LOCATION,count(1) as count,sum(AMOUNT) from " +
		// tableName
		// + " where ISPERCEPTED = 'no' group by LOCATION order by count desc";
		// ResultSet results = stmt.executeQuery(sql);

		// 建立表的连接
		Table table = connection.getTable(TableName.valueOf(tableName));
		// 创建一个空的Scan实例
		Scan scan1 = new Scan();
		// 可以指定具体的列族列
		scan1.addColumn(Bytes.toBytes("TAG_DATA"), Bytes.toBytes("EDUCATION")).addColumn(Bytes.toBytes(family),
				Bytes.toBytes("AMOUNT"));
		scan1.setCaching(60);
		scan1.setMaxResultSize(1 * 1024 * 1024); // 100k （MB1 * 1024 * 1024）
		scan1.setFilter(new PageFilter(1000));

		// 在行上获取遍历器
		ResultScanner scanner1 = table.getScanner(scan1);

		Map map = mongodbstorage.create(mongoTable, "job_education_distribution", mongoClient);// mongodb集合,key,value
		MongoCollection<Document> collection = (MongoCollection<Document>) map.get("collection");
		Document document = (Document) map.get("document");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		String da = sdf.format(date);
		mongodbstorage.appendString(document, "date", da);
		int[] cu = new int[8];
		int[] nu = new int[8];
		Vector<Document> vec = new Vector<Document>();
		for (Result res : scanner1) {
			String loc = (new String(CellUtil.cloneValue(res.rawCells()[1]))).split("-")[0];
			int total = Integer.parseInt(new String(CellUtil.cloneValue(res.rawCells()[0])));
			int num = 1;
			if (!educations.contains(loc)) {
			} else {
				// String prov = jobanalysisreposity.appendProvinceDistribution(document, loc,
				// total, mongoClient);
				String prov = loc;
				switch (prov) {
				case "初中":
					cu[0] += total;
					nu[0] += num;
					break;
				case "高中":
					cu[1] += total;
					nu[1] += num;
					break;
				case "中技":
					cu[2] += total;
					nu[2] += num;
					break;
				case "中专":
					cu[3] += total;
					nu[3] += num;
					break;
				case "大专":
					cu[4] += total;
					nu[4] += num;
					break;
				case "本科":
					cu[5] += total;
					nu[5] += num;
					break;
				case "硕士":
					cu[6] += total;
					nu[6] += num;
					break;
				case "博士":
					cu[7] += total;
					nu[7] += num;
					break;
				}
			}
			logger.info(loc + ":" + total);
		}
		Document[] doc = new Document[8];
		for (int i = 0; i < 8; i++) {
			doc[i] = new Document();
		}
		mongodbstorage.appendExperience(doc[0], "初中", cu[0], nu[0]);
		mongodbstorage.appendExperience(doc[1], "高中", cu[1], nu[1]);
		mongodbstorage.appendExperience(doc[2], "中技", cu[2], nu[2]);
		mongodbstorage.appendExperience(doc[3], "中专", cu[3], nu[3]);
		mongodbstorage.appendExperience(doc[4], "大专", cu[4], nu[4]);
		mongodbstorage.appendExperience(doc[5], "本科", cu[5], nu[5]);
		mongodbstorage.appendExperience(doc[6], "硕士", cu[6], nu[6]);
		mongodbstorage.appendExperience(doc[7], "博士", cu[7], nu[7]);
		
		for (int i = 0; i < 8; i++) {
			vec.add(doc[i]);
		}
		mongodbstorage.appendArray(document, "category", vec);
		mongodbstorage.insertOne(collection, document);
	}

	/**
	 * 对数据（量）进行统计。
	 * 
	 * @param mongoTable
	 * @param mongoClient
	 * @param stmt
	 * @throws IOException
	 */
	public void doDataStatistics() throws IOException {
		// TODO 通过配置循环创建，不能硬编码，如果增加新的岗位分析，无法实现！
		operate("job_internet", "job_internet","PERCEPT_DATA");
//		operate("job_cloud", "job_cloud","cloud");
	}

	/**
	 * 数据统计
	 * 
	 * @param tableName
	 * @param mongoTable
	 * @param mongoClient
	 * @param stmt
	 * @throws IOException
	 */
	private void operate(String tableName, String mongoTable, String family) throws IOException {
		long start = new Date().getTime();
		try {
			countEducationDistribution(tableName, mongoTable,family);
			countProvinceDistribution(tableName, mongoTable,family);
		} catch (SQLException e) {
			logger.info(e.toString());
		}
		long end = new Date().getTime();
		long date = end - start;
		logger.info(String.valueOf(date));
	}

}
