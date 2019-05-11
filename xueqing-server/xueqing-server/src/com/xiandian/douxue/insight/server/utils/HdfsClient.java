/*
 * Copyright (c) 2017, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.douxue.insight.server.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

/**
 * hdfs涉及的所有方法
 * 
 * @author XianDian Cloud Team
 * @since V2.0
 * 
 */
public class HdfsClient {
	
	private Properties  hadoopProperties = UtilTools.getConfig(System.getProperty("user.dir") + "/configuration/hadoop.properties");	

	static FileSystem fs;
	static Configuration conf;
	private static final HdfsClient instance = new HdfsClient();

	public static HdfsClient getInstance() {
		return instance;
	}

	private HdfsClient() {
		conf = new Configuration();
		String hdfs = hadoopProperties.getProperty("hdfs");
		conf.set("fs.defaultFS", hdfs);
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param localfile
	 *            :本地的文件路径
	 * @param remotefile
	 *            ：上传到hdfs上的文件路径
	 * @throws Exception
	 */
	public void upload(String localfile, String remotefile) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					localfile));
			OutputStream out = fs.create(new Path(remotefile));
			IOUtils.copyBytes(in, out, 4096, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	  * @author XianDian Cloud Team 
	  * @Description: 上传文件到hdfs 
	  * @return void    返回类型  
	  * @throws
	 */
	public static  void uploadByIo(InputStream is, String remotefile) {
		try {
			OutputStream out = fs.create(new Path(remotefile));
			IOUtils.copyBytes(is, out, 4096, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 

	/**
	 * 下载文件
	 * @param path
	 * @param local
	 * @throws Exception
	 */
	public void downLoad(String path,String local) throws Exception {
		FSDataInputStream in = fs.open(new Path(path));
		OutputStream out = new FileOutputStream(local);
		IOUtils.copyBytes(in, out, 4096, true);
	}
	
	/**
	 * 删除文件及文件夹
	 * @param name
	 * @throws Exception
	 */
	public void delete(String name) throws Exception {
		fs.delete(new Path(name), true);
	}
	

	
//	public static void main(String[] args) throws Exception {
//
//		HdfsClient hdfsDB = new HdfsClient();
//		File file = new File("C:\\Users\\Administrator\\Desktop\\test816");
//		// File file = new File("F://webmagic");
//		if (file.exists()) {
//			File[] files = file.listFiles();
//			for (File file2 : files) {
//				if (!file2.isDirectory()) {
//					hdfsDB.upload(file2.getAbsolutePath(), "/user/PC/dict_in/"
//							+ file2.getName());
//				}
//			}
//		}
//
//	}
}
