/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.shiro;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.WebSubject;
import org.springframework.beans.factory.annotation.Autowired;

import com.xiandian.cloud.common.cons.Constants;
import com.xiandian.cloud.entity.core.User;
import com.xiandian.cloud.entity.core.UserRole;
import com.xiandian.cloud.service.UserService;

/**
 * cas realm
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
public class MyCasRealm extends CasRealm {

	@Autowired
	private UserService userService;

	/**
	 * 授权操作
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		// String username = (String) getAvailablePrincipal(principals);
		String username = (String) principals.getPrimaryPrincipal();

		List<UserRole> urlist = userService.getUserRole(username);

		// 角色名的集合
		Set<String> roles = new HashSet<String>();
		// 权限名的集合
		Set<String> permissions = new HashSet<String>();

		for (UserRole ur : urlist) {
			String rolekey = ur.getRole().getRolekey();
			roles.add(rolekey);
		}
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

		authorizationInfo.addRoles(roles);
		authorizationInfo.addStringPermissions(permissions);

		return authorizationInfo;
	}

	/**
	 * 身份验证操作
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) {
		AuthenticationInfo info = super.doGetAuthenticationInfo(token);
		// 如果用户不为空，缓存
		String username = (String) token.getPrincipal();
		User user = userService.getUserByname(username);
		if (user != null) {
			ServletRequest request = ((WebSubject) SecurityUtils.getSubject())
					.getServletRequest();
			HttpSession httpSession = ((HttpServletRequest) request)
					.getSession();

			if (httpSession.getAttribute(Constants.USER) == null) {
				httpSession.setAttribute(Constants.USER, user);
			}
		}
		return info;
	}

	@Override
	public String getName() {
		return getClass().getName();
	}

}
