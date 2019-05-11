/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

import java.util.List;

/** 
* @author xuanhuidong E-mail: 1259023939@qq.com
* @version 创建时间：2017年11月8日 下午3:57:55 
* 类说明 
*/
public class SkillBean {
	private String name;
	private List<Double> value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Double> getValue() {
		return value;
	}
	public void setValue(List<Double> value) {
		this.value = value;
	}
	
}
