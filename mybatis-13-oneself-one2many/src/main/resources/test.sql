#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `test`.`newslabel` ( `id` INT NOT NULL AUTO_INCREMENT ,
`name` VARCHAR(20) NOT NULL , `pid` INT(20) NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;
#初始化数据表
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('1', '娱乐新闻', '0');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('2', '体育新闻', '0');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('3', 'NBA', '2');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('4', 'CBA', '2');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('5', '火箭', '3');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('6', '湖人', '3');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('7', '北京金瓯', '4');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('8', '浙江广夏', '4');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('9', '青岛双星', '4');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('10', '港台明星', '1');
INSERT INTO `newslabel` (`id`, `name`, `pid`) VALUES ('11', '内地影视', '1');