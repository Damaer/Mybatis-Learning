注：代码已托管在`GitHub`上，地址是：`https://github.com/Damaer/Mybatis-Learning` ，项目是`mybatis-16-lazyload`，需要自取，需要配置maven环境以及`mysql`环境(`sql`语句在`resource`下的`test.sql`中)，觉得有用可以点个小星星。

`docsify`文档地址在：`https://damaer.github.io/Mybatis-Learning/#/`


`mybatis`的懒加载，也称为延迟加载，是指在进行关联查询的时候，按照设置延迟规则推迟对关联对象的`select`查询，延迟加载可以有效的减少数据库压力。**延迟加载只对关联对象有延迟设置，主加载对象都是直接执行查询语句的**
## 关联对象加载类型
mybatis的关联对象的查询select语句的执行时机，可以分为3类，直接加载，侵入式加载与深度延迟加载。

### 1.直接加载
执行完主加载对象的select语句，马上就会执行关联对象的select语句。
### 2.侵入式延迟加载
执行对主加载对象的查询时，不会执行关联对象的查询，但是当访问主加载对象的详情时，就会马上执行关联对象的select查询，也就是说关联对象的查询执行，侵入到了主加载对象的详情访问中，可以理解为，将关联对象的详情侵入到主加载对象的详情中，作为它的一部分出现了。
### 3.深度延迟加载
执行对主加载对象的查询的时候，不会执行对关联对象的查询，访问主加载对象的详情的时候，也不会执行关联对象的select查询，只有当真正的访问关联对象的详情的时候，才会执行对关联对象的select查询。<br>

> 注意：延迟加载的最基本要求，关联对象的查询与主加载对象的查询必须是分别放在两个语句中的，不能使用多表连接查询，因为多表连接查询相当于把多张表连接成一张表的查询，无法做到分开查询，会一次性将表的内容查询出来。
> 延迟加载，可以应用到一对多，一对一，多对一，多对多的关联查询中。

举个例子：我们使用上一个`demo`，查询`minister`与`country`之间的关系，数据库如下：

