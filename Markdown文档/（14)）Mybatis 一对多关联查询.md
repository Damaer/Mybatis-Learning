注：代码已托管在GitHub上，地址是：`https://github.com/Damaer/Mybatis-Learning` ，项目是`mybatis-10-one2many`，需要自取，需要配置`maven`环境以及`mysql`环境(`sql`语句在`resource`下的`test.sql`中)，觉得有用可以点个小星。

`docsify`文档地址在：`https://damaer.github.io/Mybatis-Learning/#/`

很多时候，当查询条件涉及到具有关联关系的多个表的时候，需要使用到关联查询，关联查询一般有四种。
 - 一对一关联查询
 - 一对多关联查询
 - 多对一关联查询
 - 多对多关联查询

下面我们需要实践的是一对多关联查询，所谓一对多就是一个对象里面的属性是一个对象的集合。比如每个国家都有几个领导。对应的国家表以及领导人的表。

![](https://img-blog.csdnimg.cn/img_convert/e257bdd0f452c0220132a6c722b7edc5.png)

![](https://img-blog.csdnimg.cn/img_convert/d23c3cdb58b36d7a9d999d0027d49311.png)

## 创建数据表
```sql
#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `test`.`country` ( `cid` INT(10) NOT NULL AUTO_INCREMENT ,`cname` VARCHAR(20) NOT NULL ,PRIMARY KEY(`cid`)) ENGINE = MyISAM;
CREATE TABLE `test`.`minister` ( `mid` INT(10) NOT NULL AUTO_INCREMENT ,`mname` VARCHAR(20) NOT NULL ,`countryId` INT(20) NOT NULL ,PRIMARY KEY(`mid`)) ENGINE = MyISAM;

#初始化数据表
INSERT INTO `country` (`cid`, `cname`) VALUES ('1', 'USA');
INSERT INTO `country` (`cid`, `cname`) VALUES ('2', 'England');

INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('1', 'aaa', '1');
INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('2', 'bbb', '1');
INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('3', 'ccc', '2');
INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('4', 'ddd', '2');
INSERT INTO `minister` (`mid`, `mname`, `countryId`) VALUES ('5', 'eee', '2');
```
## 实体类
`country`对应的实体类,属性有：`cid`，`cname`,以及`miniters`，实现`get()`以及`set()`方法，`tostring()`方法：
```java
import java.util.Set;
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
`Minister`的实体类，也需要实现`set()`以及`get()`方法,`toString()`方法：
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
在`mybatis.xml`文件里面注册`mapper`文件：
```xml
    <!-- 注册映射文件 -->
    <mappers>
        <mapper resource="mapper/mapper.xml"/>
    </mappers>
```
我们操作数据库的接口：
```java
public interface ICountryDao {
	Country selectCountryById(int cid);
	Country selectCountryById2(int cid);
}
```
`mapper.xml`文件，对应的两种方式实现一对多查询：
- 一种是嵌套查询(多表单独查询)，也就是有一个入口的`select`语句，但是这个语句只选出`country`的信息，在`resultMap`里面自定义，包括一个`<collection></collection>`，在里面指定对应的类型，`property`是属性的名字，`ofType`是这个属所对应的类型,`select` 指定调用的另一个`select`语句，`column`是传进去的参数，这样的方式可以实现延迟加载，也就是用不到的时候，不会调用里面的`SQL`。这中多表单独查询可以跨多个`mapper`文件，只要写上对应的`namespace`就可以了  
- 结果嵌套查询（多表连接查询）：也有一个入口的`select`语句，与上面不一样的是，这个`select`语句将两张表的字段都选择出来了，我们需要指定一个`resultMap`，其他子标签一样，但是`<collection></collection>`里面不再调用另外的`sql`语句，只是指定了属性与字段对应就可以了。

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

    <!-- 嵌套结果-->
    <select id="selectCountryById2" resultMap="countryMapper2">
        select * from country c,minister m where c.cid = m.countryId and c.cid= #{cid}
    </select>
    <resultMap id="countryMapper2" type="Country">
        <id column="cid" property="cid"/>
        <result column="cname" property="cname"/>
        <collection property="ministers" ofType="Minister">
            <id property="mid" column="mid"/>
            <result property="mname" column="mname"/>
        </collection>
    </resultMap>
</mapper>
```
测试test：

```java
public class MyTest {
  private ICountryDao dao;
  private SqlSession sqlSession;
  @Before
  public void Before(){
    sqlSession=MyBatisUtils.getSqlSession();
    dao=sqlSession.getMapper(ICountryDao.class);
  }
  @Test
  public void TestselectCountryById(){
    Country country=dao.selectCountryById(1);
    System.out.println(country);
  }

  @Test
  public void TestselectCountryById2(){
    Country country=dao.selectCountryById2(1);
    System.out.println(country);
  }


  @After
  public void after(){
    if(sqlSession!=null){
      sqlSession.close();
    }
  }

}

```
使用到的工具类：`MybatisUtils.java`
```java
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

两个接口的结果一致：
```shell
[service] 2018-07-12 14:01:15,835 - org.apache.ibatis.transaction.jdbc.JdbcTransaction -508  [main] DEBUG org.apache.ibatis.transaction.jdbc.JdbcTransaction  - Setting autocommit to false on JDBC Connection [com.mysql.jdbc.JDBC4Connection@4abdb505]
[service] 2018-07-12 14:01:15,837 - dao.ICountryDao.selectCountryById2 -510  [main] DEBUG dao.ICountryDao.selectCountryById2  - ==>  Preparing: select * from country c,minister m where c.cid = m.countryId and c.cid= ? 
[service] 2018-07-12 14:01:15,869 - dao.ICountryDao.selectCountryById2 -542  [main] DEBUG dao.ICountryDao.selectCountryById2  - ==> Parameters: 1(Integer)
[service] 2018-07-12 14:01:15,884 - dao.ICountryDao.selectCountryById2 -557  [main] DEBUG dao.ICountryDao.selectCountryById2  - <==      Total: 2
Country [cid=1, cname=USA, ministers=[Minister [mid=2, mname=bbb], Minister [mid=1, mname=aaa]]]
```


**【作者简介】**：  
秦怀，公众号【**秦怀杂货店**】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。这个世界希望一切都很快，更快，但是我希望自己能走好每一步，写好每一篇文章，期待和你们一起交流。

此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~ 


![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/20210107005121.png)
    