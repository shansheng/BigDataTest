/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server;

import java.util.ArrayList;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xiandian.douxue.insight.server.base.Server;
import com.xiandian.douxue.insight.server.base.Service;
import com.xiandian.douxue.insight.server.base.ServiceState;
import com.xiandian.douxue.insight.server.init.InitializeService;
import com.xiandian.douxue.insight.server.service.job.JobAnalysisService;

/**
 * 斗学学情分析系统Server。 启动顺序：
 *  1.初始化化，读取配置Initializer 。
 * 2.启动各个服务
 * 服务流程包括： 
 * 1)		数据获取：爬虫、斗学网站数据接入。(collect) 
 * 2)		数据存储。(storage) 
 * 3)		启动数据清洗处理。(process)
 * 4)		启动数据挖掘和分析(analysis) 
 * 5)		启动大数据对外服务(service)。
 * 服务支持：
 * 1) job：岗位分析；爬虫爬招聘信息，聚类，分析分布
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team
 */
public class EduInsightServer implements Server, Service {
	/**
	 * 保存5大服务（岗位、专业、学习、教学和就业）
	 */
	private ArrayList<Service> services = new ArrayList<Service>();
	private InitializeService initializer;
	private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 构造函数。
	 */
	public EduInsightServer() {
	}
	/**
	 * 启动服务
	 * 
	 * @param args
	 *            系统配置目录
	 */
	@Override
	public ServiceState start() {
		// 0 初始化(Hadoop, MongoDB, Douxue)
		logger.info("Start Initializer Service...");
		initializer = new InitializeService(this);
		initializer.start();
		services.add(initializer);
		// 1 岗位分析
		logger.info("Start jobAnalyzer Service...");
		Service jobAnalyzer = new JobAnalysisService(this, initializer);
		jobAnalyzer.start();
		
		return ServiceState.STATE_RUNNING;
	}

	@Override
	public void notifyServiceState(Service service) {

	}

	@Override
	public String getServiceName() {
		
		return null;
	}

	@Override
	public ServiceState init() {
		// TODO: 1Spring Bean

		// TODO: 2Spring Data(HDFS\HBase\MongoDB)

		// 3 Spring Scheduler(Scheduler in Spring with Quartz.)
		//ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Scheduler.xml");

		return ServiceState.STATE_INITIALIZED;
	}

	@Override
	public boolean isDone() {		
		return false;
	}

	@Override
	public ServiceState getState() {		
		return null;
	}

	@Override
	public Server getServer() {
		return this;
	}
	@Override
	public Scheduler getScheduler() {
		return initializer.getScheduler();
	}
	
	/**
	 * 启动main函数
	 * @param args
	 */
	public static void main(String[] args) {
		EduInsightServer vEduInsightServer = new EduInsightServer();
		vEduInsightServer.start();
	}
	@Override
	public ServiceState process() {
		// TODO Auto-generated method stub
		return null;
	}
}