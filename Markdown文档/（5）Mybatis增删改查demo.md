首先，项目的目录如下：<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-24/75658392.jpg)<br>
首先，我们的数据库mysql，SQL语句如下（也就是resource下的test.sql）
```
#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `student` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(20) NOT NULL ,
`age` INT NOT NULL , `score` DOUBLE NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;
```
maven管理项目，pom.xml文件管理依赖jar包：
```
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
既然数据库中有实体类，那我们也需要实体类Student:
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
使用mybatis的重要一步是配置，要不怎么知道使用哪一个数据库，有哪些mapper文件,主配置文件 mybatis.xml：
``` java
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
    </mappers>
</configuration>
```
jdbc_mysql.properties文件:
```
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/test
jdbc.user=root
jdbc.password=123456
```
日志系统的配置文件 log4j.properties:
``` 
log4j.prpp
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
在主配置文件里我们配置了去扫描mapper文件，那我们要实现的是对Student的增删改查等功能，Mapper.xml:
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="abc">
    <!-- parameterType可以省略不写 -->
    <insert id="insertStudent" parameterType="Student">
		insert into student(name,age,score) values(#{name},#{age},#{score})
	</insert>

    <insert id="insertStudentCacheId" parameterType="Student">
        insert into student(name,age,score) values(#{name},#{age},#{score})
        <!-- 指定结果类型resultType，keyProperty是属性，自动返回到属性id中，order是次序，after是指获取id是在于插入后 -->
        <selectKey resultType="int" keyProperty="id" order="AFTER">
            select @@identity
        </selectKey>
    </insert>

    <!-- 删除 -->
    <delete id="deleteStudentById" >
        delete from student where id=#{id}
        <!-- 这里的id放什么都可以，只是一个占位符，不表示什么 -->
    </delete>

    <update id="updateStudent">
		update student set name=#{name},age=#{age},score=#{score} where id=#{id}
	</update>

    <!-- 查询列表 -->
    <!-- 系统不知道返回封装为什么类型，所以要注明返回类型 -->
    <select id="selectAllStudents" resultType="Student">
        select id,name,age,score from student
        <!-- 如果数据库为tid，tname，tage，那么我们可以使用别名
        select tid id,tname name,tage age,tscore score from student -->
    </select>
    <!-- 通过id来查询学生 -->
    <select id="selectStudentById" resultType="Student">
		select * from student where id=#{xxx}

	</select>
    <!-- 模糊查询-->
    <!-- 不能写成'%#{name}%' -->
    <!-- 可以写成这样，也就是使函数拼接 select id,name,age,score from student where name like concat('%',#{xxx},'%') -->
    <!-- 也可以写成这样，‘’引起来的是写死的，而变量是不可以引起来的select id,name,age,score from student where name like '%' #{xxx} '%' -->
    <!-- '%' #{xxx} '%'中间必须有空格，要不就无效了 -->
    <select id="selectStudentsByName" resultType="Student">
        <!--最常用的（动态参数） select id,name,age,score from student where name like '%' #{name} '%' -->
        <!-- 下面的是字符串拼接 ，只能写value，了解即可，容易sql注入，执行效率低，不建议使用-->
        select id,name,age,score from student where name like '%${value}%'
    </select>
</mapper>
```
有了mapper文件还不够，我们需要定义接口与sql语句一一对应,IStudentDao.class：
```
package dao;
import bean.Student;
import java.util.List;
import java.util.Map;
public interface IStudentDao {
    // 增加学生
    public void insertStudent(Student student);
    // 增加新学生并返回id
    public void insertStudentCacheId(Student student);

    // 根据id删除学生
    public void deleteStudentById(int id);
    // 更新学生的信息
    public void updateStudent(Student student);

    // 返回所有学生的信息List
    public List<Student> selectAllStudents();
    // 返回所有学生的信息Map
    public Map<String, Object> selectAllStudentsMap();

    // 根据id查找学生
    public Student selectStudentById(int id);
    // 根据名字查找学生，模糊查询
    public List<Student>selectStudentsByName(String name);
}
```
接口的实现类如下：
sqlSession有很多方法，如果是插入一条数据需要使用insert();删除一条数据使用delete();更新一条数据使用update();如果查询返回数据的List使用SelectList()方法；如果返回查询多条数据的Map使用selectMap();如果查询一条数据，那么只需要使用selectOne()即可。
``` java
package dao;

import bean.Student;
import org.apache.ibatis.session.SqlSession;
import utils.MyBatisUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
    public void insertStudent(Student student) {
        //加载主配置文件
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            sqlSession.insert("insertStudent", student);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    public void insertStudentCacheId(Student student) {
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            sqlSession.insert("insertStudentCacheId", student);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    public void deleteStudentById(int id) {
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            sqlSession.delete("deleteStudentById", id);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    public void updateStudent(Student student) {
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            sqlSession.update("updateStudent", student);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    public List<Student> selectAllStudents() {
        List<Student> students ;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            students = sqlSession.selectList("selectAllStudents");
            //查询不用修改，所以不用提交事务
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return students;
    }

    public Map<String, Object> selectAllStudentsMap() {
        Map<String ,Object> map=new HashMap<String, Object>();
        /**
         * 可以写成Map<String ,Student> map=new HashMap<String, Student>();
         */
        try {
            sqlSession=MyBatisUtils.getSqlSession();
            map=sqlSession.selectMap("selectAllStudents", "name");
            //查询不用修改，所以不用提交事务
        } finally{
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
        return map;
    }

    public Student selectStudentById(int id) {
        Student student=null;
        try {
            sqlSession=MyBatisUtils.getSqlSession();
            student=sqlSession.selectOne("selectStudentById",id);
            sqlSession.commit();
        } finally{
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
        return student;
    }

    public List<Student> selectStudentsByName(String name) {
        List<Student>students=new ArrayList<Student>();
        try {
            sqlSession=MyBatisUtils.getSqlSession();
            students=sqlSession.selectList("selectStudentsByName",name);
            //查询不用修改，所以不用提交事务
        } finally{
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
        return students;
    }
}
```
在这里我们使用了一个自己定义的工具类，用来获取Sqlsession的实例：
``` java
package utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisUtils {
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
测试代码MyTest.class:
``` java
import bean.Student;
import dao.IStudentDao;
import dao.StudentDaoImpl;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Map;

public class MyTest {
	private IStudentDao dao;
	@Before
	public void Before(){
		dao=new StudentDaoImpl();
	}
    /**
     * 插入测试
     */
    @Test
    public void testInsert(){
        /**
         * 要是没有select id，这样是不会自动获取id的，id会一直为空
         */
        Student student=new Student("hello",14,94.6);
        System.out.println("插入前：student="+student);
        dao.insertStudent(student);
        System.out.println("插入后：student="+student);
    }
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
    /*
     * 测试删除
     *
     */
    @Test
    public void testdeleteStudentById(){
        dao.deleteStudentById(18);

    }
    /*
     * 测试修改，一般我们业务里面不这样写，一般是查询出来student再修改
     *
     */
    @Test
    public void testUpdate(){
        Student student=new Student("lallalalla",14,94.6);
        student.setId(21);
        dao.updateStudent(student);

    }
    /*
     * 查询列表
     *
     */
    @Test
    public void testselectList(){
        List<Student> students=dao.selectAllStudents();
        if(students.size()>0){
            for(Student student:students){
                System.out.println(student);
            }
        }
    }
    /*
     * 查询列表装成map
     *
     */
    @Test
    public void testselectMap(){
        Map<String,Object> students=dao.selectAllStudentsMap();
        // 有相同的名字的会直接替换掉之前查出来的，因为是同一个key
        System.out.println(students.get("helloworld"));
        System.out.println(students.get("1ADAS"));
    }
    /*
     * 通过id来查询student
     *
     */
    @Test
    public void testselectStudentById(){
        Student student=dao.selectStudentById(19);
        System.out.println(student);
    }
    /*
     * 通过模糊查询student的名字
     *
     */
    @Test
    public void testselectStudentByName(){
        List<Student>students=dao.selectStudentsByName("abc");
        if(students.size()>0){
            for(Student student:students)
                System.out.println(student);
        }

    }
}

```
至此这个demo就完成了，运行test的时候建议多跑几次插入再测试其他功能。                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        