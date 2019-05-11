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
 * 角色
 * 
 * @author 云计算应用与开发项目组
 * @since  V1.0
 * 
 */
@Entity
@Table(name = "t_role")
public class Role extends BaseEntity{

	private String name;
	//角色对应的key值，比如admin这样的字眼
	private String rolekey;
	
	public String getRolekey() {
		return rolekey;
	}
	public void setRolekey(String rolekey) {
		this.rolekey = rolekey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
