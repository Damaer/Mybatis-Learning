首先获取sqlSession实例的工具类如下：
``` java
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
#### 1.返回List（查询所有学生）
定义接口：
``` java
// 返回所有学生的信息List
public List<Student> selectAllStudents();
```
使用SqlSession.selectList()这个方法对sql进行调用：
``` java
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
```
sql语句如下,返回类型是Student，这里写的是别名，这是因为定义过别名，否则需要写全路径名：
``` java
    <!-- 查询列表 -->
    <!-- 系统不知道返回封装为什么类型，所以要注明返回类型 -->
    <select id="selectAllStudents" resultType="Student">
        select id,name,age,score from student
        <!-- 如果数据库为tid，tname，tage，那么我们可以使用别名
        select tid id,tname name,tage age,tscore score from student -->
    </select>
```
#### 2.返回Map（查询所有学生,key是名字，value是学生对象）
定义接口:
``` java
// 返回所有学生的信息Map
public Map<String, Object> selectAllStudentsMap();
```
接口实现类,使用selectMap(),里面有两个参数，一个是sql的id，一个是需要当成key的字段，注意，如果这个key在数据表里面有重复的，那么后面查出来的value会覆盖掉前面的value：
```
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
```
sql语句：用的同样是返回List的sql语句，其实这个map的处理是` map=sqlSession.selectMap("selectAllStudents", "name");`这句话帮我们处理的。
``` cml
    <!-- 查询列表 -->
    <!-- 系统不知道返回封装为什么类型，所以要注明返回类型 -->
    <select id="selectAllStudents" resultType="Student">
        select id,name,age,score from student
        <!-- 如果数据库为tid，tname，tage，那么我们可以使用别名
        select tid id,tname name,tage age,tscore score from student -->
    </select>
```
#### 3.模糊查询
我们需要查询名字的时候一般是模糊查询。那么使用下面的sql即可：
``` xml
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
```
值得注意的是关于占位符的问题，如果只是一个int类型传进来，如果使用#，我们不需要和传入的名字一样，比如#{}里面写xxx都可以：
```
<!-- 删除 -->
    <delete id="deleteStudentById" >
        delete from student where id=#{xxx}
        <!-- 这里的id放什么都可以，只是一个占位符，不表示什么 -->
    </delete>
```
当传入的是对象，那么我们就不能随便写了，因为随便写就不知道用哪一个属性了。
```
<update id="updateStudent">
	update student set name=#{name},age=#{age},score=#{score} where id=#{id}
</update>
```
如果我们使用${}，那么当传进来一个int类型的时候我们需要使用${value},里面写id都是不可以的，必须写value
```
<select id="selectStudentById" resultType="Student">
	select * from student where id=${value}
</select>
```
模糊查询的时候,一下方式是拼接的模式，容易被sql注入，所以我们一般不推荐：
``` xml
<select id="selectStudentsByName" resultType="Student">
    <!-- 下面的是字符串拼接 ，只能写value，了解即可，容易sql注入，执行效率低，不建议使用-->
    select id,name,age,score from student where name like '%${value}%'
</select>
```
注意不可以写成'%#{name}%'，拼接的必须使用 $ 符号。可以使用函数进行连接：
```
<select id="selectStudentsByName" resultType="Student">
    select id,name,age,score from student where name like concat('%',#{xxx},'%')
</select>
```
当然也可以不使用函数,注意'%'与#{name}之间是有空格的，要不会报错，这种是我们比较推荐的，也就是动态参数，可以极大减少sql注入的风险：
```
<select id="selectStudentsByName" resultType="Student">
    select id,name,age,score from student where name like '%' #{name} '%'
</select>
```