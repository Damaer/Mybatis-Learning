很多时候我们有这样的需求，数据库的字段名与实体类的属性名不一致，这个时候我们需要怎么做呢？有两种解决方案，第一种：直接在查询的时候使用别名，将别名设置成与实体类的属性名一致。第二种：使用resultType，自己定义映射关系。
整个项目的目录如下：<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-26/27469138.jpg)<br>
首先，我们需要搭建数据库mysql环境(test.sql),id我们写成了sid,name我们写成了sname,age我们写成了sage：
``` sql
#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `student` ( `sid` INT NOT NULL AUTO_INCREMENT , `sname` VARCHAR(20) NOT NULL ,
`sage` INT NOT NULL , `score` DOUBLE NOT NULL , PRIMARY KEY (`sid`)) ENGINE = MyISAM;
```
Student.class实体类：
``` java
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
pom.xml:
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
主配置文件mybatis.xml:
``` xml
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
数据库配置文件(jdbc_mysql.properties)：
```
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/test
jdbc.user=root
jdbc.password=123456
```
日志配置文件 log4j.prperties:
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
使用到的工具类（MyBatisUtils.java）：
```
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
接口定义（IStudentDao.java）：
```
public interface IStudentDao {
    // 返回所有学生的信息List
    public List<Student> selectAllStudents();
    // 根据id查找学生
    public Student selectStudentById(int id);
}
```
接口实现类(StudentDaoImpl.class)：
```
public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
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
}
```
**最主要的mapper文件：**<br>
可以直接使用别名：
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="abc">
    <select id="selectAllStudents" resultType="Student">
        select sid as id,sname as name,sage as age,score from student
    </select>
    <select id="selectStudentById" resultType="Student">
		select sid as id,sname as name,sage as age,score from student where sid=${value}
	</select>
</mapper>
```
或者可以自己定义映射：
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="abc">
    <resultMap id="StudentMapper" type="Student">
        <id column="sid" property="id"/>
        <result column="sname" property="name"/>
        <result column="sage" property="age"/>
    </resultMap>
    <select id="selectAllStudents" resultMap="StudentMapper">
        select sid as id,sname as name,sage as age,score from student
    </select>
    <select id="selectStudentById" resultMap="StudentMapper">
		select sid as id,sname as name,sage as age,score from student where sid=${value}
	</select>
</mapper>
```
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-26/93775790.jpg)<br>
需要注意的点：
> * <resultMap></resultMap>有一个id属性，这个是在其他地方使用的时候的id
> * Type - 实体类，可以写别名，要不就要写带全路径的类名
> * id - 标签是为了标记出作为 ID 的结果可以帮助提高整体性能
> * result – 注入到字段或 JavaBean 属性的普通结果
> * association – 一个复杂类型的关联;许多结果将包装成这种类型嵌套结果映射 –  关联可以指定为一个 resultMap 元素，或者引用一个
> * collection – 一个复杂类型的集合
嵌套结果映射 – 集合可以指定为一个 resultMap 元素，或者引用一个
> * discriminator – 使用结果值来决定使用哪个 resultMap
case – 基于某些值的结果映射
嵌套结果映射 – 一个 case 也是一个映射它本身的结果,因此可以包含很多相 同的元素，或者它可以参照一个外部的 resultMap。
> 如果对象名与属性名一致，我们可以不把它写入<resultMap></resultMap>

测试类MyTest.class:
``` java
public class MyTest {
	private IStudentDao dao;
	@Before
	public void Before(){
		dao=new StudentDaoImpl();
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
     * 通过id来查询student
     *
     */
    @Test
    public void testselectStudentById(){
        Student student=dao.selectStudentById(1);
        System.out.println(student);
    }

}

```