#### 多个mapper文件的时候怎么处理，namespace又是干什么用的呢<br>
首先我们来看创建数据库语句：
``` sql
#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `student` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(20) NOT NULL ,
`age` INT NOT NULL , `score` DOUBLE NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;
```
在这里我们必须重新说一下Mybatis的运行大概过程：首先我们通过`Resources.getResourceAsStream("mybatis.xml")`读取到mybatis.xml这个文件，这个文件里面配置的都是整个项目与数据库相关的配置，比如运行的时候的数据库环境(连接哪一个数据库，数据库服务器的地址，用户名，密码)，或者是配置外部配置文件等，**最重要的是，这个文件注册了映射文件**，那么我们使用`SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);`的时候，sqlSessionFactory回去读取mybatis.xml里面读取的配置文件，并且会逐一获取每一个配置文件读取到的Mapper映射文件，当我们使用openSession获取到sqlSession的实例的时候，比如我们使用`sqlSession.insert("insertStudent",student);`，就会去查找每一个mapper里面的sql配置语句，也就是类似于下面这种：
```
<mapper namespace="mapper1">
	<insert id="insertStudent" parameterType="bean.Student">
		insert into student(name,age,score) values(#{name},#{age},#{score})
	</insert>
</mapper>
```
找到id一样的就可以，那么很多人会说，既然区分使用的是id，那我的mapper文件里面的namespace属性是干什么用的？

> 当我们有两个或者以上相同的id的时候，我们必须使用namespace进行区分，如果只有一个mapper.xml文件，那么我们namespace写什么都可以，在使用的时候，只需要：`sqlSession.insert("insertStudent",student);`就可以了，如果我们的id是相同的，那我们需要使用：`mapper1.sqlSession.insert("insertStudent",student);`在前面加上namspace。否则会出现以下错误，提示我们使用全称包括namespace，或者重新定义一个id。
> 总的来说,要么id不一样，可以直接用，要么id一样，但是namespace不一样，使用的时候加上namespace区分。否则会报以下错误：
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-24/5037409.jpg)

