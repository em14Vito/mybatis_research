CREATE TABLE `bus_info` (
  `bus_info_id` int(16) unsigned NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `modify_time` timestamp NULL DEFAULT NULL,
  `bus_id` varchar(500) DEFAULT NULL,
  `bus_name` varchar(500) DEFAULT NULL,
  KEY `bus_info_id` (`bus_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8
