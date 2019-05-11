/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

import java.util.List;

/**
 * @author xuanhuidong E-mail: 1259023939@qq.com
 * @version 创建时间：2017年9月18日 下午1:15:09 类说明
 */
public class LinObj {
	private String name;
	private String type;
	private List<Double> data;
	private boolean smooth;
	
	public boolean isSmooth() {
		return smooth;
	}

	public void setSmooth(boolean smooth) {
		this.smooth = smooth;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Double> getData() {
		return data;
	}

	public void setData(List<Double> data) {
		this.data = data;
	}

}
