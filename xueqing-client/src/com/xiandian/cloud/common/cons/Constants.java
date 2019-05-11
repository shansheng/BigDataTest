/*
 * Copyright (c) 2014, 2017, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.cloud.common.cons;

/**
 * 整个应用通用常量类
 * 
 * @author 云计算应用与开发项目组
 * @since V1.0
 * 
 */
public class Constants extends CommonConstants{
	
	//环境创建返回状态
	public static final String CODE_200 = "成功";
	public static final String CODE_201 = "虚拟机达到上限";
	public static final String CODE_202 = "创建虚拟机线程返回空值";
	public static final String CODE_203 = "网络资源耗尽";
	public static final String CODE_204 = "用户环境已存在";
	// 参数学校的分页大小
	public static final int PAGE_SCHOOLSIZE = 24;
	//课程搜索分页大小
	public static final int PAGE_COURSESIZE =24;
	//首页课程分页大小
	public static final int INDEX_COURSESIZE =10;
	// 微信学校列表
	public static final int PAGE_WXSCHOOLSIZE = 10;

	public static final String STATUS_1 = "通过";
	public static final String STATUS_2 = "不通过";

	
	 public static final int APP_PAGE_SIZE = 5;
	// 错误信息
	public static final String ERROR_10 = "课程不存在";
	public static final String ERROR_11 = "课程已经存在";
	public static final String ERROR_12 = "你已经报名";
	public static final String ERROR_13 = "你所在的机构还没有报名";
	public static final String ERROR_14 = "您尚未加入任何机构因此无法报名。";
	public static final String ERROR_15 = "你所在的机构还没有审核通过报名";
	public static final String ERROR_17 = "你还没有被所在机构通过审核";
	public static final String ERROR_16 = "审核不通过";
	public static final String ERROR_18 = "待审核";
	public static final String ERROR_19 = "未选择机构";
	public static final String ERROR_20 = "标签已经存在";
	public static final String ERROR_21 = "分类已经存在";
	public static final String ERROR_22 = "office转换pdf失败";
	public static final String ERROR_23 = "模板名称已经存在";
	
	public static final String ERROR_24 = "文件夹已经存在";
    public static final String ERROR_25 = "文件已经存在";
    public static final String ERROR_26 = "删除失败";
    public static final String ERROR_27 = "原密码不对";
    public static final String ERROR_28 = "不能将文件移动到自身";
    public static final String ERROR_29 = "不能将文件复制到自身";
    public static final String ERROR_30 = "不能将文件移动到自身";
    public static final String ERROR_31 = "不能将文件复制到自身";
    public static final String ERROR_32 = "空间大小不足，请及时扩充空间";
    
    public static final String ERROR_33 = "任务已经存在"; 
    public static final String ERROR_34 = "班级不存在"; 
    public static final String ERROR_35 = "班级已经存在"; 
    
    public static final String ERROR_36 = "系统存储出现异常，请联系管理员";
    
    public static final String ERROR_37 = "积分不足";
    
    public static final String ERROR_38 = "licence已存在";
    
    public static final String ERROR_39 = "已有激活licence存在，请在过期后手动删除再导入新licence";
    public static final String ERROR_40 = "请导入可用的licence";
	// 审批状态
	public static final int IDENTITY_0 = 0;// 待审批
	public static final int IDENTITY_1 = 1;// 审批通过
	public static final int IDENTITY_2 = 2;// 审批不通过
	
