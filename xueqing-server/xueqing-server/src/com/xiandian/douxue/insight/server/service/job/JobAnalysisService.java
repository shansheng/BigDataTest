/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.service.job;

import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiandian.douxue.insight.server.base.Server;
import com.xiandian.douxue.insight.server.base.Service;
import com.xiandian.douxue.insight.server.base.ServiceState;
import com.xiandian.douxue.insight.server.dao.HBaseStorage;
import com.xiandian.douxue.insight.server.dao.MongoDBStorage;
import com.xiandian.douxue.insight.server.service.job.clean.JobCleanService;
import com.xiandian.douxue.insight.server.service.job.cluster.JobClusterService;
import com.xiandian.douxue.insight.server.service.job.collect.JobCollectService;
import com.xiandian.douxue.insight.server.utils.UtilTools;

/**
 * 岗位分析。
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team (define class).
 * @author XianDian Cloud Team (implement function).
 */
public class JobAnalysisService implements Service {
	
	/**
	 * 几大服务：岗位爬虫(HDFS,HBASE-RAWDATA)、 清洗(HBase PerceptData)、 统计（数据统计）、 分析（岗位聚类）。
	 */
	private ArrayList<Service> services = new ArrayList<Service>();
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Server server;
	private String SERVICE_NAME = "douxue/insight/job/";
	private HBaseStorage hBaseStorage = HBaseStorage.getInstance();
	private Properties hbaseProperties = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/hbase.properties");

	/**
	 * 几大服务：岗位爬虫(HDFS,HBASE-RAWDATA)、 清洗(HBase PerceptData)、 统计（数据统计）、 分析（岗位聚类）。
	 */
	public JobAnalysisService(Server server, Service initializer) {
		this.server = server;
		this.init();	
	}

	/**
	 * 初始化。
	 */
	@Override
	public ServiceState init() {
		String server = hbaseProperties.getProperty("server_ip");
		String port = hbaseProperties.getProperty("server_port");
		String path = hbaseProperties.getProperty("parent_path");
		
		hBaseStorage.setUp(server, port,path);
		return ServiceState.STATE_INITIALIZED;
	}

	@Override
	public ServiceState start() {

//		 1. 启动收集服务（虫）
//		Service jobCollector = new JobCollectService(server, this);
//		jobCollector.start();
//		// 2. 启动清洗服务（清洗）
//		Service jobCleaner = new JobCleanService(server, this);
//		jobCleaner.start();
//		// 3. 启动分析服务（聚类）
		Service jobCluster = new JobClusterService(server, this);
		jobCluster.start();
		return ServiceState.STATE_RUNNING;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public ServiceState getState() {
		return null;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public Server getServer() {

		return null;
	}

	@Override
	public ServiceState process() {
		// TODO Auto-generated method stub
		return null;
	}

}
