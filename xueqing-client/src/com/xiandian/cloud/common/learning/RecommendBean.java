/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

import java.util.List;
import java.util.Map;

/**
 * @author xuanhuidong E-mail: 1259023939@qq.com
 * @version 创建时间：2017年11月3日 上午10:04:58 类说明
 */
public class RecommendBean {
	private String id;
	private String jobname;
	private String skill;
	private String province;
	private String city;
	private String company;
	private String picurl;
	private int apply;// 是否关注 0未关注 1已关注
	private String similarity;// 相似度
	private List<SkillBean> skillBeans;
	private List<String> titles;
	private String stuSkill;

	public String getStuSkill() {
		return stuSkill;
	}

	public void setStuSkill(String stuSkill) {
		this.stuSkill = stuSkill;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public List<SkillBean> getSkillBeans() {
		return skillBeans;
	}

	public void setSkillBeans(List<SkillBean> skillBeans) {
		this.skillBeans = skillBeans;
	}

	public String getSimilarity() {
		return similarity;
	}

	public void setSimilarity(String similarity) {
		this.similarity = similarity;
	}

	public int getApply() {
		return apply;
	}

	public void setApply(int apply) {
		this.apply = apply;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobname() {
		return jobname;
	}

	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