	//上传导入
	public static final String SUCCESS_3 = "删除成功";
	public static final String SUCCESS_16 = "导入成功";
	public static final String SUCCESS_17 = "激活成功";
	
	
	// 课程內容类型
	public static final int COURSE_VIDEO = 0;// 視頻
	public static final int COURSE_DOCUMENT = 1;// 文档
//	public static final int COURSE_PPT = 1;
//	public static final int COURSE_WORD = 2;
	public static final int COURSE_ZIP = 3;
	public static final int COURSE_HTML = 4;//图文
	public static final int COURSE_EXAM = 5;
	public static final int COURSE_TRAIN = 6;//实训
//	public static final int COURSE_PDF = 7;
	public static final int COURSE_TASK = 8;//投票任务
//	public static final int COURSE_XLS = 9;
	//前台使用的
	public static final int COURSE_TASKEXAM = 10;//测验
	public static final int COURSE_TASKQUES = 11;//问卷
	public static final int COURSE_WEB = 12;//网络视频
	public static final int COURSE_TRAINEXAM = 13;//实训测验
	public static final int COURSE_LIVE = 14;//直播
	
	public static final int COURSE_WEBRES_VIDEO = 100;//资源库
	public static final int COURSE_WEBRES_DOCUMENT=101;
//	public static final int COURSE_WEBRES_PPT = 101;//资源库
//	public static final int COURSE_WEBRES_WORD = 102;//资源库
	public static final int COURSE_WEBRES_ZIP = 103;//资源库
//	public static final int COURSE_WEBRES_PDF = 107;//资源库
//	public static final int COURSE_WEBRES_XLS = 109;//资源库
	
	// 课程学习进度
	public static final String FINISH = "finish";
	public static final String HAVING = "having";
	// 课时学习进度
	public static final String UNOPEN = "未开课";
	public static final String UNSTART = "未开始";
	public static final String FINISHED = "已完结";

	// 微课上传的4种类型
	public static final String COURSE_1 = "教学视频";
	public static final String COURSE_2 = "教学课件";
	public static final String COURSE_3 = "教学设计";
	public static final String COURSE_4 = "辅助材料";

	// 考试系统对应的问题类型————>//0:单选 1：多选 2：判断 3 填空 4 论述题 5 描述题
	public static final int QTYPE_1 = 0;
	public static final int QTYPE_2 = 1;
	public static final int QTYPE_3 = 2;
	public static final int QTYPE_4 = 3;
	public static final int QTYPE_5 = 4;
	public static final int QTYPE_6 = 5;
	
   //课程级别
   public static final String LEVEL_1 = "初级";
   public static final String LEVEL_2 = "中级";
   public static final String LEVEL_3 = "高级";

   
   //日志信息记录
   public static final String LOG_1 = "提交答案：";
   public static final String LOG_2 = "提交试卷：";
   public static final String LOG_3 = "修改判分：";
   public static final String LOG_4 = "结束判分：";
   public static final String LOG_5 = "更新答案：";
   public static final String LOG_6 = "删除选手";
   public static final String LOG_7 = "删除裁判";
   //答题的对错  1：right 2：wrong 3：no
   public static final int PRIGHT_1 = 1;
   public static final int PRIGHT_2 = 2;
   public static final int PRIGHT_3 = 3;

   
   //课程发布
   public static final int PUBLISH_1 = 1; //已发布
   public static final int PUBLISH_2 = 2; //未发布
   
   public static final int ISOPEN_0 = 0; //开班
   public static final int ISOPEN_1 = 1; //不开班
   
   public static final int SERIAL_1 = 1; //1.非连载课程 
   public static final int SERIAL_2 = 2; //2.更新中 
   public static final int SERIAL_3 = 3; //3.完结

   //课程学习相关的常量
   public static final String MARK_1 = "提交了试卷答案";
   public static final String MARK_2 = "学习了课时";
   public static final String MARK_3 = "发布了实训报告";
   public static final String MARK_4 = "更新了实训报告";
   public static final String MARK_5 = "删除了实训报告";
   public static final String MARK_6 = "评价了课程";
   public static final String MARK_7 = "提交了课程问题";
   public static final String MARK_8 = "回复了课程问题";
   public static final String MARK_9 = "做了课程笔记";
   public static final String MARK_10 = "删除了课程笔记";
   public static final String MARK_11 = "提交了任务答案";
   public static final String MARK_12 = "删除了任务答案";
   public static final String MARK_13 = "加入了课程";
   public static final String MARK_14 = "完成了课时";
   public static final String MARK_17 = "删除了问题";
   public static final String MARK_18 = "下载了资料";
   
