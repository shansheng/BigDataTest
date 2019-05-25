/*
 * Copyright (c) 2014, 2017, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.MongoClient;
import com.xiandian.cloud.common.bean.FileBean;
import com.xiandian.cloud.common.bean.MessageBean;
import com.xiandian.cloud.common.learning.CsvStorage;
import com.xiandian.cloud.common.learning.EchartsObj;
import com.xiandian.cloud.common.learning.GetStudentMyData;
import com.xiandian.cloud.common.learning.JobAnalysisReposity;
import com.xiandian.cloud.common.learning.MongoDBStorage;
import com.xiandian.cloud.common.util.HdfsDB;
import com.xiandian.cloud.entity.core.User;

import net.sf.json.JSONObject;

/**
 * 学情相关控制器
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
@Controller
@RequestMapping("/learning")
public class LearningController extends BaseController {
	private MongoDBStorage mongodbstorage = MongoDBStorage.getInstance();
	private MongoClient mongoClient = mongodbstorage.setUp();
	private JobAnalysisReposity jobAnalysisReposity = JobAnalysisReposity.getInstance();
	private CsvStorage csvstorage = new CsvStorage();
	// mongoDB里对应的表名
	private final static String province = "job_province_distribution";
	private final static String salary = "job_salary_distribution";
	private final static String education = "job_education_distribution";
	private final static String experience = "job_experience_distribution";
	private final static String nature = "job_company_nature_distribution";
	private final static String scale = "job_company_scale_distribution";
	private final static String[] jobs = { "develop", "framework", "operation", "test", "game", "web" };

	// hdfs存储服务
	private HdfsDB hdfsDB = HdfsDB.getInstance();

	@RequestMapping("/testurl")
	public ModelAndView testurl() {
		String url = "course/test";
		ModelAndView modelAndView = new ModelAndView(url);
		return modelAndView;
	}

	/**
	 * 
	 * @author xuanhuidong @Description: 学情首页 @param @return 参数 @return ModelAndView
	 *         返回类型 @throws
	 */
	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request) {
		String url = null;
		ModelAndView mv;
		User user = getSessionUser(request);
		if (user == null) {
			url = "user/outxqfx";
			mv = new ModelAndView(url);
			return mv;
		}
		url = "learning/index";
		mv = new ModelAndView(url);
		// 岗位总数
		Object jobTotal = jobAnalysisReposity.findAllJobs(mongoClient, "job_internet", "job_total");
		Map<String, Object> map = jobAnalysisReposity.getMatch(mongoClient, "question_survey", "survey");

		mv.addObject("alljobs", jobTotal);
		mv.addObject("totalman", map.get("totalman"));
		mv.addObject("match", map.get("match"));
		return mv;
	}

	/**
	 * 岗位方向 @author xuanhuidong @Description: @return ModelAndView 返回类型 @throws
	 */
	@RequestMapping("/gwfx")
	public ModelAndView gwfx(HttpServletRequest request) {
		User user = getSessionUser(request);
		String url = "learning/gwfx";
		if (user == null) {
			url = "redirect:/course/tologin";
		}

		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

	@RequestMapping("/getfullskill")
	@ResponseBody
	public Object getFullSkill(String param) {
		Map<String, Object> map = jobAnalysisReposity.getfullSkills(mongoClient, "job_skills", "static_skills", param);
		return new MessageBean(true, "", map);
	}

	/**
	 * 云计算运维专业技能
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getcloudops")
	@ResponseBody
	public Object getCloudOps(HttpServletRequest request, String param) {
		User user = getSessionUser(request);
		int userid = user.getId();
		String userids = String.valueOf(userid);
		Map<String, Object> map = jobAnalysisReposity.getfullSkills(mongoClient, "job_skills", "static_skills", param);
		GetStudentMyData data = new GetStudentMyData();
		// TODO 学生相应的学号 944换成user.getId().toString()
		// 计算学生的学年
		String str_joinDate = user.getJoindate();
		Calendar currDay = Calendar.getInstance();
		int currYear = currDay.get(Calendar.YEAR);
		int joinYear = Integer.parseInt(str_joinDate);
		int yearly = currYear - joinYear;
		List list = new ArrayList<>();
		for (int i = 1; i <= yearly; i++) {
			Map maps = data.getStandardJob(param, userids, i);
			list.add(maps);
		}
		return new MessageBean(true, list, map);
	}

	/**
	 * 
	 * @author xuanhuidong @Description: 问卷调查页 @param @return 参数 @return
	 *         ModelAndView 返回类型 @throws
	 */
	@RequestMapping("/survey")
	public ModelAndView survey(HttpServletRequest request) {
		User user = getSessionUser(request);
		String url = "learning/question_survey";
		if (user == null) {
			url = "redirect:/course/tologin";
		}

		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

	/**
	 * 
	 * @author xuanhuidong @Description: 保存问卷调查 @return Object 返回类型 @throws
	 */
	@RequestMapping("/savesurvey")
	@ResponseBody
	public Object saveQues(int year, String school, String isjoin, String itmeyear, String itme, String ranking,
			String industry, String jobcategory, String jobname, String skill, String nature, String scale,
			String salary, String province, String city, String isyes) {
		jobAnalysisReposity.saveques(mongoClient, "question_survey", "survey", year, school, isjoin, itmeyear, itme,
				ranking, industry, jobcategory, jobname, skill, nature, scale, salary, province, city, isyes);
		return new MessageBean(true);

	}

	/**
	 * 
	 * @author xuanhuidong @Description:学情首页，获取饼图和岗位总量 @param @return 参数 @return
	 *         MessageBean 返回类型 @throws
	 */
	@RequestMapping("/jobPie")
	@ResponseBody
	public MessageBean jobPie() {
		List<EchartsObj> echartsObjs = jobAnalysisReposity.getPie(mongoClient);
		long total = 0;
		for (EchartsObj echartsObj : echartsObjs) {
			total += echartsObj.getValue();
		}
		Map<String, Object> map = new HashMap<>();
		map.put("total", total);
		map.put("echartsObjs", echartsObjs);
		return new MessageBean(true, "", map);

	}

	/**
	 * 
	 * @author xuanhuidong @Description: 岗位分析->数据统计 页面 @return ModelAndView
	 *         返回类型 @throws
	 */
	@RequestMapping("/gwdata")
	public ModelAndView gwdata(HttpServletRequest request) {
		User user = getSessionUser(request);
		String url = "learning/gwdata";
		if (user == null) {
			url = "redirect:/course/tologin";
		}
		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

	/**
	 * 
	 * @author xuanhuidong @Description: 岗位分析->数据统计 获取echarts数据 @return MessageBean
	 *         返回类型 @throws
	 */
	@RequestMapping("/gwechart")
	@ResponseBody
	public MessageBean getgwEchartsData(String id) {
		Map<String, Object> map = new HashMap<>();
		// 地区分布
		Map<String, Object> echartsObjs1 = jobAnalysisReposity.getMap(mongoClient, id, province);
		map.put("province", echartsObjs1);
		// 薪资
//		Map<String, Object> map1 = jobAnalysisReposity.getSalary(mongoClient, id, salary);
//		map.put("salary", map1);
		// 学历
		List<EchartsObj> echartsObjs2 = jobAnalysisReposity.getEducation(mongoClient, id, education);
		map.put("education", echartsObjs2);
		// 经验
//		List<EchartsObj> echartsObjs3 = jobAnalysisReposity.getExperience(mongoClient, id, experience);
//		map.put("experience", echartsObjs3);
		// 性质
//		Map<String, Object> map2 = jobAnalysisReposity.getNature(mongoClient, id, nature);
//		map.put("nature", map2);
		// 规模
//		Map<String, Object> map3 = jobAnalysisReposity.getScale(mongoClient, id, scale);
//		map.put("scale", map3);
		return new MessageBean(true, "", map);

	}

	/**
	 * 
	 * @author xuanhuidong @Description: 岗位分析->定向分析 页面 @return ModelAndView
	 *         返回类型 @throws
	 */
	@RequestMapping("/dxdata")
	public ModelAndView dxdata(HttpServletRequest request) {
		User user = getSessionUser(request);
		String url = "learning/dxdata";
		if (user == null) {
			url = "redirect:/course/tologin";
		}
		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

	/**
	 * 
	 * @author xuanhuidong @Description: 专业分析->培养目标 页面 @return ModelAndView
	 *         返回类型 @throws
	 */
	@RequestMapping("/schoolmb")
	public ModelAndView schoolmb(HttpServletRequest request) {
		User user = getSessionUser(request);
		String url = "learning/schoolmb";
		if (user == null) {
			url = "redirect:/course/tologin";
		}
		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

	/**
	 * 学生聚类分析
	 * 
	 * @return
	 */
	@RequestMapping("/stujl")
	public ModelAndView stujl() {
		String url = "learning/stujl";
		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

	/**
	 * 
	 * @author xuanhuidong @Description: 就业分析->就业情况 页面 @return ModelAndView
	 *         返回类型 @throws
	 */
	@RequestMapping("/stujob")
	public ModelAndView stujob(HttpServletRequest request) {
		User user = getSessionUser(request);
		String url = "learning/stujob";
		if (user == null) {
			url = "redirect:/course/tologin";
		}
		ModelAndView mv = new ModelAndView(url);
		Map<String, Object> map = jobAnalysisReposity.getMatch(mongoClient, "question_survey", "survey");
		mv.addObject("totalman", map.get("totalman"));
		return mv;
	}

	/**
	 * 
	 * @author xuanhuidong @Description: 专业分析->培养目标 获取echarts折线图数据 @return Object
	 *         返回类型 @throws
	 */
	@RequestMapping("/contrast")
	@ResponseBody
	public Object getschoolContrast() {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> maps = jobAnalysisReposity.getSchool(mongoClient, "major", "training_goal");
		List<String> skills = jobAnalysisReposity.getmajor(mongoClient, "major", "training_goal");
		map.put("skill", skills);
		map.put("schools", maps);
		return new MessageBean(true, "", map);

	}

	/**
	 * 
	 * @author xuanhuidong @Description: 岗位分析-> 定向分析 获取echarts饼图 @return Object
	 *         返回类型 @throws
	 */
	@RequestMapping("/dxpie")
	@ResponseBody
	public Object dxPie(String id) {
		Map<String, Object> map = new HashMap<>();
		List<EchartsObj> big = new ArrayList<>();
		List<EchartsObj> small = new ArrayList<>();
		long s = 0;
		String jobname = null;
		String direction = null;
		for (String str : jobs) {
			// 大类
			String strname = id + "_" + str;
			EchartsObj echartsObj = jobAnalysisReposity.getJobBigClass(mongoClient, id, strname);
			if (echartsObj != null) {
				big.add(echartsObj);
			}
			// 小类
			List<EchartsObj> echartsObjs = jobAnalysisReposity.getJobSmallClass(mongoClient, id, strname);
			if (echartsObjs != null && echartsObjs.size() > 0) {
				for (EchartsObj echartsObjs1 : echartsObjs) {
					if (echartsObjs1.getValue() >= s) {
						s = echartsObjs1.getValue();
						jobname = echartsObjs1.getName();
						direction = id;
						s = echartsObjs1.getValue();
					}
				}
				if (echartsObjs != null) {
					small.addAll(echartsObjs);
				}
			}
		}
		long bigLong = 0;
		String cate = null;
		for (EchartsObj echartsObj : big) {
			if (echartsObj.getValue() > bigLong) {
				cate = echartsObj.getName();
			}
			switch (echartsObj.getName()) {
			case "develop":
				echartsObj.setName("开发");
				break;
			case "framework":
				echartsObj.setName("架构");
				break;
			case "operation":
				echartsObj.setName("运维");
				break;
			case "test":
				echartsObj.setName("测试");
				break;
			case "game":
				echartsObj.setName("游戏开发");
				break;
			case "web":
				echartsObj.setName("前端开发");
				break;
			default:
				break;
			}

		}
		List<EchartsObj> objs = jobAnalysisReposity.getJobSmallClass(mongoClient, id, id + "_develop");
		if (objs != null && objs.size() > 0) {
			Collections.sort(objs, new Comparator<EchartsObj>() {

				@Override
				public int compare(EchartsObj arg0, EchartsObj arg1) {
					long s1 = arg0.getValue();
					long s2 = arg1.getValue();
					if (s1 > s2) {
						return -1;
					} else if (s1 < s2) {
						return 1;
					} else {
						return 0;
					}
				}

			});
		}
		Map<String, Object> map2 = jobAnalysisReposity.getSkills(mongoClient, direction, jobname);
		map.put("bigname", "开发");
		if (objs != null) {
			if (objs.size() < 3) {
				map.put("smallrank", objs.subList(0, objs.size()));
			} else {
				map.put("smallrank", objs.subList(0, 3));
			}
		}
		map.put("dataX", big);
		map.put("dataY", small);
		map.put("skills", map2);
		return new MessageBean(true, "", map);

	}

	/**
	 * 
	 * @author xuanhuidong @Description: 岗位分析-> 定向分析 获取echarts的雷达图数据 @return Object
	 *         返回类型 @throws
	 */
	@RequestMapping("/dxrader")
	@ResponseBody
	public Object dxRader(String param, String allparam) {
		// String id = "job_cloud";
		Map<String, Object> map = jobAnalysisReposity.getSkills(mongoClient, allparam, param);
		return new MessageBean(true, "", map);
	}

	/**
	 * 进入培养方案页面
	 * 
	 * @return
	 */
	@RequestMapping("/schoolfa")
	public ModelAndView schoolfa() {
		String url = "learning/schoolfa";
		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

	/**
	 * 进入专业学习页面
	 * 
	 * @return
	 */
	@RequestMapping("/stuzy")
	public ModelAndView stuzy() {
		String url = "learning/stuzy";
		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

	/**
	 * 进入就业推荐页面
	 * 
	 * @return
	 */
	@RequestMapping("/jytj")
	public ModelAndView jytj(HttpServletRequest request) {
		User user = getSessionUser(request);
		String url = "learning/jytj";
		if (user == null) {
			url = "redirect:/course/tologin";
		}
		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

	/**
	 * 培养方案中培养方案雷达图数据加载
	 * 
	 * @return
	 */
	@RequestMapping("/getrctest")
	@ResponseBody
	public Object getRctest(HttpServletRequest request) {
		String schoolid = request.getParameter("schoolid");
		Map<Object, Object> map = jobAnalysisReposity.getrctest(mongoClient, schoolid);
		return new MessageBean(true, map);
	}

	/**
	 * 培养方案岗位匹配度雷达图数据加载
	 * 
	 * @return
	 */
	@RequestMapping("/getradartest")
	@ResponseBody
	public Object getRadartest(HttpServletRequest request) {
		String schoolid = request.getParameter("schoolid");
		List<Map<Object, String>> list = jobAnalysisReposity.getradartest(mongoClient, schoolid);
		List<Map<Object, String>> listjob = new ArrayList<Map<Object, String>>();
		Map<Object, String> maptu = new HashMap<Object, String>();
		int i = 0;
		for (Map<Object, String> m : list) {
			Map<Object, String> map = new HashMap<Object, String>();
			String jobname = m.get("jobname");
			String similarty = m.get("similarity");
			map.put("jobname", jobname);
			map.put("similarty", similarty);
			listjob.add(map);
			if (i == 0) {
				maptu = new HashMap<Object, String>();
				String skls = m.get("skls");
				String major = m.get("major");
				String job = m.get("job");
				maptu.put("skls", skls);
				maptu.put("major", major);
				maptu.put("job", job);
				i++;
			}
		}
		return new MessageBean(true, maptu, listjob);
	}

	/**
	 * 培养方案中的培养目标d3图加载
	 * 
	 * @return
	 */
	@RequestMapping("/getcsv")
	@ResponseBody
	public Object getCsv(HttpServletRequest request) {
		String schoolid = request.getParameter("schoolid");
		csvstorage.createCSV(mongoClient, schoolid);
		return new MessageBean(true, schoolid);
	}

	/**
	 * 培养方案岗位匹配根据选择的岗位不同展示相应的匹配雷达图
	 * 
	 * @return
	 */
	@RequestMapping("/changeradar")
	@ResponseBody
	public Object changeRadar(HttpServletRequest request) {
		String schoolid = request.getParameter("schoolid");
		String jobname = request.getParameter("jobname");
		List<Map<Object, String>> list = jobAnalysisReposity.getradartest(mongoClient, schoolid);
		Map<Object, String> maptu = new HashMap<Object, String>();
		for (Map<Object, String> m : list) {
			Map<Object, String> map = new HashMap<Object, String>();
			String name = m.get("jobname");
			if (name.equals(jobname)) {
				maptu = new HashMap<Object, String>();
				String skls = m.get("skls");
				String major = m.get("major");
				String job = m.get("job");
				maptu.put("skls", skls);
				maptu.put("major", major);
				maptu.put("job", job);
				return new MessageBean(true, maptu);
			}
		}
		return new MessageBean(true, maptu);
	}

	/**
	 * 
	 * @author xuanhuidong @Description: 就业分析->就业情况 获取ecahrts数据 @return Object
	 *         返回类型 @throws
	 */
	@RequestMapping("/getjob")
	@ResponseBody
	public Object getJob() {
		Map<String, Object> map = jobAnalysisReposity.getSurveyPie(mongoClient, "question_survey", "survey");
		return new MessageBean(true, "", map);

	}

	/**
	 * 学生聚类图表展示数据获取
	 * 
	 * @return
	 */
	@RequestMapping("/getstujl")
	@ResponseBody
	public Object getStujl(HttpServletRequest request) {
		Map msp = jobAnalysisReposity.getstujl(mongoClient);
		Vector<String> vce = (Vector<String>) msp.get("map");
		List<JSONObject> list = (List<JSONObject>) msp.get("list");
		Vector<String> year = (Vector<String>) msp.get("year");
		Map<Object, Vector<String>> map = new HashMap<Object, Vector<String>>();
		map.put("vce", vce);
		map.put("year", year);
		return new MessageBean(true, list, map);
	}

	/**
	 * 学生聚类根据年级查询
	 * 
	 * @return
	 */
	@RequestMapping("/selectgrade")
	@ResponseBody
	public Object selectGrade(HttpServletRequest request) {
		String grade = request.getParameter("grade");
		Map msp = jobAnalysisReposity.selectgrade(mongoClient, grade);
		Vector<String> vce = (Vector<String>) msp.get("map");
		List<JSONObject> list = (List<JSONObject>) msp.get("list");
		Map map = new HashMap<>();
		map.put("vce", vce);
		map.put("grade", grade);
		return new MessageBean(true, list, map);
	}

	/**
	 * 专业学习图表数据加载
	 * 
	 * @return
	 */
	@RequestMapping("/loadcourse")
	@ResponseBody
	public Object loadCourse(HttpServletRequest request) {
		String name = request.getParameter("name");
		String sname = request.getParameter("sname");
		String id = request.getParameter("id");
		Map msp = jobAnalysisReposity.loadCourse(mongoClient, name);
		return new MessageBean(true, msp, sname);
	}

	/**
	 * 教师评价数据加载
	 * 
	 * @return
	 */
	@RequestMapping("/getteapj")
	@ResponseBody
	public Object getTeapj(HttpServletRequest request) {
		String teacher = request.getParameter("teacher");
		List<Map<Object, String>> msp = jobAnalysisReposity.getteapj(mongoClient, teacher);
		List<String> lst = jobAnalysisReposity.getteaname(mongoClient);
		return new MessageBean(true, msp, lst);
	}

	/**
	 * 就业推荐查询
	 * 
	 * @return
	 */
	@RequestMapping("/getjytj")
	@ResponseBody
	public Object getJytj(HttpServletRequest request) {
		String html = request.getParameter("html");
		String[] skill = html.split(",");
		Map<String, Object> msp = jobAnalysisReposity.getjytj(mongoClient, skill, 3);
		return new MessageBean(true, msp);
	}

	/**
	 * 课程评价数据加载
	 * 
	 * @return
	 */
	@RequestMapping("/getcourrad")
	@ResponseBody
	public Object getCourrad(HttpServletRequest request) {
		String course = request.getParameter("course");
		List<Map> mspnum = jobAnalysisReposity.getcounum(mongoClient, course);
		String radtu = jobAnalysisReposity.getcouskill(mongoClient, course);
		List<Map<String, String>> map = jobAnalysisReposity.getallpiccourse(mongoClient);
		List lisy = new ArrayList<>();
		lisy.add(mspnum);
		lisy.add(radtu);
		lisy.add(course);
		lisy.add(map);
		return new MessageBean(true, lisy);
	}

	/**
	 * 课程评价折线图加载
	 * 
	 * @return
	 */
	@RequestMapping("/getbroleline")
	@ResponseBody
	public Object getBroleline(HttpServletRequest request) {
		String course = request.getParameter("course");
		List<String> brole = jobAnalysisReposity.getcoutime(mongoClient, course);
		return new MessageBean(true, brole);
	}

	@RequestMapping("/getGrade")
	@ResponseBody
	public Object getGrade(HttpServletRequest request) {
		Map<String, Object> map = jobAnalysisReposity.getGrade(mongoClient, "study", "students_cluster");
		return new MessageBean(true, "", map);
	}

	@RequestMapping("/crawlerhtml")
	@ResponseBody
	public Object crawlerHtml(HttpServletRequest request, String strhtml) {
		String contextpaht = request.getSession().getServletContext().getRealPath("/");
		String path = "/user/douxue/douxue/insight/job/crawler/" + strhtml;
		try {
			// hdfsDB.downLoad(path, contextpaht+"/51job/"+strhtml);
			hdfsDB.downLoad(path, contextpaht + "/WEB-INF/view/51job/" + strhtml);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new MessageBean(false, "", "");
		}
		return new MessageBean(true, "", "");
	}

	@RequestMapping(value = "/crawlerlist")
	public ModelAndView crawlerlist(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("course/crawlerlist");
		List<FileBean> listfb = new ArrayList<FileBean>();
		List<FileBean> fb = new ArrayList<FileBean>();
		try {
			// 读取hdfs中的页面展示在页面之中
			listfb = hdfsDB.queryAll("/user/douxue/douxue/insight/job/crawler");
			int size = listfb.size();
			if (size >= 200) {
				size = 200;
			}
			for (int i = 0; i < size; i++) {
				fb.add(listfb.get(i));
			}
			mv.addObject("filebean", fb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
}
