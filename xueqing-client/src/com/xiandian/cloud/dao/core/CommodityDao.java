package com.xiandian.cloud.dao.core;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiandian.cloud.dao.BaseDao;
import com.xiandian.cloud.entity.core.Commodity;

@Repository
public class CommodityDao extends BaseDao<Commodity> {

	public List<Commodity> getAll(){
		String hql="from Commodity";
		return find(hql);
	}
	
	public List<Commodity> search(String keyword) {
		String hql = "from Commodity commodity where commodity.name like '%" + keyword+"%'";
		return find(hql);
	}
}
