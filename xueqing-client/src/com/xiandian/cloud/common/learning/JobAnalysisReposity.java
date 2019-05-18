/**
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * MongoDB的数据存储。
 * 
 * @since v1.0
 * @date 20170815
 * @author SongXueyong
 */
public class JobAnalysisReposity {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private MongoDBStorage mongoDBStorage = MongoDBStorage.getInstance();
	// private Properties hbaseProperties =
	// UtilTools.getConfig(System.getProperty("user.dir") +
	// "/configuration/hbase.properties");

	/**
	 * 构造
	 */
	private static JobAnalysisReposity instance;

	public static synchronized JobAnalysisReposity getInstance() {
		if (instance == null) {
			instance = new JobAnalysisReposity();
		}
		return instance;
	}

	/**
	 * document拼接城市分布专用
	 * 
	 * @param document
	 * @param name
	 * @param value
	 */
	public void appendCityDistribution(Document document, String name, int value, int num, MongoClient mongoClient) {
		Object code = null;
		Object parent_code = null;
		Map<String, Object> map = null;
		map = retrieveCity("China_province_location", 33, name, mongoClient);
		if (map != null) {
			code = map.get("code");
		}
		if (code == null) {
			map = retrieveCity("China_cities_location", 343, name, mongoClient);
			if (map != null) {
				code = map.get("code");
				parent_code = map.get("parent_code");
			}
		}
		document.append("city", name).append("amount", value).append("number", num).append("code", code)
				.append("parent_code", parent_code);
	}

	/**
	 * document拼接城市分布专用
	 * 
	 * @param document
	 * @param name
	 * @param value
	 * @return
	 */
	public String appendProvinceDistribution(Document document, String name, int value, MongoClient mongoClient) {
		Object code = null;
		Object parent_code = null;
		Map<String, Object> map = null;
		map = retrieveCity("China_province_location", 33, name, mongoClient);
		if (map != null) {
			code = map.get("code");
		}
		if (code == null) {
			map = retrieveCity("China_cities_location", 343, name, mongoClient);
			if (map != null) {
				code = map.get("code");
				parent_code = map.get("parent_code");
				String prov = (String) findParentCity("China_province_location", 33, parent_code, mongoClient);
				return prov;
			}
		}
		return name;
	}

