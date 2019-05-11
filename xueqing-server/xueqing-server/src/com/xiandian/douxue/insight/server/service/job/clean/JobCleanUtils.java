/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.service.job.clean;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.xiandian.douxue.insight.server.dao.HBaseStorage;
import com.xiandian.douxue.insight.server.dao.JobAnalysisReposity;
import com.xiandian.douxue.insight.server.dao.JobDataReposity;
import com.xiandian.douxue.insight.server.utils.UtilTools;

import net.paoding.analysis.analyzer.PaodingAnalyzer;


/** 
* @ClassName: 
* @Description: 
* @author XianDian Cloud Team
* @date ${2017.9.26} 
* 
* ${tags} 
*/
public class JobCleanUtils {
	private static Properties  educations = UtilTools.getConfig(System.getProperty("user.dir") + "/configuration/job_education.properties");	
	private static JobAnalysisReposity jobanalysisreposity=JobAnalysisReposity.getInstance();
	static HBaseStorage baseStorage = new HBaseStorage();
	JobDataReposity jobDataReposity = JobDataReposity.getInstance();

	/** 
	* @Title: ${cleanDate} 
	* @Description: ${清洗字段日期}
	* @param ${date：爬取的字段日期}  
	* @return ${清洗过的日期}
	* @throws 
	*/
	public static String cleanDate(String date) {//清洗字段日
		return "";
	}

	/** 
	* @Title: ${cleanNature} 
	* @Description: ${清洗公司性质}
	* @param ${nature：爬取的公司性质}  
	* @return ${清洗过的公司性质}
	* @throws 
	*/
	public static String cleanNature(String nature) {//清洗公司性质
		return "";
	}

	/** 
	* @Title: ${cleanIndustry} 
	* @Description: ${清洗公司行业}
	* @param ${nature：爬取的公司行业}  
	* @return ${清洗过的公司行业}
	* @throws 
	*/
	public static String cleanIndustry(String industry) {
		return industry.substring(2, industry.length());

	}

	/** 
	* @Title: ${cleanScale} 
	* @Description: ${清洗公司规模}
	* @param ${scale：爬取的公司规模}  
	* @return ${清洗过的公司规模}
	* @throws 
	*/
	public static String cleanScale(String scale) {
		String scaleFilter = scale.substring(2, scale.length() - 3);
		String scaleresult = "";
		switch (scaleFilter) {
		case "少于50人":
			scaleresult = "(0,50)";
			break;
		case "50-150人":
			scaleresult = "(50,150)";
			break;
		case "150-500人":
			scaleresult = "(150,500)";
			break;
		case "500-1000人":
			scaleresult = "(500,1000)";
			break;
		case "1000-5000人":
			scaleresult = "(1000,5000)";
			break;
		case "5000-10000人":
			scaleresult = "(5000,10000)";
			break;
		case "10000人以上":
			scaleresult = "(10000,+)";
			break;
		default:
			break;
		}
		return scaleresult;
	}

	/** 
	* @Title: ${cleanExperience} 
	* @Description: ${清洗工作经验}
	* @param ${scale：爬取的工作经验}  
	* @return ${清洗过的工作经验}
	* @throws 
	*/
	public static int cleanExperience(String experience) {
		Integer exp = 0;
		String exp2 = "";
		if (experience != null) {
			if (experience.contains("无"))
				return 0;
			else {
				if (experience.contains("-")) {
					exp2 = getNumber(experience);
					return Integer.parseInt(exp2.substring(0, experience.indexOf("-")));
				} else
					return Integer.parseInt(getNumber(experience));
			}
		} else
			return exp;
	}

	/** 
	* @Title: ${getNumber} 
	* @Description: ${提取字符串中的数字}
	* @param ${number：待提取的字符串}  
	* @return ${字符串中提取的}
	* @throws 
	*/
	public static String getNumber(String number) {
		String str1 = number.trim();
		String str2 = "";
		for (int i = 0; i < str1.length(); i++)
			if (str1.charAt(i) >= 48 && str1.charAt(i) <= 57)
				str2 += str1.charAt(i);
		return str2;
	}

	/** 
	* @Title: ${cleanAmount} 
	* @Description: ${清洗工作人数}
	* @param ${scale：爬取的工作人数}  
	* @return ${清洗过的工作人数}
	* @throws 
	*/
	public static int cleanAmount(String amount, String scale) {
		if (amount.contains("若干"))
			return Math.round(Integer.parseInt(cleanScale(scale).substring(1, cleanScale(scale).indexOf(","))) / 50);
		else
			return Integer.parseInt(getNumber(amount));
	}

