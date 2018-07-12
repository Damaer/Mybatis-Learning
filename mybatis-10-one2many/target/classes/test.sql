#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `test`.`country` ( `cid` INT(10) NOT NULL AUTO_INCREMENT ,`cname` VARCHAR(20) NOT NULL ,PRIMARY KEY(`cid`)) ENGINE = MyISAM;
CREATE TABLE `test`.`minister` ( `mid` INT(10) NOT NULL AUTO_INCREMENT ,`mname` VARCHAR(20) NOT NULL ,`countryId` INT(20) NOT NULL ,PRIMARY KEY(`mid`)) ENGINE = MyISAM;

#初始化数据表
INSERT INTO `country` (`cid`, `cname`) VALUES ('1', 'USA');
INSERT INTO `country` (`cid`, `cname`) VALUES ('2', 'England');

INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('1', 'aaa', '1');
INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('2', 'bbb', '1');
INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('3', 'ccc', '2');
INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('4', 'ddd', '2');
INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('5', 'eee', '2');