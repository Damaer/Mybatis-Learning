> 代码直接放在Github仓库【https://github.com/Damaer/Mybatis-Learning 】，可直接运行，就不占篇幅了。

[TOC]
## 1.#{}占位符
1.#{}占位符可以用来设置参数，如果传进来的是基本类型，也就是(`string`,`long`,`double`,`int`,`boolean`,`float`等)，那么`#{}`里面的变量名可以随意写，什么`abc`,`xxx`等等，这个名字和传进来的参数名可以不一致。

2.如果传进来的是`pojo`类型，那么`#{}`中的变量名必须是`pojo`的属性名，可以写成`属性名`,也可以写`属性名.属性名`。

参数是`int`，不需要设置`parameterType`：
``` xml
<delete id="deleteStudentById" >
    delete from student where id=#{XXXdoukeyi}
</delete>
```
`parameterType`是`pojo`类,如果使用`pojo`类型作为参数，那么必须提供`get`方法，也就是框架在运行的时候需要通过反射根据`#{}`中的名字，拿到这个值放到`sql`语句中，如果占位符中的名称和属性不一致，那么报`ReflectionException`。

``` xml
<insert id="insertStudent" parameterType="Student">
	insert into student(name,age,score) values(#{name},#{age},#{score})
</insert>
```

3.#{}占位符不能解决的三类问题：

> 动态表名不可以用#{} ：Select * from #{table}
> 动态列名不可以用#{} : select #{column} from table
> 动态排序列不可以用#{} : select * from table order by #{column}

注意：不能这样写：
``` xml
<insert id="insertStudent" parameterType="Student">
	insert into student(name,age,score) values(#{Student.name},#{Student.age},#{Student.score})
</insert>
```

否则会报一个错误(会将`Student`当成一个属性)，所以我们类名就直接省略不写就可以了：
``` xml
### Cause: org.apache.ibatis.reflection.ReflectionException: There is no getter for property named 'Student' in 'class bean.Student'
```

## 2.${}拼接符
1.如果传进来的是基本类型，也就是(`string`,`long`,`double`,`int`,`boolean`,`float`等)，那么`#{}`里面的变量名必须写`value`。

```xml
<delete id="deleteStudentById" >
    delete from student where id=${value}
</delete>
```

2.如果传进来的是`pojo`类型，那么`#{}`中的变量名必须是`pojo`的属性名，可以写成`属性名`,也可以写`属性名.属性名`。**但是由于是拼接的方式，对于字符串我们需要自己加引号。**


```xml
<insert id="insertStudent" parameterType="Student">
	insert into student(name,age,score) values('${name}',${age},${score})
</insert>
```
与上面一样，不能将类名写进来：

```xml
<!--这是错误的-->
<insert id="insertStudent" parameterType="Student">
	insert into student(name,age,score) values('${Student.name}',${Student.age},${Student.score})
</insert>
```

3.${}占位符是字符串连接符，可以用来动态设置表名，列名，排序名
> 动态表名 ：Select * from ${table}
> 动态列名 : select ${column} from table
> 动态排序 : select * from table order by ${column}


4.${}可以作为连接符使用,但是这样的方式是**不安全的**，很容易发生sql注入问题，sql注入问题可以参考**https://blog.csdn.net/aphysia/article/details/80465600**：
```xml
<select id="selectStudentsByName" resultType="Student">
    select id,name,age,score from student where name like '%${value}%'
</select>
```
## 3.#{}与${}区别

> * 1.能使用`#{}`的时候尽量使用`#{}`，不使用`${}`。
> * 2.`#{}`相当于`jdbc`中的`preparedstatement`（预编译），`${}`是直接使用里面的值进行拼接，如果解释预编译和直接拼接，我想可以这么理解预编译：比如将一个`#{name}`传进来，预编译是先将`sql`语句编译成为模板，也就是我知道你要干什么，假设这个`sql`是要查询名字为`xxx`的学生信息，那无论这个`xxx`里面是什么信息，我都只会去根据名字这一列查询，里面无论写的是什么，都只会当做一个字符串，这个类型在预编译的时候已经定义好了。
> * 3.`${}`就不一样，是将语句拼接之后才确定查询条件/类型的，那么就会有被注入的可能性，有些人故意将名字设置为删除条件，这时候`sql`就变成删除操作了。
> * 所以我们一般类似模糊查询都是用`#{}`拼接
```xml
<select id="selectStudentsByName" resultType="Student">
    select id,name,age,score from student where name like '%' #{name} '%'
</select>
```

> * 但是对于`order by` 我们是用不了`#{}`的，因为用了这个就会被自动转换成字符串，自动加引号，这样语句就不生效了。

```xml
<select id="selectStudentsByName" resultType="Student">
    select id,name,age,score from student order by #{column}
</select>

<!--编译出来的结果如下：-->
select * from table order by 'column'
```
那我们需要怎么处理呢？我们只能使用`${}`，`MyBatis`不会修改或转义字符串。这样是不安全的，会导致潜在的`SQL`注入攻击，我们需要自己限制，不允许用户输入这些字段，或者通常自行转义并检查。所以这必须过滤输入的内容。

**【作者简介】**：  
秦怀，公众号【**秦怀杂货店**】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。这个世界希望一切都很快，更快，但是我希望自己能走好每一步，写好每一篇文章，期待和你们一起交流。

此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~ 


![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/blog/20201012000828.png)