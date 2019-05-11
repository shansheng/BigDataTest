/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.init;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiandian.douxue.insight.server.base.Server;
import com.xiandian.douxue.insight.server.base.Service;
import com.xiandian.douxue.insight.server.base.ServiceState;

/**
 * 系统初始化：5大服务的配置信息：包括Hadoop配置信息、HBASE配置信息、MongoDB配置信息、爬虫配置信息。
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team
 */
public class InitializeService implements Service {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Scheduler sched;
	private SchedulerFactory sf;
	private Server server;

	/**
	 * 返回全局调度器。
	 * 
	 * @return
	 */
	public Scheduler getScheduler() {
		return sched;
	}

	@Override
	public Server getServer() {
		return server;
	}

	/**
	 * Singleton Instance.
	 */
	public InitializeService(Server server) {
		this.server = server;
		init();
	}

	@Override
	public ServiceState init() {
		// 创建调度器
		try {
			sf = new StdSchedulerFactory();
			sched = sf.getScheduler();
			// 启动
			sched.start();
		} catch (SchedulerException e) {
			logger.warn(this.getServiceName() + "Scheduler Started Failed!");
		}
		return null;
	}

	@Override
	public ServiceState start() {

		return null;
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

		return null;
	}

	@Override
	public ServiceState process() {
		// TODO Auto-generated method stub
		return null;
	}

}