   public static final String LOCAL_FILE = "/local_file";//用户课程本地上传的素材库
   
   public static final String DEFAULT_CLASS = "默认班级";
   
	public static final String STATUS_STOPPED= "stopped";
	public static final String STATUS_ACTIVE= "ACTIVE";
	public static final String STATUS_SUSPENDED= "Suspended";
	
	public static final String EXAM_SERVICE_UNOPEN="云考试服务未开启！";
	
	/*
	 * 云主机状态
	 */
	public static final String QSERVER_STATUS_1 = "故障";
	public static final String QSERVER_STATUS_2 = "运行中";
	public static final String QSERVER_STATUS_3 = "创建中";
	public static final String QSERVER_STATUS_4 = "已关机";
	public static final String QSERVER_STATUS_5 = "已退还";
	public static final String QSERVER_STATUS_6 = "退还中";
	public static final String QSERVER_STATUS_7 = "重启中";
	public static final String QSERVER_STATUS_8 = "开机中";
	public static final String QSERVER_STATUS_9 = "关机中";
	public static final String QSERVER_STATUS_10 = "密码重置中";
	public static final String QSERVER_STATUS_11 = "格式化中";
	public static final String QSERVER_STATUS_12 = "镜像制作中";
	public static final String QSERVER_STATUS_13 = "带宽设置中";
	public static final String QSERVER_STATUS_14 = "重装系统中";
	public static final String QSERVER_STATUS_15 = "域名绑定中";
	public static final String QSERVER_STATUS_16 = "域名解绑中";
	public static final String QSERVER_STATUS_17 = "负载均衡绑定中";
	public static final String QSERVER_STATUS_18 = "负载均衡解绑中";
	public static final String QSERVER_STATUS_19 = "升级中";
	public static final String QSERVER_STATUS_20 = "密钥下发中";
	public static final String QSERVER_STATUS_21 = "已挂起";
	public static final String QSERVER_STATUS_22 = "已暂停";
	public static final String QSERVER_STATUS_23 = "调整大小中";
	public static final String QSERVER_STATUS_24 = "确认大小中";
	public static final String QSERVER_STATUS_25 = "恢复大小中";
	public static final String QSERVER_STATUS_26 = "确认密码中";
	public static final String QSERVER_STATUS_27 = "未知";
	public static final String QSERVER_STATUS_28 = "确认大小中";
	public static final String QSERVER_STATUS_29 = "停止";
	public static final String QSERVER_STATUS_30 = "迁移中";
	public static final String QSERVER_STATUS_31 = "无法识别";
	
	//log
	public static final String OBJECT_TYPE = "dxw";
	public static final String SYSTEM = "斗学网";
	
	/**
	 * 课程性质,0代表自主课程，1代表教学任务
	 */
	public static final int COURSE_NATURE_0= 0;
	public static final int COURSE_NATURE_1= 1;
	
	/**
	 * 课程类型
	 */
	public static final int IS_PUBLIC= 0;
	public static final int IS_PRIVATE= 1;
	
	/**
	 * 
	 */
	public static final String USERENVIRONMENT_START = "开机";
	public static final String USERENVIRONMENT_STOP = "关机";
	
	/**
	 * 课程层级
	 */
	public static final String COURSE_LEVEL0 = "课程";
	public static final String COURSE_LEVEL1 = "微课";
	public static final String COURSE_LEVEL2 = "培训项目";
	
	/**
	 * 直播状态
	 */
	public static final int LIVE_CREATE = -1; // 初始化
	public static final int LIVE_STOP = 0; // 已结束
	public static final int LIVE_PUSH = 1; // 直播中
	public static final int LIVE_FORBID = -2; // 禁播
	public static final int LIVE_PLAY = 100; // 回放
	public static final int LIVE_SCREENSHO = 200; // 截图
	
}
