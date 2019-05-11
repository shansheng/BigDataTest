/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.service.job.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.ml.feature.Word2Vec;
import org.apache.spark.ml.feature.Word2VecModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.*;
import org.apache.spark.sql.SparkSession;

/**
 * spark中内置的文字转VEC向量
 * 
 * @author XianDian Cloud Team
 *
 */
public class Word2VEC {
	/**
	 * 清洗技能点小于5个的岗位描述
	 * 
	 * @param dou
	 * @return
	 */
	public static List<Integer> clean(List<String> dou) {
		List<Integer> index = new ArrayList<>();
		for (int i = 0; i < dou.size(); i++) {
			if (dou.get(i).split("/").length > 5) {
				index.add(i);
			}
		}
		return index;
	}

	/**
	 * 使用spark VEC 将岗位描述转换为vec向量
	 * 
	 * @param descriptions
	 *                岗位描述
	 * @return
	 */
	public static List<String> word2Vec(List<String> descriptions) {
		SparkSession spark = SparkSession.builder().appName("Word2VecDemo").master("local[2]").getOrCreate();
		List<Row> data = new ArrayList<>();
		for (String str : descriptions)
			data.add(RowFactory.create(Arrays.asList(str.split("/"))));
		StructType schema = new StructType(new StructField[] { new StructField("text",
				new ArrayType(DataTypes.StringType, true), false, Metadata.empty()) });
		Dataset<Row> documentDF = spark.createDataFrame(data, schema);

		Word2Vec word2Vec = new Word2Vec().setInputCol("text").setOutputCol("result").setVectorSize(150)
				.setMinCount(0);
		Word2VecModel model = word2Vec.fit(documentDF);
		Dataset<Row> result = model.transform(documentDF);
		List<String> weight = new ArrayList<>();
		for (Row r : result.select("result").takeAsList(descriptions.size())) {
			// System.out.println(r.toString());
			String i = r.toString();
			weight.add(i.substring(2, i.length() - 2));
			spark.close();
		}
		return weight;
	}

	/**
	 * 将spark输出的向量结果转为Double型
	 * 
	 * @param weights
	 * @return
	 */
	public static List<double[]> transform(List<String> weights) {
		int t = weights.get(0).split(",").length;
		List<double[]> weight = new ArrayList<double[]>();
		double[] w = new double[t];
		for (String s : weights) {
			for (int i = 0; i < s.split(",").length; i++) {
				w[i] = Double.parseDouble(s.split(",")[i]);
			}
			weight.add(w);
			w = new double[t];
		}
		return weight;
	}
}
