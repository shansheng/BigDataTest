/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.web;

import javax.servlet.http.HttpServletRequest;

import com.xiandian.cloud.common.cons.CommonConstants;
import com.xiandian.cloud.entity.core.User;

/**
 * 活动后台管理基类
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
public class BaseController {

	// 缓存用户信息
	protected User getSessionUser(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute(CommonConstants.USER);
		return user;
	}

	protected void setSessionUser(HttpServletRequest request, User user) {
		request.getSession().setAttribute(CommonConstants.USER, user);
	}

}
