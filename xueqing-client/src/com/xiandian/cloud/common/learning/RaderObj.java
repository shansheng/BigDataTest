/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.cloud.common.learning;

/**
 * @author xuanhuidong E-mail: 1259023939@qq.com
 * @version 创建时间：2017年9月14日 上午10:58:41 类说明
 */
public class RaderObj {
	private String name;
	private int max;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	@Override
	public String toString() {
		return "RaderObj [name=" + name + ", max=" + max + "]";
	}

}
