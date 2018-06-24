#### 1.框架是什么
> * 框架（Framework）是整个或部分系统的可重用设计，表现为一组抽象构件及构件实例间交互的方法;另一种定义认为，框架是可被应用开发者定制的应用骨架。前者是从应用方面而后者是从目的方面给出的定义。
> * 一个框架是一个**可复用**的设计构件，它规定了应用的**体系结构**，阐明了整个设计、协作构件之间的依赖关系、责任分配和控制流程，表现为一组抽象类以及其实例之间协作的方法，它为构件复用提供了上下文(Context)关系。因此构件库的大规模**重用**也需要框架。
> * 个人理解：框架最重要的是把我们常用的，可以重复使用的功能抽象出来，不需要我们去重复写，我们只需要调用，或者按照规定配置，按照规则使用就可以了。这样的缺点是很多时候我们不知道为什么可以这样子使用，里面实现的细节被屏蔽掉了，这也是很多初学者很懵逼的地方，这时候追根问底就有一定必要性了。

#### 2.Mybatis的介绍
Mybatis本来是Apache的一个开源项目iBatis，这个项目2010年由apache迁移到了google，更名为Mybatis，2013年正式迁移到Gihub。<br>
Mybatis是一个java中一个持久层的框架，在里面封装了jdbc操作，如果还不了解java如何使用jdbc访问数据库，[那么可以查看这篇文章](https://blog.csdn.net/Aphysia/article/details/80465635)，封装使开发者只需要把精力放在开发sql语句上，不用去注册驱动，创建Connection，配置Statement，自己写代码管理事物等等。<br>
Mybatis通过xml或者注解的方式将需要执行的statement配置好，通过映射将java对象与sql中的动态参数一起生成最终的sql语句，执行完之后返回对象，其中也是映射的结果。

#### 3.Mybatis和Hibernate对比
1.Hibernate是全自动的ORM框架，也就是完全实现了POJO和数据库表之间的映射，会自动生成SQL。但是Mybatis不会自动生成，SQL还是需要自己写，但是映射关系框架会自动处理，这样一个好处就是可以看得到SQL，很多时候系统自动生成SQL并不是高效的，我们有时候需要优化SQL，或者一些复杂的查询可能自动化的很难做到，缺点就是需要花时间写。<br>
2.使用XML文件进行配置，分离了了sql与代码，这样比较容易维护。
3.Mybatis是一个轻量级的框架。学习成本比Hibernate低很多，jar包依赖也很少，上手比较快。

#### 4.Mybatis的结构图：
<center>
<img src="http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-24/27548139.jpg" width="60%" height="60%" />
</center>
<br>

> Mybatis的运行机制：我们通过配置Mybatis.xml(里面配置好数据库，需要扫描的mapper.xml文件等)，程序会自动扫描配置好的mapper文件，当我们请求一个接口（请求数据库），接口会直接映射到对应的sql标签，同时将我们所写的配置文件读取并将数据库字段与对象属性匹配（这也是映射，如果不一致，需要自己手写映射关系），将sql参数传进去，然后执行相关的sql，返回时又做了一次映射，把对象返回给我们。当然，这么描述是很表面的，因为mybatis还有事务，缓存等方面，以上只是大概。
<center>
<img src="http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-23/45373379.jpg" width="50%" height="50%" />
</center>
<br>

#### 5.IDEA创建第一个程序
这里我们会使用idea创建项目，如果maven没有配置好，请参考：https://blog.csdn.net/aphysia/article/details/80363684
##### 5.1创建mysql数据库

``` mysql
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE TABLE `student` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(20) NOT NULL , 
`age` INT NOT NULL , `score` DOUBLE NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM; 
```
##### 5.2 使用idea创建项目(Maven)
项目结构图（`bean`下面放的类对应我们的数据库里面的`student`表，也就是它的一个实体类，`dao`包下面放着我们的数据库的操作，`resources`下面放着我们的xml或者各种资源）:<br>
<center>
<img src="http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-24/8080643.jpg" width="300" height="400" />
</center>
<br>
new --> Project -->点击Maven<br>
<center>
<img src="http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-24/37574174.jpg" width="70%" height="50%" />
</center>
<br>
点击next,GroupId,ArtifactId可以自己指定，点击下一步<br>
<center>
<img src="http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-24/62397112.jpg" width="70%" height="50%" />
</center>
<br>
自己指定project name（项目名），点击finish
<center>
<img src="http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-24/68142088.jpg" width="70%" height="50%" />
</center>
<br>
往pom.xml里面添加依赖,每一个<dependency></dependency>之间都是一种依赖包，选中项目右键--> Maven --> Reimport,这样就可以下载我们所需要的依赖了：

```
    <dependencies>
        <!-- mybatis核心包 -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.3.0</version>
        </dependency>
        <!-- mysql驱动包 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.29</version>
        </dependency>
        <!-- junit测试包 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>
        <!-- 日志文件管理包 -->
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version> 
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.12</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.12</version>
        </dependency>
    </dependencies>
```
为了能实现日志的打印功能，我们在pom.xml文件中已经引入了<dependency>标签，在这里还需要在resources文件夹下新建配置`log4j.properties`文件,具体配置代表什么意思，可以参考[log4j 与log4j2详解](https://blog.csdn.net/Aphysia/article/details/80470163)
> **log4j.properties**

```
log4j.rootLogger=DEBUG, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=[service] %d - %c -%-4r [%t] %-5p %c %x - %m%n

#log4j.appender.R=org.apache.log4j.DailyRollingFileAppender    
#log4j.appender.R.File=../logs/service.log    
#log4j.appender.R.layout=org.apache.log4j.PatternLayout    
#log4j.appender.R.layout.ConversionPattern=[service] %d - %c -%-4r [%t] %-5p %c %x - %m%n    

#log4j.logger.com.ibatis = debug    
#log4j.logger.com.ibatis.common.jdbc.SimpleDataSource = debug    
#log4j.logger.com.ibatis.common.jdbc.ScriptRunner = debug    
#log4j.logger.com.ibatis.sqlmap.engine.impl.SqlMapClientDelegate = debug    
#log4j.logger.java.sql.Connection = debug    
log4j.logger.java.sql.Statement = debug
log4j.logger.java.sql.PreparedStatement = debug
log4j.logger.java.sql.ResultSet =debug
```
配置好这些之后，前面结构图上面写到有一个mybatis.xml文件，里面配置了运行的环境（关于数据库的连接），连接的数据库可以配置多个，但是必须指定使用哪一个，这样做的原因的世界在xml文件进行修改不需要重新编译，更换数据库比较简单,除此之外，里面还需要配置mapper.xml,也就是映射文件，我们要告诉它，我们将sql配置写在哪个文件。
> **mybatis.xml**

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
  		<!-- 配置运行环境 -->
  		<!-- default 表示默认使用哪一个环境，可以配置多个，比如开发时的测试环境，上线后的正式环境等 -->
  		<environments default="mysqlEM">  		
  			<environment id="mysqlEM">
  				<transactionManager type="JDBC">		
  				</transactionManager>
  				<dataSource type="POOLED">
  					<property name="driver" value="com.mysql.jdbc.Driver"/>
  					<property name="url" value="jdbc:mysql://127.0.0.1:3306/test"/>
  					<property name="username" value="root"/>
  					<property name="password" value="123456"/>
  				</dataSource>
  			</environment>
  			<environment id="testEM">
  				<transactionManager type="JDBC">
  				</transactionManager>
  				<dataSource type="POOLED">
  					<property name="driver" value="com.mysql.jdbc.Driver"/>
  					<property name="url" value="jdbc:mysql://127.0.0.1:3306/test"/>
  					<property name="username" value="root"/>
  					<property name="password" value="123456"/>
  				</dataSource>
  			</environment>
  		</environments>
  		<!-- 注册映射文件 -->
  		<mappers>
  			<mapper resource="mapper.xml"/>
  		</mappers>
  </configuration>
```
配置好mybatis.xml文件我们就需要写sql语句，根据上面的mybatis.xml,我们需要写mapper.xml文件,下面的namespace现在可以随意命名，因为只有一个mapper.xml文件,sql标签的id没有重复，执行时就是根据id来查找的，同时这里parameterType对应的是参数类型，类型要写带完整路径名的类：
> **mapper.xml**

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper 
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="abc">
	<insert id="insertStudent" parameterType="bean.Student">
		insert into student(name,age,score) values(#{name},#{age},#{score})
	</insert>
</mapper>
```
在bean包下面创建与数据库对应的Student类【在bean包下】（这里先把属性名字和数据库的字段名一致，如果不一致需要自己写映射）,注意里面的方法我们需要实现set和get方法，这个在IDEA里面，打开当前类，右键--> Gernarate -->setter and getter全选就可以生成。
> **Student.class**

``` java
package bean;
public class Student {
    // id属性
	private Integer id;
	// 名字属性
	private String name;
	// 年龄属性
	private int age;
	//分数属性
	private double score;
	// 构造方法，除了id，因为我们的id在数据库中是自增的
	public Student( String name, int age, double score) {
		super();
		this.name = name;
		this.age = age;
		this.score = score;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", age=" + age
				+ ", score=" + score + "]";
	}
	
}

```
在这里我们需要写一个接口，来表示操作学生信息，先写一个插入学生信息的接口，那么肯定是传一个学生对象进去。
> **IStduent.class**

``` java
package dao;
import bean.Student;
public interface IStudentDao {
	public void insertStu(Student student);
}

```
下面就是接口的实现类（重点）：
> **StudentDaoImpl.class**

``` java
package dao;
import java.io.IOException;
import java.io.InputStream;
import bean.Student;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
public class StudentDaoImpl implements IStudentDao {
    // 实现插入的接口方法
	public void insertStu(Student student) {
		try {
			InputStream inputStream;
		    // 读取配置信息的文件
			inputStream = Resources.getResourceAsStream("mybatis.xml");
			// 由于这个文件里面配置了mapper.xml,框架会帮我们扫描这些mapper.xml,下面是初始化一个SqlSessionFactory对象
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
			// 工厂会获取一个SqlSession对象
			SqlSession sqlSession=sqlSessionFactory.openSession();
			// 通过这个对象可以查找之前mapper.xml里面配置好的sql的id与 `insertStudent`相等的，执行对应的sql
			sqlSession.insert("insertStudent",student);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
}

```
测试方法,如果不了解Junit测试请参考[Junit测试详解](https://blog.csdn.net/aphysia/article/details/80368980)：
> **MyTest.class**

``` java
import bean.Student;
import dao.IStudentDao;
import dao.StudentDaoImpl;
import org.junit.Before;
import org.junit.Test;
public class MyTest {
	private IStudentDao dao;
	@Before
	public void Before(){
		dao=new StudentDaoImpl();
	}
	@Test
	public void testInsert(){
		Student student=new Student("1ADAS",23,94.6);
		dao.insertStu(student);
	}
}
```
测试的结果，从最后三行我们可以看到执行的sql语句，以及参数等：<br>
<center>
<img src="http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-24/90815593.jpg" width="800" height="400" />
</center>
<br>

##### 5.3 使用eclips，MyEclipse创建项目(Maven)
> 区别不大，直接创建java Project，如果下面没有lib这个文件，就要新建一个，然后把需要的包导（复制粘贴）进去，选中lib下面所有的包，右键-->Build Path--> Add to Path<br>

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-24/4526668.jpg)<br>
> 下面是myEclipse下面的结构图，代码没有改变，需要自己下载相关的包
<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-24/48461912.jpg)<br>

> * 声明：这样的写法不是最好的，但是这是初学者最容易接受的写法，后面我们会慢慢的精简，我们到时候不需要写接口的实现类，同时会把关于sqlSessionFactory相关的操作抽取出来成为一个工具类，就不用写那么多相同的代码了。


