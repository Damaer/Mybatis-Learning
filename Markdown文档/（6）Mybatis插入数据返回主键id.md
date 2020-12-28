
> 代码直接放在Github仓库【https://github.com/Damaer/Mybatis-Learning/tree/master/mybatis-05-CURD 】  
需要声明的是：此`Mybatis`学习笔记，是从原始的`Mybatis`开始的，而不是整合了其他框架（比如`Spring`）之后，个人认为，这样能对它的功能，它能帮我们做什么，有更好的理解，后面再慢慢叠加其他的功能。


我们知道很多时候我们有一个需求，我们需要把插入数据后的id返回来，以便我们下一次操作。

其实一开始的思路是我插入之后，再执行一次select，根据一个唯一的字段来执行`select`操作，但是`Student`这个类如果插入后再根据名字或者年龄查出来，这根本就是不可行的！！！重名与同年龄的人一定不少。
我们的测试方法如下,我们可以看到插入前是没有值的，插入后就有了值：
``` java
/**
 * 测试插入后获取id
 */
@Test
public void testinsertStudentCacheId(){
    Student student=new Student("helloworld",17,85);
    System.out.println("插入前：student="+student);
    dao.insertStudentCacheId(student);
    System.out.println("插入后：student="+student);
}
```

# useGeneratedKeys 设置主键自增
``` xml
    <insert id="insertStudentCacheId" useGeneratedKeys="true" keyProperty="id" parameterType="Student">
        insert into student(name,age,score) values(#{name},#{age},#{score})
    </insert>
```
需要注意的点：
- 1.`useGeneratedKeys="true"`表示设置属性自增
- 2.`keyProperty="id"`设置主键的字段
- 3.`parameterType="Student"`设置传入的类型
- 4.注意：虽然有返回类型，但是我们不需要手动设置返回的类型，这个是由框架帮我们实现的，所以对应的接口方法也是没有返回值的,会修改我们插入的对象，设置id值。
- 5.实体类中id属性字段一定需要set以及get方法

![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/blog/20201205213916.png)

#### 使用selectKey 查询主键
``` xml
    <insert id="insertStudentCacheId" parameterType="Student">
        insert into student(name,age,score) values(#{name},#{age},#{score})
        <!-- 指定结果类型resultType，keyProperty是属性，自动返回到属性id中，order是次序，after是指获取id是在于插入后 -->
        <selectKey resultType="int" keyProperty="id" order="AFTER">
            select @@identity
        </selectKey>
    </insert>
```
或者写成：
``` xml
    <insert id="insertStudentCacheId" parameterType="Student">
        insert into student(name,age,score) values(#{name},#{age},#{score})
        <!-- 指定结果类型resultType，keyProperty是属性，自动返回到属性id中，order是次序，after是指获取id是在于插入后 -->
        <selectKey resultType="int" keyProperty="id" order="AFTER">
            select LAST_INSERT_ID()
        </selectKey>
    </insert>
```
两种方式的结果：
[](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-25/94139460.jpg)

注意要点：
- 1.最外层的`<insert></insert>`没有返回属性`（resultType）`，但是里面的`<selectKey></selectKey>`是有返回值类型的。
- 2.`order="AFTER"`表示先执行插入，之后才执行`selectkey`语句的。
- 3.`select @@identity`和`select LAST_INSERT_ID()`都表示选出刚刚插入的最后一条数据的id。
- 4.实体类中id属性字段一定需要set以及get方法
- 5.此时，接口中仍不需要有返回值，框架会自动将值注入到我们`insert`的那个对象中，我们可以直接使用就可以了。

其实，我们的接口中可以有返回值，但是这个返回值不是id,而是表示**插入后影响的行数**，此时sql中仍和上面一样，不需要写返回值。
``` xml
<insert id="insertStudentCacheIdNoReturn" parameterType="Student">
        insert into student(name,age,score) values(#{name},#{age},#{score})
        <!-- 指定结果类型resultType，keyProperty是属性，自动返回到属性id中，order是次序，after是指获取id是在于插入后 -->
        <selectKey resultType="int" keyProperty="id" order="AFTER">
            select LAST_INSERT_ID()
        </selectKey>
    </insert>
```
接口中：
``` java
// 增加新学生并返回id返回result
public int insertStudentCacheId(Student student);
```
接口的实现类：
``` java
public int  insertStudentCacheId(Student student) {
        int result;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            result =sqlSession.insert("insertStudentCacheId", student);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return result;
    }
```
Test中：
``` java
public void testinsertStudentCacheId(){
        Student student=new Student("helloworld",17,101);
        System.out.println("插入前：student="+student);
        int result = dao.insertStudentCacheId(student);
        System.out.println(result);
        System.out.println("插入后：student="+student);
    }
```
结果证明：result的值为1，表示插入了一行，查看数据库，确实插入了数据。


PS：如果无法创建连接，需要把`Mysql`的jar包升级：
```xml
        <!-- mysql驱动包 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.21</version>
        </dependency>
```

如果报以下的错误，那么需要将&改成转义后的符号`&amp;`：
``` sql
org.apache.ibatis.exceptions.PersistenceException: 
### Error building SqlSession.
### Cause: org.apache.ibatis.builder.BuilderException: Error creating document instance.  Cause: org.xml.sax.SAXParseException; lineNumber: 14; columnNumber: 107; 对实体 "serverTimezone" 的引用必须以 ';' 分隔符结尾。
```
在xml里面配置需要转义，不在xml文件里面配置则不需要
```xml
<property name="url" value="jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&amp;serverTimezone=UTC"/>
```



**【作者简介】**：  
秦怀，公众号【**秦怀杂货店**】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。这个世界希望一切都很快，更快，但是我希望自己能走好每一步，写好每一篇文章，期待和你们一起交流。

此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~ 


![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/blog/20201012000828.png)