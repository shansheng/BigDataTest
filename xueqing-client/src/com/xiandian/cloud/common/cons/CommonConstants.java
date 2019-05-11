/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.common.cons;

/**
 * 整个应用通用常量类
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
public class CommonConstants {

	// 分页大小
	public static final int PAGE_SIZE = 10;
	// 角色key值，不要修改这些值
	public static final String ROLE_ADMIN = "admin";
	public static final String TEACHER = "teacher";
	public static final String STUDENT = "student";
	public static final String ORG_ADMIN = "orgadmin";
	public static final String COM_ADMIN = "enterpriseadmin";
	
	public static final int ADMIN_ID = 1;
	public static final int ORGADMIN_ID = 2;
	public static final int TEACHER_ID = 3;
	public static final int STUDENT_ID = 4;
	// 缓存用户
	public static final String USER = "user";
	// 审批状态
	public static final int SP_0 = 0;// 待审批
	public static final int SP_1 = 1;// 审批不通过
	public static final int SP_2 = 2;// 审批通过

	public static final String SUCCESS_1 = "保存成功";
	public static final String SUCCESS_2 = "编辑成功";
	public static final String SUCCESS_3 = "删除成功";
	public static final String SUCCESS_4 = "发送成功";
	public static final String SUCCESS_5 = "注册成功";
	public static final String SUCCESS_6 = "入驻成功";
	public static final String SUCCESS_7 = "重置成功";
	public static final String SUCCESS_8 = "申请已提交";
	public static final String SUCCESS_9 = "审核成功";
	public static final String SUCCESS_10 = "取消审核成功";
	public static final String SUCCESS_11 = "游客登录成功";

	// 错误信息
	public static final String ERROR_1 = "角色已经存在";
	public static final String ERROR_2 = "机构已经存在";
	public static final String ERROR_3 = "用户已经存在";
	public static final String ERROR_4 = "没有找到该账号";
	public static final String ERROR_5 = "发送邮件失败";
	public static final String ERROR_6 = "邮箱已经存在";
	public static final String ERROR_7 = "用户名或密码错误，请重新登录";
	public static final String ERROR_8 = "用户名或密码错误，请重新登录";
	public static final String ERROR_9 = "身份证号已经存在";
	public static final String ERROR_10 = "发送失败";
	public static final String ERROR_11 = "登录失败";
	public static final String ERROR_12 = "licence已经存在";
	public static final String ERROR_13 = "注册游客数已达上限";
	public static final String ERROR_14 = "licence不存在或输入错误";
	public static final String ERROR_15 = "已超过licence限定";
	public static final String ERROR_16 = "请导入可用的licence";

	public static final String EXCEPTION = "系统出现异常，请联系管理员";

	public static final String LOG_REGIST = "用户注册";
	public static final String LOG_LOGIN = "用户登录";
	public static final String LOG_INDEX = "访问首页";
	public static final String LOG_BROWSERES = "查看资源";
	public static final String LOG_DOWNLOADRES = "下载资源";
	public static final String LOG_UPLOADRES = "上传资源";
	public static final String LOG_DELETERES = "删除资源";
	public static final String LOG_CHECKRES = "审核资源";
	
	public static final String LOG_ADDCOURSE = "添加课程";
	public static final String LOG_DELETECOURSE = "删除课程";
	public static final String LOG_ADDUNIT = "添加课程单元";
	public static final String LOG_DELETEUNIT = "删除课程单元";
	public static final String LOG_ADDTASK = "添加任务";
	public static final String LOG_DELETETASK = "删除任务";
	public static final String LOG_ADDEXAM = "添加考试";
	public static final String LOG_DELETEEXAM = "删除考试";
	public static final String LOG_JOINCOURSE = "加入课程";
	public static final String LOG_LERANUNIT = "学习课程";
	public static final String LOG_ADDNOTE = "添加笔记";
	public static final String LOG_COMMENTCOURSE = "评论课程";
	public static final String LOG_ADDQUES = "添加问题";
	public static final String LOG_ASKQUES = "回答问题";
	public static final String LOG_SUBMIT = "提交答案";
	
	/**
	 * 个人信息已完善部分
	 */
	public static final int USER_INFO_STATUS = 3;
	/**
	 * 专业方向班级信息未完善
	 */
	public static final int USER_CLASS_INFO_STATUS = 10;
	
	/**
	 * 院校企业
	 */
	public static final int IS_SCHOOL = 0;
	public static final int IS_COMPANY = 1;
	
	
	public static final String TEACHING_TASK = "教学任务";
}
