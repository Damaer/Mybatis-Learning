mybatis有一个强大的特性，其他框架在拼接sql的时候要特别谨慎，比如哪里需要空格，还要注意去掉列表最后一个列名的逗号，mybtis的动态sql可以帮助我们逃离这样的痛苦挣扎，那就是**动态SQL**.它还可以处理一种情况，当你不确定你的参数不知道是不是为空的时候，我们不需要在业务逻辑中判断，直接在sql中处理，代码无比简洁。主要的动态sql标签如下：<br>
 - <if></if>
 - <where></where>(trim,set)
 - <choose></choose>（when, otherwise）
 - <foreach></foreach>
 
注意事项：
在mapper中如果出现大于号（>）,小于号（），大于等于号（），小于等于号（）等,最好需要转换成为实体符号，这是因为mapper是XML文件，xml文件本身就含有较多的<>这样的尖括号，所以解析的时候可能会解析出错。

原符号| < | <= | > | >= | & | ' | "
---|---|---|---|---|---|---|---
替换符号 | `&lt;`| `&lt;=`| `&gt;`| `&gt;=`| `&amp;`| `&apos;`| `&quot;`

#### <if>
我们经常需要根据where后面的条件筛选出需要的数据，当多个条件拼接的时候，我们一般使用<if></if>，如果if里面的条件成立，那么就会使用标签的语句，但是我们可以知道where句子第一个标签是没有and的，而后面的条件都需要and，所以有一种做法是第一个使用where 1 = 1，这个条件恒成立，后面的所有子语句都加上and，如果增加判断，那么我们只需要加<if>标签就可以了。
``` java
    <!-- 动态sql if标签-->
    <!-- &可以使用and来代替 ，注意！=需要连在一起写-->
    <select id="selectStudentByDynamicSQL" resultType="Student">
        <!--最常用的（动态参数） select id,name,age,score from student where name like '%' #{name} '%' -->
        <!-- 下面的是字符串拼接 ，只能写value，了解即可，容易sql注入，执行效率低，不建议使用-->
        select id,name,age,score
        from student
        where 1=1
        <if test="name != null and name != ''">
            and name like '%' #{name} '%'
        </if>
        <if test="age > 0">
            and age > #{age}
        </if>
    </select>
```
当有两个查询条件的时候，sql语句是：select * from student where 1=1 and name like '%' ? '%' and age > ? <br>
当有一个查询条件的时候：sql语句就变成：select * from student where 1=1 and name like '%' ? '%' <br>
当没有查询条件的时候，sql语句是：
select * from student where 1=1<br>
<if></if>标签需要手动在where后面添加1=1语句，这是因为如果<if>后面的条件都是false的时候，where后面如果没有1=1语句，sql就剩下一个空空的where,sql就会报错。所以在where后面需要加上永真句子1=1，但是这样有一个问题，当数据量比较大的时候，会严重影响sql的查询效率。
#### <where></where>,<trim></trim>,<set></set>
使用<where></where>标签，在有查询语句的时候，自动补上where子句，在没有查询条件的时候，不会加上where子句，这也就解决了我们上面所涉及到的问题，剩下的就是<if>标签的and子句，第一个，<if>片段里面可以不包含and，也可以包含，系统会自动去掉and，但是其他的<if>片段里面的and，必须写上，否则会出错。下面的写法中，如果name为null，第二个if标签中的if也会被去掉，不会报错。
``` java
    <select id="selectStudentByDynamicSQLWhere" resultType="Student">
        <!--最常用的（动态参数） select id,name,age,score from student where name like '%' #{name} '%' -->
        <!-- 下面的是字符串拼接 ，只能写value，了解即可，容易sql注入，执行效率低，不建议使用-->
        select id,name,age,score
        from student
        <where>
            <if test="name != null and name != ''">
                and name like '%' #{name} '%'
            </if>
            <if test="age > 0">
                and age > #{age}
            </if>
        </where>
    </select>
```
如果where里面是不规范的，那我们可以通过<trim></trim>来自定义where元素的功能,<trim>标签主要有以下属性：
 - prefix:在包含的内容前加上前缀，不是百分之百会加，会根据需要自动加
 - suffix：在包含的内容后面加上后缀，不是百分之百会加，会根据需要自动加
 - prefixOverrides：可以把包含内容的首部某些内容忽略（不能自己增加），不一定会忽略，根据需要自动忽略
 - suffixOverrides：也可以把包含内容的尾部的某些内容忽略（不能自己增加），同上

下面这样的是**错误的**，当传入的name不为空，而且age大于0的时候
``` java
    <select id="selectStudentByDynamicSQLWhere" resultType="Student">
        select id,name,age,score
        from student
        <trim prefix="where" prefixOverrides="and">
            <if test="name != null and name != ''">
                name like '%' #{name} '%'
            </if>
            <if test="age > 0">
                age > #{age}
            </if>
        </trim>
    </select>
```
不会自己增加and在第二个age前面：
<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-7-10/3753734.jpg)<br>
下面的是正确的,我们在两个<if>标签前面都增加了and，第二个and会自动去掉：
```
    <select id="selectStudentByDynamicSQLWhere" resultType="Student">
        select id,name,age,score
        from student
        <trim prefix="where" prefixOverrides="and">
            <if test="name != null and name != ''">
                and name like '%' #{name} '%'
            </if>
            <if test="age > 0">
                and age > #{age}
            </if>
        </trim>
    </select>
```
下面是后缀模式,` prefix="set"`表示在整个语句前面加上前缀set，` suffixoverride=","`表示每一个语句后面的后缀","可以被忽略，**如果是需要的话**。`suffix=" where id = #{id}`表示在整个语句后面增加where id = #{id},：
```
update user
<trim prefix="set" suffixoverride="," suffix=" where id = #{id} ">
　　<if test="name != null and name.length()>0"> name=#{name} , </if>
　　<if test="age != null "> age=#{age} ,  </if>
</trim>
```
当然，我们对上面的语句还有动态解决的方案,那就是<set>标签：
```
    <update id="updateStudent">
		update student
		<set>
            <!-- 第一个if标签的逗号一定要有，最后一个标签的逗号可以没有-->
            <if test="name != null"> name=#{name},</if>
            <if test="age != null">age=#{age},</if>
            <if test="score != null"> score=#{score},</if>
        </set>
         where id=#{id}
	</update>
```