```sql
#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `test`.`student` ( `sid` INT(10) NOT NULL AUTO_INCREMENT ,`sname` VARCHAR(20) NOT NULL ,PRIMARY KEY(`sid`)) ENGINE = MyISAM;
CREATE TABLE `test`.`course` ( `cid` INT(10) NOT NULL AUTO_INCREMENT ,`cname` VARCHAR(20) NOT NULL ,PRIMARY KEY(`cid`)) ENGINE = MyISAM;
CREATE TABLE `test`.`middle` (
`id` INT(10) NOT NULL AUTO_INCREMENT ,`studentId` INT(10) NOT NULL ,`courseId` INT(10) NOT NULL ,PRIMARY KEY(`id`)) ENGINE = MyISAM;
#初始化数据表
INSERT INTO `course` (`cid`, `cname`) VALUES ('1', 'JAVA') ;
INSERT INTO `course` (`cid`, `cname`) VALUES ('2', 'C++') ;
INSERT INTO `course` (`cid`, `cname`) VALUES ('3', 'JS') ;

INSERT INTO `student` (`sid`, `sname`) VALUES ('1', 'Jam') ;
INSERT INTO `student` (`sid`, `sname`) VALUES ('2', 'Lina') ;

INSERT INTO `middle` (`id`, `studentId`, `courseId`) VALUES ('1', '1', '1');
INSERT INTO `middle` (`id`, `studentId`, `courseId`) VALUES ('2', '1', '2');
INSERT INTO `middle` (`id`, `studentId`, `courseId`) VALUES ('3', '2', '1');
INSERT INTO `middle` (`id`, `studentId`, `courseId`) VALUES ('4', '2', '3');
```
与之对应的实体类:`Country.class`
```java
public class Country {
	private Integer cid;
	private String cname;

	private Set<Minister> ministers;
	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}
	public String getName() {
		return cname;
	}
	public void setName(String cname) {
		this.cname = cname;
	}
	public Set<Minister> getMinisters() {
		return ministers;
	}
	public void setMinisters(Set<Minister> ministers) {
		this.ministers = ministers;
	}
	@Override
	public String toString() {
		return "Country [cid=" + cid + ", cname=" + cname + ", ministers="
				+ ministers + "]";
	}
}
```
`Minister.class`:领导人实体类
```java
public class Minister {
	private Integer mid;
	private String mname;
	@Override
	public String toString() {
		return "Minister [mid=" + mid + ", mname=" + mname + "]";
	}
	public Integer getMid() {
		return mid;
	}
	public void setMid(Integer mid) {
		this.mid = mid;
	}
	public String getMname() {
		return mname;
	}
	public void setMname(String mname) {
		this.mname = mname;
	}
	
}

```
主配置文件`mybatis.xml`：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 配置数据库文件 -->
    <properties resource="jdbc_mysql.properties">

    </properties>
    <settings>
        <setting name="lazyLoadingEnabled" value="false"/>
        <!--<setting name="aggressiveLazyLoading" value="false"/>-->
    </settings>
    <!-- 别名，对数据对象操作全名太长，需要使用别名 -->
    <typeAliases>
        <!--<typeAlias type="bean.Student" alias="Student"/>-->
        <!--直接使用类名即可，对于整个包的路径配置（别名），简单快捷 -->
        <package name="beans"/>
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
`mapper.xml`文件：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="dao.ICountryDao">
    <!-- 	resultMap 能解决字段和属性不一样的问题 -->
    <!-- 以后用得比较多 ，是因为可以使用延迟加载-->
    <!-- 嵌套查询 -->
    <select id="selectMinisterByCountry" resultType="Minister">
	select mid,mname from minister where countryId=#{ooo}
	</select>
    <resultMap type="Country" id="countryMapper">
        <id column="cid" property="cid"/>
        <result column="cname" property="cname"/>
        <!-- country中有一个成员变量是ministers，它的泛型是Minister -->
        <collection property="ministers"
                    ofType="Minister"
                    select="selectMinisterByCountry"
                    column="cid">
        </collection>
    </resultMap>
    <select id="selectCountryById" resultMap="countryMapper">
		select cid,cname
		from country
		where
		cid=#{cid}
	</select>
</mapper>
```
与之对应的`sql`接口：
```java
public interface ICountryDao {
	Country selectCountryById(int cid);
}

```
使用到的工具类：
```java
public class MyBatisUtils {
  private static SqlSessionFactory sqlSessionFactory;

