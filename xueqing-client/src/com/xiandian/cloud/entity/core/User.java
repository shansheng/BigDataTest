/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.entity.core;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.xiandian.cloud.entity.BaseEntity;

/**
 * 用户
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "t_user")
public class User extends BaseEntity {

	private String bm;// 编码
	private String xh;// 学号
	private String xjh;// 学籍号
	private String username;
	private String password;
	private String email;
	private String realname;
	private String phone;
	private String qq;

	// 激活
	private int status;//0待激活，1审核未通过，2已激活, 3 个人信息已完善部分
	private String headimg;
	private int sex;// 0男，1女，2未知
	private String describle;
	private String code;
	private String codeimg;
	// 激活匹配使用
	private String token;
	// 忘记密码匹配使用
	private String pwdtoken;
	private String job;
//	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//	@JoinTable(name = "t_orguser", joinColumns = { @JoinColumn(name = "userid") }, inverseJoinColumns = { @JoinColumn(name = "orgid") })
//	private Org org;
	// 是否禁用
	private int isenable;//0正常，1禁用
	// 注册时间
	private String createtime;
	// 熟悉的平台
	private String platform;
	// 专长领域
	private String field;
	// 网盘存储大小
	private int storagesize = 500;
	private String birthday;
	// 入学年份
	private String joindate;
	// 省
	private String province;
	// 市
	private String city;
	// 更新时间
	private String modifiedtime;
//	// 所属专业
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "majorid")
//	private Major major;
	
/*	//登陆认证那里报错，int改完Integer
	private Integer majorid;

	public Integer getMajorid() {
		return majorid;
	}

	public void setMajorid(Integer majorid) {
		this.majorid = majorid;
	}*/

	//	// 班级
//	@Transient
//	private Classes classes;
	// 注册时间
	private String rip;
	// 用户积分总量
	private int points;
	private int isrecommend;// 0未推荐，1已推荐
	private String openid;//qq第三方登录唯一标识
	private String unionid;//微信用户唯一标识
	
	//用户邮箱激活状态  0未激活  1已激活
	private int validateCode;
	
	public int getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(int validateCode) {
		this.validateCode = validateCode;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getRip() {
		return rip;
	}

	public void setRip(String rip) {
		this.rip = rip;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public int getStoragesize() {
		return storagesize;
	}

	public void setStoragesize(int storagesize) {
		this.storagesize = storagesize;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public int getIsenable() {
		return isenable;
	}

	public void setIsenable(int isenable) {
		this.isenable = isenable;
	}

	public String getPwdtoken() {
		return pwdtoken;
	}

	public void setPwdtoken(String pwdtoken) {
		this.pwdtoken = pwdtoken;
	}

//	public Org getOrg() {
//		return org;
//	}
//
//	public void setOrg(Org org) {
//		this.org = org;
//	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getDescrible() {
		return describle;
	}

	public void setDescrible(String describle) {
		this.describle = describle;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeimg() {
		return codeimg;
	}

	public void setCodeimg(String codeimg) {
		this.codeimg = codeimg;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getJoindate() {
		return joindate;
	}

	public void setJoindate(String joindate) {
		this.joindate = joindate;
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

	public String getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(String modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

//	public Major getMajor() {
//		return major;
//	}
//
//	public void setMajor(Major major) {
//		this.major = major;
//	}

	public String getBm() {
		return bm;
	}

	public void setBm(String bm) {
		this.bm = bm;
	}

	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public String getXjh() {
		return xjh;
	}

	public void setXjh(String xjh) {
		this.xjh = xjh;
	}

	public int getIsrecommend() {
		return isrecommend;
	}

	public void setIsrecommend(int isrecommend) {
		this.isrecommend = isrecommend;
	}

//	public Classes getClasses() {
//		return classes;
//	}
//
//	public void setClasses(Classes classes) {
//		this.classes = classes;
//	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	
}
