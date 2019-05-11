/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.service.job.collect;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiandian.douxue.insight.server.dao.JobDataReposity;
import com.xiandian.douxue.insight.server.utils.Format_transform;
import com.xiandian.douxue.insight.server.utils.HdfsClient;
import com.xiandian.douxue.insight.server.utils.ReadFile;
import com.xiandian.douxue.insight.server.utils.UtilTools;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 网络爬虫。 
 * 1. url configuration 
 * 2. Download HTML --> HDFS 
 * 3. RowID -->HBbase
 * |rowID|Raw Data (hdfs file path) | Tag Data(( Comp, Major Fields) |Flag )| *
 * 4.Clean Data.
 * 
 * @since v1.0
 * @date 20170816
 * @author XianDian Cloud Team
 */
public class WYJobPageCrawler extends JobPageCrawler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private JobDataReposity jobDataReposity = JobDataReposity.getInstance();
	private static final String HDFS = "hdfs";
	private static final String Suffix = ".html";
	// 原始数据存储HDFS
	private Properties hadoopProperties = UtilTools
			.getConfig(System.getProperty("user.dir") + "/configuration/hadoop.properties");
	// hdfs存储路径
	private String job_rawdata_path = hadoopProperties.getProperty("job_rawdata_path");
	private Pattern pattern = Pattern.compile("/([0-9]+)\\.html");
	// 结束搜索网址
	private String breakUrl = "";
	// 列表网页网址
	private String listUrl = "";
	// 岗位网页网址
	private String pageUrl = "";

	// 解析配置文件

	/**
	 * 51Job的爬虫类。
	 */
	public WYJobPageCrawler() {
		String jobConfig = ReadFile.ReadFile(System.getProperty("user.dir") + "/configuration/job_config.json");
		try {
			JSONObject jsonObject = new JSONObject(jobConfig);
			// 收集 urls，根据搜索的内容，爬多个网站和多个网站的urls
			JSONArray wbsites = jsonObject.getJSONArray("wbsites");
			for (int i = 0; i < wbsites.length(); i++) {
				JSONObject wbsite = wbsites.getJSONObject(i);
				if (wbsite.getString("wbsitename").equals("51Job")) {
					breakUrl = wbsite.getString("breakurl");
					listUrl = wbsite.getString("listurl");
					pageUrl = wbsite.getString("pageurl");
					break;
				}
			}

		} catch (JSONException exp) {
			logger.error(exp.toString());
		}

	}

	/**
	 * 爬虫单个页面的处理方法
	 */
	@Override
	public void process(Page page) {
		
	}
}
