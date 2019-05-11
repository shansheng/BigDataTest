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
import java.util.Properties;
import java.util.Map.Entry;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class GetStudentMyData {

	private Logger logger = LoggerFactory.getLogger(getClass());
	DecimalFormat df = new DecimalFormat("######0.0");

	public List<Object> connect(String studentID, int term) {
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
			String sql = "select * from t_class_score where userid = " + studentID + " and term <=" + (term*2);
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
											double newweight = skills_weights
													.get(skill.getString(
															"keyskill"))
													+ skill.getInteger(
															"weight")
															* 1.0
															/ 1000000
															* xf
															* 24.0
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
															* 24.0
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
				allskills.add(mapping.getKey().toLowerCase());
				allweights.add(Double.parseDouble(df.format(mapping.getValue())));
//				System.out.println(mapping.getKey() + " " + mapping.getValue());
			}
			alls.add(allskills);
			alls.add(allweights);
//			for (int i = 0; i < allskills.size(); i++)
//				System.out.println(allskills.get(i) + ": " + allweights.get(i));

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

	public Map getStandardJob(String jobname, String id, int term) {
		Map<String, Object> result = new HashMap<>();
		List<Double> student_weights = new ArrayList<>();
		Map map = new HashMap<>();
		try {
			MongoClient mongoClient = new MongoClient("10.10.4.35", 27017);
			MongoDatabase mongoDatabase = mongoClient.getDatabase("job_skills");
			MongoCollection<Document> collection = mongoDatabase.getCollection("static_skills");
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			List<String> categories = new ArrayList<>();
			List<List<String>> skills = new ArrayList<>();
			List<List<Double>> weights = new ArrayList<>();
			List<List<String>> difficulities = new ArrayList<>();
			while (mongoCursor.hasNext()) {
				Document document = mongoCursor.next();
				if (document.getString("direction_name").equals(jobname)) {
					@SuppressWarnings("unchecked")
					List<Document> document_category = (List<Document>) document.get("categories");
					for (Document category : document_category) {
						categories.add(category.getString("category"));
						@SuppressWarnings("unchecked")
						List<Document> document_skills = (List<Document>) category
								.get("skill_points");
						List<String> category_skill = new ArrayList<>();
						List<Double> category_weight = new ArrayList<>();
						List<String> category_difficulity = new ArrayList<>();
						for (Document skill_point : document_skills) {
							category_skill.add(skill_point.getString("skill"));
							category_weight.add(Double.parseDouble(
									df.format(skill_point.getDouble("weight"))));
							category_difficulity.add(skill_point.getString("difficulty"));
						}
						skills.add(category_skill);
						weights.add(category_weight);
						difficulities.add(category_difficulity);
					}
				}
			}
			result.put("categories", categories);
			result.put("skills", skills);
			result.put("weights", weights);
			result.put("difficulities", difficulities);
			mongoClient.close();
			List<Object> outcome = connect(id, term);
			List<String> student_skill = (List<String>) outcome.get(0);
			List<Double> student_weight = (List<Double>) outcome.get(1);
			List<String> all_skills = new ArrayList<>();
			for (int i = 0; i < categories.size(); i++) {
				for (int j = 0; j < skills.get(i).size(); j++) {
					all_skills.add(skills.get(i).get(j).toLowerCase());
				}
			}
			for (int i = 0; i < categories.size(); i++) {
				if (student_skill.contains(categories.get(i).toLowerCase())) {
					for (int j = 0; j < skills.get(i).size(); j++)
						student_weights.add(student_weight
								.get(position(categories.get(i), student_skill))
								/ skills.get(i).size());
				} else
					for (int j = 0; j < skills.get(i).size(); j++)
						student_weights.add(0.0);
			}
			for (int i = 0; i < all_skills.size(); i++) {
				if (student_skill.contains(all_skills.get(i))) {
					student_weights.remove(i);
					student_weights.add(i, student_weight.get(position(all_skills.get(i), student_skill)));
				}
			}
//			for (int i = 0; i < all_skills.size(); i++) {
//				System.out.println(all_skills.get(i) + " " + student_weights.get(i));
//			}
		} catch (Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
		}
		map.put("student_weights", student_weights);
		return map;
	}

	public int position(String a, List<String> b) {
		for (int i = 0; i < b.size(); i++)
			if (b.get(i).toLowerCase().equals(a.toLowerCase()))
				return i;
		return -1;
	}

	public static void main(String[] args) {
		GetStudentMyData data = new GetStudentMyData();
		Map result = data.getStandardJob("云计算运维", "6367", 3);
	}
}

