/*
 * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
 * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xiandian.douxue.insight.server.base;


/**
 * 服务运行状态。 

 * @since v1.0
 * @date 20170815
 *  @author XianDian Cloud Team
 */
public class ServiceState {
	/**
	 * 服务初始化完成，等待启动。
	 */
	public static final ServiceState STATE_INITIALIZED =  new ServiceState("Initialized");
	/**
	 * 服务没有启动。
	 */
	public static final ServiceState STATE_NOTSTART =  new ServiceState("NOT Start!");
	/**
	 * 服务已经开始。
	 */
	public static final ServiceState STATE_STARTED =  new ServiceState("Started");	
	/**
	 * 服务在执行中。
	 */
	public static final ServiceState STATE_RUNNING =  new ServiceState("Running");

	
	/**
	 * Service Runing State.
	 */
	private String stateInfo;
	
	/**
	 * 状态类。ֹ
	 * (Base On Thread of Service)
	 * 
	 * @param stateInfo
	 */
	public ServiceState( String stateInfo)
	{	
		this.stateInfo = stateInfo;
	}

	/**
	 * Get Service State.
	 * @return Service State.
	 */
	public String getStateInfo() {
		return stateInfo;
	}


}
