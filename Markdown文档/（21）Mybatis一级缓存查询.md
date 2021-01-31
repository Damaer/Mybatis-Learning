
注：代码已托管在`GitHub`上，地址是：`https://github.com/Damaer/Mybatis-Learning` ，项目是`mybatis-17-sqlsessionCache`，需要自取，需要配置maven环境以及`mysql`环境(`sql`语句在`resource`下的`test.sql`中)，觉得有用可以点个小星星。

`docsify`文档地址在：`https://damaer.github.io/Mybatis-Learning/#/`

查询缓存的使用，主要是为了提高查询访问速度，不用每次都从数据库查询数据，可以提高访问速度，而且可以减少数据库查询次数，减小数据库压力。
## 一级查询缓存
- 1.`mybati`s一级缓存是基于`org.apache.ibatis.cache.impl.PerpetualCache`类的`HashMap`本地缓存，作用域是`SqlSession`，也就是在同一个`sqlsession`中两次执行相同的`sql`查询语句，第一次执行完毕之后，就会将查询结果写入缓存中，第二次会从缓存里面直接获取数据，不需要去数据库查询。

- 2.**当一个`SqlSession`结束，这个`SqlSession`的以及缓存就不存在了，`mybatis`默认开启一级缓存，而且不可以关闭**

- 3.一级缓存存的时候是根据`sql`语句的id，不是根据`sql`的具体内容。


## 证明一级缓存的存在
在这里不放大部分代码，只放上核心代码，`sql`的接口如下：
```java
public interface IStudentDao {
    public Student selectStudentById(int id);
}
```

`mapper.xml`与之对应的`sql`语句：
```xml
	<!-- 通过id来查询学生 -->
	<select id="selectStudentById" resultType="Student">
		select * from student where id=#{xxx}
	
	</select>
```

单元测试：
```java
	@Test
	public void testselectStudentById(){

		// 第一次查询
		Student student=dao.selectStudentById(17);
		System.out.println(student);
		// 第二次查询
		Student student2=dao.selectStudentById(17);
		System.out.println(student2);
	}
```
结果,我们可以看到我们只执行了一次查询，第二次查询的时候直接走的一级缓存，没有对数据库进行查询，如果是对数据库进行查询，那么会有`sql`语句打印出来：
```bash
[service] 2018-07-21 09:48:22,534 - dao.IStudentDao.selectStudentById -1349 [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 09:48:22,635 - dao.IStudentDao.selectStudentById -1450 [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 09:48:22,677 - dao.IStudentDao.selectStudentById -1492 [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
Student [id=17, name=hello, age=14, score=94.6]
```
## 从缓存中读取数据的依据是sql的id还是sql本身？
我们此时有一个疑问，缓存到底是根据什么进行缓存的，那么我们需要做实验，写两个拥有不同的`id`但是`sql`完全一致的`sql`就知道结果了。下面是在同一个`namespace`中进行：
sql接口：
```java
public interface IStudentDao {
    public Student selectStudentById(int id);
    // 测试不同的id的sql
    public Student selectStudentById2(int id);
}
```
`mapper.xml`文件：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper 
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 把mapper的namespace改成类名的全名，那么直接调用接口的方法的时候才有可能却找到对应的mapper中的对应的方法 -->
<mapper namespace="dao.IStudentDao">

	<!-- 通过id来查询学生 -->
	<select id="selectStudentById" resultType="Student">
		select * from student where id=#{xxx}
	
	</select>

    <!-- 测试不同id的sql -->
    <select id="selectStudentById2" resultType="Student">
		select * from student where id=#{xxx}

	</select>
</mapper>
```

测试代码：
```java
    @Test
    public void testDiffereentId2(){

        // 第一次查询
        Student student=dao.selectStudentById(17);
        System.out.println(student);
        // 第二次查询，测试不同的id，同一个namespace
        Student student2=dao.selectStudentById2(17);
        System.out.println(student2);
    }
