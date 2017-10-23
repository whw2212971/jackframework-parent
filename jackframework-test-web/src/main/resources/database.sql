SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_data
-- ----------------------------
DROP TABLE IF EXISTS `t_data`;
CREATE TABLE `t_data` (
  `data_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_string` varchar(255) DEFAULT NULL,
  `data_int` int(11) DEFAULT NULL,
  `data_decimal` decimal(11,6) DEFAULT NULL,
  `data_date` date DEFAULT NULL,
  `data_datetime` datetime DEFAULT NULL,
  `data_boolean` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;