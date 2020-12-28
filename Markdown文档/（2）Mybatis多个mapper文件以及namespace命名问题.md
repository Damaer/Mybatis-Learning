[TOC]

# 多个mapper文件以及namespace作用

要是多个mapper文件的时候怎么处理，namespace又是干什么用的呢
首先我们来看创建数据库语句：
``` sql
#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `student` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(20) NOT NULL ,
`age` INT NOT NULL , `score` DOUBLE NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;
```
在这里我们必须重新说一下Mybatis的运行大概过程：首先我们通过`Resources.getResourceAsStream("mybatis.xml")`读取到`mybatis.xml`这个文件，这个文件里面配置的都是整个项目与数据库相关的配置，比如运行的时候的数据库环境(连接哪一个数据库，数据库服务器的地址，用户名，密码)，或者是配置外部配置文件等，**最重要的是，这个文件注册了映射文件(mapper文件)**，那么我们使用`SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);`的时候，`sqlSessionFactory`回去读取`mybatis.xml`里面读取的配置文件，并且会逐一获取每一个配置文件读取到的`Mapper`映射文件，当我们使用`openSession()`获取到`sqlSession`的实例的时候，比如我们使用`sqlSession.insert("insertStudent",student);`，就会去查找每一个mapper里面的sql配置语句，也就是类似于下面这种：
```xml
<mapper namespace="mapper1">
	<insert id="insertStudent" parameterType="bean.Student">
		insert into student(name,age,score) values(#{name},#{age},#{score})
	</insert>
</mapper>
```


找到`id`一样的就可以，那么很多人会说，既然区分使用的是`id`，那我的mapper文件里面的namespace属性是干什么用的？

> 当我们有两个或者以上相同的`id`的时候，我们必须使用namespace进行区分，如果只有一个`mapper.xml`文件，那么我们`namespace`写什么都可以，在使用的时候，只需要：`sqlSession.insert("insertStudent",student);`就可以了，如果我们的id是相同的，那我们需要使用：`sqlSession.insert("mapper1.insertStudent",student);`在前面加上`namspace`。否则会出现以下错误，提示我们使用全称包括`namespace`，或者重新定义一个`id`。
> 总的来说,要么`id`不一样，可以直接用，要么`id`一样，但是`namespace`不一样，使用的时候加上`namespace`区分。否则会报以下错误：
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-24/5037409.jpg)


多个mapper文件，在在mybatis.xml文件里面需要注册两个文件：
```xml
  		<!-- 注册映射文件 -->
  		<mappers>
  			<mapper resource="mapper/mapper1.xml"/>
            <mapper resource="mapper/mapper2.xml"/>
  		</mappers>
```
使用时候加上namespace:
![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/blog/20201008213833.png)

**【作者简介】**：  
秦怀，公众号【**秦怀杂货店**】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。这个世界希望一切都很快，更快，但是我希望自己能走好每一步，写好每一篇文章，期待和你们一起交流。

此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~ 


![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/blog/20201012000828.png)