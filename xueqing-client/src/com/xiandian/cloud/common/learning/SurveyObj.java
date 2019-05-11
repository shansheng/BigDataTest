/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

import java.util.List;

/**
 * @author xuanhuidong E-mail: 1259023939@qq.com
 * @version 创建时间：2017年9月19日 下午2:42:32 类说明
 */
public class SurveyObj {
	//性质
	private String nature;
	//规模
	private String scale;
	private String jobname;
	private String salary;
	private String pro;
	private String industry;
	private String education;
	private String isjoin;
	private String skills;
	private String ismatch;
	private String shcool;
	
	public String getShcool() {
		return shcool;
	}

	public void setShcool(String shcool) {
		this.shcool = shcool;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getJobname() {
		return jobname;
	}

	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getPro() {
		return pro;
	}

	public void setPro(String pro) {
		this.pro = pro;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}


	public String getIsjoin() {
		return isjoin;
	}

	public void setIsjoin(String isjoin) {
		this.isjoin = isjoin;
	}

	public String getIsmatch() {
		return ismatch;
	}

	public void setIsmatch(String ismatch) {
		this.ismatch = ismatch;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	@Override
	public String toString() {
		return "SurveyObj [nature=" + nature + ", scale=" + scale + ", jobname=" + jobname + ", salary=" + salary
				+ ", pro=" + pro + ", industry=" + industry + ", education=" + education + ", isjoin=" + isjoin
				+ ", skills=" + skills + ", ismatch=" + ismatch + "]";
	}

	

}