```

结果如下,我们可以看到里面执行了两次`sql`，那么就证明了**一级缓存不是依据`sql`本身来的，而是依据`sql`的`id`缓存的**：
```bash
[service] 2018-07-21 10:26:32,844 - dao.IStudentDao.selectStudentById -957  [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 10:26:32,954 - dao.IStudentDao.selectStudentById -1067 [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 10:26:32,989 - dao.IStudentDao.selectStudentById -1102 [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
[service] 2018-07-21 10:26:32,990 - dao.IStudentDao.selectStudentById2 -1103 [main] DEBUG dao.IStudentDao.selectStudentById2  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 10:26:32,991 - dao.IStudentDao.selectStudentById2 -1104 [main] DEBUG dao.IStudentDao.selectStudentById2  - ==> Parameters: 17(Integer)
[service] 2018-07-21 10:26:32,996 - dao.IStudentDao.selectStudentById2 -1109 [main] DEBUG dao.IStudentDao.selectStudentById2  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
```
## 不同的`namespace`下的相同`id`呢？
不同的`namespace`,就算是同一个`id`，那么一级缓存也是**不生效**的，因为`sql`缓存的时候是根据`namespace`不同来区分的。
两个sql接口如下：
```java
public interface IStudentDao {
    public Student selectStudentById(int id);
}

public interface IStudentDao2 {
    //不同的namespace下的相同id的测试
    public Student selectStudentById(int id);
}
```
两个`mapper`文件如下:
```xml
<?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE mapper
                PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
                "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
        <!-- 把mapper的namespace改成类名的全名，那么直接调用接口的方法的时候才有可能却找到对应的mapper中的对应的方法 -->
<mapper namespace="dao.IStudentDao">
<!-- 通过id来查询学生 -->
<select id="selectStudentById" resultType="Student">
	select * from student where id=#{xxx}
</select>
</mapper>
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 把mapper的namespace改成类名的全名，那么直接调用接口的方法的时候才有可能却找到对应的mapper中的对应的方法 -->
<mapper namespace="dao.IStudentDao2">
    <!-- 通过id来查询学生 -->
    <select id="selectStudentById" resultType="Student">
		select * from student where id=#{xxx}
	</select>

</mapper>
```

单元测试如下：
```java
    @Test
    public void testDiffereentNamespaceSameId(){

        // 第一次查询
        Student student=dao.selectStudentById(17);
        System.out.println(student);
        // 第二次查询，测试相同的id，不同的namespace
        IStudentDao2 dao2= sqlSession.getMapper(IStudentDao2.class);
        Student student2=dao2.selectStudentById(17);
        System.out.println(student2);
    }
```
结果如下,我们可以看到执行了两次`sql`。也就证明了一级缓存根据的是同一个`namespace`下的同一个`id`，就算是同一个`id`，但是`namespace`也不会生效，因为一级缓存是根据`namespace`来区分存放的。：
```bash
[service] 2018-07-21 10:37:36,916 - dao.IStudentDao.selectStudentById -1545 [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 10:37:37,154 - dao.IStudentDao.selectStudentById -1783 [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 10:37:37,194 - dao.IStudentDao.selectStudentById -1823 [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
[service] 2018-07-21 10:37:37,202 - dao.IStudentDao2.selectStudentById -1831 [main] DEBUG dao.IStudentDao2.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 10:37:37,204 - dao.IStudentDao2.selectStudentById -1833 [main] DEBUG dao.IStudentDao2.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 10:37:37,210 - dao.IStudentDao2.selectStudentById -1839 [main] DEBUG dao.IStudentDao2.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
```
## 增删改对一级缓存的影响

**增删改操作无论是否提交sqlsession.commit()，都会清空以及查询缓存，让查询缓存再次从DB中查询。**

`sql`请求的接口如下：
```java
public interface IStudentDao {
    public Student selectStudentById(int id);
    // 测试不同的id的sql
    public Student selectStudentById2(int id);


    // 增加学生
    public void insertStudent(Student student);
    // 根据id删除学生
    public void deleteStudentById(int id);
    // 更新学生的信息
    public void updateStudent(Student student);
}
```
`mapper.xml`文件：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 把mapper的namespace改成类名的全名，那么直接调用接口的方法的时候才有可能却找到对应的mapper中的对应的方法 -->
<mapper namespace="dao.IStudentDao">

    <!-- 通过id来查询学生 -->
    <select id="selectStudentById" resultType="Student">
		select * from student where id=#{xxx}

	</select>

    <!-- 测试不同id的sql -->
    <select id="selectStudentById2" resultType="Student">
		select * from student where id=#{xxx}
</select>

    <insert id="insertStudent" parameterType="Student">
		insert into student(name,age,score) values(#{name},#{age},#{score})
	</insert>
    <!-- 删除 -->
    <delete id="deleteStudentById">
        delete from student where id=#{id}
        <!-- 这里的id放什么都可以，只是一个占位符，不表示什么 -->
    </delete>

    <update id="updateStudent">
		update student set name=#{name},age=#{age},score=#{score} where id=#{id}
	</update>
</mapper>
```
单元测试：
```java
    public void test03(){

        // 第一次查询
        Student student=dao.selectStudentById(17);
        System.out.println(student);
        //插入学生
        Student student1 = new Student("12112",12,21.6);
        //dao.insertStudent(student1);
        dao.updateStudent(student1);
        student=dao.selectStudentById(17);
        System.out.println(student);
    }
```
当我们执行第一次查询之后，执行一次插入操作的时候，我们发现一级缓存已经被更新了：
```bash
[service] 2018-07-21 13:07:27,136 - dao.IStudentDao.selectStudentById -1059 [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 13:07:27,247 - dao.IStudentDao.selectStudentById -1170 [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 13:07:27,288 - dao.IStudentDao.selectStudentById -1211 [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
[service] 2018-07-21 13:07:27,289 - dao.IStudentDao.insertStudent -1212 [main] DEBUG dao.IStudentDao.insertStudent  - ==>  Preparing: insert into student(name,age,score) values(?,?,?) 
[service] 2018-07-21 13:07:27,291 - dao.IStudentDao.insertStudent -1214 [main] DEBUG dao.IStudentDao.insertStudent  - ==> Parameters: 12112(String), 12(Integer), 21.6(Double)
[service] 2018-07-21 13:07:27,295 - dao.IStudentDao.insertStudent -1218 [main] DEBUG dao.IStudentDao.insertStudent  - <==    Updates: 1
[service] 2018-07-21 13:07:27,295 - dao.IStudentDao.selectStudentById -1218 [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 13:07:27,295 - dao.IStudentDao.selectStudentById -1218 [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 13:07:27,302 - dao.IStudentDao.selectStudentById -1225 [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
```

当我们执行第一次查询之后，再执行一次更新操作，就算更新的不是查询的数据，但是属于同一张表,一级缓存同样被更新：
```bash
[service] 2018-07-21 23:43:28,073 - dao.IStudentDao.selectStudentById -1081 [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 23:43:28,202 - dao.IStudentDao.selectStudentById -1210 [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 23:43:28,236 - dao.IStudentDao.selectStudentById -1244 [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
[service] 2018-07-21 23:43:28,239 - dao.IStudentDao.updateStudent -1247 [main] DEBUG dao.IStudentDao.updateStudent  - ==>  Preparing: update student set name=?,age=?,score=? where id=? 
[service] 2018-07-21 23:43:28,241 - dao.IStudentDao.updateStudent -1249 [main] DEBUG dao.IStudentDao.updateStudent  - ==> Parameters: 12112(String), 12(Integer), 21.6(Double), 18(Integer)
[service] 2018-07-21 23:43:28,246 - dao.IStudentDao.updateStudent -1254 [main] DEBUG dao.IStudentDao.updateStudent  - <==    Updates: 1
[service] 2018-07-21 23:43:28,246 - dao.IStudentDao.selectStudentById -1254 [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 23:43:28,246 - dao.IStudentDao.selectStudentById -1254 [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 23:43:28,251 - dao.IStudentDao.selectStudentById -1259 [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
```

当我们执行一次查询操作之后，执行一次删除操作，那么一级缓存同样会被更新：
```bash
[service] 2018-07-21 23:44:49,296 - dao.IStudentDao.selectStudentById -1172 [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 23:44:49,457 - dao.IStudentDao.selectStudentById -1333 [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 23:44:49,504 - dao.IStudentDao.selectStudentById -1380 [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
[service] 2018-07-21 23:44:49,505 - dao.IStudentDao.deleteStudentById -1381 [main] DEBUG dao.IStudentDao.deleteStudentById  - ==>  Preparing: delete from student where id=? 
[service] 2018-07-21 23:44:49,505 - dao.IStudentDao.deleteStudentById -1381 [main] DEBUG dao.IStudentDao.deleteStudentById  - ==> Parameters: 18(Integer)
[service] 2018-07-21 23:44:49,508 - dao.IStudentDao.deleteStudentById -1384 [main] DEBUG dao.IStudentDao.deleteStudentById  - <==    Updates: 1
[service] 2018-07-21 23:44:49,509 - dao.IStudentDao.selectStudentById -1385 [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select * from student where id=? 
[service] 2018-07-21 23:44:49,509 - dao.IStudentDao.selectStudentById -1385 [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 17(Integer)
[service] 2018-07-21 23:44:49,517 - dao.IStudentDao.selectStudentById -1393 [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 1
Student [id=17, name=hello, age=14, score=94.6]
```

【作者简介】：
秦怀，公众号【秦怀杂货店】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。求个 赞 和 在看 ，对我，是莫大的鼓励和认可，让我更有动力持续写出好文章。
此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~
![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/20210107005121.png)

