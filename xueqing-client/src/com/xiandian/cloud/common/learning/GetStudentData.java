package com.xiandian.cloud.common.learning;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class GetStudentData {
	private Logger logger = LoggerFactory.getLogger(getClass());
	DecimalFormat df = new DecimalFormat("######0.0");

	public List<Object> connect(String studentID) {
		MongoClient mongoClient = new MongoClient("10.10.4.35", 27017);
		MongoDatabase mongoDatabase = mongoClient.getDatabase("major");
		MongoCollection<Document> collection = mongoDatabase.getCollection("major_course");
		// 声明Connection对象
		Connection con = null;
		String driver = "com.mysql.jdbc.Driver"; // URL指向要访问的数据库名mydata
		String url = "jdbc:mysql://10.10.0.161:3306/douxue";
		// MySQL配置时的用户名
		String user = "root";
		// MySQL配置时的密码
		String password = "123456";
		// 遍历查询结果集
		ResultSet rs = null;
		List<Object> alls = new ArrayList<>();
		List<String> allskills = new ArrayList<>();
		List<Double> allweights = new ArrayList<>();
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
			String sql = "select * from t_class_score where userid = " + studentID;
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
				else if (rs.getString("df").equals("及格"))
					df = 60.0;
				else if (rs.getString("df").equals("不及格"))
					df = 50.0;
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
						System.out.println("缺少课程: " + coursename + "的依赖课程的定义。");
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
			alls.add(allskills);
			alls.add(allweights);
			for (int i = 0; i < allskills.size(); i++)
				System.out.println(allskills.get(i) + ": " + allweights.get(i));

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
		return alls;
	}

	public Map<String,Object> GetJobData(String industry, String cluster, String id, int total_study_time, int skill_number) {
		Map<String,Object> map=new HashMap<>();
		MongoClient mongoClient = new MongoClient("10.10.4.35", 27017);
		List<List<Object>> allSimilarities = new ArrayList<>();
		List<String> jobnames = new ArrayList<>();
		List<List<Object>> skills = new ArrayList<>();
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");
			MongoCollection<Document> collection = mongoDatabase.getCollection("all_cluster_job");
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document document = mongoCursor.next();
				@SuppressWarnings("unchecked")
				List<Document> categories = (List<Document>) document.get("categories");
				for (Document category : categories) {
					if (category.getString("job_category_name").equals(industry + "_" + cluster)) {
						@SuppressWarnings("unchecked")
						List<Document> categs = (List<Document>) category.get("category");
						for (Document categ : categs) {
							jobnames.add(categ.getString("job_name"));
							List<Document> skill = (List<Document>) categ.get("skills");
							List<Object> s = new ArrayList<>();
							List<String> sn = new ArrayList<>();
							List<Double> sw = new ArrayList<>();
							List<Double> sw1 = new ArrayList<>();
							for (Document ski : skill) {
								sn.add(ski.getString("Skill_name"));
								sw1.add(ski.getDouble("Skill_weight"));
							}
							double sum = 0.0;
							for (double d : sw1 )
								sum = sum + d;
							for (double d : sw1 )
								sw.add(Double.parseDouble(df.format(d/sum*total_study_time)));
							s.add(sn);
							s.add(sw);
							skills.add(s);
						}
					}
				}
			}
			mongoClient.close();
			List<Object> major_skill_weight = connect(id);
			List<String> major_skill = new ArrayList<>();
			List<Double> major_weight = new ArrayList<>();
			if (((List<String>) major_skill_weight.get(0)).size() > skill_number)
				major_skill = ((List<String>) major_skill_weight.get(0)).subList(0, skill_number);
			else
				major_skill = ((List<String>) major_skill_weight.get(0));
			if (((List<Double>) major_skill_weight.get(1)).size() > skill_number)
				major_weight = ((List<Double>) major_skill_weight.get(1)).subList(0, skill_number);
			else
				major_weight = ((List<Double>) major_skill_weight.get(1));

			for (List<Object> k : skills) {// 遍历岗位聚类结果中的每一类
				List<Object> oneMatch = new ArrayList<>();
				List<String> job_skill = new ArrayList<>();
				List<Double> job_weight = new ArrayList<>();
				if (((List<String>) k.get(0)).size() > skill_number)
					job_skill = ((List<String>) k.get(0)).subList(0, skill_number);
				else
					job_skill = ((List<String>) k.get(0));
				if (((List<String>) k.get(1)).size() > skill_number)
					job_weight = ((List<Double>) k.get(1)).subList(0, skill_number);
				else
					job_weight = ((List<Double>) k.get(1));
				List<String> allSkills = union(major_skill, job_skill);
				List<Double> major_vector = new ArrayList<>();
				List<Double> job_vector = new ArrayList<>();
				for (String str : allSkills) {
					if (major_skill.contains(str)) {
						major_vector.add(major_weight.get(major_skill.indexOf(str)));
					} else
						major_vector.add(0.0);
					if (job_skill.contains(str))
						job_vector.add(job_weight.get(job_skill.indexOf(str)));
					else
						job_vector.add(0.0);
				}
				double similarity = cosSimilarity(job_vector, major_vector);
				oneMatch.add(similarity);
				oneMatch.add(allSkills);
				oneMatch.add(major_vector);
				oneMatch.add(job_vector);
				allSimilarities.add(oneMatch);
			}
			List<Double> similarity = new ArrayList<>();
			
			map.put("percent", allSimilarities.get(0).get(0));
			map.put("skills", allSimilarities.get(0).get(1));
			map.put("stucount", allSimilarities.get(0).get(2));
			map.put("comcount", allSimilarities.get(0).get(3));
			for (int i = 0; i < allSimilarities.size(); i++) {
				
				similarity.add((double) allSimilarities.get(i).get(0));
			}
			int indexs = getMax(1, similarity).get(0);
//			for (String skill : (List<String>)allSimilarities.get(indexs).get(1))
//				System.out.print(skill+ " ");
//			System.out.println(" ");
//			for (double d1 : (List<Double>)allSimilarities.get(indexs).get(2))
//				System.out.print(d1+ " ");
//			System.out.println(" ");
//			for (double d2 : (List<Double>)allSimilarities.get(indexs).get(3))
//				System.out.print(d2+ " ");
			
		} catch (Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
			mongoClient.close();
		}
		return map;
	}
	
	public static List<Integer> getMax(int number, List<Double> similarity) {
		List<Integer> indexs = new ArrayList<>();
		List<Double> copy = new ArrayList<>();
		for (double dou : similarity) {
			copy.add(dou);
		}
		for (int i = 0; i < number; i++) {
			double ini = 0.0;
			int index = 0;
			for (int k = 0; k < copy.size(); k++)
				if (copy.get(k) > ini) {
					ini = copy.get(k);
					index = k;
				}
			indexs.add(index);
			copy.remove(index);
			copy.add(index, 0.0);
		}
		return indexs;
	}

	/**
	 * 计算两个技能权重的相似度
	 * 
	 * @param major
	 * @param job
	 * @return
	 */
	public static double cosSimilarity(List<Double> major, List<Double> job) {
		double zi = 0.0;
		double ma = 0.0;
		double jo = 0.0;
		for (int i = 0; i < job.size(); i++) {
			zi = zi + major.get(i) * job.get(i);
			ma = ma + major.get(i) * major.get(i);
			jo = jo + job.get(i) * job.get(i);
		}
		return zi / (Math.sqrt(ma) * Math.sqrt(jo));
	}

	public static List<String> union(List<String> arr1, List<String> arr2) {
		List<String> copy = new ArrayList<>();
		for (String str : arr1)
			copy.add(str);
		for (String str : arr2)
			if (!copy.contains(str))
				copy.add(str);
		return copy;
	}

	public static void main(String[] args) {
		GetStudentData data = new GetStudentData();
		data.GetJobData("cloud","develop","944", 3000, 10);
//		data.connect("2039");
	}
}
