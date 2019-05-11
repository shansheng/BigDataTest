/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiandian.cloud.common.bean.MessageBean;
import com.xiandian.cloud.common.cons.Constants;
import com.xiandian.cloud.common.util.MD5;
import com.xiandian.cloud.common.util.UtilTools;
import com.xiandian.cloud.entity.core.User;
import com.xiandian.cloud.service.MethodLog;
import com.xiandian.cloud.service.UserService;

/**
 * 相关控制器
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
@Controller
@RequestMapping("/course")
public class CourseController extends BaseController {
	@Autowired
	private UserService userService;

	@RequestMapping("/tologin")
	public ModelAndView tologin(HttpServletRequest request) {

		String url = "course/tologin";

		ModelAndView mv = new ModelAndView(url);
		// String redirect_uri = UtilTools.getConfig().getProperty("redirect_uri");
		// mv.addObject("redirect_uri", redirect_uri);
		return mv;
	}

	/**
	 * 判断用户是否登录
	 * 
	 * @param currUser
	 * @return
	 */
	@RequestMapping(value = "/login")
	@ResponseBody
	public Object login(HttpServletRequest request, HttpServletResponse response, User user) {
		Subject users = SecurityUtils.getSubject();
		MD5 md5 = new MD5();
		String password = md5.getMD5ofStr(user.getPassword());
		User userByphone = userService.getUserByphone(user.getUsername());
		User userBymail = userService.getUserBymail(user.getUsername());
		if (userByphone != null) {
			user.setUsername(userByphone.getUsername());
		} else if (userBymail != null) {
			user.setUsername(userBymail.getUsername());
		}
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), password);
		token.setRememberMe(true);
		try {
			users.login(token);
			// boolean flag = users.isAuthenticated();
			User tuser = userService.getUserByname(user.getUsername());
			setSessionUser(request, tuser);

			SavedRequest savedRequest = WebUtils.getSavedRequest(request);
			String redirecturl = null;
			// 获取保存的URL
			if (savedRequest != null && savedRequest.getRequestUrl() != null) {
				redirecturl = savedRequest.getRequestUrl();
			}
			return new MessageBean(true, "", redirecturl);
		} catch (UnknownAccountException e) {
			token.clear();
			return new MessageBean(false, Constants.ERROR_7);
		} catch (IncorrectCredentialsException ee) {
			token.clear();
			return new MessageBean(false, Constants.ERROR_8);
		}
	}

	@RequestMapping(value = "/logout")
	public ModelAndView logout(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		setSessionUser(request, null);
		String sso = UtilTools.getConfig().getProperty("sso");
		String url = "redirect:/";
		ModelAndView mv = new ModelAndView(url);
		return mv;
	}

}
