我们下面需要改进的是别名，也趁这个机会介绍一下别名的作用。<br>
其实在我们实际开发中，大多数情况下，一个mapper.xml文件对应的是对一个对象的操作，当前的mapper如下：
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper 
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mapper1">
	<insert id="insertStudent" parameterType="bean.Student">
		insert into student(name,age,score) values(#{name},#{age},#{score})
	</insert>
</mapper>
```
我们可以看出parameterType有时候会写很长很长，每写一个sql我们就要使用parameterType传值或者使用返回类型，意思就是这个parameterType太长了，有没有什么办法可以让我们就写类名就可以了，其实是有的！！！那就是别名，mybatis可以让我们起一个别名给它，别名定义是在mybatis.xml主配置文件中。注意别名标签应该定义在<properties></properties>后面，在<environments></environments>前面，顺序不能颠倒。`<typeAliases></typeAliases>`这个标签里面可以定义很多别名<typeAlias/>
``` xml
<!-- 别名，对数据对象操作全名太长，需要使用别名 -->
<typeAliases>
    <typeAlias type="bean.Student" alias="Student"/>
</typeAliases>
```
我们在上面的别名中的意思是给bean包下Student这个类起了一个别名，名字叫Student，那么我们就可以使用了，很简单：
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper 
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mapper1">
	<insert id="insertStudent" parameterType="Student">
		insert into student(name,age,score) values(#{name},#{age},#{score})
	</insert>
</mapper>
```
但是要是bean里面有很多类，我们是不是要写很多别名呢？其实不用，我们可以观察到`<typeAliases></typeAliases>`这个标签下面有一个`<package/>`标签，他的作用就体现出来了。将指定的包中的类的简单类名当做别名。
``` xml
<typeAliases>
  	<!-- 配置一个类的别名 -->
  	<!-- <typeAlias type="com.aphysia.beans.Student" alias="Student"/> -->
	<!--直接使用类名即可，对于整个包的路径配置（别名），简单快捷 -->
  	<package name="bean"/>
</typeAliases>
```
#### 贴代码
在这里贴一下代码，代码结构如下：<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-24/7568510.jpg)<br>
bean包下的Student类：
``` java
package bean;

public class Student {
	private Integer id;
	private String name;
	private int age;
	private double score;
	public Student(){

    }
	public Student(String name, int age, double score) {
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
dao包下面的IStudentDao接口：
```
package dao;
import bean.Student;
public interface IStudentDao {
	public void insertStu(Student student);
}

```
dao包下的实现类：
```
import bean.Student;
import org.apache.ibatis.session.SqlSession;
import utils.MyBatisUtils;
public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
    public void insertStu(Student student) {
        //加载主配置文件
        try {
            sqlSession=MyBatisUtils.getSqlSession();
            sqlSession.insert("mapper1.insertStudent",student);
            sqlSession.commit();
        } finally{
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
    }
}
```
util包下面的工具类：
``` java
public class MyBatisUtils {

    /*	public SqlSession getSqlSession(){
            InputStream is;
            try {
                is = Resources.getResourceAsStream("mybatis.xml");
                return new SqlSessionFactoryBuilder().build(is).openSession();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }*/
    static private SqlSessionFactory sqlSessionFactory;

    static public SqlSession getSqlSession() {
        InputStream is;
        try {
            is = Resources.getResourceAsStream("mybatis.xml");
            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
            }
            return sqlSessionFactory.openSession();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
```
resource资源目录下mapper目录下的mapper.xml(mapper1.xml也一样内容，只是里面namespace不一样)：
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mapper1">
    <!--此处parameterType可以省略不写-->
    <insert id="insertStudent" parameterType="Student">
		insert into student(name,age,score) values(#{name},#{age},#{score})
	</insert>
</mapper>
```
jdbc_mysql.properties文件（jdbc_oracle.properties是空文件）：
```
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/test
jdbc.user=root
jdbc.password=123456
```
log4j.properties
```
log4j.prpp
log4j.rootLogger=DEBUG, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=[service] %d - %c -%-4r [%t] %-5p %c %x - %m%n
log4j.logger.java.sql.Statement = debug
log4j.logger.java.sql.PreparedStatement = debug
log4j.logger.java.sql.ResultSet =debug
```
主配置文件mybatis.xml:
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 配置数据库文件 -->
    <properties resource="jdbc_mysql.properties">

    </properties>
    <!-- 别名，对数据对象操作全名太长，需要使用别名 -->
    <typeAliases>
        <!--<typeAlias type="bean.Student" alias="Student"/>-->
        <!--直接使用类名即可，对于整个包的路径配置（别名），简单快捷 -->
        <package name="bean"/>
    </typeAliases>
    <!-- 配置运行环境 -->
    <!-- default 表示默认使用哪一个环境，可以配置多个，比如开发时的测试环境，上线后的正式环境等 -->
    <environments default="mysqlEM">
        <environment id="mysqlEM">
            <transactionManager type="JDBC">
            </transactionManager>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.user}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>
    <!-- 注册映射文件 -->
    <mappers>
        <mapper resource="mapper/mapper.xml"/>
        <mapper resource="mapper/mapper1.xml"/>
    </mappers>
</configuration>
```
test.sql
```
#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `student` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(20) NOT NULL ,
`age` INT NOT NULL , `score` DOUBLE NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;
```
测试文件MyTest.java:
``` 
public class MyTest {
	private IStudentDao dao;
	@Before
	public void Before(){
		dao=new StudentDaoImpl();
	}
	@Test
	public void testInsert(){
		Student student=new Student("1ADAS",23,100);
		dao.insertStu(student);
	}
}
```
Maven配置文件pom.xml:
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.test</groupId>
    <artifactId>test</artifactId>
    <version>1.0-SNAPSHOT</version>
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
</project>
```
至此，整个项目的代码结束。