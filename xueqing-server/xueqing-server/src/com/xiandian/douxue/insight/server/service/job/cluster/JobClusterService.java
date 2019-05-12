/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.service.job.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.xiandian.douxue.insight.server.base.Server;
import com.xiandian.douxue.insight.server.base.Service;
import com.xiandian.douxue.insight.server.base.ServiceJob;
import com.xiandian.douxue.insight.server.base.ServiceState;
import com.xiandian.douxue.insight.server.dao.HbaseStatistics;
import com.xiandian.douxue.insight.server.dao.MongoDBStorage;
import com.xiandian.douxue.insight.server.utils.ReadFile;
import com.xiandian.douxue.insight.server.utils.UtilTools;

/**
 * 岗位聚类。岗位通过爬取，取标签，然后在分感知数据，最后进行岗位距离。
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team
 */
public class JobClusterService implements Service {
	private Properties cronProperties = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/service_cron.properties");
	private Server server;
	private MongoClient mongoClient;
	private Service parent;

	private static MongoDBStorage mongodbstorage = MongoDBStorage.getInstance();

	private String SERVICE_NAME = "douxue/insight/job/cluster";
	private Logger logger = LoggerFactory.getLogger(getClass());

	// 记录任务是否完成
	private AtomicBoolean isTaskDone = new AtomicBoolean();
	private int[] clusterCount = { 3 };

	/**
	 * 岗位聚类服务。
	 * 
	 * @param server
	 * @param service
	 */
	public JobClusterService(Server server, Service service) {
		this.server = server;
		this.parent = service;
		init();
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public ServiceState init() {
		isTaskDone.set(true);
		return ServiceState.STATE_INITIALIZED;

	}

	@Override
	public ServiceState start() {
		String isStop = cronProperties.getProperty("jobcluster_service");
		if (isStop.equals("stop")) {
			logger.info("外部User控制不启动服务" + SERVICE_NAME);
			return ServiceState.STATE_NOTSTART;
		}
		// 本次强制执
		ServiceJob.submitOnceJob(server, SERVICE_NAME + "/start", this.parent.getServiceName(), this);

		String cron = cronProperties.getProperty("jobcluster_cron");
		logger.info(this.SERVICE_NAME + cron);
		ServiceJob.submitCronJob(server, SERVICE_NAME, this.parent.getServiceName(), this, cron);
		return ServiceState.STATE_STARTED;
	}

	/**
	 * 具体的实现。
	 */
	public ServiceState process() {/////
		JobCluster jobCluster = new JobCluster();
		mongoClient = mongodbstorage.setUp();

		String jobConfig = ReadFile.ReadFile(System.getProperty("user.dir") + "/configuration/job_classification.json");
		JSONObject jsonObject = new JSONObject(jobConfig);
		JSONArray totals = jsonObject.getJSONArray("total");
		for (int i = 0; i < totals.length(); i++) {
			JSONObject total = totals.getJSONObject(i);
			String industry = total.getString("industry");
			JSONArray classfications = total.getJSONArray("classification");
			for (int ii = 0; ii < classfications.length(); ii++) {
				JSONObject classfication = classfications.getJSONObject(ii);
				String category = classfication.getString("category");
				String[] keywords = classfication.getString("keywords").split(",");
				List<String> dictionary = new ArrayList<String>();
				for (String keyword : keywords) {
					dictionary.add(keyword);
				}
				jobCluster.cluster(industry, category, dictionary, clusterCount[0], mongoClient);
			}
		}
		return ServiceState.STATE_RUNNING;
	}

	@Override
	public boolean isDone() {
		return isTaskDone.get();
	}

	@Override
	public ServiceState getState() {

		return null;
	}

}
