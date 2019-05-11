/*
 * Copyright (c) 2017, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * DAO基类，其它DAO可以直接继承这个DAO，不但可以复用共用的方法，还可以获得泛型的好处。
 * 
 * @author 云计算应用与开发项目组
 * @since V2.0
 */
public class BaseDao<T> {

	@Autowired
	protected SessionFactory sessionFactory;

	// 泛型反射类
	private Class<T> entityClass;

	// 通过反射获取子类确定的泛型类
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BaseDao() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		entityClass = (Class) params[0];
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	/*
	 * 根据id获取PO
	 */
	@SuppressWarnings("unchecked")
	public T get(int id) {
		return (T) this.getCurrentSession().get(this.entityClass, id);
	}

	public Serializable save(T o) {
		return this.getCurrentSession().save(o);
	}

	public void delete(T o) {
		this.getCurrentSession().delete(o);
	}

	public void update(T o) {
		this.getCurrentSession().update(o);
	}
	
	public void merge(T o) {
		this.getCurrentSession().merge(o);
	}

	public void saveOrUpdate(T o) {
		this.getCurrentSession().saveOrUpdate(o);
	}

	public List<T> find(String hql) {
		return this.getCurrentSession().createQuery(hql).list();
	}
	
	public int execute(String hql, Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.executeUpdate();
	}
	public Object getResult(String hql, Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.uniqueResult();
	}
	
	public Object getsqlResult(String sql, Object[] param) {
		Query q = this.getCurrentSession().createSQLQuery(sql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.uniqueResult();
	}

	public List<T> find(String hql, Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.list();
	}
	
	public List<T> findLimit(String hql,int limit,Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql).setMaxResults(limit);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.list();
	}
	
	public List findSql(String sql, Object[] param) {
		Query q = this.getCurrentSession().createSQLQuery(sql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.list();
	}
	
	public List<T> findSql(String sql, Object[] param,Class<T> pojoClass) {
		Query q = this.getCurrentSession().createSQLQuery(sql).addEntity(pojoClass);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.list();
	}
	
	public List<T> find(String hql, List<Object> param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.size() > 0) {
			for (int i = 0; i < param.size(); i++) {
				q.setParameter(i, param.get(i));
			}
		}
		return q.list();
	}

	public List<T> find(String hql, Object[] param, Integer page, Integer rows) {
		if (page == null || page < 1) {
			page = 1;
		}
		if (rows == null || rows < 1) {
			rows = 10;
		}
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}

