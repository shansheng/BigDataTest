package com.xiandian.cloud.common.learning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class marcketRecommend {
	static DecimalFormat df = new DecimalFormat("######0.00");
	public static void main(String[] args) {
		marcketRecommend marcketRecommend = new marcketRecommend();
		marcketRecommend.match();
	}

	public void match() {
		marcketRecommend marcketRecommend = new marcketRecommend();
		List<List<Object>> result = marcketRecommend.getJobSkills();
		System.out.println(result.size());
		for (List<Object> a : result)
			System.out.println(a.get(2));
	}

	public List<RecommendBean> skillsMatch(List<String> major_skill, List<Double> major_weight) {
		List<RecommendBean> recommendBeans=new ArrayList<>();
		List<List<Object>> job_skill_weight = getJobSkills();// 根据产业和类别获取岗位技能点和权重
		for (List<Object> k : job_skill_weight) {// 遍历岗位聚类结果中的每一类
			List<SkillBean> skillBeans=new ArrayList<>();
			RecommendBean recommendBean=new RecommendBean();
			Map<String, Object> oneMatch = new HashMap<>();
			List<String> job_skill = (List<String>) k.get(0);
			List<Double> job_weight = ((List<Double>) k.get(1));
			double sum = 0.0;
			for (Double d : job_weight)
				sum += d;
			List<Double> job_weights = new ArrayList<>();
			for (Double d : job_weight)
				job_weights.add(d / sum * 400);
			List<Double> job_vector = new ArrayList<>();
			int amount = 0;
			for(String str:job_skill) {
				System.out.print(str+",");
				
			}
			System.out.println("---------------");
			for (String s : major_skill)
				if (job_skill.contains(s)) {
					job_vector.add(job_weights.get(job_skill.indexOf(s)));
					amount ++;
				}
				else
					job_vector.add(0.0);
			//oneMatch.put("jobname", (String) k.get(2));
			//oneMatch.put("weight", job_vector);
			recommendBean.setJobname((String) k.get(2));
			SkillBean skillBean=new SkillBean();
			skillBean.setValue(job_vector);
			skillBeans.add(skillBean);
			recommendBean.setSkillBeans(skillBeans);
			double similarity = Double.parseDouble(df.format(amount * 1.0 / job_vector.size()));
			recommendBean.setSimilarity(similarity+"");
			recommendBeans.add(recommendBean);
		}
		return recommendBeans;	
	}

	public static List<List<Object>> getJobSkills() {
		List<List<Object>> skill_weight = new ArrayList<>();
		MongoClient mongoClient = new MongoClient("10.10.4.35", 27017);
		MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");
		MongoCollection<Document> collection = mongoDatabase.getCollection("all_cluster_job");
		FindIterable<Document> findIterable = collection.find();
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		while (mongoCursor.hasNext()) {
			Document doc = mongoCursor.next();
			List<Document> categoriess = (List<Document>) doc.get("categories");
			for (Document cats : categoriess) {
				List<Document> categories = (List<Document>) cats.get("category");

				for (Document cat : categories) {
					List<Object> job_skills = new ArrayList<>();
					List<String> skill1 = new ArrayList<>();
					List<Double> weight = new ArrayList<>();
					String name = cat.getString("job_name");
					List<Document> skills = (List<Document>) cat.get("skills");
					for (Document skill : skills) {
						if (skill != null) {
							skill1.add(skill.getString("Skill_name"));
							weight.add(skill.getDouble("Skill_weight"));
						}
					}
					double sum = 0;
					for (double dou : weight)
						sum = sum + dou;
					List<Double> weight0 = new ArrayList<>();
					for (double dou : weight)
						weight0.add(dou / sum);
					job_skills.add(skill1);
					job_skills.add(weight);
					job_skills.add(name);
					skill_weight.add(job_skills);
				}
			}
		}
		mongoClient.close();
		return skill_weight;
	}
}
