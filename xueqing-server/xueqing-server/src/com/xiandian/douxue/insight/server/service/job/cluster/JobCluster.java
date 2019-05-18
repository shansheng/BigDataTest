/*
 * Copyright (c) 2017, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.douxue.insight.server.service.job.cluster;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.xiandian.douxue.insight.server.dao.HBaseStorage;
import com.xiandian.douxue.insight.server.dao.JobDataReposity;
import com.xiandian.douxue.insight.server.dao.MongoDBStorage;
import com.xiandian.douxue.insight.server.utils.ReadFile;
import com.xiandian.douxue.insight.server.utils.UtilTools;

/**
 * JobCluster 岗位聚类实现类。
 * 
 * @author XianDian Cloud Team
 */
public class JobCluster {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static MongoDBStorage mongostorage = MongoDBStorage.getInstance();
	final static List dis = new ArrayList<>();
	private static HBaseStorage hbaseStorage = HBaseStorage.getInstance();
	private static JobDataReposity jobDataReposity = JobDataReposity.getInstance();

	private Properties hbaseProperties = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/hbase.properties");
	private Properties sparkProperties = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/spark.properties");

	/**
	 * 岗位循环聚类
	 * 
	 * @param industry
	 *            岗位行业
	 * @param category
	 *            岗位行业类别
	 * @param dictionary
	 *            岗位技能点词典
	 * @param clusterN
	 *            聚类数量
	 * @param mongoClient
	 */
	public void cluster(String industry, String category, List<String> dictionary, int clusterN,
			MongoClient mongoClient) {
		List<String> id = new ArrayList<>();
		List<String> jobname = new ArrayList<>();

		try {
			// id = jobDataReposity.queryDataByColumn("job_" + industry, industry, "ID");
			// jobname = jobDataReposity.queryDataByColumn("job_" + industry, industry,
			// "JOB_NAME");
			id = jobDataReposity.queryDataByColumn("job_internet", "TAG_DATA", "ID");
			jobname = jobDataReposity.queryDataByColumn("job_internet", "TAG_DATA", "JOB_NAME");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, Map<String, String>> allJob = allJobClassification(id, jobname, industry);// 将取出的岗位群分类
		List<String> ids = new ArrayList<>();
		if (allJob != null) {
			Map<String, String> category1 = allJob.get(category);// 取小类
			for (Map.Entry<String, String> m : category1.entrySet())
				ids.add(m.getKey()); // 取出所有开发类对应的ID
			if (ids.size() > 1) {
				List<String> jdes = new ArrayList<>();
				List<String> jname = new ArrayList<>();
				try {
					// jdes = jobDataReposity.queryTableByCondition(ids, "job_" + industry,
					// industry,
					// "ID", "DESCRIPTION");
					jdes = jobDataReposity.queryTableByCondition(ids, "job_internet", "TAG_DATA", "ID", "DESCRIPTION");// 根据ID取出所有开发类的岗位描述
					jname = jobDataReposity.queryTableByCondition(ids, "job_internet", "TAG_DATA", "ID", "JOB_NAME");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tfKmeans(jdes, jname, dictionary, industry, category, clusterN, mongoClient);// 对开发类岗位描述进行聚类
			} else
				logger.info(industry + "-" + category + " not enough data.");
		}
	}

	/**
	 * 根据词频聚类并存储
	 * 
	 * @param description
	 *            岗位描述
	 * @param jobname
	 *            岗位名称
	 * @param jobSkills
	 *            技能词典
	 * @param industryName
	 *            产业
	 * @param categoryName
	 *            类别
	 * @param clusterNum
	 *            聚类数量
	 * @param mongoClient
	 */
	public void tfKmeans(List<String> description, List<String> jobname, List<String> jobSkills, String industryName,
			String categoryName, int clusterNum, MongoClient mongoClient) {
		// String tableName = industryName + "_" + categoryName;
		List<String> descriptions = new ArrayList<>();
		List<String> jobnames = new ArrayList<>();
		for (int i = 0; i < description.size(); i++) {
			String ss = description.get(i);
			if (ss != null && !ss.equals("")) {
				descriptions.add(description.get(i));
				jobnames.add(jobname.get(i));
			}
		}
		List<double[]> weights = Word2TF.word2weight(descriptions, jobSkills);
		List<Integer> index1 = Word2TF.clean(weights);
		List<String> descriptions1 = new ArrayList<>();
		List<String> jobname1 = new ArrayList<>();
		List<double[]> outcome1 = new ArrayList<>();
		for (int i = 0; i < index1.size(); i++) {
			descriptions1.add(descriptions.get(index1.get(i)));
			outcome1.add(weights.get(index1.get(i)));
			jobname1.add(jobnames.get(index1.get(i)));
		}
		List<String> cluster = new ArrayList<>();
		List<String> names = new ArrayList<>();
		List<double[]> jobWeight = new ArrayList<>();
		List<Integer> indexs = kMeans(outcome1, clusterNum);
		int js = 15;
		Map map = mongostorage.create("job_" + industryName, "job_" + industryName + "_" + categoryName, mongoClient);
		MongoCollection<Document> collection = (MongoCollection<Document>) map.get("collection");
		Document document = (Document) map.get("document");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		String da = sdf.format(date);
		mongostorage.appendString(document, "date", da);
		mongostorage.append(document, "count", clusterNum);
		Document[] docu2 = new Document[clusterNum];
		// 岗位数量分布
		Map maps = mongostorage.create("job_" + industryName, industryName + "_" + categoryName, mongoClient);
		MongoCollection<Document> collections = (MongoCollection<Document>) maps.get("collection");
		Document documents = (Document) maps.get("document");
		Document[] docus2 = new Document[clusterNum];
		mongostorage.appendString(documents, "date", da);
		mongostorage.appendString(documents, "type", industryName);
		mongostorage.append(documents, "count", clusterNum);
		List<String> existNames = new ArrayList<>();
		for (int i = 0; i < clusterNum; i++) {
			Document[] docu = new Document[js];
			logger.info("cluster" + (i + 1) + ":");
			for (int j = 0; j < indexs.size(); j++) {
				if (indexs.get(j) == i) {
					cluster.add(Word2TF.word2string(descriptions1.get(j), jobSkills));
					names.add(jobname1.get(j).trim().toLowerCase());
					jobWeight.add(outcome1.get(j));
				}
			}
			logger.info("cluster" + (i + 1) + "  共有：" + cluster.size());
			int amount = cluster.size();
			ClusterDataStorage.save(cluster, js, docu);
			cluster = new ArrayList<>();
			docu2[i] = new Document();
			String name = getName(names, jobWeight, existNames);
			mongostorage.appendString(docu2[i], "job_name", name);
			mongostorage.append(docu2[i], "amount", amount);
			mongostorage.appendArray(docu2[i], "skills", Arrays.asList(docu));

			docus2[i] = new Document();
			mongostorage.appendString(docus2[i], "name", name);
			mongostorage.append(docus2[i], "amount", amount);
			// MongoDBStorage.appendAnalyse(document , docu, jobnames[i]);
			existNames.add(name);
		}
		mongostorage.appendString(document, "job_category_name", industryName);
		mongostorage.appendArray(document, "category", Arrays.asList(docu2));
		mongostorage.insertOne(collection, document);
		mongostorage.appendArray(documents, "category", Arrays.asList(docus2));
		mongostorage.insertOne(collections, documents);
	}

	/**
	 * kmeans聚类算法
	 * 
	 * @param des1
	 *            输入向量
	 * @param cluster
	 *            聚类数量
	 * @return
	 */
	public List<Integer> kMeans(List<double[]> des1, int cluster) {
		List<Integer> indexs = new ArrayList<>();
		SparkConf conf  = new SparkConf().setMaster("local[2]")
				.setAppName("xueqing-server").set("spark.driver.allowMultipleContexts","true");
		JavaSparkContext jsc = new JavaSparkContext(conf);

		JavaRDD<double[]> data = jsc.parallelize(des1);
		JavaRDD<Vector> parsedata = data.map(s -> {
			Vector value = Vectors.dense(s);
			return value;
		});

		parsedata.cache();
		KMeansModel clusters = KMeans.train(parsedata.rdd(), cluster, 20, 10);

		indexs = clusters.predict(parsedata).collect();
		jsc.stop();
		return indexs;
	}

	/**
	 * 岗位分类
	 * 
	 * @param id
	 *            岗位id
	 * @param jobname
	 *            岗位名称
	 * @param industry
	 *            岗位产业
	 * @return
	 */
	private Map<String, Map<String, String>> allJobClassification(List<String> id, List<String> jobname,
			String industry) {
		Map<String, String> allJob = new HashMap<String, String>();
		for (int i = 0; i < id.size(); i++)
			allJob.put(id.get(i), jobname.get(i));

		Map<String, List<Object>> map = new HashMap<>();
		String JsonContext = ReadFile
				.ReadFile(System.getProperty("user.dir") + "/configuration/job_classification.json");
		try {
			JSONObject jsonObject = new JSONObject(JsonContext);
			System.out.println(jsonObject);
			JSONArray total = jsonObject.getJSONArray("total");
			for (int i = 0; i < total.length(); i++) {
				JSONObject catorage = total.getJSONObject(i);
				String industry1 = catorage.getString("industry");
				JSONArray classifictions = catorage.getJSONArray("classification");
				List<Object> listson = new ArrayList<>();
				List<String> cate = new ArrayList<>();
				List<String[]> jobwords = new ArrayList<>();
				for (int j = 0; j < classifictions.length(); j++) {
					JSONObject jsonObject2 = classifictions.getJSONObject(j);
					String[] k = jsonObject2.getString("keywords").split(",");
					jobwords.add(k);
					cate.add(jsonObject2.getString("category"));
					listson.add(jobwords);
					listson.add(cate);
				}
				map.put(industry1, listson);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		List<Object> classification = (List<Object>) map.get(industry);
		Map<String, Map<String, String>> alljobs = new HashMap<String, Map<String, String>>();
		for (int i = 0; i < ((List<String>) classification.get(1)).size(); i++) {
			Map<String, String> ca = new HashMap<String, String>();
			alljobs.put(((List<String>) classification.get(1)).get(i), ca);
		}
		Map<String, String> allJob1 = allJob;
		for (Map.Entry<String, String> m : allJob.entrySet()) {
			for (int i = 0; i < ((List<String>) classification.get(1)).size(); i++) {
				if (judge(((List<String[]>) classification.get(0)).get(i), m.getValue())) {
					alljobs.get(((List<String>) classification.get(1)).get(i)).put(m.getKey(), m.getValue());
					// allJob1.remove(m.getKey());
					break;
				}
			}
		}
		for (Map.Entry<String, String> k : allJob1.entrySet())
			alljobs.get("other").put(k.getKey(), k.getValue());
		for (Map.Entry<String, Map<String, String>> a : alljobs.entrySet())
			logger.info(a.getKey() + "  " + a.getValue().size());
		return alljobs;
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean judge(String[] a, String b) {
		for (String x : a)
			if (b.contains(x))
				return true;
		return false;
	}

	/**
	 * 获取一类岗位名称
	 * 
	 * @param names
	 *            所有岗位名称
	 * @param weights
	 *            一类向量
	 * @param nas
	 * @return
	 */
	public String getName(List<String> names, List<double[]> weights, List<String> nas) {
		List<Integer> indexes = new ArrayList<>();
		List<Double> cosses = new ArrayList<>();
		for (int i = 0; i < weights.size(); i++)
			cosses.add(coss(weights.get(i), weights));
		for (int i = 0; i < cosses.size(); i++) {
			double replace = 100000000.0;
			int index = 0;
			for (int k = 0; k < cosses.size(); k++) {
				if (cosses.get(k) < replace) {
					replace = cosses.get(k);
					index = k;
				}
			}
			indexes.add(index);
			cosses.remove(index);
			cosses.add(index, 100000000.0);
		}
		for (int i : indexes) {
			if (!nas.contains(names.get(i).toLowerCase())) {
				return names.get(i);
			}
		}
		return names.get(indexes.get(0));
	}

	/**
	 * 向量与其他所有向量距离计算
	 * 
	 * @param one
	 * @param all
	 * @return
	 */

	public double coss(double[] one, List<double[]> all) {
		double sum = 0;
		for (double[] x : all)
			sum = sum + sum(x, one);
		return sum;
	}

	/**
	 * 两个向量距离计算
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public double sum(double[] one, double[] two) {
		double sum = 0;
		for (int i = 0; i < one.length; i++)
			sum = sum + (one[i] - two[i]) * (one[i] - two[i]);
		return sum;
	}
}