	public List<T> find(String hql, List<Object> param, Integer page, Integer rows) {
		if (page == null || page < 1) {
			page = 1;
		}
		if (rows == null || rows < 1) {
			rows = 10;
		}
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.size() > 0) {
			for (int i = 0; i < param.size(); i++) {
				q.setParameter(i, param.get(i));
			}
		}
		return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}

	public T get(Class<T> c, Serializable id) {
		return (T) this.getCurrentSession().get(c, id);
	}

	public T get(String hql, Object[] param) {
		List<T> l = this.find(hql, param);
		if (l != null && l.size() > 0) {
			return l.get(0);
		} else {
			return null;
		}
	}

	public T get(String hql, List<Object> param) {
		List<T> l = this.find(hql, param);
		if (l != null && l.size() > 0) {
			return l.get(0);
		} else {
			return null;
		}
	}

	public Long count(String hql) {
		// return (Long)
		// this.getCurrentSession().createQuery(hql).uniqueResult();
		return new Long(this.getCurrentSession().createQuery(hql).list().size());
	}

	public Long count(String hql, Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		int size = q.list().size();
		return new Long(size);
	}
	
	public Long count(String hql, List<Object> param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.size() > 0) {
			for (int i = 0; i < param.size(); i++) {
				q.setParameter(i, param.get(i));
			}
		}
		int size = q.list().size();
		return new Long(size);
	}

	public Integer executeHql(String hql) {
		return this.getCurrentSession().createQuery(hql).executeUpdate();
	}

	public Integer executeHql(String hql, Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.executeUpdate();
	}

	public Integer executeSql(String sql, Object[] param) {
		Query q = this.getCurrentSession().createSQLQuery(sql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.executeUpdate();
	}
	
	public Integer executeHql(String hql, List<Object> param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.size() > 0) {
			for (int i = 0; i < param.size(); i++) {
				q.setParameter(i, param.get(i));
			}
		}
		return q.executeUpdate();
	}

	/**
	 * <根据HQL得到记录数>
	 */
	public Long countByHql(String hql, Object... values) {
		Query query = this.getCurrentSession().createQuery(hql);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return (Long) query.uniqueResult();
	}

	/**
	 * <HQL分页查询>
	 */
	public Page pagedQuery(String hql, int pageNo, int pageSize, Object... values) {
		Query query = this.getCurrentSession().createQuery(hql);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		int currentPage = pageNo > 1 ? pageNo : 1;
		// retValue.setCurrentPage(currentPage);
		// retValue.setPageSize(pageSize);
		int totalCount;
//		if (countHql == null) 
		{
			ScrollableResults results = query.scroll();
			results.last();
			totalCount = results.getRowNumber() + 1;
			// retValue.setTotalCount(results.getRowNumber() + 1);// 设置总记录数
		}
//		else {
//			Long count = countByHql(countHql, values);
//			// retValue.setTotalCount(count.intValue());
//			totalCount = count.intValue();
//		}
		// retValue.resetPageNo();
		
		// 实际查询返回分页对象
		int startIndex = Page.getStartOfPage(pageNo, pageSize);
				
		List<T> itemList = query.setFirstResult(startIndex).setMaxResults(pageSize).list();
		if (itemList == null) {
			itemList = new ArrayList<T>();
		}
		// retValue.setResults(itemList);
		return new Page(startIndex, totalCount, pageSize, itemList);
	}
	
	/**
	 * <HQL分页查询>
	 */
	public Page pagedSqlQuery(String sql, int pageNo, int pageSize, Object... values) {
		Query query = this.getCurrentSession().createSQLQuery(sql);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		int currentPage = pageNo > 1 ? pageNo : 1;
		// retValue.setCurrentPage(currentPage);
		// retValue.setPageSize(pageSize);
		int totalCount;
//		if (countHql == null) 
		{
			ScrollableResults results = query.scroll();
			results.last();
			totalCount = results.getRowNumber() + 1;
			// retValue.setTotalCount(results.getRowNumber() + 1);// 设置总记录数
		}
//		else {
//			Long count = countByHql(countHql, values);
//			// retValue.setTotalCount(count.intValue());
//			totalCount = count.intValue();
//		}
		// retValue.resetPageNo();
		
		// 实际查询返回分页对象
		int startIndex = Page.getStartOfPage(pageNo, pageSize);
				
		List<T> itemList = query.setFirstResult(startIndex).setMaxResults(pageSize).list();
		if (itemList == null) {
			itemList = new ArrayList<T>();
		}
		// retValue.setResults(itemList);
		return new Page(startIndex, totalCount, pageSize, itemList);
	}
	
	/**
	 * <HQL分页查询>
	 */
	public Page pagedQuery(String hql, int pageNo, int pageSize, List values) {
		Query query = this.getCurrentSession().createQuery(hql);
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				query.setParameter(i, values.get(i));
			}
		}
		int currentPage = pageNo > 1 ? pageNo : 1;
		int totalCount;
		ScrollableResults results = query.scroll();
		results.last();
		totalCount = results.getRowNumber() + 1;
		// 实际查询返回分页对象
		int startIndex = Page.getStartOfPage(pageNo, pageSize);
		List<T> itemList = query.setFirstResult(startIndex).setMaxResults(pageSize).list();
		if (itemList == null) {
			itemList = new ArrayList<T>();
		}
		return new Page(startIndex, totalCount, pageSize, itemList);
	}
	
	public List<T> limit(String hql,int limit, Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql).setMaxResults(limit);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.list();
	}
	
	/*public void saveLosts(List<T> list) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        int i = 0;
        for (T t : list) {
            if (session.contains(t)) {
                continue;
            } else {
                session.save(t);
                i++;
            }
            if (i % 20 == 0) {
                session.getTransaction().commit();
                session.flush();
                session.clear();
                session.beginTransaction();
            }
        }
        session.getTransaction().commit();
        session.close();
    }*/
}