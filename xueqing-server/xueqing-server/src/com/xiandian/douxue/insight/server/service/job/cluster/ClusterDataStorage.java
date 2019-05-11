/**
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.service.job.cluster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiandian.douxue.insight.server.dao.MongoDBStorage;

/**
 * 将聚类结果保存到mangoDB
 * 
 * @author XianDian Cloud Team
 */
public class ClusterDataStorage {
	private static Logger logger = LoggerFactory.getLogger(ClusterDataStorage.class);
	private static MongoDBStorage mongostorage = MongoDBStorage.getInstance();

	/**
	 * 
	 * @param descriptions
	 *                岗位技能点描述
	 * @param num
	 *                展示聚类结果数量
	 * @param docu
	 *                mangoDB document
	 */
	public static void save(List<String> descriptions, int num, Document[] docu) {
		List<String> dictionary = unionAll(descriptions);
		List<double[]> weights = Word2TF.word2weight(descriptions, unionAll(descriptions));
		double[] weight = getWeight(weights);
		Integer[] order = order(weight);
		weight = getWeight(weights);
		if (order == null) {
			logger.error("Job Cluster Data Save Order Data is Null");
			return;
		}
		if (num < order.length) {
			for (int i = 0; i < num; i++) {
				docu[i] = new Document();
				mongostorage.appendString(docu[i], "Skill_name", dictionary.get(order[i]));
				mongostorage.appendDouble(docu[i], "Skill_weight",
						((int) (weight[order[i]] * 100)) / 100.0);
			}
		} else {
			for (int i = 0; i < order.length; i++) {
				docu[i] = new Document();
				mongostorage.appendString(docu[i], "Skill_name", dictionary.get(order[i]));
				mongostorage.appendDouble(docu[i], "Skill_weight",
						((int) (weight[order[i]] * 100)) / 100.0);
			}
		}
	}

	/**
	 * 对聚类岗位根据权重排序
	 * 
	 * @param weight
	 *                技能权重
	 * @return 排序后的index
	 */
	public static Integer[] order(double[] weight) {
		if (weight != null && weight.length > 0) {
			Integer[] orders = new Integer[weight.length];
			for (int i = 0; i < weight.length; i++) {
				orders[i] = max(weight);
				weight[max(weight)] = 0;
			}
			return orders;
		}
		return null;
	}

	/**
	 * 取权重中最大值
	 * 
	 * @param weight
	 * @return 最大序列
	 */
	public static int max(double[] weight) {
		if (weight != null && weight.length > 0) {
			int index = 0;
			double copy = weight[0];
			for (int i = 1; i < weight.length; i++)
				if (weight[i] > copy) {
					index = i;
					copy = weight[i];
				}
			return index;
		}
		return 0;
	}

	/**
	 * 技能点向量求和
	 * 
	 * @param weights
	 * @return 求和后的岗位技能点
	 */
	public static double[] getWeight(List<double[]> weights) {
		if (weights != null && weights.size() > 0) {
			int width = weights.get(0).length;
			double[] weight = new double[width];
			int length = weights.size();
			double sum = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < length; j++)
					sum = sum + weights.get(j)[i];
				weight[i] = sum / length;
				sum = 0;
			}
			return weight;
		}
		return null;
	}

	/**
	 * 所有岗位技能点并集
	 * 
	 * @param des
	 *                所有技能点
	 * @return 技能点并集
	 */
	public static List<String> unionAll(List<String> des) {
		if (des != null && des.size() > 0) {
			String[] total = des.get(0).split("/");
			if (des.size() > 1) {
				for (String str : des)
					total = union(total, str.split("/"));
			}
			List<String> dic = new ArrayList<String>();
			for (String str : total)
				dic.add(str);
			return dic;
		}
		return null;
	}

	/**
	 * 合并两个技能点描述
	 * 
	 * @param arr1
	 *                第一个技能点描述
	 * @param arr2
	 *                第二个技能点描述
	 * @return 两个技能描述并集
	 */
	public static String[] union(String[] arr1, String[] arr2) {
		Set<String> set = new HashSet<String>();
		for (String str : arr1)
			set.add(str.toLowerCase());
		for (String str : arr2)
			set.add(str.toLowerCase());
		String[] result = {};
		return set.toArray(result);
	}

}