  public static SqlSession getSqlSession() {
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
## 直接加载查询
关于懒加载的配置，我们只需要在`mybatis.xml`文件里面使用`<settings></settings>`就可以了，懒加载有一个总开关，`lazyloadingEnabled`，只要置为`false`就可以将延迟加载关掉，那就是直接加载查询了。配置在`<properties></properties>`与`<typeAliases></typeAliases>`之间。
```xml
    <properties resource="jdbc_mysql.properties">
    </properties>
    <settings>
        <setting name="lazyLoadingEnabled" value="flase"/>
    </settings>
    <!-- 别名，对数据对象操作全名太长，需要使用别名 -->
    <typeAliases>
        <!--<typeAlias type="bean.Student" alias="Student"/>-->
        <!--直接使用类名即可，对于整个包的路径配置（别名），简单快捷 -->
        <package name="bean"/>
    </typeAliases>
```
当单元测试是直接査`country`对象的时候：
```java
	@Test
	public void TestselectCountryById(){
		Country country=dao.selectCountryById(1);
	}
```
结果是,我们可以看到两条`sql`，除了查询`country`之外，连同`minister`关联对象也一起查询了，这就是直接加载，简单粗暴，不管是否使用，都会先将关联查询加载：
```bash
[service] 2018-07-17 09:59:00,796 - dao.ICountryDao.selectCountryById -491  [main] DEBUG dao.ICountryDao.selectCountryById  - ==>  Preparing: select cid,cname from country where cid=? 
[service] 2018-07-17 09:59:00,823 - dao.ICountryDao.selectCountryById -518  [main] DEBUG dao.ICountryDao.selectCountryById  - ==> Parameters: 1(Integer)
[service] 2018-07-17 09:59:00,838 - dao.ICountryDao.selectMinisterByCountry -533  [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - ====>  Preparing: select mid,mname from minister where countryId=? 
[service] 2018-07-17 09:59:00,838 - dao.ICountryDao.selectMinisterByCountry -533  [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - ====> Parameters: 1(Integer)
[service] 2018-07-17 09:59:00,849 - dao.ICountryDao.selectMinisterByCountry -544  [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - <====      Total: 2
[service] 2018-07-17 09:59:00,850 - dao.ICountryDao.selectCountryById -545  [main] DEBUG dao.ICountryDao.selectCountryById  - <==      Total: 1
```
## 侵入式延迟加载
需要将延迟加载开关开启（`true`），同时也需要将侵入式加载开关开启（`true`）
```xml
    <settings>
        <setting name="lazyLoadingEnabled" value="true"/>
        <setting name="aggressivelazyLoading" value="true"/>
    </settings>
```
1.当我们只查询`country`的时候，只会执行`country`的查询，不会执行关联查询`minister`：

```java
	@Test
	public void TestselectCountryById(){
		Country country=dao.selectCountryById(1);
	}
```
结果如下，只有一条sql：
```shell
[service] 2018-07-17 14:30:55,471 - dao.ICountryDao.selectCountryById -902  [main] DEBUG dao.ICountryDao.selectCountryById  - ==>  Preparing: select cid,cname from country where cid=? 
[service] 2018-07-17 14:30:55,494 - dao.ICountryDao.selectCountryById -925  [main] DEBUG dao.ICountryDao.selectCountryById  - ==> Parameters: 1(Integer)
[service] 2018-07-17 14:30:55,590 - dao.ICountryDao.selectCountryById -1021 [main] DEBUG dao.ICountryDao.selectCountryById  - <==      Total: 1
```
当我们查询`country`的属性，但是不查询`minister`属性的时候：
```java
	@Test
	public void TestselectCountryById(){
		Country country=dao.selectCountryById(1);
		System.out.println(country.getCid());
	}
```
结果如下，会加载到关联对象`minister`，这就是侵入式延迟加载：
```bash
[service] 2018-07-17 14:32:37,959 - dao.ICountryDao.selectCountryById -724  [main] DEBUG dao.ICountryDao.selectCountryById  - ==>  Preparing: select cid,cname from country where cid=? 
[service] 2018-07-17 14:32:37,979 - dao.ICountryDao.selectCountryById -744  [main] DEBUG dao.ICountryDao.selectCountryById  - ==> Parameters: 1(Integer)
[service] 2018-07-17 14:32:38,170 - dao.ICountryDao.selectCountryById -935  [main] DEBUG dao.ICountryDao.selectCountryById  - <==      Total: 1
[service] 2018-07-17 14:32:38,171 - dao.ICountryDao.selectMinisterByCountry -936  [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - ==>  Preparing: select mid,mname from minister where countryId=? 
[service] 2018-07-17 14:32:38,171 - dao.ICountryDao.selectMinisterByCountry -936  [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - ==> Parameters: 1(Integer)
[service] 2018-07-17 14:32:38,173 - dao.ICountryDao.selectMinisterByCountry -938  [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - <==      Total: 2
1
```
## 深度延迟加载
需要将延迟加载开关`lazyLoadingEnabled`开启（`true`），同时需要将侵入式加载开关`aggressivelazyLoading`关闭（`false`）
```xml
    <settings>
        <setting name="lazyLoadingEnabled" value="true"/>
        <setting name="aggressivelazyLoading" value="false"/>
    </settings>
```
1.当我们只查询出`country`的时候，只会查询`country`，而不会查询`minister`：
单元测试代码：
```java
	@Test
	public void TestselectCountryById(){
		Country country=dao.selectCountryById(1);
	}
```

```bash
[service] 2018-07-17 14:20:38,608 - dao.ICountryDao.selectCountryById -1271 [main] DEBUG dao.ICountryDao.selectCountryById  - ==>  Preparing: select cid,cname from country where cid=? 
[service] 2018-07-17 14:20:38,631 - dao.ICountryDao.selectCountryById -1294 [main] DEBUG dao.ICountryDao.selectCountryById  - ==> Parameters: 1(Integer)
[service] 2018-07-17 14:20:38,980 - dao.ICountryDao.selectCountryById -1643 [main] DEBUG dao.ICountryDao.selectCountryById  - <==      Total: 1
```
2.当我们访问`country`的属性的时候，也不会加载关联查询的`minister`：
```java
	@Test
	public void TestselectCountryById(){
		Country country=dao.selectCountryById(1);
		System.out.println(country.getCid());
	}
```
结果同样是：
```bash
[service] 2018-07-17 14:24:03,004 - org.apache.ibatis.transaction.jdbc.JdbcTransaction -686  [main] DEBUG org.apache.ibatis.transaction.jdbc.JdbcTransaction  - Setting autocommit to false on JDBC Connection [com.mysql.jdbc.JDBC4Connection@cb51256]
[service] 2018-07-17 14:24:03,030 - dao.ICountryDao.selectCountryById -712  [main] DEBUG dao.ICountryDao.selectCountryById  - ==>  Preparing: select cid,cname from country where cid=? 
[service] 2018-07-17 14:24:03,078 - dao.ICountryDao.selectCountryById -760  [main] DEBUG dao.ICountryDao.selectCountryById  - ==> Parameters: 1(Integer)
[service] 2018-07-17 14:24:03,160 - dao.ICountryDao.selectCountryById -842  [main] DEBUG dao.ICountryDao.selectCountryById  - <==      Total: 1
```
3.当我们查询`country`属性**minister**的时候：
```java
	@Test
	public void TestselectCountryById(){
		Country country=dao.selectCountryById(1);
		System.out.println(country.getMinisters());
	}
```
我们可以看到结果,执行了`minister`的查询,这个时候才是真正的加载`minister`查询
```bash
[service] 2018-07-17 14:26:55,913 - dao.ICountryDao.selectCountryById -1540 [main] DEBUG dao.ICountryDao.selectCountryById  - ==>  Preparing: select cid,cname from country where cid=? 
[service] 2018-07-17 14:26:55,943 - dao.ICountryDao.selectCountryById -1570 [main] DEBUG dao.ICountryDao.selectCountryById  - ==> Parameters: 1(Integer)
[service] 2018-07-17 14:26:56,161 - dao.ICountryDao.selectCountryById -1788 [main] DEBUG dao.ICountryDao.selectCountryById  - <==      Total: 1
[service] 2018-07-17 14:26:56,162 - dao.ICountryDao.selectMinisterByCountry -1789 [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - ==>  Preparing: select mid,mname from minister where countryId=? 
[service] 2018-07-17 14:26:56,163 - dao.ICountryDao.selectMinisterByCountry -1790 [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - ==> Parameters: 1(Integer)
[service] 2018-07-17 14:26:56,168 - dao.ICountryDao.selectMinisterByCountry -1795 [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - <==      Total: 2
[Minister [mid=2, mname=bbb], Minister [mid=1, mname=aaa]]
```

加载方式 | lazyLoadingEnabled | aggressiveLazyLoading
---|---|---
直接加载 | 必须是false，默认是false | 不管是什么，只要lazyLoadingEnabled是false就是直接加载
侵入式延迟加载 | 必须是true | 必须是true
深度延迟加载 | 必须是true | 必须是false，默认是false

**此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~**

**技术之路不在一时，山高水长，纵使缓慢，驰而不息。**

**公众号：秦怀杂货店**

![](https://img-blog.csdnimg.cn/img_convert/7d98fb66172951a2f1266498e004e830.png)