	/** 
	 *初中,高中,中技,中专,大专,本科,硕士,博士
	* @Title: ${cleanEducation} 
	* @Description: ${清洗工作学历}
	* @param ${scale：爬取的工作学历}  
	* @return ${清洗过的工作学历}
	* @throws 
	*/
	public static String cleanEducation(String education) {//清洗学历 至初中,高中,中技,中专,大专,本科,硕士,博士,其他 其一
		return "";
	}

	/** 
	* @Title: ${cleanLocation} 
	* @Description: ${清洗工作地点}
	* @param ${scale：爬取的工作地点}  
	* @return ${清洗过的工作地点}
	* @throws 
	*/
	public static String cleanLocation(String location,MongoClient mongoClient) {
		if (location.contains("-")) {
			String loc = location.substring(0, location.indexOf("-"));
			Object parent_code = jobanalysisreposity.retrieveParentCity("areas_location", 2854, loc,mongoClient);
			String locname = (String) jobanalysisreposity.findParentCity("cities_location", 343, parent_code,mongoClient);
			if (locname != null) {
				String lname = locname.substring(0, locname.length() - 1);
				return lname;
			} else {
				return loc;
			}
		} else {
			Object parent_code = jobanalysisreposity.retrieveParentCity("areas_location", 2854, location,mongoClient);
			String locname = (String) jobanalysisreposity.findParentCity("cities_location", 343, parent_code,mongoClient);
			if (locname != null) {
				String lname = locname.substring(0, locname.length() - 1);
				return lname;
			} else {
				return location;
			}
		}
	}

	/** 
	* @Title: ${cleanSalary} 
	* @Description: ${清洗工资}
	* @param ${scale：爬取的工资}  
	* @return ${清洗过的工资}
	* @throws 
	*/
	public static double cleanSalary(String salary) {
		if (!salary.equals("") && !(salary == null)) {
		double sal = 0;
		if (salary.contains("-")) {
			double salary1 = Double.parseDouble(salary.substring(0, salary.indexOf("-")));
			double salary2 = Double.parseDouble(salary.substring(salary.indexOf("-") + 1, salary.indexOf("/") - 1));
			sal = (salary1 + salary2) / 2;
		}
		else {
			salary=salary.trim();
			String str2="";
			if(salary != null && !"".equals(salary)){
				for(int i=0;i<salary.length();i++){
					if((salary.charAt(i)>=48 && salary.charAt(i)<=57) ||  salary.charAt(i) == 46){
						str2+=salary.charAt(i);
					}
				}
			}
			sal = Double.parseDouble(str2);
		}
		
		if (salary.contains("万"))
			sal = sal * 10000;
		if (salary.contains("千"))
			sal = sal * 1000;
		if (salary.contains("年"))
			return sal/12;
		else if (salary.contains("日") || salary.contains("天"))
			return  sal*30;
		else 
			return sal;}
		else return 3000;
	}

	/**
	 * 去除停用词
	 * @param str 
	 * @return 去除停用词后的字符串
	 */
	public static String participleJobName(String str) {
		List<String> stopword = new ArrayList<String>();
		String[] sw = {" "};
		for (String stri : sw)
			stopword.add(stri);

		Analyzer analyzer = new PaodingAnalyzer();
		String result = "";
		TokenStream tokenStream = analyzer.tokenStream(str, new StringReader(str));
		try {
			Token t;
			while ((t = tokenStream.next()) != null) {
				if (stopword.contains(t.termText()) != true) {
					result += t.termText() + "/";
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toLowerCase();
	}

	/**
	 * 岗位描述分析
	 * @param str 岗位描述
	 * @return 分词后的岗位描述
	 */
	public static String fenci(String str) {
		Analyzer analyzer = new PaodingAnalyzer();
		String result = "";
		TokenStream tokenStream = analyzer.tokenStream(str, new StringReader(str));
		try {
			Token t;
			while ((t = tokenStream.next()) != null) {
				if (t.termText().charAt(0) < '\u4e00' && t.termText().charAt(0) >= '\u0041') {
					result += t.termText() + "/";
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toLowerCase();

	}
}
