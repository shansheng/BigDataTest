/**
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import com.mongodb.MongoClient;

/**
 * 用于生成.csv文件（培养方案中培养目标d3图需要）
 * 
 * @since v1.0
 * @date 20170815
 * @author whh
 */
public class CsvStorage {
	private static MongoDBStorage mongodbstorage = MongoDBStorage.getInstance();
	private static MongoClient mongoClient = mongodbstorage.setUp();
	private static JobAnalysisReposity jobAnalysisReposity = JobAnalysisReposity.getInstance();
	
	 /**
      * 创建CSV文件
     */
	public static void createCSV(MongoClient mongoClient,String filename) {
		// 表格头
		Object[] head = { "id", "value", "color", };
		List<Object> headList = Arrays.asList(head);
        //数据
		@SuppressWarnings("static-access")
		List<List<Object>> dataList = jobAnalysisReposity.getTarget(mongoClient,filename);
        String fileName = filename  + ".csv";//文件名称
        String realpath = com.xiandian.cloud.common.util.UtilTools.getConfig().getProperty("realpath");
        String filePath = realpath + "/src/main/webapp/learning/"; //文件路径
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
           csvFile = new File(filePath + fileName);
           File parent = csvFile.getParentFile();
           if (parent != null && !parent.exists()) {
               parent.mkdirs();
           }
           csvFile.createNewFile();
           // GB2312使正确读取分隔符","
	       csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"), 1024);
	       int num = headList.size() / 2;
	       StringBuffer buffer = new StringBuffer();
           for (int i = 0; i < num; i++) {
        	   buffer.append(" ,");
           }
	       // 写入文件头部
           writeRow(headList, csvWtriter);
           // 写入文件内容
	       for (List<Object> row : dataList) {
	    	   writeRow(row, csvWtriter);
	       }
	       csvWtriter.flush();
        } catch (Exception e) {
	              e.printStackTrace();
        } finally {
        	try {
        		csvWtriter.close();
        	} catch (IOException e) {
              e.printStackTrace();
            }
        }
	}
	      
	/**
	* 写一行数据
	* @param row 数据列表
	* @param csvWriter
	* @throws IOException
	*/
	private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
		for (Object data : row) {
			StringBuffer sb = new StringBuffer();
			String rowStr = sb.append("\"").append(data).append("\",").toString();
			csvWriter.write(rowStr);
		}
		csvWriter.newLine();
	}
	      
	public static void main(String[] args) {
		MongoClient mongoClient = mongodbstorage.setUp();
		createCSV(mongoClient,"zzkjzyjsxy");
	}
}
