/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.base;

import org.quartz.Scheduler;

/**
 * 斗学学情分析系统Server。 启动顺序：
 * 1.初始化化，读取配置Initializer 。
 * 2.启动各个服务(JobService\MajorService\LeaningService\TeachingService\EmployService）
 * 每个服务包括：
 * 			1)数据获取：爬虫、斗学网站数据接入。(access)
 * 			2) 数据存储。(storage)
 * 			3)启动数据清洗处理。(process)
 * 			4)启动数据挖掘和分析(analysis/insight)
 * 			5)启动大数据对外服务(service)。
 *  3. 消息通知，当一个任务完成后，向所有服务派发消息，如果服务有依赖关系，可以进行处理。

 * @since v1.0
 * @date 20170815
 *  @author XianDian Cloud Team
 */
public interface Server extends Service{
		/**
	 * 派发服务状态。
	 * @param service
	 */
	public void notifyServiceState(Service service);
	
	/**
	 * 获取全局任务调用器。
	 * @return
	 */
	public Scheduler getScheduler();

}
