#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `test`.`student` ( `sid` INT(10) NOT NULL AUTO_INCREMENT ,`sname` VARCHAR(20) NOT NULL ,PRIMARY KEY(`sid`)) ENGINE = MyISAM;
CREATE TABLE `test`.`course` ( `cid` INT(10) NOT NULL AUTO_INCREMENT ,`cname` VARCHAR(20) NOT NULL ,PRIMARY KEY(`cid`)) ENGINE = MyISAM;
CREATE TABLE `test`.`middle` (
`id` INT(10) NOT NULL AUTO_INCREMENT ,`studentId` INT(10) NOT NULL ,`courseId` INT(10) NOT NULL ,PRIMARY KEY(`id`)) ENGINE = MyISAM;
#初始化数据表
INSERT INTO `course` (`cid`, `cname`) VALUES ('1', 'JAVA') ;
INSERT INTO `course` (`cid`, `cname`) VALUES ('2', 'C++') ;
INSERT INTO `course` (`cid`, `cname`) VALUES ('3', 'JS') ;

INSERT INTO `student` (`sid`, `sname`) VALUES ('1', 'Jam') ;
INSERT INTO `student` (`sid`, `sname`) VALUES ('2', 'Lina') ;

INSERT INTO `middle` (`id`, `studentId`, `courseId`) VALUES ('1', '1', '1');
INSERT INTO `middle` (`id`, `studentId`, `courseId`) VALUES ('2', '1', '2');
INSERT INTO `middle` (`id`, `studentId`, `courseId`) VALUES ('3', '2', '1');
INSERT INTO `middle` (`id`, `studentId`, `courseId`) VALUES ('4', '2', '3');