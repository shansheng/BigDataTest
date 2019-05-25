package com.xiandian.cloud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiandian.cloud.dao.core.CommodityDao;
import com.xiandian.cloud.entity.core.Commodity;

@Service
public class CommodityService {
	@Autowired
	private CommodityDao commodityDao;
	
	public List<Commodity> getAll(){
		return commodityDao.getAll();
	}
	
	public List<Commodity> Search(String keyword){
		return commodityDao.search(keyword);
	}
}
