
我们知道很多时候我们有一个需求，我们需要把插入数据后的id返回来，以便我们下一次操作。<br>
其实一开始的思路是我插入之后，再执行一次select，根据一个唯一的下字段来select，但是Student这个类如果插入后再根据名字或者年龄查出来，这根本就是不可行的！！！重名与同年龄的人一定不少。
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
#### 解决方案1:
```
    <insert id="insertStudentCacheId" useGeneratedKeys="true" keyProperty="id" parameterType="Student">
        insert into student(name,age,score) values(#{name},#{age},#{score})
    </insert>
```
需要注意的点：
> 1.useGeneratedKeys="true"表示设置主键自增
> 2.keyProperty="id"设置主键的字段
> 3.parameterType="Student"设置传入的类型
> 4.注意：虽然有返回类型，但是我们不需要手动设置返回的类型，这个是由框架帮我们实现的，所以对应的接口方法也是没有返回值的。
``` java
public void insertStudentCacheId(Student student);
```
> 5.实体类中id属性字段一定需要set以及get方法

#### 另一种方式（数据库中实现了自增）
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
两种方式的结果：<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-25/94139460.jpg)<br>
注意要点：
> 1.最外层的<insert></insert>没有返回属性（resultType），但是里面的<selectKey></selectKey>是有返回值类型的。
> 2.order="AFTER"表示先执行插入，之后才执行selectkey语句的。
> 3.select @@identity和select LAST_INSERT_ID()都表示选出刚刚插入的最后一条数据的id。
> 4.实体类中id属性字段一定需要set以及get方法
> 5.此时，接口中仍不需要有返回值，框架会自动将值注入到我们insert的那个对象中，我们可以直接使用就可以了。

其实，我们的接口中可以有返回值，但是这个返回值不是id,而是表示**插入后影响的行数**，此时sql中仍和上面一样，不需要写返回值。
```
<insert id="insertStudentCacheIdNoReturn" parameterType="Student">
        insert into student(name,age,score) values(#{name},#{age},#{score})
        <!-- 指定结果类型resultType，keyProperty是属性，自动返回到属性id中，order是次序，after是指获取id是在于插入后 -->
        <selectKey resultType="int" keyProperty="id" order="AFTER">
            select LAST_INSERT_ID()
        </selectKey>
    </insert>
```
接口中：
```
// 增加新学生并返回id返回result
public int insertStudentCacheId(Student student);
```
接口的实现类：
```
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