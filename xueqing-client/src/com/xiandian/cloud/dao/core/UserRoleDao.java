/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.dao.core;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiandian.cloud.common.cons.CommonConstants;
import com.xiandian.cloud.dao.BaseDao;
import com.xiandian.cloud.dao.Page;
import com.xiandian.cloud.entity.core.UserRole;

/**
 * 用户角色Dao
 * 
 * @author 云计算应用与开发项目组
 * @since  V1.0
 * 
 */
@Repository
public class UserRoleDao extends BaseDao<UserRole> {
	
	public List<Object> findUserRole(String username)
	{
//		String hql = "from UserRole where status=? and userid = ?";
		String sql = "select r.rolekey from t_user as u,t_userrole as ur,t_role as r where u.username = ? and u.id = ur.userid and ur.roleid = r.id";
		return findSql(sql,new Object[]{username});
	}
	
	public List<UserRole> getAll()
	{
		String hql = "from UserRole";
		return find(hql);
	}
	
	public void removeByuserid(int userid)
	{
		String hql = "delete UserRole where userid=?";
		executeHql(hql, new Object[]{userid});
	}
	
	public List<UserRole> getByuserid(int userid)
	{
		String hql = "from UserRole where status=? and userid = ?";
		return find(hql,new Object[]{CommonConstants.SP_2,userid});
	}
	public List<UserRole> getByuseridNoStatus(int userid)
	{
		String hql = "from UserRole where userid = ?";
		return find(hql,new Object[]{userid});
	}
	
	public List<UserRole> getByUidRid(int userid,int roleid)
	{
		String hql = "from UserRole where userid = ? and role.id=?";
		return find(hql,new Object[]{userid,roleid});
	}
	
	public UserRole getByUidRids(int userid,String roles)
	{
		String hql = "from UserRole where userid = ? and role.id in ("+roles+")";
		return get(hql,new Object[]{userid});
	}
	
	public UserRole getByUidRkey(int userid,String rolekey)
	{
		String hql = "from UserRole where userid = ? and role.rolekey=?";
		return get(hql,new Object[]{userid,rolekey});
	}
	
	public int delByUidRid(int userid,int roleid)
	{
		String hql = "delete from UserRole where userid = ? and role.id=?";
		return execute(hql, new Object[]{userid,roleid});
	}
	
	public int delByUidRids(String userids,int roleid)
	{
		String hql = "delete from UserRole where userid in ("+userids+") and role.id=?";
		return execute(hql, new Object[]{roleid});
	}
	
	public int delByUserid(int userid)
	{
		String hql = "delete from UserRole where userid = ?";
		return execute(hql, new Object[]{userid});
	}
	
	public Page getAllUserByorgid(int pageNo,int pageSize,int orgid){
		String hql = "from OrgUser where orgid=?";
		return pagedQuery(hql, pageNo, pageSize,orgid);
		
    }
	
	public List<UserRole> getByRoleId(int roleid)
	{
		String hql = "from UserRole where role.id = ?";
		return find(hql,new Object[]{roleid});
	}
	
	public void removeByUMO(String userids,int roleid){
		String hql="delete from UserRole where userid in ("+userids+") and role.id=?";
		executeHql(hql,new Object[]{roleid});
	}
}
