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
 * 网络爬虫。 1. url configuration 2. Download HTML --> HDFS 3. RowID -->HBbase
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
		// Init select and urls
		Selectable select = null;
		List<String> urls = null;
		// 如果是下一级别的内容
		if (page.getUrl().toString().contains("http://localhost:8080/xueqing-web/course/index/")) {
			// ?
			select = page.getHtml().xpath("//p[@class='PositionName']//a");
			urls = select.links().all();
			page.addTargetRequests(urls);
			// ?
			select = page.getHtml().xpath("//div[@class='dw_page']");
			urls = select.links().all();
			// ?
			Iterator<String> it = urls.iterator();
			// ?
			while (it.hasNext()) {
				String x = it.next();
				if (x.equals(breakUrl)) {
					it.remove();
				}
			}
			// 收集下一级的url
			page.addTargetRequests(urls);
		}
		// 岗位页面
		else if (page.getUrl().toString().startsWith("http://localhost:8080/xueqing-web/")) {
			try {
				Map<String, Object> map = new HashMap<>();
				// TODO: 每个页面不能重新读取配置，每次爬虫任务
				String JsonContext = ReadFile
						.ReadFile(System.getProperty("user.dir") + "/configuration/job_config.json");
				JSONObject jsonObject = new JSONObject(JsonContext);
				JSONArray wbsites = jsonObject.getJSONArray("wbsites");
				for (int i = 0; i < wbsites.length(); i++) {
					JSONObject wbsite = wbsites.getJSONObject(i);
					String wbsitename = wbsite.getString("wbsitename");
					if (wbsitename.equals("51Job")) {
						// 设置来源
						map.put("resource", wbsitename);
						JSONArray fields = wbsite.getJSONArray("fields");
						for (int j = 0; j < fields.length(); j++) {
							JSONObject field = fields.getJSONObject(j);
							String chinesename = field.getString("chinesename");
							String name = field.getString("name");
							String path = field.getString("path");
							if (name.equals("companymess")) {
								String companymess = Format_transform
										.gb2312ToUtf8(page.getHtml().xpath(path).toString());
								String[] strs = UtilTools.parseCompony(companymess);
								map.put("nature", Format_transform.change(strs[0]));
								map.put("scale", Format_transform.change(strs[1]));
								map.put("industry", Format_transform.change(strs[2]));
							} else {
								// ?
								if (path.startsWith("//")) {
									String objectStr = Format_transform
											.gb2312ToUtf8(page.getHtml().xpath(path).toString());
									logger.info(chinesename + ":" + objectStr);
									map.put(name, Format_transform.change(objectStr));
								} else {
									String objectStr = Format_transform
											.gb2312ToUtf8(page.getHtml().regex(path).toString());
									logger.info(chinesename + ":" + objectStr);
									map.put(name, Format_transform.change(objectStr));
								}
								// ?
							}
						}
					}
				}

				// 设置url
				map.put("url", page.getUrl().toString());
				// 设置岗位ID
				Matcher matcher = pattern.matcher(page.getUrl().toString());
				String pageID = null;
				while (matcher.find()) {
					pageID = matcher.group(1);
				}
				// 如果没有ID，不处理
				if (pageID == null) {
					return;
				}
				// 网址是否已经爬取
				boolean isExist = false;

				isExist = jobDataReposity.queryDataById("job_internet", "TAG_DATA", "ID", pageID);
				// 保存到HBase中，并设置结束日期为明天
				if (pageID != null && isExist == false) {
					map.put("id", pageID);
					// 需要常量
					map.put(HDFS, job_rawdata_path + pageID + Suffix);
					jobDataReposity.insertData("job_internet", map);
					// DownloadHtml.Save(page.getUrl().toString(), job_rawdata_path);
				} else {
					// 找到这个岗位，设置它的结束日期为今天+1
					jobDataReposity.insertEndTime("job_internet", map);
				}
			} catch (Exception exp) {
				logger.error(page.getUrl() + exp.toString());
			}
		}
	}
}
