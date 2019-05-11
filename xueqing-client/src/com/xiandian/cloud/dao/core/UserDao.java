/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.dao.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiandian.cloud.common.cons.CommonConstants;
import com.xiandian.cloud.dao.BaseDao;
import com.xiandian.cloud.dao.Page;
import com.xiandian.cloud.entity.core.User;

/**
 * 用户Dao
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
@Repository
public class UserDao extends BaseDao<User> {
	
	public Object[] findUser(String username) {
		String sql = "select id,username,realname,password from t_user where username = ?";
		List l = findSql(sql, new Object[]{username});
		if (l != null && l.size() > 0) {
			return (Object[])l.get(0);
		} else {
			return null;
		}
	}
	
	public User getUserByname(String username) {
		String hql = "from User where username = ?";
		return get(hql, new Object[]{username});
	}
	
	public User getUserById(Integer id) {
		String hql = "from User where id = ?";
		return get(hql, new Object[]{id});
	}
	
	public User getUserBynameAndPhone(String username) {
		String hql = "from User where username = ? or phone = ? ";
		return get(hql, new Object[]{username,username});
	}
	
	public User getUserByphone(String phone) {
		String hql = "from User where phone = ?";
		return get(hql, new Object[]{phone});
	}
	
	public User getUserByToken(String token) {
		String hql = "from User where token = ?";
		return get(hql, new Object[]{token});
	}
	
	public User getUserByPwdtoken(String pwdtoken) {
		String hql = "from User where pwdtoken = ?";
		return get(hql, new Object[]{pwdtoken});
	}
	
	public User getUserBymail(String email) {
		String hql = "from User where email = ?";
		return get(hql, new Object[]{email});
	}
	
	public User getUserBycode(String code) {
		String hql = "from User where code = ?";
		return get(hql, new Object[]{code});
	}
	
	public List<User> getAll() {
		String hql = "from User";
		return find(hql);
	}
	
	public Page getAllUser(int pageNo,int pageSize)
	{
		String hql = "from User where id != 1 order by id desc";
		return pagedQuery(hql, pageNo, pageSize);
	}
	
	public List<User> getteachers()
	{
		String hql = "from User where id in (select userid from UserRole where role.id<=?) order by username";
		return find(hql,new Object[]{CommonConstants.TEACHER_ID});
	}
	
	
	public List<User> searchUser(String name,int orgid){
		
		name="%"+name+"%";
		String hql = "from User u where (u.username like ? or u.realname like ?) and u.id not in (select ou.user.id from OrgUser ou where ou.org.id=?)";
		return find(hql, new Object[]{name,name,orgid});
	}
	
	public List<User> search(String name){
		name="%"+name+"%";
		String hql = "from User u where (u.username like ? or u.realname like ?) order by id desc";
		return find(hql, new Object[]{name,name});
	}
	
	public List searchUser(String name){
		name="%"+name+"%";
		String sql = "select username,isenable,joindate,realname,email,id   from t_user  where ( username like ? or realname like ? ) order by id desc";
		return findSql(sql, new Object[]{name,name});
	}

	public Page getUsersByOrgId(int orgid, int pageNo, int pageSize) {
		Page pagedSqlQuery = pagedSqlQuery("select a.id,a.username,a.realname from t_user as a LEFT JOIN t_orguser as b on a.id=b.userid LEFT JOIN t_userrole as c on a.id=c.userid  where c.roleid=3 and b.orgid=?", pageNo, pageSize, new Object[]{orgid});
		return pagedSqlQuery;
	}
	
	public User getBm(String bm) {
		String hql = "from User where bm = ?";
		return get(hql, new Object[]{bm});
	}
	
	public User getXh(String xh) {
		String hql = "from User where xh = ?";
		return get(hql, new Object[]{xh});
	}
	
	public User getByUsername(String username) {
		String hql = "from User where username = ?";
		return get(hql, new Object[]{username});
	}
	
	public Page geByOrgId(int pageNo, int pageSize,String orgids,String roleids) {
		String hql = "from User where id != 1 and id in (select user.id from OrgUser where org.id in ("+orgids+")) " +
					"and id in (select userid from UserRole where role.id in ("+roleids+")) order by id desc";
		return pagedQuery(hql, pageNo, pageSize);
	}
	
	public Page getByParmars(int pageNo, int pageSize,String orgids,String name,String joindate,Integer majorid,Integer classid,Integer isrecommend)
	{
		List values=new ArrayList<>();
		String hql =" select u.id,u.username,u.realname,u.joindate,m.name as name1,c1.name as name2,u.isrecommend from t_user u inner join t_orguser  ou on  u.id = ou.userid   " +
				" inner join t_major m on u.majorid = m.id inner join t_classes c1 on u.majorid = c1.majorid where ou.orgid in ("+orgids+") ";
		if(name!=null){
			name="%"+name+"%";
			hql+="and (u.username like ? or u.realname like ?) ";
			values.add(name);
			values.add(name);
		}
		if(joindate!=null){
			hql+="and u.joindate=? ";
			values.add(joindate);
		}
		if(majorid!=null){
			hql+="and u.id in ( select cu.userid from t_classuser  cu inner join t_classes c on cu.classid = c.id where c.majorid=? ) ";
			values.add(majorid);
		}
		if(classid!=null){
			hql+="and u.id in ( select du.userid from t_classuser du where du.classid=? )";
			values.add(classid);
		}
		if(isrecommend!=null){
			hql+="and u.isrecommend=? ";
			values.add(isrecommend);
		}
		Object[] objs = values.toArray();
		hql+="and u.id not in (select ur.userid from t_userrole ur where ur.roleid=1 or ur.roleid=2 or ur.roleid=3) order by u.id desc";
		return pagedSqlQuery(hql, pageNo, pageSize,objs);
	}
	
	public void recommend(int id, int isrecommend) {
		String hql = "update User set isrecommend =? where id=?";
		executeHql(hql, new Object[] {isrecommend, id });
	}
	
	public void remove(String ids) {
		String hql = "delete from User where id in ("+ids+")";
		executeHql(hql);
	}
	
	public void resetpsw(String ids,String psw) {
		String hql = "update from User set password =? where id in ("+ids+")";
		executeHql(hql,new Object[]{psw});
	}
	
	public void updateisenable(String ids,int isenable) {
		String hql = "update from User set isenable =? where id in ("+ids+")";
		executeHql(hql,new Object[]{isenable});
	}
	
	public Page getByOrgidRoleid(int pageNo,int pageSize,String orgids,Integer classid,int roleid,String name){
		name="%"+name+"%";
		String hql = "from User where id != 1 and id in (select ou.user.id from OrgUser ou where ou.org.id in ("+orgids+")) and ";
		String hql1 = "id in (select cu.user.id from ClassUser cu where cu.classes.id = "+ classid +") and" ;
		String hql2 = "(username like ? or realname like ? or joindate like ? or email like ? or phone like ? or qq like ? or province like ? or city like ?) "
					+ "and id in (select userid from UserRole where role.id=?) order by id desc";
		String hqllast = null;
		if (classid != null || ("").equals(classid) || ("0").equals(classid)) {
			hqllast = hql + hql1 + hql2;
		} else {
			hqllast = hql + hql2;
		}
		return pagedQuery(hqllast, pageNo, pageSize,name,name,name,name,name,name,name,name,roleid);
	}
	
	
	public List<User> getIsRecommendList(){
		String hql = "from User where isrecommend = 1";
		return find(hql);
	}
	
	public List<User> getByRolekey(int orgid,String  rolekeys)
	{
		//String hql = "from User where org.id=? and id in (select userid from UserRole where role.rolekey in ("+rolekeys+")) order by username,realname";
		String hql = "from User where id in (select user.id  from OrgUser where orgid = ? ) and id in (select userid from UserRole where role.rolekey in ("+rolekeys+")) order by username,realname";
		return find(hql,new Object[]{orgid});
	}
	
	public List<User> getByOrgids(String orgids,int roleid)
	{
		String hql = "from User where org.id in ("+orgids+") and id in (select userid from UserRole where role.id=?) order by username,realname";
    	return find(hql,new Object[]{roleid});
	}
	
	public Object getCount(int orgid,int roleid)
	{
		String hql = "select count(*) from User where org.id=? and id in (select userid from UserRole where role.id=?)";
    	return getResult(hql,new Object[]{orgid,roleid});
	}
	
	public Object getCount(int roleid)
	{
		String hql = "select count(*) from User where id in (select userid from UserRole where role.id=?)";
    	return getResult(hql,new Object[]{roleid});
	}
	
	public Object getCountNew(int roleids)
	{
		String hql = "select count(*) from User where id in (select userid from UserRole where role.id in ?)";
    	return getResult(hql,new Object[]{roleids});
	}
	
	public Object getCountNewId(String roleids)
	{
		String hql = "select count(*) from User where id in (select userid from UserRole where role.id in ?)";
    	return find(hql, new Object[]{roleids});
	}
	
	
	
	public List<User> getByOrgids(String orgids)
	{
		String hql = "from User u,OrgUser ou where u.id = ou.user.id and ou.org.id in ("+orgids+") order by u.username,u.realname";
    	List<User> users = new ArrayList<>();
		List list =find(hql);
		for (Object object : list) {
			Object[] objects = (Object[])object;
			users.add((User)objects[0]);
	}
    	return users;
	}
	
	public Page getByParmars(int pageNo, int pageSize,int orgid,String joindate,int majorid)
	{
		List<Object> values=new ArrayList<Object>();
		String hql ="from User where 1=1 ";
		if (orgid!=-1) {
			hql+="and org.id=? ";
			values.add(orgid);
		}
		if(!"null".equals(joindate)){
			hql+="and joindate=? ";
			values.add(joindate);
		}
		if(majorid!=-1){
			hql+="and major.id=? ";
			values.add(majorid);
		}
	
		hql+="order by id desc";
		return pagedQuery(hql, pageNo, pageSize,values);
	}
	
	public Page getByClassid(int pageNo,int pageSize,int classid){
		String hql = "from User where id in (select user.id from ClassUser where classes.id=?) order by username";
		return pagedQuery(hql, pageNo, pageSize,classid);
	}
	
	public User getByUseridRolename(int userid,String rolekey){
		String hql = "from User where id=(select userid from UserRole where role.rolekey=?) order by id";
		return get(hql, new Object[]{userid,rolekey});
	}
	
	public User getUserByPhone(String phone) {
		String hql = "from User where phone = ?";
		return get(hql, new Object[]{phone});
	}
	
	public User getUserByEmail(String email) {
		String hql = "from User where email = ?";
		return get(hql, new Object[]{email});
	}
	
	public boolean usernameExist(int userid,String username) {
		String sql = " select id from t_user where id != ? and username = ? ";
		List list = findSql(sql, new Object[]{userid,username});
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	public List<User> getUserByid(int id) {
		String hql = "from User where id = ?";
		return find(hql, new Object[]{id});
	}
	
	public User getUserByQq(String openid) {
		String hql = "from User where openid = ?";
		return get(hql, new Object[]{openid});
	}
	
	public User getUserByWx(String unionid) {
		String hql = "from User where unionid = ?";
		return get(hql, new Object[]{unionid});
	}
	
	/*public List getListSql(int userid){
		String sql =" select u.id uid,u.username uname,u.realname,u.email,u.phone,u.qq,u.sex,u.joindate,c.name  name2,u.isrecommend,c.id cid,o.id  oid,o.name oname from t_user u inner join t_orguser ou on u.id = ou.userid inner join t_org o on ou.orgid = o.id  " +
				" inner join t_classes c on u.majorid = c.majorid where u.id = ? ";
		return findSql(sql, new Object[]{userid} );
	}*/
	
	public List getListSql(int userid){
		String sql =" select u.id uid,u.username uname,u.realname,u.email,u.phone,u.qq,u.sex,u.joindate,c.name  name2,u.isrecommend,c.id cid,o.id  oid,o.name oname from t_user u inner join t_orguser ou on u.id = ou.userid inner join t_org o on ou.orgid = o.id  " +
				"inner join t_classes c on u.majorid = c.majorid where u.id = ? ";
		return findSql(sql, new Object[]{userid} );
	}
	public List<String> getMajorName(int userid){
		String sql="select t_major.name from t_classuser inner join t_classes on t_classuser.classid=t_classes.id inner join t_major on t_classes.majorid=t_major.id where t_classuser.userid = ?";
		return findSql(sql, new Object[]{userid});
	}
	
	public List<String> getOrg(int userid){
		String sql1="select t_org.name from t_classuser inner join t_classes on t_classuser.classid=t_classes.id inner join t_major on t_classes.majorid=t_major.id inner join t_org on t_major.orgid=t_org.id where t_classuser.userid=?";
		String sql2="select t_org.name from t_orguser join t_org on t_org.id=t_orguser.orgid where t_orguser.userid=?";
		List<String> list=findSql(sql1, new Object[]{userid});
		if(list.size()>0){
			return list;
		}else{
			return findSql(sql2, new Object[]{userid});
		}
		
	}
	public List<Integer> getvalidateCode(int userid){
		String sql="select t_user.validateCode from t_user where t_user.id=?";
		return findSql(sql, new Object[]{userid});
	}
}