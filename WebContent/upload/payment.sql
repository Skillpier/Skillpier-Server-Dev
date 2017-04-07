# MySQL-Front 5.1  (Build 4.2)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;


# Host: 47.88.79.54    Database: skillopedia
# ------------------------------------------------------
# Server version 5.5.15

#
# Source for table payment
#

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `payment_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '姓名-必填',
  `money` double(20,2) NOT NULL DEFAULT '0.00' COMMENT '金额-必填',
  `come_in_account` varchar(255) DEFAULT '' COMMENT '出落谁户口（一次性）',
  `change_payment_account` varchar(255) DEFAULT '' COMMENT '要改Payment户口',
  `remarker` varchar(255) DEFAULT '' COMMENT '备注',
  `post_time` datetime DEFAULT NULL COMMENT '更新时间',
  `submitters` varchar(255) DEFAULT '' COMMENT '提交者（审批流程）',
  `submitters_id` int(11) DEFAULT '0' COMMENT '提交者ID',
  `approvers` varchar(255) DEFAULT '' COMMENT '审判者（审批流程）',
  `status` int(11) DEFAULT '1' COMMENT '状态：1-未提交（摸版），2-未处理，3-已审批，4-已拒绝,5-已转账',
  PRIMARY KEY (`payment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='预支付';

#
# Dumping data for table payment
#

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (42,'谢晓阳',1000,'小吧','小白','啊 啊  啊啊啊啊 啊 啊   啊啊啊啊啊','2016-05-15 00:23:39','admin',0,'',3);
INSERT INTO `payment` VALUES (43,'谢晓阳1',2000,'小吧','小白','啊啊啊啊 啊啊啊啊啊啊 啊','2016-05-15 00:24:19','admin',0,'',2);
INSERT INTO `payment` VALUES (44,'谢小阳3',3000,'小黄','小黑','啊啊啊啊 啊啊啊啊 啊啊啊啊啊','2016-05-15 00:25:00','admin',0,'',2);
INSERT INTO `payment` VALUES (46,'谢晓阳5',50000,'小妮','小小',' 啊啊啊啊 啊啊啊 啊啊 啊啊','2016-05-15 00:26:34','admin',0,'',2);
INSERT INTO `payment` VALUES (47,'谢晓阳6',60000,'效益','小龙','啊 啊啊 啊啊啊 啊啊啊 啊','2016-05-15 00:27:07','admin',0,'',3);
INSERT INTO `payment` VALUES (48,'谢晓阳7',7000,'小谢','小小','啊 啊啊啊 啊啊啊 啊啊啊啊','2016-05-15 00:27:45','admin',0,'',2);
INSERT INTO `payment` VALUES (49,'谢晓阳8',80000,'小鸟','晓晓','啊啊 啊啊  啊啊啊 啊 啊啊啊啊啊啊啊','2016-05-15 00:28:27','admin',0,'',4);
INSERT INTO `payment` VALUES (50,'谢晓阳2',90000,'小二','小三','啊 啊啊啊 啊啊啊 啊啊 ','2016-05-15 00:29:38','admin',0,'',2);
INSERT INTO `payment` VALUES (51,'谢晓阳9',3000000,'百合','文章','啊 啊啊啊 啊啊 啊啊啊啊啊 啊 啊','2016-05-15 00:30:20','admin',0,'',5);
INSERT INTO `payment` VALUES (53,'罗问焕',1212,'猪猪','八佰伴','厚厚的回得很','2016-05-16 08:54:47','admin',0,'',2);
INSERT INTO `payment` VALUES (54,'Luo文焕',9000,'侧罗测','侧罗测','侧罗测','2016-05-16 09:00:57','haha',0,'admin',5);
INSERT INTO `payment` VALUES (60,'xiaoming',5656,'test','test','test','2016-05-16 15:16:26','admin',1,'',2);
INSERT INTO `payment` VALUES (61,'meimei',5555,'tettt','tttt','tttt','2016-05-16 15:49:39','admin',1,'',2);
INSERT INTO `payment` VALUES (62,'jiejie',5555,'haohao','haohao','haohao','2016-05-16 15:50:52','test',7,'',2);
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
