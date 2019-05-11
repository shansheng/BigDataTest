/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * 岗位推荐
 * 
 * @author guoyi
 *
 */
public class Recommend {
	private Logger logger = LoggerFactory.getLogger(getClass());
	DecimalFormat df = new DecimalFormat("######0.00");

	/**
	 * @Title: ${jobRecommend} @Description: ${根据输入的技能点推荐岗位} @param ${skills：输入的技能点
	 *         number： 输出岗位的数量} @return ${void} @throws
	 */
	public List<RecommendBean> jobRecommend(String studentid, int number, String term) {
		List<RecommendBean> recommendBeans = new ArrayList<>();
		Map<String, Object> result = connect(studentid, term);
		List<String> skills = (List<String>) result.get("skill");
		List<Double> weights = (List<Double>) result.get("weight");
		List<String> description = new ArrayList<>();
		List<String> companyName = new ArrayList<>();
		List<String> provice = new ArrayList<>();
		List<String> city = new ArrayList<>();
		List<String> image = new ArrayList<>();
		List<String> jobname = new ArrayList<>();
		List<String> id = new ArrayList<>();
		List<Double> number1 = new ArrayList<>();
		List<Double> similarity = new ArrayList<>();
		List<List<Double>> weight = new ArrayList<>();
		MongoClient mongoClient = new MongoClient("10.10.4.35", 27017);
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase("employ");
			MongoCollection<Document> collection = mongoDatabase.getCollection("job");
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				List<Document> jobs = (List<Document>) doc.get("jobs");
				for (Document s : jobs) {
					List<Double> w = new ArrayList<>();
					description.add(s.getString("skills"));
					jobname.add(s.getString("job_name"));
					companyName.add(s.getString("company_name"));
					provice.add(s.getString("provice"));
					city.add(s.getString("city"));
					id.add(s.getString("id"));
					image.add(s.getString("image"));
					number1.add((double) getNumber(s.getString("skills"), skills, weights)
							.get("similarity"));
					similarity.add((double) getNumber(s.getString("skills"), skills, weights)
							.get("similarity"));
					weight.add((List<Double>) getNumber(s.getString("skills"), skills, weights)
							.get("weight"));

				}
			}
			mongoClient.close();
		} catch (

		Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
			mongoClient.close();
		}
		
		Map<String, Object> res = new HashMap<>();
		for (int x = 1; x < Integer.parseInt(term); x++) {
			Map<String, Object> re = connect(studentid, Integer.toString(x));
			res.put(Integer.toString(x), re);
		}

		for (int i = 0; i < number; i++) {
			List<SkillBean> beans = new ArrayList<>();
			List<String> titles = new ArrayList<>();
			int index = 0;
			double replace = 0;
			for (int k = 0; k < number1.size(); k++) {
				if (number1.get(k) > replace) {
					replace = number1.get(k);
					index = k;
					number1.remove(k);
					number1.add(k, 0.0);
				}
			}
			if (replace > 0) {
				RecommendBean recommendBean = new RecommendBean();
				recommendBean.setJobname(jobname.get(index));
				int s = (int) (similarity.get(index) * 100);
				recommendBean.setSimilarity(s + "%");
				recommendBean.setProvince(provice.get(index));
				recommendBean.setCity(city.get(index));
				recommendBean.setId(id.get(index));
				recommendBean.setPicurl(image.get(index));
				recommendBean.setCompany(companyName.get(index));
				SkillBean bean = new SkillBean();
				bean.setValue(weight.get(index));
				switch (term) {
				case "1":
					bean.setName("第一学期");
					titles.add("第一学期");
					break;
				case "2":
					bean.setName("第二学期");
					titles.add("第二学期");
					break;
				case "3":
					bean.setName("第三学期");
					titles.add("第三学期");
					break;
				default:
					break;
				}
				beans.add(bean);
				recommendBean.setSkill(description.get(index));
				Map<String, Object> map = new HashMap<>();
				if (Integer.parseInt(term) > 1) {
					for (int x = 1; x < Integer.parseInt(term); x++) {
						// 每学期结果Map<String, Object> result = connect(id, term);
						List<Double> doubles = terms(studentid, Integer.toString(x),
								description.get(index),(Map<String, Object>)res.get(Integer.toString(x)));
						for (Double v : doubles)
							System.out.println(v);
						System.out.println("----");
						SkillBean bean1 = new SkillBean();
						bean1.setValue(doubles);
						switch (x) {
						case 1:
							bean1.setName("第一学期");
							titles.add("第一学期");
							break;
						case 2:
							bean1.setName("第二学期");
							titles.add("第二学期");
							break;
						case 3:
							bean1.setName("第三学期");
							titles.add("第三学期");
							break;
						default:
							break;
						}
						beans.add(bean1);
					}
				}
				recommendBean.setTitles(titles);
				recommendBean.setSkillBeans(beans);
				recommendBeans.add(recommendBean);

			} else
				break;
		}
		return recommendBeans;
	}

	public List<Double> terms(String id, String term, String skills, Map<String, Object> result) {
		List<Double> weights = new ArrayList<>();
		List<String> skill = (List<String>) result.get("skill");
		List<Double> weight = (List<Double>) result.get("weight");
		for (String s : skills.split(","))
			if (skill.contains(s.toLowerCase()))
				weights.add(weight.get(findPosition(skill, s)));
			else
				weights.add(0.0);
		return weights;
	}

	public int findPosition(List<String> list, String s) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).toLowerCase().equals(s.toLowerCase()))
				return i;
		return -1;
	}

	public Map<String, Object> connect(String studentID, String term) {
		MongoClient mongoClient = new MongoClient("10.10.4.35", 27017);
		MongoDatabase mongoDatabase = mongoClient.getDatabase("major");
		MongoCollection<Document> collection = mongoDatabase.getCollection("major_course");
		List<String> allskills = new ArrayList<>();
		List<Double> allweights = new ArrayList<>();
		// 声明Connection对象
		Connection con = null;
		// 驱动程序名
		String driver = "com.mysql.jdbc.Driver"; // URL指向要访问的数据库名mydata
		String url = "jdbc:mysql://10.10.0.161:3306/douxue";
		// MySQL配置时的用户名
		String user = "root";
		// MySQL配置时的密码
		String password = "123456";
		// 遍历查询结果集
		ResultSet rs = null;
		try {
			// 加载驱动程序
			Class.forName(driver);
			// 1.getConnection()方法，连接MySQL数据库！！
			con = DriverManager.getConnection(url, user, password);
			if (!con.isClosed())
				System.out.println("Succeeded connecting to the Database!");
			// 2.创建statement类对象，用来执行SQL语句！！
			Statement smt = con.createStatement();
			// 要执行的SQL语句
			String sql = "select * from t_class_score where userid = " + studentID + " and term <= "
					+ Integer.parseInt(term);
			// 3.ResultSet类，用来存放获取的结果集！！
			rs = smt.executeQuery(sql);
			Map<String, Double> skills_weights = new HashMap<>();
			while (rs.next()) {
				String coursename = rs.getString("coursename");
				int xf = rs.getInt("xf");
				double df = 0.0;
				if (rs.getString("df").equals("优秀"))
					df = 90.0;
				else if (rs.getString("df").equals("良好"))
					df = 80.0;
				else if (rs.getString("df").equals("一般"))
					df = 70.0;
				else if (rs.getString("df").equals("中等"))
					df = 70.0;
				else if (rs.getString("df").equals("及格"))
					df = 60.0;
				else if (rs.getString("df").equals("不及格"))
					df = 50.0;
				else if (rs.getString("df").equals("不合格"))
					df = 50.0;
				else if (rs.getString("df").equals("停考"))
					df = 30.0;
				else if (rs.getString("df").equals("缺考"))
					df = 30.0;
				else if (rs.getString("df").contains("/"))
					df = Double.parseDouble(rs.getString("df").split("/")[0]);
				else
					df = rs.getDouble("df");
				if (StudentData.classify(coursename).equals("技能")) {
					if (CourseMatch.course_depedency.containsKey(coursename)) {
						String depency = CourseMatch.course_depedency.get(coursename);
						if (depency.length() == 0 || depency == null) {
							System.out.println("课程: " + coursename + "还未定义技能点");
						} else {
							FindIterable<Document> findIterable = collection.find();
							MongoCursor<Document> mongoCursor = findIterable.iterator();
							while (mongoCursor.hasNext()) {
								Document document = mongoCursor.next();
								if (document.getString("code").equals(depency)) {
									@SuppressWarnings("unchecked")
									List<Document> skills = (List<Document>) document
											.get("skills");
									for (Document skill : skills) {
										if (skills_weights.containsKey(skill
												.getString("keyskill"))) {
											System.out.println(
													skills_weights.get(
															skill.getString("keyskill")));
											double newweight = skills_weights
													.get(skill.getString(
															"keyskill"))
													+ skill.getInteger(
															"weight")
															* 1.0
															/ 1000000
															* xf
															* 12.0
															* df;
											skills_weights.put(skill
													.getString("keyskill"),
													newweight);
										} else
											skills_weights.put(skill
													.getString("keyskill"),
													skill.getInteger(
															"weight")
															* 1.0
															/ 1000000
															* xf
															* 12.0
															* df);
									}
								}
							}
						}
					} else
						System.out.println("缺少课程: '" + coursename + "'的依赖课程的定义。");
				}
			}
			rs.close();
			con.close();
			mongoClient.close();

			List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
					skills_weights.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
				@Override
				public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
					return o2.getValue().compareTo(o1.getValue()); // 降序
					// return o1.getValue().compareTo(o2.getValue()); // 升序
				}
			});
			for (Map.Entry<String, Double> mapping : list) {
				allskills.add(mapping.getKey());
				allweights.add(Double.parseDouble(df.format(mapping.getValue())));
			}

		} catch (ClassNotFoundException e) {
			// 数据库驱动类异常处理
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			// 数据库连接失败异常处理
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				con.close();
				mongoClient.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> result = new HashMap<>();
		result.put("skill", allskills);
		result.put("weight", allweights);
		return result;
	}

	/**
	 * @Title: ${getNumber} @Description: ${输出每个岗位中包含技能点的数量} @param
	 *         ${description：岗位描述 skills： 输入的技能点} @return ${岗位包含技能点的数量} @throws
	 */
	public static Map<String, Object> getNumber(String description, List<String> skills, List<Double> weights) {
		int z = 0;
		List<Double> weight = new ArrayList<>();
		for (int k = 0; k < description.split(",").length; k++) {
			if (skills.contains(description.split(",")[k])) {
				for (int i = 0; i < skills.size(); i++)
					if (skills.get(i).toLowerCase()
							.equals(description.split(",")[k].toLowerCase())) {
						weight.add(weights.get(i));
						break;
					}
				z++;
			} else
				weight.add(0.0);
		}
		Map<String, Object> result = new HashMap<>();
		result.put("similarity", z * 1.0 / description.split(",").length);
		result.put("weight", weight);
		return result;
	}

	public static void main(String[] args) {
		Recommend recommend = new Recommend();
		recommend.jobRecommend("2039", 15, "2");
	}
}