#### <choose>, <when>, <otherwise>
有时候，我们只想去匹配第一个条件，或者第一个条件不匹配的时候才会去匹配第二个条件，不像<where></where>标签里面的<if></if>一样会去判断所有的子语句是否可以匹配，而是遇到一个匹配的就会执行跳出<choose></choose>
```
    <!-- 	selectStudentByDynamicSQLChoose 类似于switch，满足后就不会判断后面的了-->
    <!-- 如果名字不为空，那么按照名字来查询，如果名字为空，就按照年龄来查询，如果没有查询条件，就没有查询条件 -->
    <select id="selectStudentByDynamicSQLChoose" resultType="Student">
        <!--最常用的（动态参数） select id,name,age,score from student where name like '%' #{name} '%' -->
        select id,name,age,score
        from student
        <where>
            <choose>
                <when test="name != null and name != ''">
                    and name like '%' #{name} '%'
                </when>
                <when test="age > 0">
                    and age > #{age}
                </when>
                <otherwise>
                    and 1 != 1
                </otherwise>
            </choose>
        </where>
    </select>
```
<choose>标签就像是switch语句，每一个<when>都像是case,**后面默认跟上break语句**，只要满足一个就不会判断后面的子语句了，当前面所有的<when></when>都不执行的时候，就会执行<otherwise></otherwise>标签的内容，这个内容也就像是switch语句里面的default。
#### foreach
动态SQL要有一个比较多的操作是对一个集合进行遍历，通常是在构建IN条件语句的时候。需要注意的点：<br>
 - collection 表示需要遍历的集合类型，array表示需要遍历的数组
 - open，close，separator是对遍历内容的SQL拼接
 - foreach 元素的功能非常强大，它允许你指定一个集合，声明可以在元素体内使用的集合项（item）和索引（index）变量。它也允许你指定开头与结尾的字符串以及在迭代结果之间放置分隔符。
 - 你可以将任何可迭代对象（如 List、Set 等）、Map 对象或者数组对象传递给 foreach 作为集合参数。当使用可迭代对象或者数组时，index 是当前迭代的次数，item 的值是本次迭代获取的元素。当使用 Map 对象（或者 Map.Entry 对象的集合）时，index 是键，item 是值。

1.比如我们需要查找学生的id为1，2，3的学生信息，我们不希望分开一次査一个，而是希望将数组id一次传进去，查出来一个学生的集合。<br>
sql接口可以这样写,传入一个对象的数组：
```
public List<Student>selectStudentByDynamicSQLForeachArray(Object[]studentIds);
```
sql语句如下,遍历array数组的时候，指定左边符号是左括号，右边是右括号，元素以逗号分隔开：
```
    <!-- select * from student where id in (1,3) -->
    <select id="selectStudentByDynamicSQLForeachArray" resultType="Student">
        select id,name,age,score
        from student
        <if test="array !=null and array.length > 0 ">
            where id in
            <foreach collection="array" open="(" close=")" item="myid" separator=",">
                #{myid}
            </foreach>
        </if>
    </select>
```

2.当遍历的是一个类型为int的list列表时：
```
public List<Student>selectStudentByDynamicSQLForeachList(List<Integer>studentIds);
```
sql语句如下,colleaction指定为list：
```
    <select id="selectStudentByDynamicSQLForeachList" resultType="Student">
        select id,name,age,score
        from student
        <if test="list !=null and list.size > 0 ">
            where id in
            <foreach collection="list" open="(" close=")" item="myid" separator=",">
                #{myid}
            </foreach>
        </if>
    </select>
```
3.当遍历的是一个类型为对象的list：
```
public List<Student>selectStudentByDynamicSQLForeachListStudent(List<Student>students);
```
sql语句里面与上面相似，只是在使用属性的时候不太一样：
```
<select id="selectStudentByDynamicSQLForeachListStudent" resultType="Student">
        select id,name,age,score
        from student
        <if test="list !=null and list.size > 0 ">
            where id in
            <foreach collection="list" open="(" close=")" item="stu" separator=",">
                #{stu.id}
            </foreach>
        </if>
    </select>
```
#### <sql></sql>
用于定义sql片段，方便在其他SQL标签里面复用，在其他地方复用的时候需要使用<include></include>子标签，<sql>可以定义sql的任何部分，所以<include>标签可以放在动态SQL的任何位置。
```
    <sql id="selectHead">
		select id,name,age,score
		 from student
	</sql>
    <!-- 可读性比较差 -->
    <select id="selectStudentByDynamicSQLfragment" resultType="Student">
        <include refid="selectHead"></include>
        <if test="list !=null and list.size > 0 ">
            where id in
            <foreach collection="list" open="(" close=")" item="stu" separator=",">
                #{stu.id}
            </foreach>
        </if>
    </select>
```
动态sql让SQL写起来更加简洁，减少了很多重复代码，动态sql之间可以相互拼接，只要符合sql语句规范即可。