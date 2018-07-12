#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `test`.`country` ( `cid` INT(10) NOT NULL AUTO_INCREMENT ,`cname` VARCHAR(20) NOT NULL ,`mid` INT(10) NOT NULL,PRIMARY KEY(`cid`)) ENGINE = MyISAM;
CREATE TABLE `test`.`minister` ( `mid` INT(10) NOT NULL AUTO_INCREMENT ,`mname` VARCHAR(20) NOT NULL,PRIMARY KEY(`mid`)) ENGINE = MyISAM;

#初始化数据表
INSERT INTO `country` (`cid`, `cname`, `mid`) VALUES ('1', 'aaa', '1');
INSERT INTO `country` (`cid`, `cname`, `mid`) VALUES ('2', 'bbb', '2');
INSERT INTO `country` (`cid`, `cname`, `mid`) VALUES ('3', 'ccc', '3');

INSERT INTO `minister` (`mid`, `mname`) VALUES ('1', 'sam');
INSERT INTO `minister` (`mid`, `mname`) VALUES ('2', 'jane');
INSERT INTO `minister` (`mid`, `mname`) VALUES ('3', 'jone');