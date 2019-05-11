/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.5.17 : Database - xueqing-client
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`xueqing-client` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `xueqing-client`;

/*Table structure for table `t_role` */

DROP TABLE IF EXISTS `t_role`;

CREATE TABLE `t_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` char(50) NOT NULL,
  `rolekey` char(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `rolekey` (`rolekey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_role values ('1', '系统管理员', 'admin');
insert into t_role values ('2', '老师', 'teacher');
insert into t_role values ('3', '学生', 'student');

/*Data for the table `t_role` */

/*Table structure for table `t_user` */

DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bm` varchar(100) DEFAULT NULL,
  `xh` varchar(100) DEFAULT NULL,
  `xjh` varchar(100) DEFAULT NULL,
  `username` char(100) NOT NULL,
  `realname` char(100) DEFAULT NULL,
  `password` char(100) NOT NULL,
  `email` char(100) DEFAULT NULL,
  `status` int(11) DEFAULT '0',
  `headimg` varchar(100) DEFAULT NULL,
  `sex` char(10) DEFAULT '0',
  `describle` varchar(1000) DEFAULT NULL,
  `code` char(100) DEFAULT NULL,
  `codeimg` char(100) DEFAULT NULL,
  `token` char(100) DEFAULT NULL,
  `pwdtoken` char(100) DEFAULT NULL,
  `job` char(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `qq` varchar(20) DEFAULT NULL,
  `isenable` int(11) DEFAULT '0',
  `createtime` char(100) DEFAULT NULL,
  `platform` varchar(1000) DEFAULT NULL,
  `storagesize` int(11) DEFAULT '500',
  `field` varchar(1000) DEFAULT NULL,
  `birthday` varchar(100) DEFAULT NULL,
  `joindate` varchar(100) DEFAULT NULL,
  `province` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `modifiedtime` varchar(100) DEFAULT NULL,
  `majorid` int(11) DEFAULT '-1',
  `rip` varchar(100) DEFAULT NULL,
  `isrecommend` int(11) DEFAULT '0',
  `points` int(11) DEFAULT '0',
  `openid` varchar(255) DEFAULT NULL,
  `unionid` varchar(255) DEFAULT NULL,
  `validateCode` int(1) DEFAULT '0',
  `wxopenid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into t_user(id,username,realname,password,email,status) values ('1', 'admin','管理员', 'e10adc3949ba59abbe56e057f20f883e', 'admin@qq.com', '2');
/*Data for the table `t_user` */

/*Table structure for table `t_userrole` */

DROP TABLE IF EXISTS `t_userrole`;

CREATE TABLE `t_userrole` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `roleid` int(11) NOT NULL,
  `status` int(11) DEFAULT '0',
  `reply` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userid` (`userid`,`roleid`),
  KEY `t_userrole_ibfk_2` (`roleid`),
  CONSTRAINT `t_userrole_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `t_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_userrole_ibfk_2` FOREIGN KEY (`roleid`) REFERENCES `t_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `t_userrole` VALUES ('1', '1', '1', '2', null);
INSERT INTO `t_userrole` VALUES ('2', '1', '2', '2', null);
INSERT INTO `t_userrole` VALUES ('3', '1', '3', '2', null);
/*Data for the table `t_userrole` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
