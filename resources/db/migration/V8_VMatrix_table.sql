CREATE TABLE `vmatrix` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `active` tinyint(1) NOT NULL DEFAULT '0',
  `iconid` int(11) NOT NULL DEFAULT '0',
  `skillid1` int(11) NOT NULL DEFAULT '0',
  `skillid2` int(11) NOT NULL DEFAULT '0',
  `skillid3` int(11) NOT NULL DEFAULT '0',
  `skillLv` int(11) NOT NULL DEFAULT '0',
  `masterLv` int(11) NOT NULL DEFAULT '0',
  `experience` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `characterid` (`characterid`),
  CONSTRAINT `vmatrix_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;