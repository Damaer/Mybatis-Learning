注：代码已托管在`GitHub`上，地址是：`https://github.com/Damaer/Mybatis-Learning` ，项目是`mybatis-15-oneself-many2many`，需要自取，需要配置maven环境以及`mysql`环境(`sql`语句在`resource`下的`test.sql`中)，觉得有用可以点个小星星。

`docsify`文档地址在：`https://damaer.github.io/Mybatis-Learning/#/`


所谓多对多查询，就是类似于:一个学生可以选多门课程，一门可能可以有多个学生。

数据表设计如下：

![](https://img-blog.csdnimg.cn/img_convert/498afa96dd48fd2157a5fdcda150bc37.png)

![](https://img-blog.csdnimg.cn/img_convert/cf7e9233abd5e7a6f4af943d063cb470.png)

![](https://img-blog.csdnimg.cn/img_convert/04441576382dd07fa2dbffe971bc690c.png)

与数据库对应的实体类`Course.java`,值得注意的是，`toString()`方法里面我们没有加入`students`属性，这是因为在`Student`的`tostring()`方法里面已经加入我们的`Course`这个类了，如果这里加入就会死循环，只加一个就可以了。
```java
import java.util.Set;

public class Course {
	private Integer cid;
	private String cname;
	private Set<Student>students;
	@Override
	public String toString() {
		return "Course [cid=" + cid + ", cname=" + cname +"]";
	}
	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public Set<Student> getStudents() {
		return students;
	}
	public void setStudents(Set<Student> students) {
		this.students = students;
	}
}

```
`Student.java`:
```java
import java.util.Set;

public class Student {
	private Integer sid;
	private String sname;
	private Set<Course>courses;
	public Integer getSid() {
		return sid;
	}
	public void setSid(Integer sid) {
		this.sid = sid;
	}
	public String getSname() {
		return sname;
	}
	public void setSname(String sname) {
		this.sname = sname;
	}
	public Set<Course> getCourses() {
		return courses;
	}
	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}
	@Override
	public String toString() {
		return "Student [sid=" + sid + ", sname=" + sname + ", courses="
				+ courses + "]";
	}
}

```
定义的接口部分：
```java
public interface IStudentDao {
	Student selectStudentById(int id);
}
```
`mapper.xml`文件,查询的时候，查的是三张表，通过`sid=studentId and cid=courseId and sid=#{xxx}`关联起来。

对结果做了一个映射，除了主键以及`sname`,`courses`属性做了集合映射，也就是对`Course`类型进行映射。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="dao.IStudentDao">
    <resultMap type="Student" id="studentMapper">
        <id column="sid" property="sid"/>
        <result column="sname" property="sname"/>
        <collection property="courses" ofType="Course">
            <id column="cid" property="cid"/>
            <result column="cname" property="cname"/>
        </collection>
    </resultMap>
    <select id="selectStudentById" resultMap="studentMapper">
		select sid,sname,cid,cname
		from student,middle,course
		where sid=studentId and cid=courseId and sid=#{xxx}
	</select>
</mapper>
```
单元测试类：
```java
public class MyTest {
  private IStudentDao dao;
  private SqlSession sqlSession;
  @Before
  public void Before(){
    sqlSession=MyBatisUtils.getSqlSession();
    dao=sqlSession.getMapper(IStudentDao.class);
  }
  @Test
  public void TestselectMinisterById(){
    Student student=dao.selectStudentById(1);
    System.out.println(student);
  }
  @After
  public void after(){
    if(sqlSession!=null){
      sqlSession.close();
    }
  }
}
```
结果：
```bash
[service] 2018-07-16 20:25:37,846 - dao.IStudentDao.selectStudentById -843  [main] DEBUG dao.IStudentDao.selectStudentById  - ==>  Preparing: select sid,sname,cid,cname from student,middle,course where sid=studentId and cid=courseId and sid=? 
[service] 2018-07-16 20:25:37,894 - dao.IStudentDao.selectStudentById -891  [main] DEBUG dao.IStudentDao.selectStudentById  - ==> Parameters: 1(Integer)
[service] 2018-07-16 20:25:37,935 - dao.IStudentDao.selectStudentById -932  [main] DEBUG dao.IStudentDao.selectStudentById  - <==      Total: 2
Student [sid=1, sname=Jam, courses=[Course [cid=1, cname=JAVA], Course [cid=2, cname=C++]]]
```

**此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~**

**技术之路不在一时，山高水长，纵使缓慢，驰而不息。**

**公众号：秦怀杂货店**

![](https://img-blog.csdnimg.cn/img_convert/7d98fb66172951a2f1266498e004e830.png)