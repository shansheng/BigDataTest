/**
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.xiandian.douxue.insight.server.utils.UtilTools;

import net.sf.json.JSONObject;

/**
 * MongoDB的数据存储。
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team
 */
public class MongoDBStorage {
	private Properties PROPERTIES = UtilTools.getConfig(System.getProperty("user.dir") + "/configuration/mongodb.properties");	
	private Logger logger = LoggerFactory.getLogger(getClass()); 
	private MongoClient mongoClient;
	/**
	 * 构造
	 */
	private static MongoDBStorage instance;

	public static synchronized MongoDBStorage getInstance() {
		if (instance == null) {
			instance = new MongoDBStorage();
		}
		return instance;
	}
	/**
	 * 插入数据
	 * @param collections
	 * @param name
	 * @param value
	 */
	public void insert(String collections,String name,int value,MongoClient mongoClient) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");
			logger.info("Connect to database successfully");
			MongoCollection<Document> collection = mongoDatabase.getCollection(collections);
			logger.info("集合选择成功");
			/**
			 * 1. 创建文档 org.bson.Document 参数为key-value的格式 2. 创建文档集合List
			 * <Document> 3. 将文档集合插入数据库集合中 mongoCollection.insertMany(List
			 * <Document>) 插入单个文档可以用 mongoCollection.insertOne(Document)
			 */
			Document document = new Document();
			document.append(name, value);
			collection.insertOne(document);
			logger.info("文档插入成功");
		} catch (Exception e) {
			logger.error(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * 连接数据库并创建Document
	 * @param collections
	 * @param name
	 * @param value
	 * @return 
	 * @return 
	 */
	public MongoClient setUp() {
		try {
			if(mongoClient!=null) {
				return mongoClient;
			}
			// 连接到 mongodb 服务
			 mongoClient = new MongoClient(PROPERTIES.getProperty("server_ip"),Integer.parseInt(PROPERTIES.getProperty("server_port")));
			return mongoClient;
		} catch (Exception e) {
			logger.error(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 连接数据库并创建Document
	 * @param collections
	 * @param name
	 * @param value
	 * @return 
	 */
	public Map create(String database,String collections,MongoClient mongoClient) {
		try {
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
			logger.info("Connect to database successfully");
			MongoCollection<Document> collection = mongoDatabase.getCollection(collections);
			logger.info("集合选择成功");
			/**
			 * 1. 创建文档 org.bson.Document 参数为key-value的格式 2. 创建文档集合List
			 * <Document> 3. 将文档集合插入数据库集合中 mongoCollection.insertMany(List
			 * <Document>) 插入单个文档可以用 mongoCollection.insertOne(Document)
			 */
			Document document = new Document();
			Map map = new HashMap<>();
			map.put("collection", collection);
			map.put("document", document);
			return map;
		} catch (Exception e) {
			logger.error(e.getClass().getName() + ": " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * document拼接
	 * @param document
	 * @param name
	 * @param value
	 */
	public void append(Document document,String name,int value){
		document.append(name,value);
	}
	
	public void appendString(Document document,String name,String value){
		document.append(name,value);
	}
	
	public void appendObject(Document document,String name,Object value){
		document.append(name,value);
	}
	
	public void appendDouble(Document document,String name,double value){
		document.append(name,value);
	}
	
	public void appendArray(Document document,String name,List<Document> list){
		document.append(name,list);
	}
	
	public void appendArrayVector(Document document,String name,Vector<Document> vec){
		document.append(name,vec);
	}
	
	public void appendProvince(Document document,String name,int value, int num){
		document.append("name", name).append("amount", value).append("number", num);
	}
	
	public void appendExperience(Document document,String name,int value,int num){
		document.append("type",name).append("amount", value).append("number", num);
	}
	
	public void appendScale(Document document,String name,String scale,int value,int num){
		document.append("type",name).append("name", scale).append("amount", value).append("number", num);
	}
	
	public void appendCount(Document document,String name,int value){
		document.append("type",name).append("amount", value);
	}
	
	public void appendAnalyse(Document document,Document docu,String job){
		document.append(job,docu);
	}
	
	public void appendAnalyses(Document document,String job){
		document.append(job,document);
	}

	public void insertOne(MongoCollection<Document> collection,Document document){
		collection.insertOne(document);
	}
	/**
	 * 查询所有
	 */
	public void retrieve(MongoClient mongoClient) {
		try{   
	         // 连接到数据库
	         MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");  
	         logger.info("Connect to database successfully");
	         MongoCollection<Document> collection = mongoDatabase.getCollection("job_province_distribution");
	         logger.info("集合选择成功");
	         //检索所有文档  
	         /** 
	         * 1. 获取迭代器FindIterable<Document> 
	         * 2. 获取游标MongoCursor<Document> 
	         * 3. 通过游标遍历检索出的文档集合 
	         * */  
	         FindIterable<Document> findIterable = collection.find();  
	         MongoCursor<Document> mongoCursor = findIterable.iterator();  
	         while(mongoCursor.hasNext()){  
	            Document doc = mongoCursor.next();
	            Object _id = doc.get("_id");
	            doc.remove("_id");
	            String zz = doc.toString();
	            String ss =  doc.toJson();
	            JSONObject jsStr = JSONObject.fromObject(ss);
	            Iterator a = jsStr.keys();
	            String[] sh = new String[34];
	            int[] zh = new int[34];
	            JSONObject json = new JSONObject();
	            String[] com =new String[34];
	            for(int i=0;i<34;i++){
	            	sh[i] = (String) a.next();
	            	zh[i] = (int) jsStr.get(sh[i]);
	            	System.out.println(zh[i]);
	            	json.put("name", sh[i]);
	            	json.put("value", zh[i]);
	            	com[i] = json.toString();
	            }
	            logger.info(Arrays.toString(com));
	         }  
	      
	      }catch(Exception e){
	         logger.error( e.getClass().getName() + ": " + e.getMessage() );
	      }
	}
	
	/**
	 * 删除
	 */
	public void delCollection(MongoClient mongoClient) {
		try{   
	         // 连接到数据库
	         MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");  
	         logger.info("Connect to database successfully");
	         MongoCollection<Document> collection = mongoDatabase.getCollection("runoob");
	         logger.info("集合选择成功");
	         //检索所有文档  
	         /** 
	         * 1. 获取迭代器FindIterable<Document> 
	         * 2. 获取游标MongoCursor<Document> 
	         * 3. 通过游标遍历检索出的文档集合 
	         * */  
	         mongoClient.dropDatabase("job_integer");
	         collection.drop();
	         FindIterable<Document> findIterable = collection.find();  
	         MongoCursor<Document> mongoCursor = findIterable.iterator();  
	      }catch(Exception e){
	         logger.error( e.getClass().getName() + ": " + e.getMessage() );
	      }
	}
	
}
