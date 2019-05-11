/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

/**
 * @author xuanhuidong E-mail: 1259023939@qq.com
 * @version 创建时间：2017年9月12日 上午8:46:48 类说明
 */
public class EchartsObj {
	private String name;
	private long value;

	public EchartsObj(String name, long value) {
		this.name = name;
		this.value = value;
	}

	public EchartsObj() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "EchartsObj [name=" + name + ", value=" + value + "]";
	}
	

}
