/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.service.job.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * 技能描述转化为词频向量
 * 
 * @author XianDian Cloud Team
 *
 */
public class Word2TF {
	/**
	 * 清洗岗位描述中技能点小于5个的描述
	 * 
	 * @param dou
	 * @return
	 */
	public static List<Integer> clean(List<double[]> dou) {
		List<Integer> index = new ArrayList<>();
		for (int i = 0; i < dou.size(); i++) {
			if (sum(dou.get(i)) >= 0) {
				index.add(i);
			}
		}
		return index;
	}

	/**
	 * 向量求和
	 * 
	 * @param dous
	 * @return
	 */
	public static double sum(double[] dous) {
		double sum = 0;
		for (int i = 0; i < dous.length; i++)
			sum = sum + dous[i];
		return sum;
	}

	/**
	 * 将一个字符串根据技能词典转换为以/分开的技能点
	 * 
	 * @param str
	 * @param dictionary
	 * @return
	 */
	public static String word2string(String str, List<String> dictionary) {
		if (str.equals("")) {
			return "";
		} else {
			String vec = "";
			for (int i = 0; i < dictionary.size(); i++) {
				if (str.toLowerCase().contains(dictionary.get(i)))
					vec = vec + dictionary.get(i) + "/";

			}
			return vec;
		}
	}

	/**
	 * 批量转换岗位描述为规范技能点
	 * 
	 * @param strs
	 * @param dictionary
	 * @return
	 */
	public static List<String> word2Strings(List<String> strs, List<String> dictionary) {
		List<String> strings = new ArrayList<>();
		for (String str : strs)
			strings.add(word2string(str, dictionary));
		return strings;
	}

	/**
	 * 岗位描述根据转化为0-1向量
	 * 
	 * @param str
	 * @param dictionary
	 * @return
	 */
	public static double[] word2tf(String str, List<String> dictionary) {
		double[] vec = new double[dictionary.size()];
		for (int i = 0; i < dictionary.size(); i++) {
			if (str.toLowerCase().contains(dictionary.get(i)))
				vec[i] = 1;
			else
				vec[i] = 0;
		}
		return vec;
	}

	/**
	 * 批量转换为0-1向量
	 * 
	 * @param strs
	 * @param dictionary
	 * @return
	 */
	public static List<double[]> word2weight(List<String> strs, List<String> dictionary) {
		List<double[]> weights = new ArrayList<>();
		for (String str : strs)
			weights.add(word2tf(str, dictionary));
		return weights;
	}
}