	/**
	 * 城市归类处理
	 * 
	 * @return
	 */
	public Map<String, Object> retrieveCity(String citylevel, int num, String name, MongoClient mongoClient) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");
			logger.info("Connect to database successfully");
			MongoCollection<Document> collection = mongoDatabase.getCollection(citylevel);
			logger.info("集合选择成功");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				for (int i = 0; i <= num; i++) {
					Document ds = (Document) doc.get("" + i + "");
					Object na = ds.get("name");
					Object code = ds.get("code");
					Object parent_code = ds.get("parent_code");
					if (((String) na).contains(name)) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("code", code);
						map.put("parent_code", parent_code);
						return map;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * 返回parent_code
	 * 
	 * @return
	 */
	public Object retrieveParentCity(String citylevel, int num, String name, MongoClient mongoClient) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");
			logger.info("Connect to database successfully");
			MongoCollection<Document> collection = mongoDatabase.getCollection(citylevel);
			logger.info("集合选择成功");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				for (int i = 0; i <= num; i++) {
					Document ds = (Document) doc.get("" + i + "");
					Object na = ds.get("name");
					if (((String) na).contains(name)) {
						Object code = ds.get("code");
						Object parent_code = ds.get("parent_code");
						return parent_code;
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * 通过parentcode的查找父城市
	 * 
	 * @return
	 */
	public Object findParentCity(String citylevel, int num, Object parent_code, MongoClient mongoClient) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");
			logger.info("Connect to database successfully");
			MongoCollection<Document> collection = mongoDatabase.getCollection(citylevel);
			logger.info("集合选择成功");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				for (int i = 0; i <= num; i++) {
					Document ds = (Document) doc.get("" + i + "");
					Object na = ds.get("name");
					Object code = ds.get("code");
					if (code.equals(parent_code)) {
						return na;
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * 获取问卷调查
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public static List<SurveyObj> getSurvey(MongoClient mongoClient, String database, String collectionname) {
		List<SurveyObj> surveyObjs = new ArrayList<>();
		MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionname);
		// 检索所有文档
		/**
		 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
		 */
		FindIterable<Document> findIterable = collection.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		while (mongoCursor.hasNext()) {
			SurveyObj surveyObj = new SurveyObj();
			Document doc = mongoCursor.next();
			String natrue = doc.getString("nature");
			surveyObj.setNature(natrue);
			String scale = doc.getString("scale");
			surveyObj.setScale(scale);
			String jobname = doc.getString("jobname");
			surveyObj.setJobname(jobname);
			String salary = doc.getString("salary");
			surveyObj.setSalary(salary);
			String pro = doc.getString("province");
			surveyObj.setPro(pro);
			String industry = doc.getString("industry");
			surveyObj.setIndustry(industry);
			String isjoin = doc.getString("isjoin");
			surveyObj.setIsjoin(isjoin);
			String skills = doc.getString("skill");
			surveyObj.setSkills(skills);
			String ismatch = doc.getString("isyes");
			surveyObj.setIsmatch(ismatch);
			String shcool = doc.getString("school");
			surveyObj.setShcool(shcool);
			surveyObjs.add(surveyObj);
		}
		return surveyObjs;
	}

	public static List<EchartsObj> groupBy(List<String> list, List<EchartsObj> echartsObjs) {
		Map<String, Integer> groupby = new HashMap<>();
		for (String str : list) {
			if (!groupby.containsKey(str))
				groupby.put(str, 1);
			else
				groupby.put(str, groupby.get(str) + 1);
		}
		for (String key : groupby.keySet()) {
			EchartsObj echartsObj = new EchartsObj();
			echartsObj.setName(key);
			echartsObj.setValue(groupby.get(key));
			echartsObjs.add(echartsObj);
		}

		return echartsObjs;
	}

	public Map<String, Object> getMatch(MongoClient mongoClient, String database, String collectionname) {
		Map<String, Object> map = new HashMap<>();
		List<SurveyObj> surveyObjs = getSurvey(mongoClient, database, collectionname);
		int totalman = 0;
		int math = 0;
		for (SurveyObj surveyObj : surveyObjs) {
			totalman++;
			if (surveyObj.getIsmatch().equals("对口")) {
				math++;
			}
		}
		double f = (double) math / totalman;
		double s = Math.round(f * 100);
		map.put("totalman", totalman);
		map.put("match", s + "%");
		return map;

	}

	public Map<String, Object> getSurveyPie(MongoClient mongoClient, String database, String collectionname) {
		Map<String, Object> map = new HashMap<>();
		int totalman = 0;
		List<EchartsObj> schoolLine = new ArrayList<>();
		List<EchartsObj> skillLine = new ArrayList<>();
		List<EchartsObj> natruepie = new ArrayList<>();
		List<EchartsObj> scalepie = new ArrayList<>();
		List<EchartsObj> jobnamepie = new ArrayList<>();
		List<EchartsObj> salarypie = new ArrayList<>();
		List<EchartsObj> propie = new ArrayList<>();
		List<EchartsObj> industrypie = new ArrayList<>();
		// List<EchartsObj> educationpie = new ArrayList<>();
		List<EchartsObj> isjoinpie = new ArrayList<>();
		List<EchartsObj> ismatchpie = new ArrayList<>();
		List<SurveyObj> surveyObjs = getSurvey(mongoClient, database, collectionname);
		if (surveyObjs != null && surveyObjs.size() > 0) {
			totalman = surveyObjs.size();
			List<String> schools = new ArrayList<>();
			List<String> skills = new ArrayList<>();
			List<String> natrues = new ArrayList<>();
			List<String> scales = new ArrayList<>();
			List<String> jobnames = new ArrayList<>();
			List<String> salarys = new ArrayList<>();
			List<String> pros = new ArrayList<>();
			List<String> industrys = new ArrayList<>();
			// List<String> educations = new ArrayList<>();
			List<String> isjoins = new ArrayList<>();
			List<String> ismatchs = new ArrayList<>();
			for (SurveyObj surveyObj : surveyObjs) {
				String skill = surveyObj.getSkills();
				String[] skillstr = skill.split(",");
				for (int i = 0; i < skillstr.length - 1; i++) {
					skills.add(skillstr[i]);
				}
				String natrue = surveyObj.getNature();
				natrues.add(natrue);
				String scale = surveyObj.getScale();
				scales.add(scale);
				String jobname = surveyObj.getJobname();
				jobnames.add(jobname);
				String salary = surveyObj.getSalary();
				salarys.add(salary);
				String pro = surveyObj.getPro();
				pros.add(pro);
				String industry = surveyObj.getIndustry();
				industrys.add(industry);
				String isjoin = surveyObj.getIsjoin();
				isjoins.add(isjoin);
				String ismathch = surveyObj.getIsmatch();
				ismatchs.add(ismathch);
				String school = surveyObj.getShcool();
				schools.add(school);
			}
			schoolLine = groupBy(schools, schoolLine);
			skillLine = groupBy(skills, skillLine);
			natruepie = groupBy(natrues, natruepie);
			scalepie = groupBy(scales, scalepie);
			jobnamepie = groupBy(jobnames, jobnamepie);
			salarypie = groupBy(salarys, salarypie);
			propie = groupBy(pros, propie);
			industrypie = groupBy(industrys, industrypie);
			isjoinpie = groupBy(isjoins, isjoinpie);
			ismatchpie = groupBy(ismatchs, ismatchpie);
		}
		// 技能点图
		List<String> skillnames = new ArrayList<>();
		List<Double> skillvalues = new ArrayList<>();
		for (EchartsObj echartsObj : skillLine) {
			skillnames.add(echartsObj.getName());
			long l = echartsObj.getValue();
			double d = ((double) echartsObj.getValue() / surveyObjs.size()) * 100;
			double d_ = Math.round(d);
			skillvalues.add(d_);
		}
		// 对口率图

		List<String> schoolstotal = new ArrayList<>();
		//
		List<Double> matchValues = new ArrayList<>();
		List<Double> jiuye = new ArrayList<>();
		for (EchartsObj echartsObj : schoolLine) {
			schoolstotal.add(echartsObj.getName().substring(0, 4));
			int i = 0;
			for (SurveyObj surveyObj : surveyObjs) {
				if (echartsObj.getName().equals(surveyObj.getShcool()) && surveyObj.getIsmatch().equals("对口")) {
					i++;
				}
			}
			double s = ((double) (i / echartsObj.getValue())) * 100;
			matchValues.add(s);
			jiuye.add(100.0);
		}
		map.put("jiuye", jiuye);
		map.put("schools", schoolstotal);
		map.put("matchValues", matchValues);
		map.put("total", totalman);
		map.put("skillnames", skillnames);
		map.put("skillvalues", skillvalues);
		map.put("natruepie", natruepie);
		map.put("scalepie", scalepie);
		map.put("jobnamepie", jobnamepie);
		map.put("salarypie", salarypie);
		map.put("propie", propie);
		map.put("industrypie", industrypie);
		map.put("isjoinpie", isjoinpie);
		map.put("ismatchpie", ismatchpie);
		return map;
	}

	/**
	 * 获取学校
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public Map<String, Object> getSchool(MongoClient mongoClient, String database, String collectionname) {
		Map<String, Object> map = new HashMap<>();
		List<String> schools = new ArrayList<>();
		List<LinObj> linObjs = new ArrayList<>();
		MongoDatabase mongoDatabase = mongoClient.getDatabase("major");
		MongoCollection<Document> collection = mongoDatabase.getCollection("training_goal");
		// 检索所有文档
		/**
		 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
		 */
		FindIterable<Document> findIterable = collection.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		while (mongoCursor.hasNext()) {
			LinObj linObj = new LinObj();
			linObj.setType("line");
			linObj.setSmooth(true);
			Document doc = mongoCursor.next();
			String ss = doc.toJson();
			JSONObject jsonObject = JSONObject.fromObject(ss);
			String school = jsonObject.getString("school");
			schools.add(school);
			linObj.setName(school);
			JSONArray jsonArray = jsonObject.getJSONArray("cluster");
			List<Double> values = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				double value = object.getDouble("value");
				// DecimalFormat df = new DecimalFormat("#.##");
				// df.format(value);
				double s = Math.round(value);
				values.add(s);
			}
			linObj.setData(values);
			linObjs.add(linObj);
		}

		map.put("school", schools);
		map.put("linObjs", linObjs);
		return map;
	}

	/**
	 * 获取专业
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public List<String> getmajor(MongoClient mongoClient, String database, String collectionname) {
		List<String> skills = new ArrayList<>();
		String ss = getJson(mongoClient, database, collectionname);
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray_ = jsonObject.getJSONArray("cluster");
		for (int i = 0; i < jsonArray_.size(); i++) {
			String skill = jsonArray_.getJSONObject(i).getString("cluster_skill");
			skills.add(skill);
		}
		return skills;
	}

	protected static String getJson(MongoClient mongoClient, String database, String collectionname) {
		MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
		if (mongoDatabase == null) {
			return null;
		}
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionname);
		FindIterable<Document> findIterable = collection.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		Document doc = null;
		while (mongoCursor.hasNext()) {
			doc = mongoCursor.next();
		}
		if (doc == null) {
			return null;
		}
		String ss = doc.toJson();
		return ss;
	}

	public Map<String, Object> getColor(String color, String area, int x1, int y1, int x2, int y2) {
		Map<String, Object> markArea = new HashMap<>();
		markArea.put("silent", true);
		Map<String, Object> normal = new HashMap<>();
		normal.put("color", color);
		normal.put("borderWidth", 1);
		normal.put("borderType", "dashed");
		Map<String, Object> itemStyle = new HashMap<>();
		itemStyle.put("normal", normal);
		markArea.put("itemStyle", itemStyle);
		List<List<Map<String, Object>>> data = new ArrayList<>();
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map1 = new HashMap<>();
		map1.put("name", area);
		map1.put("xAxis", x1);
		map1.put("yAxis", y1);
		list.add(map1);
		Map<String, Object> map2 = new HashMap<>();
		map2.put("xAxis", x2);
		map2.put("yAxis", y2);
		list.add(map2);
		data.add(list);
		markArea.put("data", data);
		return markArea;
	}

	public Map<String, Object> getGrade(MongoClient mongoClient, String database, String collectionname) {
		Map<String, Object> map = new HashMap<>();
		List<GradeBean> gradeBeans = new ArrayList<>();
		String ss = getJson(mongoClient, database, collectionname);
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("schools");
		List<String> schoolNames = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			Map<String, Object> markArea = null;
			switch (i) {
			case 0:
				markArea = getColor("#F5F5F5", "潜力者", 50, 50, 75, 75);
				break;
			case 1:
				markArea = getColor("#F5F5F5", "挑战者", 50, 75, 75, 100);
				break;
			case 2:
				markArea = getColor("#F5F5F5", "跟随者", 75, 50, 100, 75);
				break;
			case 3:
				markArea = getColor("#F5F5F5", "领跑者", 75, 75, 100, 100);
				break;
			default:
				break;
			}
			GradeBean gradeBean = new GradeBean();
			gradeBean.setMarkArea(markArea);
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			String schoolName = jsonObject_.getString("schoolID");
			schoolNames.add(schoolName);
			gradeBean.setName(schoolName);
			gradeBean.setType("scatter");
			JSONArray jsonArray_ = jsonObject_.getJSONArray("students");
			List<List<String>> data = new ArrayList<>();
			for (int j = 0; j < jsonArray_.size(); j++) {
				List<String> Strings = new ArrayList<>();
				JSONObject jsonObject_1 = jsonArray_.getJSONObject(j);
				String ability = jsonObject_1.getString("ability");
				String potential = jsonObject_1.getString("potential");
				Strings.add(potential);
				Strings.add(ability);

				data.add(Strings);
			}
			// 加线条
			List<Map<String, Object>> maps = new ArrayList<>();
			Map<String, Object> map2 = new HashMap<>();
			Map<String, Object> map3 = new HashMap<>();
			Map<String, Object> map6 = new HashMap<>();
			Map<String, Object> map7 = new HashMap<>();
			map3.put("yAxis", 75);
			map2.put("xAxis", 50);
			map6.put("xAxis", 100);
			map7.put("yAxis", 100);
			maps.add(map3);
			maps.add(map2);
			maps.add(map6);
			maps.add(map7);
			Map<String, Object> map4 = new HashMap<>();
			map4.put("data", maps);
			Map<String, Object> map5 = new HashMap<>();
			map5.put("type", "solid");
			map5.put("color", "#4D76B3");
			Map<String, Object> map8 = new HashMap<>();
			map8.put("normal", map5);
			map4.put("lineStyle", map8);
			gradeBean.setMarkLine(map4);
			gradeBean.setData(data);
			gradeBeans.add(gradeBean);
		}
		map.put("schoolNames", schoolNames);
		map.put("gradeBeans", gradeBeans);
		return map;
	}

	/**
	 * 获取不同岗位总数
	 * 
	 * @param mongoClient
	 * @return
	 */
	public Object findAllJobs(MongoClient mongoClient, String database, String collectionname) {
		String ss = getJson(mongoClient, database, collectionname);
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("category");
		long total = 0;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			String type = jsonObject_.getString("type");
			if (type.equals("招聘总人数")) {
				total = jsonObject_.getLong("amount");
			}
		}
		return total;
	}

	public Map<String, Object> getfullSkills(MongoClient mongoClient, String database, String collectionname,
			String param) {
		Map<String, Object> map = new HashMap<>();
		DecimalFormat df = new DecimalFormat("######0.00");
		MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
		if (mongoDatabase == null) {
			return null;
		}
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionname);
		FindIterable<Document> findIterable = collection.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		Document doc = null;
		List<String> categories = new ArrayList<>();
		List<List<String>> skills = new ArrayList<>();
		List<List<Double>> weights = new ArrayList<>();
		while (mongoCursor.hasNext()) {
			Document document = mongoCursor.next();
			if (document.getString("direction_name").equals(param)) {
				@SuppressWarnings("unchecked")
				List<Document> document_category = (List<Document>) document.get("categories");
				for (Document category : document_category) {
					categories.add(category.getString("category"));
					@SuppressWarnings("unchecked")
					List<Document> document_skills = (List<Document>) category.get("skill_points");
					List<String> category_skill = new ArrayList<>();
					List<Double> category_weight = new ArrayList<>();
					for (Document skill_point : document_skills) {
						category_skill.add(skill_point.getString("skill"));
						category_weight.add(Double.parseDouble(df.format(skill_point.getDouble("weight"))));
					}
					skills.add(category_skill);
					weights.add(category_weight);
				}
			}
		}
		List<String> list = new ArrayList<>();
		List<Double> list2 = new ArrayList<>();
		for (List<String> list1 : skills) {
			for (String str : list1)
				list.add(str);
		}
		for (List<Double> list3 : weights) {
			for (Double double1 : list3)
				list2.add(double1);
		}
		map.put("categories", categories);
		map.put("skills", skills);
		map.put("weights", weights);
		map.put("list1", list);
		map.put("list2", list2);
		return map;
	}

