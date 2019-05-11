/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.dao.core;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiandian.cloud.dao.BaseDao;
import com.xiandian.cloud.entity.core.Role;

/**
 * 角色Dao
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
@Repository
public class RoleDao extends BaseDao<Role> {

	public List<Role> getAll() {
		String hql = "from Role";
		return find(hql);
	}

	public Role getRoleByname(String name) {
		String hql = "from Role where name = ?";
		List<Role> list = find(hql, new Object[] { name });
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public Role getRoleBykey(String rolekey) {
		String hql = "from Role where rolekey = ?";
		List<Role> list = find(hql, new Object[] { rolekey });
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public List<Role> getRoleWithoutAd() {
		String hql = "from Role where rolekey not like '%admin%'";
		return super.find(hql);
	}

	public void update(int id, String name) {
		String hql = "update Role set name =? where id=?";
		executeHql(hql, new Object[] { name, id });
	}

	public Role getByRoleName(String roleName) {
		String hql = "from Role where name = ?";
		List<Role> list = find(hql, new Object[] { roleName });
		if (list.size() == 0) {
			return null;
		} else {

			return list.get(0);
		}
	}
}
