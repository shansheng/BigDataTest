/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

import java.util.List;
import java.util.Map;

/**
 * @author xuanhuidong E-mail: 1259023939@qq.com
 * @version 创建时间：2017年11月6日 下午3:28:05 类说明
 */
public class GradeBean {
	private String name;
	private String type;
	private List<List<String>> data;
	private Map<String,Object> markLine;
	private Map<String,Object> markArea;
	
	public Map<String, Object> getMarkArea() {
		return markArea;
	}

	public void setMarkArea(Map<String, Object> markArea) {
		this.markArea = markArea;
	}

	public Map<String, Object> getMarkLine() {
		return markLine;
	}

	public void setMarkLine(Map<String, Object> markLine) {
		this.markLine = markLine;
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

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}

}
