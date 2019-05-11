/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.service.job.collect;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiandian.douxue.insight.server.base.Server;
import com.xiandian.douxue.insight.server.base.Service;
import com.xiandian.douxue.insight.server.base.ServiceJob;
import com.xiandian.douxue.insight.server.base.ServiceState;
import com.xiandian.douxue.insight.server.utils.ReadFile;
import com.xiandian.douxue.insight.server.utils.UtilTools;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;

/**
 *岗位收集类，收集来自互联网、系统发布的招聘岗位、学校合作企业的招聘岗位。 
 * 收集服务是个任务，按照周期执行。
 * 每个互联网网站的信息解析不同，应该有不同的实现方法。
 * 
 * 
 * @since v1.0
 * @date 20170815
 * @author XianDian Cloud Team
 */
public class JobCollectService implements Service{
	private Server server;
	private Service parent;
	private String SERVICE_NAME = "douxue/insight/job/collect";
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Properties cronProperties = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/service_cron.properties");
	/*
	 * 使用WebMagic 爬虫爬网站招聘信息。
	 * 开源官网参考：http://webmagic.io/docs/zh/
	 */
	private Spider spider;

	/**
	 * 创建数据收集服务。
	 * @param server
	 * @param service
	 */
	public JobCollectService(Server server, Service service) {
		this.server = server;
		this.parent = service;
		init();
	}

	/**
	 * Get Edu Insighter server to submit cron job. 
	 */
	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	/**
	 * 初始化。
	 */
	@Override
	public ServiceState init() {
		return ServiceState.STATE_INITIALIZED;
	}

	/**
	 * 第一次启动服务，立即启动爬虫。
	 */
	@Override
	public ServiceState start() {		
		//控制服务，每个服务都可以独立运行或不启动
		//支持程序部署多个
		//TODO：后期可以增加Zookeeper调度
		String isStop = cronProperties.getProperty("jobcollect_service");
		if (isStop.equals("stop"))
		{
			logger.info("User控制不启动服务：" + SERVICE_NAME);
			return ServiceState.STATE_NOTSTART;
		}
		//第一次先启动(名称必须和周期任务不一样）
		ServiceJob.submitOnceJob(server, SERVICE_NAME+"/start", this.parent.getServiceName(), this);		
		
		// 周期执行0 0 23 * * ? (every day 23 clock)		
		String cron=cronProperties.getProperty("jobcollect_cron");		
		ServiceJob.submitCronJob(server, SERVICE_NAME, this.parent.getServiceName(), this, cron);
		
		logger.info(this.SERVICE_NAME + cron);
		return ServiceState.STATE_STARTED;
	}

	/**
	 * 具体的爬虫实现，周期任务调用。
	 */
	public ServiceState process() {
		// 启动互联网爬虫爬取岗位
		startInternetJobCrawler();
		return ServiceState.STATE_RUNNING;
	}

	/**
	 * 开始网络爬虫。
	 */
	private synchronized void startInternetJobCrawler() {
		// 存放所有的初始URL
		List<String> jobUrls = new ArrayList<>();
		// 读取初始化网页
		logger.info(getServiceName(), "读取爬虫网站、URL和字段的配置信息job_config.json");
		//读取网址列表,读取要爬取的字段		
		String jobConfig = ReadFile.ReadFile(System.getProperty("user.dir") + "/configuration/job_config.json");
		
		logger.info(getServiceName(), "爬虫配置信息详情:" + jobConfig);
		try {
			// json to Object
			JSONObject jsonObject = new JSONObject(jobConfig);
			// 收集 urls，根据搜索的内容，爬多个网站和多个网站的urls
			JSONArray wbsites = jsonObject.getJSONArray("wbsites");
			for (int i = 0; i < wbsites.length(); i++) {
				JSONObject wbsite = wbsites.getJSONObject(i);		
					//解析爬去的地址
					JSONArray jobUrlsJson = wbsite.getJSONArray("initurl");
					for (int j = 0; j < jobUrlsJson.length(); j++) {						
						String url = jobUrlsJson.getJSONObject(j).getString("url");
						jobUrls.add(url);
				}
			}
			// 开始爬取数据
			if (jobUrls != null && jobUrls.size() > 0) {
				//创建爬虫
				spider = Spider.create(new WYJobPageCrawler());
				 for (String str : jobUrls) {
					 spider.addUrl(str);
				 }
				 // start 异步执行 (spider.run)
				spider.thread(50).setExitWhenComplete(true);
				spider.start();				
			}
		} catch (Exception exp) {
			logger.error("JobCrawler exception:" + exp.toString());
		}
	}

	/**
	 * 数据收集一次周期是否完成。
	 */
	@Override
	public boolean isDone() {
		return spider != null ? spider.getStatus() == Spider.Status.Stopped : true;
	}

	@Override
	public ServiceState getState() {
		return ServiceState.STATE_RUNNING;
	}	
}
