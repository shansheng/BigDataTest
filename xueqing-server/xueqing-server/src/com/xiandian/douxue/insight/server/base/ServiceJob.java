/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.base;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务的周期执行封装，由于周期任务每次都是New Job对象，此类只完成对服务任务的调用。
 * 数据收集、数据清洗、数据分析统计、数据展示每个服务后台周期执行。
 * 
 * 
 * @since v1.0
 * @date 20170906
 * @author XianDian Cloud Team
 */
public class ServiceJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(ServiceJob.class);
	public static final String KEY_SERVICE = "Service";
	private String serviceName = "'";
	public ServiceJob() {
	}


	/**
	 * 周期执行调用本方法。
	 * 
	* @param args context
	 */
	@Override
	public void execute(JobExecutionContext context) {
		try {
			Job job = context.getJobInstance();			
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			Service service = (Service) dataMap.get(KEY_SERVICE);
			((ServiceJob)job).serviceName = service.getServiceName();
			logger.info(service != null ? (service.getServiceName() + " : " + service.getServiceName())
					: "Service is NULL!");
			if (service != null && service.isDone()) {
				service.process();
			}
		} catch (Exception e) {
			logger.error("service scheduler error"+e.toString());
		}

	}

	/**
	 * 提交周期任务。
	 * 
	 * @param name
	 * @param group
	 * @param service
	 * @param cron
	 */
	public static void submitCronJob(Server server, String name, String group, Service service, String cron) {
		try {
			// 初始化周期任务
			JobDetail job = newJob(ServiceJob.class).withIdentity(name, group).build();
			job.getJobDataMap().put(ServiceJob.KEY_SERVICE, service);
			// 触发器每天23点开始，第一次，启动就执行
			CronTrigger trigger = newTrigger().withIdentity(name + "Trigger", name + "Trigger")
					.withSchedule(cronSchedule(cron)).build();
			// 提交
			Date ft = server.getScheduler().scheduleJob(job, trigger);
			logger.info(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: "
					+ trigger.getCronExpression());
		} catch (SchedulerException e) {
			logger.info(name + "Job scheduler Failed ");
		}
	}

	/**
	 * 提交一次性任务。
	 * 
	 * @param name
	 * @param group
	 * @param service
	 * @param cron
	 */
	public static void submitOnceJob(Server server, String name, String group, Service service) {
		try {
			// 初始化周期任务
			JobDetail job = newJob(ServiceJob.class).withIdentity(name, group).build();
			job.getJobDataMap().put(ServiceJob.KEY_SERVICE, service);
			// 触发器每天23点开始，第一次，启动就执行
			Trigger trigger = newTrigger().withIdentity(name + "Trigger", name + "Trigger").startNow().build();
			// 提交
			Date ft = server.getScheduler().scheduleJob(job, trigger);
			logger.info(job.getKey() + " has been scheduled to run Now");
		} catch (SchedulerException e) {
			logger.info(name + "Job scheduler Failed ");
		}
	}
	/**
	 * To String
	 */
	public String toString()
	{
		return "ServiceJob" + ":" + serviceName;
	}

}
