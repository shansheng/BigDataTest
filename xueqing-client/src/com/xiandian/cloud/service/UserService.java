/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiandian.cloud.dao.core.RoleDao;
import com.xiandian.cloud.dao.core.UserDao;
import com.xiandian.cloud.dao.core.UserRoleDao;
import com.xiandian.cloud.entity.core.User;
import com.xiandian.cloud.entity.core.UserRole;

/**
 * 用户service
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private UserRoleDao userRoleDao;

	public List<UserRole> getUserRole(String username) {
		User user = userDao.getUserByname(username);
		return userRoleDao.getByuserid(user.getId());
	}

	public User getUserByname(String username) {
		return userDao.getUserByname(username);
	}

	public User getUserByphone(String phone) {
		return userDao.getUserByphone(phone);
	}

	public User getUserBymail(String email) {
		return userDao.getUserBymail(email);
	}
}
