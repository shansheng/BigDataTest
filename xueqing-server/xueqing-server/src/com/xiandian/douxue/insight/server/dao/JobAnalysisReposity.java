/**
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.xiandian.douxue.insight.server.utils.UtilTools;

/**
 * MongoDB的数据存储。
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team
 */
public class JobAnalysisReposity  {
	private Logger logger = LoggerFactory.getLogger(getClass()); 

	
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
	 * @param document
	 * @param name
	 * @param value
	 */
	public void appendCityDistribution(Document document,String name,int value,int num,MongoClient mongoClient){
		Object code = null; 
		Object parent_code = null;
		Map<String,Object> map = null;
		map = retrieveCity("China_province_location",33,name,mongoClient);
		if(map!=null){
			code = map.get("code");
		}
		if(code == null){
			map = retrieveCity("China_cities_location",343,name,mongoClient);
			if(map!=null){
				code = map.get("code");
				parent_code = map.get("parent_code");
			}
		}
		document.append("city", name).append("amount", value).append("number", num).append("code", code).append("parent_code", parent_code);
	}
	
	/**
	 * document拼接城市分布专用
	 * @param document
	 * @param name
	 * @param value
	 * @return 
	 */
	public String appendProvinceDistribution(Document document,String name,int value,MongoClient mongoClient){
		Object code = null; 
		Object parent_code = null;
		Map<String,Object> map = null;
		map = retrieveCity("China_province_location",33,name,mongoClient);
		if(map!=null){
			code = map.get("code");
		}
		if(code == null){
			map = retrieveCity("China_cities_location",343,name,mongoClient);
			if(map!=null){
				code = map.get("code");
				parent_code = map.get("parent_code");
				String prov = (String) findParentCity("China_province_location",33,parent_code,mongoClient);
				return prov;
			}
		}
		return name;
	}
	
	/**
	 * 城市归类处理
	 * @return 
	 */
	public Map<String, Object> retrieveCity(String citylevel,int num,String name,MongoClient mongoClient) {
		try{   
	         // 连接到数据库
	         MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");  
	         logger.info("Connect to database successfully");
	         MongoCollection<Document> collection = mongoDatabase.getCollection(citylevel);
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
	        	for(int i=0;i<=num;i++){
		        	Document ds = (Document) doc.get(""+i+"");
		        	Object na = ds.get("name");
		        	Object code = ds.get("code");
		        	Object parent_code = ds.get("parent_code");
		        	if(((String) na).contains(name)){
			            Map<String,Object> map = new HashMap<String,Object>();
			            map.put("code", code);
			            map.put("parent_code", parent_code);
			            return map;
		        	}
	        	}
	         }  
	      }catch(Exception e){
	         logger.error( e.getClass().getName() + ": " + e.getMessage() );
	      }
		return null;
	}
	
	/**
	 * 返回parent_code
	 * @return 
	 */
	public Object retrieveParentCity(String citylevel,int num,String name,MongoClient mongoClient) {
		try{   
	         // 连接到数据库
	         MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");  
	         logger.info("Connect to database successfully");
	         MongoCollection<Document> collection = mongoDatabase.getCollection(citylevel);
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
	        	for(int i=0;i<=num;i++){
		        	Document ds = (Document) doc.get(""+i+"");
		        	Object na = ds.get("name");
		        	if(((String) na).contains(name)){
			        	Object code = ds.get("code");
			        	Object parent_code = ds.get("parent_code");
			            return parent_code;
		        	}
	        	}
	         }  
	      
	      }catch(Exception e){
	         logger.error( e.getClass().getName() + ": " + e.getMessage() );
	      }
		return null;
	}
	
	/**
	 * 通过parentcode的查找父城市
	 * @return 
	 */
	public Object findParentCity(String citylevel,int num,Object parent_code,MongoClient mongoClient) {
		try{   
	         // 连接到数据库
	         MongoDatabase mongoDatabase = mongoClient.getDatabase("job_internet");  
	         logger.info("Connect to database successfully");
	         MongoCollection<Document> collection = mongoDatabase.getCollection(citylevel);
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
	        	for(int i=0;i<=num;i++){
		        	Document ds = (Document) doc.get(""+i+"");
		        	Object na = ds.get("name");
		        	Object code = ds.get("code");
		        	if(code.equals(parent_code)){
			            return na;
		        	}
	        	}
	         }  
	      
	      }catch(Exception e){
	         logger.error( e.getClass().getName() + ": " + e.getMessage() );
	      }
		return null;
	}

}
