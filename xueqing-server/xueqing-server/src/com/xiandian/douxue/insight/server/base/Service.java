/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.base;

/**
 * 服务，包括5大服务(JobService\MajorService\LeaningService\TeachingService\EmployService)
 * 每个服务有运行状态。每个服务包括数据处理过程。 每个服务包括： 1)数据获取：爬虫、斗学网站数据接入。(access) 2) 数据存储。(storage)
 * 3)启动数据清洗处理。(process) 4)启动数据挖掘和分析(analysis/insight) 5)启动大数据对外服务(service)。
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team
 */
public interface Service {
	/**
	 * 获得Edu Server.
	 * @return
	 */
	public Server getServer();

	/**
	 * ServiceName，子服务带父服务的路径。Service://JobAnalyzeService/
	 * 
	 * @return
	 */
	public String getServiceName();

	/**
	 * 服务生命周期：初始化。只做一次。
	 * 
	 * @return
	 */
	public ServiceState init();

	/**
	 * 服务生命周期：开始执行，可以反复调用。
	 * 
	 * @return
	 */
	public ServiceState start();
	
	/**
	 * 服务生命周期：任务是否完成，如果是周期性的任务，本次任务是否执行完成。
	 * 
	 * @return
	 */
	public boolean isDone();
	
	/**
	 * 
	 * 执行任务，只要任务启动完毕，就可以调用。
	 * @return
	 */
	public ServiceState process();

	/**
	 * 服务生命周期：当前状态。
	 * 
	 * @return
	 */
	public ServiceState getState();



}
