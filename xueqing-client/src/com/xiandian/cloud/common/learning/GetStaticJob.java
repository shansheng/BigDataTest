package com.xiandian.cloud.common.learning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class GetStaticJob {
	private Logger logger = LoggerFactory.getLogger(getClass());
	//private static Properties mangoDBProperties = UtilTools
			//.getConfig(System.getProperty("user.dir") + "/configuration/mongodb.properties");
	DecimalFormat df = new DecimalFormat("######0.00");  

	public void getStandardJob(String jobname) {
		try {
			MongoClient mongoClient = new MongoClient("10.10.4.35",27017);
			MongoDatabase mongoDatabase = mongoClient.getDatabase("job_skills");
			MongoCollection<Document> collection = mongoDatabase.getCollection("static_skills");
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			List<String> categories = new ArrayList<>();
			List<List<String>> skills = new ArrayList<>();
			List<List<Double>> weights = new ArrayList<>();
			while (mongoCursor.hasNext()) {
				Document document = mongoCursor.next();
				if (document.getString("direction_name").equals(jobname)) {
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
			mongoClient.close();
			for (int i = 0; i < categories.size(); i++) {
				System.out.println(categories.get(i) + ":");
				for (int j = 0; j < skills.get(i).size(); j++) {
					System.out.print(skills.get(i).get(j) + ":" + weights.get(i).get(j) + "  ");
				}
				System.out.println("");
				System.out.println("");
			}
			
			
		} catch (Exception e) {
			logger.info(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		GetStaticJob getStaticJob = new GetStaticJob();
		getStaticJob.getStandardJob("云计算运维");
	}
}
