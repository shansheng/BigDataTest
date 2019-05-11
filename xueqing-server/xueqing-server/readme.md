# 学情分析
## 构建开发环境(Windows)
### Hadoop 环境
1. 下载hadoop-2.7.0.tar.gz，并解压。下载地址:[Hadoop 2.7.1](https://archive.apache.org/dist/hadoop/common/hadoop-2.7.1/)。
2. 配置HADOOP_HOME系统环境变量，指向Hadoop跟目录。
3. 下载winutils.exe，下载bin目录的所有文件，覆盖解压Hadoop解压跟目录。下载地址:[winutils hadoop-2.7.1](https://github.com/steveloughran/winutils)。
4. Window 配置C:\Windows\System32\drivers\etc\hosts
    增加ambari Server、Agent的地址
```  
    10.10.4.35 ambari-server.hadoop
    10.10.4.36 master.hadoop
    10.10.4.38 slave2.hadoop
    10.10.4.37 slave1.hadoop
```
相关的HostName可以登录Ambari各节点查看：
```shell
[root@ambari-server ~]# hostname
ambari-server.hadoop
```
```
[root@master ~]# hostname
master.hadoop
```
```
[root@slave1 ~]# hostname
slave1.hadoop
```

## HBase 数据设计
网络爬虫爬取的招聘岗位数据，存储表“job_internet”分2个列族：RAW_DATA和TAG_DATA；
分别保存爬取的原始数据和标签获得原始数据。
job_internet结构如下：

列族RAW_DATA | 列族TAG_DATA |感知数据PERCEPT_DATA
------------ | -------------|------------ |

每列如下:

1. RAW_DATA原始数据

列名|含义
------------ | -------------|
JOB_NAME|	职位名称
DATE	|发布时间
FROM	|网站来源
HDFS_PATH|	hdfs存储路径

2. TAG_DATA标签数据

列名|含义
------------ | -------------|
JOB_NAME|	职位名称
DATE	|发布时间
ISPERCEPTED|	是否洗数据
ID|	id
LOCATION|	工作地点
EXPERIENCE|	工作经验
SALARY|	薪资
AMOUNT|	招聘人数
EDUCATION|	学历
DESCRIPTION|	岗位描述
CATEGORY|	职能类别
COMPANY_NAME|	公司名称
COMPANY_NATURE|	公司性质
COMPANY_INDUSTRY|	公司行业
COMPANY_SCALE|	公司规模

3. PERCEPT_DATA感知数据

列名|含义
------------ | -------------|
JOB_NAME	|职位名称
DATE	|发布时间
ISPERCEPTED	|是否洗数据
ID	|id
LOCATION	|工作地点
EXPERIENCE	|工作经验
SALARY	|薪资
AMOUNT	|招聘人数
EDUCATION	|学历
DESCRIPTION	|岗位描述
CATEGORY	|职能类别
COMPANY_NAME	|公司名称
COMPANY_NATURE	|公司性质
COMPANY_INDUSTRY	|公司行业
COMPANY_SCALE	|公司规模

4.岗位聚类
针对云计算、大数据、移动互联、物联网、人工智能，对爬取的数据对有用的岗位数据，过滤独立存到的对应的HBase表中，表中只保存感知数据：

HBase表	|含义
------------ | -------------|
job_cloud|云计算
job_bigdata|大数据
job_internet|移动互联
job_iot|物联网
job_mobile|移动开发
job_software|软件开发
job_ai|人工智能

## Hive 数据设计

数据的统计、查询通过Hive完成(Hive On HBase).
Hive的设计如下。

1.job_tag_data

字段	|含义
------------ | -------------|
JOB_NAME	|职位名称
RELEASEDATE|	发布时间
ISTAGED|	是否洗数据
ID|	ID
LOCATION	|工作地点
EXPERIENCE|	工作经验
SALARY|	薪资
AMOUNT	|招聘人数
EDUCATION	|学历
DESCRIPTION|	岗位描述
CATEGORY	|职能类别
COMPANY_NAME	|公司名称
COMPANY_NATURE|	公司性质
COMPANY_INDUSTRY|	公司行业
COMPANY_SCALE|	公司规模

2.job_percept_data

字段	|含义
------------ | -------------|
JOB_NAME	|岗位名称
RELEASEDATE	|发布时间
ENDDATE	|结束时间
ISPERCEPTED	|是否第三簇
ID	|ID
LOCATION	|工作地点
EXPERIENCE	|工作经验
SALARY	|薪资
AMOUNT	|招聘人数
EDUCATION	|学历
DESCRIPTION	|聚类后的岗位描述
DESCRIPTION2	|岗位描述
CATEGORY	|职能类别
COMPANY_NAME	|公司名称
COMPANY_NATURE	|公司性质
COMPANY_INDUSTRY	|公司行业
COMPANY_SCALE	|公司规模

3.job_cloud

字段	|含义
------------ | -------------|
JOB_NAME	|岗位名称
RELEASEDATE	|发布时间
ENDDATE	|结束时间
ISPERCEPTED	|是否第三簇
ID	|ID
LOCATION	|工作地点
EXPERIENCE	|工作经验
SALARY	|薪资
AMOUNT	|招聘人数
EDUCATION	|学历
DESCRIPTION	|聚类后的岗位描述
DESCRIPTION2	|岗位描述
CATEGORY	|职能类别
COMPANY_NAME	|公司名称
COMPANY_NATURE	|公司性质
COMPANY_INDUSTRY	|公司行业
COMPANY_SCALE	|公司规模

## MongoDB的设计

1.数据收集统计job_internet

字段	|含义
------------ | -------------|
	China_areas_location	|中国所有县、区
	China_cities_location	|中国所有直辖市
	China_province_location	|中国所有省级地区
	job_category_distribution	|职能分布统计
	job_company_industry_distribution|	公司行业分布统计
	job_company_nature_distribution	|公司性质分布统计
	job_company_scale_distribution	|公司规模分布统计
	job_education_distribution	|学历分布统计
	job_experience_distribution	|工作经验分布统计
	job_location_distribution|	岗位地区分布统计
	job_name_distribution	|岗位名称分布统计
	job_province_distribution	|岗位省份分布统计
	job_salary_distribution|	薪资分布统计
	job_total|	岗位数量统计

2.云计算开发的统计

字段	|含义
------------ | -------------|
    cloud_develop|	聚类云计算开发岗位数量统计
	cloud_framework	|聚类云计算架构岗位数量统计
	cloud_operation	|聚类云计算运维岗位数量统计
	job_category_distribution	|职能分布统计
	job_cloud_develop	|聚类云计算开发岗位技能点分布
	job_cloud_framework	|聚类云计算架构岗位技能点分布
	job_cloud_operation|	聚类云计算运维岗位技能点分布
	job_company_industry_distribution	|公司行业分布统计
	job_company_nature_distribution	|公司性质分布统计
	job_company_scale_distribution|	公司规模分布统计
	job_education_distribution	|学历分布统计
	job_experience_distribution	|工作经验分布统计
	job_location_distribution	|岗位地区分布统计
	job_name_distribution	|岗位名称分布统计
	job_province_distribution	|岗位省份分布统计
	job_salary_distribution	|薪资分布统计
	job_total	|岗位数量统计
	