	/**
	 * 获取不同岗位的省分布
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public Map<String, Object> getMap(MongoClient mongoClient, String database, String collectionname) {
		Map<String, Object> map = new HashMap<>();
		List<EchartsObj> echartsObjs = new ArrayList<>();
		String ss = getJson(mongoClient, database, collectionname);
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("category");
		long max = 0;
		for (int i = 0; i < jsonArray.size(); i++) {
			EchartsObj echartsObj = new EchartsObj();
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			String name = jsonObject_.getString("name");
			echartsObj.setName(name);
			long value = jsonObject_.getLong("number");
			if (max < value) {
				max = value;
			}
			echartsObj.setValue(value);
			echartsObjs.add(echartsObj);
		}
		Collections.sort(echartsObjs, new Comparator<EchartsObj>() {

			@Override
			public int compare(EchartsObj arg0, EchartsObj arg1) {
				long s1 = arg0.getValue();
				long s2 = arg1.getValue();
				if (s1 > s2) {
					return -1;
				} else if (s1 < s2) {
					return 1;
				} else {
					return 0;
				}
			}

		});
		List<EchartsObj> echartsObjs2 = echartsObjs.subList(0, 10);
		map.put("rank", echartsObjs2);
		map.put("mapobj", echartsObjs);
		map.put("max", max);
		return map;

	}

	// --------------------------------------------------------------------------------------------------------------------------
	/**
	 * 获取职位的大类
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public EchartsObj getJobBigClass(MongoClient mongoClient, String database, String collectionname) {
		// 存放大类
		EchartsObj echartsObj = new EchartsObj();
		String ss = getJson(mongoClient, database, collectionname);
		if (ss == null) {
			return null;
		}
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("category");
		// 根据表名获取前缀
		String prefix = collectionname.split("_")[2];
		echartsObj.setName(prefix);
		long total = 0;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			// String name = jsonObject_.getString("name");
			long value = jsonObject_.getLong("amount");
			total += value;

		}
		echartsObj.setValue(total);
		return echartsObj;
	}

	/**
	 * 获取小类
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public List<EchartsObj> getJobSmallClass(MongoClient mongoClient, String database, String collectionname) {
		// 存放具体岗位
		List<EchartsObj> echartsObjs = new ArrayList<>();
		String ss = getJson(mongoClient, database, collectionname);
		if (ss == null) {
			return null;
		}
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("category");
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			String name = jsonObject_.getString("job_name");
			long value = jsonObject_.getLong("amount");
			EchartsObj obj = new EchartsObj();
			obj.setName(name);
			obj.setValue(value);
			echartsObjs.add(obj);
		}
		return echartsObjs;
	}

	// --------------------------------------------------------------------------------------------------------------------------

	/**
	 * 获取学历分布的饼图
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public List<EchartsObj> getEducation(MongoClient mongoClient, String database, String collectionname) {
		List<EchartsObj> echartsObjs = new ArrayList<>();
		String ss = getJson(mongoClient, database, collectionname);
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("category");
		for (int i = 0; i < jsonArray.size(); i++) {
			EchartsObj echartsObj = new EchartsObj();
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			String name = jsonObject_.getString("type");
			echartsObj.setName(name);
			long value = jsonObject_.getLong("number");
			echartsObj.setValue(value);
			echartsObjs.add(echartsObj);
		}
		return echartsObjs;
	}

	/**
	 * 获取工作经验
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public List<EchartsObj> getExperience(MongoClient mongoClient, String database, String collectionname) {
		List<EchartsObj> echartsObjs = new ArrayList<>();
		String ss = getJson(mongoClient, database, collectionname);
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("category");
		for (int i = 0; i < jsonArray.size(); i++) {
			EchartsObj echartsObj = new EchartsObj();
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			String name = jsonObject_.getString("type");
			echartsObj.setName(name);
			long value = jsonObject_.getLong("number");
			echartsObj.setValue(value);
			echartsObjs.add(echartsObj);
		}
		return echartsObjs;
	}

	/**
	 * 获取薪资
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public Map<String, Object> getSalary(MongoClient mongoClient, String database, String collectionname) {
		Map<String, Object> map = new HashMap<>();
		List<String> salaryRanges = new ArrayList<>();
		List<Long> salarys = new ArrayList<>();
		String ss = getJson(mongoClient, database, collectionname);
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("category");
		String range = null;
		long s = 0;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			String salaryRange = jsonObject_.getString("type");
			salaryRanges.add(salaryRange);
			Long salary = jsonObject_.getLong("number");
			salarys.add(salary);
			if (salary > s) {
				range = salaryRange;
			}
		}
		map.put("range", range);
		map.put("dataX", salaryRanges);
		map.put("dataY", salarys);
		return map;

	}

	/**
	 * 公司性质
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public Map<String, Object> getNature(MongoClient mongoClient, String database, String collectionname) {
		Map<String, Object> map = new HashMap<>();
		List<String> natureRanges = new ArrayList<>();
		List<Long> natures = new ArrayList<>();
		String ss = getJson(mongoClient, database, collectionname);
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("category");
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			String salaryRange = jsonObject_.getString("type");
			natureRanges.add(salaryRange);
			Long salary = jsonObject_.getLong("number");
			natures.add(salary);
		}
		map.put("dataX", natureRanges);
		map.put("dataY", natures);
		return map;

	}

	/**
	 * 公司规模
	 * 
	 * @param mongoClient
	 * @param database
	 * @param collectionname
	 * @return
	 */
	public Map<String, Object> getScale(MongoClient mongoClient, String database, String collectionname) {
		Map<String, Object> map = new HashMap<>();
		List<String> scaleRanges = new ArrayList<>();
		List<Long> scales = new ArrayList<>();
		String ss = getJson(mongoClient, database, collectionname);
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JSONArray jsonArray = jsonObject.getJSONArray("category");
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject_ = jsonArray.getJSONObject(i);
			String salaryRange = jsonObject_.getString("name");
			scaleRanges.add(salaryRange);
			Long salary = jsonObject_.getLong("number");
			scales.add(salary);
		}
		map.put("dataX", scaleRanges);
		map.put("dataY", scales);
		return map;

	}

	public Map<String, Object> getSkills(MongoClient mongoClient, String allparam, String param) {
		// database job_cloud
		String[] jobs = { "develop", "framework", "manage", "operation", "other", "sales" };
		Map<String, Object> map = new HashMap<>();
		List<String> weight = new ArrayList<>();
		List<RaderObj> list = new ArrayList<>();
		for (String str : jobs) {
			String namestr = allparam + "_" + str;
			String ss = getJson(mongoClient, allparam, namestr);
			if (ss != null) {
				JSONObject jsonObject = JSONObject.fromObject(ss);
				JSONArray jsonArray = jsonObject.getJSONArray("category");
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject_ = jsonArray.getJSONObject(i);
					String name = jsonObject_.getString("job_name");
					if (name.equals(param)) {
						JSONArray jsonArraySkills = jsonObject_.getJSONArray("skills");
						for (int j = 0; j < jsonArraySkills.size(); j++) {
							JSONObject jsonArraySkill = jsonArraySkills.getJSONObject(j);
							if (!jsonArraySkill.isNullObject()) {
								String Skill_name = jsonArraySkill.getString("Skill_name");
								String Skill_weight = jsonArraySkill.getString("Skill_weight");
								weight.add(Skill_weight);
								RaderObj raderObj = new RaderObj();
								raderObj.setMax(1);
								raderObj.setName(Skill_name);
								list.add(raderObj);
							}
						}
					}
				}
			}
		}
		map.put("datasskill", list);
		map.put("values", weight);
		map.put("jobname", param);
		return map;

	}

	/**
	 * 饼图展示
	 * 
	 * @param mongoClient
	 * @return
	 */
	public List<EchartsObj> getPie(MongoClient mongoClient) {
		List<EchartsObj> echartsObjs = new ArrayList<>();

		// 云计算
		long cloudTotal = (long) findAllJobs(mongoClient, "job_cloud", "job_total");
		EchartsObj echartsObj1 = new EchartsObj("云计算 " + cloudTotal, cloudTotal);
		echartsObjs.add(echartsObj1);
		// 大数据
		long bigdataTotal = (long) findAllJobs(mongoClient, "job_bigdata", "job_total");
		EchartsObj echartsObj2 = new EchartsObj("大数据" + bigdataTotal, bigdataTotal);
		echartsObjs.add(echartsObj2);
		// AI
		if (findAllJobs(mongoClient, "job_ai", "job_total") != null) {
			long aiTotal = (long) findAllJobs(mongoClient, "job_ai", "job_total");
			EchartsObj echartsObj3 = new EchartsObj("人工智能" + aiTotal, aiTotal);
			echartsObjs.add(echartsObj3);
		}
		// iot物联网
		if (findAllJobs(mongoClient, "job_iot", "job_total") != null) {
			long iotTotal = (long) findAllJobs(mongoClient, "job_iot", "job_total");
			EchartsObj echartsObj4 = new EchartsObj("物联网" + iotTotal, iotTotal);
			echartsObjs.add(echartsObj4);
		}
		// 移动开发
		if (findAllJobs(mongoClient, "job_mobile", "job_total") != null) {
			long mobileTotal = (long) findAllJobs(mongoClient, "job_mobile", "job_total");
			EchartsObj echartsObj5 = new EchartsObj("移动开发" + mobileTotal, mobileTotal);
			echartsObjs.add(echartsObj5);
		}
		// 软件
		if (findAllJobs(mongoClient, "job_software", "job_total") != null) {
			long softwareTotal = (long) findAllJobs(mongoClient, "job_software", "job_total");
			EchartsObj echartsObj6 = new EchartsObj("软件开发" + softwareTotal, softwareTotal);
			echartsObjs.add(echartsObj6);
		}
		return echartsObjs;

	}

	public void saveques(MongoClient mongoClient, String database, String collectionname, int year, String school,
			String isjoin, String itmeyear, String itme, String ranking, String industry, String jobcategory,
			String jobname, String skill, String nature, String scale, String salary, String province, String city,
			String isyes) {
		if (school.equals("请选择")) {
			school = "未知院校";
		}
		Map map = mongoDBStorage.create(database, collectionname, mongoClient);
		MongoCollection<Document> collection = (MongoCollection<Document>) map.get("collection");
		Document doc = (Document) map.get("document");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		String da = sdf.format(date);
		doc.append("createtime", da);
		doc.append("year", year);
		doc.append("school", school);
		doc.append("isjoin", isjoin);
		doc.append("itmeyear", itmeyear);
		doc.append("itme", itme);
		doc.append("ranking", ranking);
		doc.append("industry", industry);
		doc.append("jobcategory", jobcategory);
		doc.append("jobname", jobname);
		doc.append("skill", skill);
		doc.append("nature", nature);
		doc.append("scale", scale);
		doc.append("salary", salary);
		doc.append("province", province);
		doc.append("city", city);
		doc.append("isyes", isyes);
		collection.insertOne(doc);

	}

	/**
	 * 从mongoDB表major_plan获取培养方案雷达图需要的数据
	 * 
	 * @param mongoClient
	 * @param schoolid
	 * @return
	 */
	public static Map<Object, Object> getrctest(MongoClient mongoClient, String schoolid) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("major");
			MongoCollection<Document> collection = mongoDatabase.getCollection("major_plan");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Integer num = 0;
				Integer cre = 0;
				Integer gj = 0;
				Integer zj = 0;
				Integer zh = 0;
				String ser = "[";
				String resultgj = "[";
				String resultzj = "";
				String resultzh = "";
				Document doc = mongoCursor.next();
				Object _id = doc.get("_id");
				doc.remove("_id");
				String id = doc.getString("id");
				if (id.equals(schoolid)) {
					List<Document> courses = (List<Document>) doc.get("courses");
					int length = 0;
					for (Document l : courses) {
						Object docvb = l.get("course");
						Document dos = (Document) docvb;
						String module = dos.getString("module");
						String name = dos.getString("name");
						Document period = (Document) dos.get("period");
						int total = period.getInteger("total");
						int credit = Integer.parseInt(dos.getString("credit"));
						if (module.equals("公共基础课")) {
							resultgj += "{\"value\":\"" + total + "\",\"name\":\"" + name + "  学分: " + credit
									+ "  学时\"},";
							gj += total;
							cre += credit;
							length++;
						}
						if (module.equals("专业基础课")) {
							resultzj += "{\"value\":\"" + total + "\",\"name\":\"" + name + "  学分: " + credit
									+ "  学时\"},";
							zj += total;
							cre += credit;
							length++;
						}
						if (module.equals("专业核心课")) {
							resultzj += "{\"value\":\"" + total + "\",\"name\":\"" + name + "  学分: " + credit
									+ "  学时\"},";
							zh += total;
							cre += credit;
							length++;
						}
					}
					String result = resultgj + resultzj + resultzh;
					String res = result.substring(0, result.length() - 1) + "]";
					ser = "[{\"value\":\"" + gj + "\",\"name\":\"公共基础课\"},{\"value\":\"" + zj
							+ "\",\"name\":\"专业基础课\"},{\"value\":\"" + zh + "\",\"name\":\"专业核心课\"}]";
					num = gj + zj + zh;
					Map<Object, Object> map = new HashMap<Object, Object>();
					map.put("res", res);
					map.put("ser", ser);
					map.put("length", length);
					map.put("num", num);
					map.put("cre", cre);
					return map;
				}
			}

		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * 从mongoDB表major_match获取培养方案岗位匹配度和雷达图所需的数据
	 * 
	 * @param mongoClient
	 * @param schoolid
	 * @return
	 */
	public static List<Map<Object, String>> getradartest(MongoClient mongoClient, String schoolid) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("major");
			MongoCollection<Document> collection = mongoDatabase.getCollection("major_match");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Integer num = 0;
				Integer gj = 0;
				Integer zj = 0;
				Integer zh = 0;
				String ser = "[";
				Document doc = mongoCursor.next();
				Object _id = doc.get("_id");
				doc.remove("_id");
				String id = doc.getString("schoolid");
				List<Map<Object, String>> list = new ArrayList<Map<Object, String>>();
				if (id.equals(schoolid)) {
					List<Document> positions = (List<Document>) doc.get("positions");
					for (Document p : positions) {
						String resultgj = "[";
						String resultzj = "[";
						String resultzh = "[";
						String jobname = p.getString("job");
						Double similarity = p.getDouble("similarity");
						List<Document> skill_weight = (List<Document>) p.get("skill_weight");
						for (Document l : skill_weight) {
							Object skill = l.get("skill");
							Object major_weight = l.get("major_weight");
							Object job_weight = l.get("job_weight");
							DecimalFormat df = new DecimalFormat(".##");
							String maw = df.format(Double.parseDouble(major_weight.toString()));
							String jow = df.format(Double.parseDouble(job_weight.toString()));

							resultgj += "{\"name\":\"" + skill + "\",\"max\":\"100\"},";
							resultzj += "\"" + maw + "\",";
							resultzh += "\"" + jow + "\",";
						}
						String skls = resultgj.substring(0, resultgj.length() - 1) + "]";
						String major = resultzj.substring(0, resultzj.length() - 1) + "]";
						String jobw = resultzh.substring(0, resultzh.length() - 1) + "]";
						Map<Object, String> map = new HashMap<Object, String>();
						map.put("skls", skls);
						map.put("major", major);
						map.put("job", jobw);
						map.put("jobname", jobname);
						map.put("similarity", similarity.toString());
						list.add(map);
					}
					return list;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * 从mongoDB表training_goal取数据用于生成.csv文件（培养方案培养目标d3图需要）
	 * 
	 * @param mongoClient
	 * @param schoolid
	 * @return
	 */
	@SuppressWarnings("null")
	public static List<List<Object>> getTarget(MongoClient mongoClient, String schoolid) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("major");
			MongoCollection<Document> collection = mongoDatabase.getCollection("training_goal");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				Object _id = doc.get("_id");
				doc.remove("_id");
				String scid = doc.getString("schoolid");
				String course = doc.getString("course");
				List<Document> cluster = (List<Document>) doc.get("cluster");
				List<List<Object>> dataList = new ArrayList<List<Object>>();
				List<Object> rowList = new ArrayList<Object>();
				rowList.add("云计算");
				dataList.add(rowList);
				if (schoolid.equals(scid)) {
					for (Document l : cluster) {
						rowList = new ArrayList<Object>();
						String cluster_skill = l.get("cluster_skill").toString();
						String ids = "云计算" + "." + cluster_skill;
						List<Document> skill_weight = (List<Document>) l.get("skill_weight");
						rowList.add(ids);
						if (skill_weight.isEmpty()) {
						} else {
							dataList.add(rowList);
						}
						if (!skill_weight.isEmpty()) {
							for (Document sk : skill_weight) {
								rowList = new ArrayList<Object>();
								String skill = sk.getString("skill");
								Double weight = sk.getDouble("weight");
								String id = "云计算" + "." + cluster_skill + "." + skill;
								String rgb = getRandColorCode();
								// if(weight>10){
								rowList.add(id);
								rowList.add(weight);
								rowList.add(rgb);
								dataList.add(rowList);
								// }
							}
						}
					}
					return dataList;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取随机颜色
	 * 
	 * @return
	 */
	public static String getRandColorCode() {
		String r, g, b;
		Random random = new Random();
		r = Integer.toHexString(random.nextInt(256)).toUpperCase();
		g = Integer.toHexString(random.nextInt(256)).toUpperCase();
		b = Integer.toHexString(random.nextInt(256)).toUpperCase();

		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;

		return r + g + b;
	}

	/**
	 * 获取表student_cluster数据，进行学生聚类数据展示
	 * 
	 * @param mongoClient
	 * @return
	 */
	public static Map getstujl(MongoClient mongoClient) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("study");
			MongoCollection<Document> collection = mongoDatabase.getCollection("student_cluster");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				List<Document> schools = (List<Document>) doc.get("schools");
				List<JSONObject> list = new ArrayList<JSONObject>();
				Vector<String> sdf = new Vector<String>();
				Vector<String> year = new Vector<String>();
				for (Document h : schools) {
					String schoolname = h.getString("schoolname");
					sdf.add(schoolname);
					List<Document> students = (List<Document>) h.get("student");
					JSONObject json = new JSONObject();
					json.put("name", schoolname);
					json.put("type", "scatter");
					Vector vec = new Vector<>();
					for (Document s : students) {
						Vector vdc = new Vector<>();
						String orgname = s.getString("orgname");
						String joindate = s.getString("joindate");
						String name = s.getString("name");
						Double x = s.getDouble("x");
						Double y = s.getDouble("y");
						vdc.add(x);
						vdc.add(y);
						vdc.add("(" + joindate + ")");
						vec.add(vdc);
						year.add(joindate);
					}
					json.put("data", vec);
					list.add(json);
				}
				Map map = new HashMap<>();
				map.put("map", sdf);
				map.put("list", list);
				map.put("year", year);
				return map;
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * 学生聚类，根据年级查询表student_cluster
	 * 
	 * @param mongoClient
	 * @param grade
	 * @return
	 */
	public static Map selectgrade(MongoClient mongoClient, String grade) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("study");
			MongoCollection<Document> collection = mongoDatabase.getCollection("student_cluster");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				List<Document> schools = (List<Document>) doc.get("schools");
				List<JSONObject> list = new ArrayList<JSONObject>();
				Vector<String> sdf = new Vector<String>();
				Vector<String> year = new Vector<String>();
				for (Document h : schools) {
					String schoolname = h.getString("schoolname");
					sdf.add(schoolname);
					List<Document> students = (List<Document>) h.get("student");
					JSONObject json = new JSONObject();
					json.put("name", schoolname);
					json.put("type", "scatter");
					Vector vec = new Vector<>();
					for (Document s : students) {
						Vector vdc = new Vector<>();
						String orgname = s.getString("orgname");
						String joindate = s.getString("joindate");
						if (joindate.equals(grade)) {
							String name = s.getString("name");
							Double x = s.getDouble("x");
							Double y = s.getDouble("y");
							vdc.add(x);
							vdc.add(y);
							vdc.add("(" + joindate + ")");
							vec.add(vdc);
							year.add(joindate);
						}
					}
					json.put("data", vec);
					list.add(json);
				}
				Map map = new HashMap<>();
				map.put("map", sdf);
				map.put("list", list);
				map.put("year", year);
				return map;
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * 从表course_study获取专业学习图表需要的数据
	 * 
	 * @param mongoClient
	 * @param name
	 * @return
	 */
	public static Map loadCourse(MongoClient mongoClient, String name) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("study");
			MongoCollection<Document> collection = mongoDatabase.getCollection("course_study");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				List<Document> schools = (List<Document>) doc.get("schools");
				List list = new ArrayList<>();
				for (Document h : schools) {
					String schoolname = h.getString("schoolname");
					if (schoolname.equals(name)) {
						List<Document> courses = (List<Document>) h.get("courses");
						Vector<String> vec = new Vector<String>();
						Vector<Double> pj = new Vector<Double>();
						Vector<Double> mx = new Vector<Double>();
						Vector<Double> mi = new Vector<Double>();
						for (Document s : courses) {
							String kcname = s.getString("kcname");
							List<Document> students = (List<Document>) s.get("students");
							// System.out.println(students);
							vec.add(kcname);
							int count = students.size();
							Double avg = 0.0;
							Double max = 0.0;
							Double min = 100.0;
							for (Document t : students) {
								Double score = 0.0;
								if (t.getString("score").equals("优秀"))
									score = 90.0;
								else if (t.getString("score").equals("良好"))
									score = 80.0;
								else if (t.getString("score").equals("一般"))
									score = 70.0;
								else if (t.getString("score").equals("中等"))
									score = 70.0;
								else if (t.getString("score").equals("及格"))
									score = 60.0;
								else if (t.getString("score").equals("不及格"))
									score = 50.0;
								else if (t.getString("score").equals("不合格"))
									score = 50.0;
								else if (t.getString("score").equals("停考"))
									score = 30.0;
								else if (t.getString("score").equals("缺考"))
									score = 30.0;
								else if (t.getString("score").contains("/"))
									score = Double.parseDouble(t.getString("score").split("/")[0]);
								else
									score = Double.parseDouble(t.getString("score"));
								avg = avg + score;
								if (max < score) {
									max = score;
								}
								if (min > score) {
									min = score;
								}
							}
							avg = avg / count;
							double d = Math.round(avg);
							pj.add(d);
							mx.add(max);
							mi.add(min);
						}
						Map map = new HashMap<>();
						map.put("vec", vec);
						map.put("pj", pj);
						map.put("mx", mx);
						map.put("mi", mi);
						return map;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * 获取教学评价数据
	 * 
	 * @param mongoClient
	 * @return
	 */
	public static Map getteapj(MongoClient mongoClient) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("teach");
			MongoCollection<Document> collection = mongoDatabase.getCollection("teacher_assessment");
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3. 通过游标遍历检索出的文档集合
			 */
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				List<Document> schools = (List<Document>) doc.get("schools");
				List list = new ArrayList<>();
				for (Document h : schools) {
					String schoolname = h.getString("schoolname");
					List<Document> courses = (List<Document>) h.get("courses");
					Vector<String> vec = new Vector<String>();
					Vector<Double> pj = new Vector<Double>();
					Vector<Double> mx = new Vector<Double>();
					Vector<Double> mi = new Vector<Double>();
					for (Document s : courses) {
						String kcname = s.getString("kcname");
						List<Document> students = (List<Document>) s.get("students");
						vec.add(kcname);
						int count = students.size();
						Double avg = 0.0;
						Double max = 0.0;
						Double min = 0.0;
						for (Document t : students) {
							Double score = Double.parseDouble(t.getString("score"));
							min = score;
							avg = avg + score;
							if (max < score) {
								max = score;
							} else {
								min = score;
							}
						}
						avg = avg / count;
						pj.add(avg);
						mx.add(max);
						mi.add(min);
						Map map = new HashMap<>();
						map.put("vec", vec);
						map.put("pj", pj);
						map.put("mx", mx);
						map.put("mi", mi);
						return map;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * 就业推荐数据信息从表job查询
	 * 
	 * @param mongoClient
	 * @param skills
	 * @param number
	 * @return
	 */
	public static Map<String, Object> getjytj(MongoClient mongoClient, String[] skills, int number) {
		Map<String, Object> map_ = new HashMap<>();
		List<String> description = new ArrayList<>();
		List<String> companyName = new ArrayList<>();
		List<String> location = new ArrayList<>();
		List<String> jobname = new ArrayList<>();
		List<Integer> number1 = new ArrayList<>();
		List<List<RaderObj>> lists = new ArrayList<>();
		List<List<Integer>> lists2 = new ArrayList<>();
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase("employ");
			MongoCollection<Document> collection = mongoDatabase.getCollection("job");
			FindIterable<Document> findIterable = null;
			findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				List<Document> jobs = (List<Document>) doc.get("jobs");
				for (Document s : jobs) {
					description.add(s.getString("skills"));
					jobname.add(s.getString("job_name"));
					companyName.add(s.getString("company_name"));
					String provice = s.getString("provice");
					String city = s.getString("city");
					location.add(provice + " " + city);

					number1.add(getNumber(s.getString("skills"), skills));
				}
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		List<String> companyName2 = new ArrayList<>();
		List<String> location2 = new ArrayList<>();
		List<String> jobname2 = new ArrayList<>();
		List<String> description2 = new ArrayList<>();
		List<Map<Object, String>> job = new ArrayList<Map<Object, String>>();
		for (int i = 0; i < number; i++) {
			int index = 0;
			int replace = 0;
			for (int k = 0; k < number1.size(); k++) {
				if (number1.get(k) > replace) {
					replace = number1.get(k);
					index = k;
					number1.remove(k);
					number1.add(k, 0);
				}
			}
			if (replace > 0) {
				jobname2.add(jobname.get(index));
				System.out.println(jobname.get(index));
				location2.add(location.get(index));
				System.out.println(location.get(index));
				companyName2.add(companyName.get(index));
				System.out.println(companyName.get(index));
				description2.add(description.get(index));
				System.out.println(description.get(index));
				List<RaderObj> raderObjs = new ArrayList<>();
				List<Integer> values = new ArrayList<>();
				String[] strs = description.get(index).split(",");
				for (int j = 0; j < strs.length - 1; j++) {
					RaderObj raderObj = new RaderObj();
					raderObj.setName(strs[j]);
					raderObj.setMax(10);
					raderObjs.add(raderObj);
					values.add(5);
				}
				lists.add(raderObjs);
				lists2.add(values);
				Map<Object, String> map = new HashMap<Object, String>();
				map.put("jobname", jobname.get(index));
				map.put("localtion", location.get(index));
				map.put("companyname", companyName.get(index));
				map.put("description", description.get(index));
				job.add(map);
			} else
				break;
		}
		map_.put("values", lists2);
		map_.put("job", job);
		map_.put("rader", lists);
		return map_;
	}

	public static int getNumber(String description, String[] skills) {
		int i = 0;
		for (String str : skills)
			if (description.toLowerCase().contains(str.toLowerCase()))
				i++;
		return i;
	}

	/**
	 * 教师评价从表teacher_assessment图表数据获取
	 * 
	 * @param mongoClient
	 * @param teacher
	 * @return
	 */
	public static List<Map<Object, String>> getteapj(MongoClient mongoClient, String teacher) {
		Map<String, Map<String, List<Object>>> tevaluation = teacherEvaluation(mongoClient);
		List<Map<Object, String>> listcourse = new ArrayList<Map<Object, String>>();
		for (Map.Entry<String, Map<String, List<Object>>> m : tevaluation.entrySet()) {
			for (Map.Entry<String, List<Object>> k : m.getValue().entrySet()) {
				String tname = m.getKey();
				String result = "[";
				if (tname.equals(teacher)) {
					Map<Object, String> map = new HashMap<Object, String>();
					map.put("teacher", k.getKey());
					for (int s = 0; s < 3; s++) {
						Object zhi = k.getValue().get(s);
						String rez = zhi.toString();
						String zzz = rez.substring(0, rez.indexOf("."));
						result += " " + zzz + ",";
					}
					String res = result.substring(0, result.length() - 1) + "]";
					map.put("val", res);
					listcourse.add(map);
				}
			}
		}
		return listcourse;
	}

	/**
	 * 从表teacher_assessment获取教师评价教师信息
	 * 
	 * @param mongoClient
	 * @return
	 */
	public static List<String> getteaname(MongoClient mongoClient) {
		MongoDatabase mongoDatabase = mongoClient.getDatabase("teach");
		MongoCollection<Document> collection = mongoDatabase.getCollection("teacher_assessment");
		FindIterable<Document> findIterable = null;
		findIterable = collection.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		while (mongoCursor.hasNext()) {
			Document doc = mongoCursor.next();
			List<Document> teachers = (List<Document>) doc.get("teachers");
			List<String> lst = new ArrayList<String>();
			Vector<String> vc = new Vector<String>();
			for (Document d : teachers) {
				String tname = d.getString("teachername");
				vc.add(tname);
			}
			for (int i = 0; i < vc.size(); i++) {
				for (int j = i + 1; j < vc.size(); j++) {
					if (vc.get(i).equals(vc.get(j))) {
						j = ++i;
					}
				}
				lst.add(vc.get(i));
			}
			return lst;
		}
		return null;
	}

	/**
	 * 将成绩聚类成三个分数
	 * 
	 * @param mongoClient
	 * @return
	 */
	public static Map<String, Map<String, List<Object>>> courseEvaluation(MongoClient mongoClient) {
		Map<String, Map<String, List<Object>>> evaluation = new HashMap<>();
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase("teach");
			MongoCollection<Document> collection = mongoDatabase.getCollection("course_assessment");
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				List<Document> courses = (List<Document>) doc.get("courses");
				for (Document c : courses) {
					Map<String, List<Object>> course = new HashMap<>();
					List<Document> teachers = (List<Document>) c.get("teachers");
					for (Document t : teachers) {
						List<Object> record = new ArrayList<>();
						List<Integer> grade = new ArrayList<>();
						List<Document> students = (List<Document>) t.get("students");
						for (Document s : students)
							grade.add(Integer.parseInt(s.getString("studentscore")));
						record.add(courseDesign(grade));
						record.add(teachMethod(grade));
						record.add(professionalCapability(grade));
						record.add((int) grade.size());
						course.put(t.getString("teachername"), record);
					}
					evaluation.put(c.getString("coursename"), course);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return evaluation;
	}

	/**
	 * 将成绩聚类成三个分数
	 * 
	 * @param mongoClient
	 * @return
	 */
	public static Map<String, Map<String, List<Object>>> teacherEvaluation(MongoClient mongoClient) {
		Map<String, Map<String, List<Object>>> evaluation = new HashMap<>();
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase("teach");
			MongoCollection<Document> collection = mongoDatabase.getCollection("teacher_assessment");
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				List<Document> teachers = (List<Document>) doc.get("teachers");
				for (Document t : teachers) {
					Map<String, List<Object>> teacher = new HashMap<>();
					List<Document> courses = (List<Document>) t.get("courses");
					for (Document c : courses) {
						List<Object> record = new ArrayList<>();
						List<Integer> grade = new ArrayList<>();
						List<Document> scores = (List<Document>) c.get("scores");
						for (Document s : scores) {
							grade.add(Integer.parseInt(s.getString("studentscore")));
						}
						String coursename = c.getString("coursename");
						String time = c.getString("time").trim();
						int ts = 1;
						try {
							ts = Integer.parseInt(time);
						} catch (Exception e) {

						}

						if (!teacher.containsKey(coursename)
								|| (teacher.containsKey(coursename) && ts > (int) teacher.get(coursename).get(3))) {
							record.add(courseDesign(grade));
							record.add(teachMethod(grade));
							record.add(professionalCapability(grade));
							record.add(Integer.parseInt(c.getString("time")));
							teacher.put(c.getString("coursename"), record);
						}
					}
					evaluation.put(t.getString("teachername"), teacher);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return evaluation;
	}

	/**
	 * 课程综合成绩计算
	 * 
	 * @param grades
	 * @return
	 */
	public static double courseDesign(List<Integer> grades) {
		int sum = 0;
		for (int i : grades)
			sum += i;
		return sum / grades.size();
	}

	/**
	 * 老师综合成绩计算
	 * 
	 * @param grades
	 * @return
	 */
	public static double teachMethod(List<Integer> grades) {
		double average = courseDesign(grades);
		double less = 0;
		double more = 0;
		for (int i : grades)
			if (i < 80)
				less++;
			else
				more++;
		return (80 + (more - less) / (more + less) * 20);
	}

	/**
	 * 匹配度计算
	 * 
	 * @param grades
	 * @return
	 */
	public static double professionalCapability(List<Integer> grades) {
		List<Double> a = new ArrayList<>();
		for (int g : grades) {
			double i = g * 1.0;
			if (i >= 80)
				a.add(1 / (1 + Math.exp(80 - i * 1.0)) * 20 + 80);
			else
				a.add(80 - 1 / (1 + Math.exp(i * 1.0 - 80)) * 20);
		}
		int sum = 0;
		for (double i : a)
			sum += i;
		return sum / a.size();
	}

	/**
	 * 课程评价，从表course_assessment获取该课程下的老师、学生、综合的分信息
	 * 
	 * @param mongoClient
	 * @param course
	 * @return
	 */
	public static List getcounum(MongoClient mongoClient, String course) {
		MongoDatabase mongoDatabase = mongoClient.getDatabase("teach");
		MongoCollection<Document> collection = mongoDatabase.getCollection("course_assessment");
		FindIterable<Document> findIterable = collection.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		while (mongoCursor.hasNext()) {
			Document doc = mongoCursor.next();
			List<Document> courses = (List<Document>) doc.get("courses");
			for (Document c : courses) {
				String coursename = c.getString("coursename");
				String picurl = c.getString("picurl");
				List list = new ArrayList<>();
				if (coursename.equals(course)) {
					List<Document> teachers = (List<Document>) c.get("teachers");
					List<String> cou = new ArrayList<String>();
					Map map = new HashMap<>();
					int count = 0;
					int score = 0;
					for (Document t : teachers) {
						String teachername = t.getString("teachername");
						cou.add(teachername);
						List<Document> students = (List<Document>) t.get("students");
						count += students.size();
						for (Document s : students) {
							String sc = s.getString("studentscore");
							score += Integer.parseInt(sc);
						}
					}
					int tscore = (int) (score / count);
					map.put("count", count);
					map.put("tscore", tscore);
					list.add(cou);
					list.add(map);
					return list;
				}
			}
		}
		return null;
	}

	/**
	 * 课程评价，从表course_assessment获取课程雷达图的数据
	 * 
	 * @param mongoClient
	 * @param course
	 * @return
	 */
	public static String getcouskill(MongoClient mongoClient, String course) {
		Map<String, Map<String, List<Object>>> cevaluation = courseEvaluation(mongoClient);
		for (Map.Entry<String, Map<String, List<Object>>> m : cevaluation.entrySet()) {
			String coursename = m.getKey();
			if (coursename.equals(course)) {
				int zy = 0;
				int jx = 0;
				int kc = 0;
				int i = 0;
				for (Map.Entry<String, List<Object>> k : m.getValue().entrySet()) {
					i++;
					Double zyd = (Double) k.getValue().get(0);
					Double jxd = (Double) k.getValue().get(1);
					Double kcd = (Double) k.getValue().get(2);
					zy += zyd;
					jx += jxd;
					kc += kcd;
					// System.out.println(m.getKey() + " " + k.getKey() + " " + k.getValue().get(0)
					// + " "
					// + k.getValue().get(1) + " " + k.getValue().get(2) + " "
					// + k.getValue().get(3));
				}
				int avzy = (int) (zy / i);
				int avjx = (int) (jx / i);
				int avkc = (int) (kc / i);
				String result = "[" + avzy + "," + avjx + "," + avkc + "]";
				return result;
			}
		}
		return null;
	}

	/**
	 * 将course_time成绩进行聚类生成三个分数
	 * 
	 * @param mongoClient
	 * @return
	 */
	public static Map<String, Map<String, List<Object>>> timeEvaluation(MongoClient mongoClient) {
		Map<String, Map<String, List<Object>>> evaluation = new HashMap<>();
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase("teach");
			MongoCollection<Document> collection = mongoDatabase.getCollection("course_time");
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				Document doc = mongoCursor.next();
				List<Document> courses = (List<Document>) doc.get("courses");
				for (Document c : courses) {
					Map<String, List<Object>> course = new HashMap<>();
					List<Document> teachers = (List<Document>) c.get("times");
					for (Document t : teachers) {
						List<Object> record = new ArrayList<>();
						List<Integer> grade = new ArrayList<>();
						List<Document> students = (List<Document>) t.get("students");
						for (Document s : students)
							grade.add(Integer.parseInt(s.getString("studentscore")));
						record.add(courseDesign(grade));
						record.add(teachMethod(grade));
						record.add(professionalCapability(grade));
						record.add((int) grade.size());
						course.put(t.getString("timename"), record);
					}
					evaluation.put(c.getString("coursename"), course);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return evaluation;
	}

	/**
	 * 课程评价，折线图数据获取从表course_time
	 * 
	 * @param mongoClient
	 * @param course
	 * @return
	 */
	public static List<String> getcoutime(MongoClient mongoClient, String course) {
		Map<String, Map<String, List<Object>>> cevaluation = timeEvaluation(mongoClient);
		List<String> list = new ArrayList<String>();
		String result = "";
		String resultzy = "[";
		String resultkc = "[";
		String resultjx = "[";
		for (Map.Entry<String, Map<String, List<Object>>> m : cevaluation.entrySet()) {
			String coursename = m.getKey();
			if (coursename.equals(course)) {
				for (Map.Entry<String, List<Object>> k : m.getValue().entrySet()) {
					String timame = "第" + k.getKey() + "次开课";
					Double zyd = (Double) k.getValue().get(0);
					Double kcd = (Double) k.getValue().get(1);
					Double jxd = (Double) k.getValue().get(2);
					int avzy = (int) (zyd / 1);
					int avjx = (int) (jxd / 1);
					int avkc = (int) (kcd / 1);
					result += "" + timame + ",";
					resultzy += "" + avzy + ",";
					resultkc += "" + avkc + ",";
					resultjx += "" + avjx + ",";
					// System.out.println(m.getKey() + " " + k.getKey() + " " + k.getValue().get(0)
					// + " "
					// + k.getValue().get(1) + " " + k.getValue().get(2) + " "
					// + k.getValue().get(3));
				}

			}
		}
		String res = result.substring(0, result.length() - 1);
		String rezy = resultzy.substring(0, resultzy.length() - 1) + "]";
		String rekc = resultkc.substring(0, resultkc.length() - 1) + "]";
		String rejx = resultjx.substring(0, resultjx.length() - 1) + "]";
		list.add(res);
		list.add(rezy);
		list.add(rekc);
		list.add(rejx);
		return list;
	}

	/**
	 * 课程评价，获取所有课程的图片路径
	 * 
	 * @param mongoClient
	 * @return
	 */
	public static List<Map<String, String>> getallpiccourse(MongoClient mongoClient) {
		MongoDatabase mongoDatabase = mongoClient.getDatabase("teach");
		MongoCollection<Document> collection = mongoDatabase.getCollection("course_assessment");
		FindIterable<Document> findIterable = collection.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		while (mongoCursor.hasNext()) {
			Document doc = mongoCursor.next();
			List<Document> courses = (List<Document>) doc.get("courses");
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (Document c : courses) {
				String coursename = c.getString("coursename");
				String picurl = c.getString("picurl");
				String id = c.getString("id");
				Map<String, String> map = new HashMap<String, String>();
				map.put("picurl", picurl);
				map.put("id", id);
				map.put("coursename", coursename);
				list.add(map);
			}
			return list;
		}
		return null;
	}

	public static void main(String[] args) {
		MongoDBStorage mongoDBStorage = MongoDBStorage.getInstance();
		MongoClient mongoClient = mongoDBStorage.setUp();
		loadCourse(mongoClient, "济南职业学院");
		// JobAnalysisReposity jobAnalysisReposity = JobAnalysisReposity.getInstance();
		// List<SurveyObj> surveyObjs = jobAnalysisReposity.getSurvey(mongoClient,
		// "question_survey", "survey");
		// for (SurveyObj obj : surveyObjs) {
		// System.out.println(obj.toString());
		// }
	}
}
