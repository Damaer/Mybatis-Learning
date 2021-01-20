注：代码已托管在`GitHub`上，地址是：`https://github.com/Damaer/Mybatis-Learning` ，项目是`mybatis-11-one2one`，需要自取，需要配置`maven`环境以及`mysql`环境(`sql`语句在`resource`下的`test.sql`中)，觉得有用可以点个小星星。

`docsify`文档地址在：`https://damaer.github.io/Mybatis-Learning/#/`

所谓一对一多表查询，举个例子：我们有很多国家，每一个国家只有一个领导人（假设），我们需要根据`id`查询国家信息，带上领导人的信息。

## 创建数据表
设计表的时候，我们需要考虑由于是一对多关系，我们需要在国家表里面使用一个字段对应领导人的信息。
``` sql
#创建数据库
CREATE DATABASE `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
#创建数据表
CREATE TABLE `test`.`country` ( `cid` INT(10) NOT NULL AUTO_INCREMENT ,`cname` VARCHAR(20) NOT NULL ,`mid` INT(10) NOT NULL,PRIMARY KEY(`cid`)) ENGINE = MyISAM;
CREATE TABLE `test`.`minister` ( `mid` INT(10) NOT NULL AUTO_INCREMENT ,`mname` VARCHAR(20) NOT NULL,PRIMARY KEY(`mid`)) ENGINE = MyISAM;

#初始化数据表
INSERT INTO `country` (`cid`, `cname`, `mid`) VALUES ('1', 'aaa', '1'); 
INSERT INTO `country` (`cid`, `cname`, `mid`) VALUES ('2', 'bbb', '2');
INSERT INTO `country` (`cid`, `cname`, `mid`) VALUES ('3', 'ccc', '3'); 

INSERT INTO `minister` (`mid`, `mname`) VALUES ('1', 'sam'); 
INSERT INTO `minister` (`mid`, `mname`) VALUES ('2', 'jane'); 
INSERT INTO `minister` (`mid`, `mname`) VALUES ('3', 'jone'); 
```
数据表如下：

![](https://img-blog.csdnimg.cn/img_convert/09d4010c70b55aa4b2658558792027ce.png)
![](https://img-blog.csdnimg.cn/img_convert/50fa74775a235b26ab0e1e6bf8bb439d.png)


## 实体类
`country`对应的实体类：
```java
public class Country {
	private Integer cid;
	private String cname;
	private Minister minister;

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

	public Minister getMinister() {
		return minister;
	}

	public void setMinister(Minister minister) {
		this.minister = minister;
	}

	@Override
	public String toString() {
		return "Country{" +
						"cid=" + cid +
						", cname='" + cname + '\'' +
						", minister=" + minister +
						'}';
	}
}

```
`Minister`对应的实体类：
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
`MybatisUtils`工具类，用于获取单例的`sqlSession`对象：
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
在`mybatis.xml`里面注册`mapper`文件：
```xml
<mappers>
    <mapper resource="mapper/mapper.xml"/>
</mappers>
```
我们的两个接口（对应`mapper`文件中的两个`sql`语句）：
```java
public interface ICountryDao {
	Country selectCountryById(int cid);
	Country selectCountryById2(int cid);
}
```
`mapper`文件,有两种写法，一种是**嵌套结果**，一种是**嵌套查询**。

- 嵌套结果的就是使用一条sql完成查询，需要在自己定义的resultMap里面使用`<association></association>`来组织结果属性。

- 嵌套查询也有一个`<association></association>`，但是在最外面的`<select>`标签并没有将所有需要的东西查询出来，而是在`<association></association>`里面指定需要关联查询的`sql`语句的`id`，这样的方式就是**懒加载**:


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
	select mid,mname from minister where mid=#{ooo}
	</select>
	<resultMap type="Country" id="countryMapper">
		<id column="cid" property="cid"/>
		<result column="cname" property="cname"/>
		<!-- country中有一个成员变量是ministers，它的泛型是Minister -->
		<association property="minister"
					select="selectMinisterByCountry"
					column="mid">
		</association>
	</resultMap>
	<select id="selectCountryById" resultMap="countryMapper">
		select cid,cname,mid
		from country 
		where 
		cid=#{cid}
	</select>

    <!-- 嵌套结果-->
    <select id="selectCountryById2" resultMap="countryMapper2">
        select * from country c,minister m where c.mid = m.mid and c.cid= #{cid}
    </select>
    <resultMap id="countryMapper2" type="Country">
        <id column="cid" property="cid"/>
        <result column="cname" property="cname"/>
        <association property="minister" javaType="Minister">
			<id property="mid" column="mid"/>
			<result property="mname" column="mname"/>
		</association>
    </resultMap>
</mapper>
```


单元测试：
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
结果：
```shell
[service] 2018-07-12 15:23:38,971 - dao.ICountryDao.selectCountryById -537  [main] DEBUG dao.ICountryDao.selectCountryById  - ==>  Preparing: select cid,cname,mid from country where cid=? 
[service] 2018-07-12 15:23:39,004 - dao.ICountryDao.selectCountryById -570  [main] DEBUG dao.ICountryDao.selectCountryById  - ==> Parameters: 1(Integer)
[service] 2018-07-12 15:23:39,037 - dao.ICountryDao.selectMinisterByCountry -603  [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - ====>  Preparing: select mid,mname from minister where mid=? 
[service] 2018-07-12 15:23:39,037 - dao.ICountryDao.selectMinisterByCountry -603  [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - ====> Parameters: 1(Integer)
[service] 2018-07-12 15:23:39,040 - dao.ICountryDao.selectMinisterByCountry -606  [main] DEBUG dao.ICountryDao.selectMinisterByCountry  - <====      Total: 1
[service] 2018-07-12 15:23:39,041 - dao.ICountryDao.selectCountryById -607  [main] DEBUG dao.ICountryDao.selectCountryById  - <==      Total: 1
Country{cid=1, cname='aaa', minister=Minister [mid=1, mname=sam]}
```

**【作者简介】**：  
秦怀，公众号【**秦怀杂货店**】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。这个世界希望一切都很快，更快，但是我希望自己能走好每一步，写好每一篇文章，期待和你们一起交流。

此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~ 

![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/20210107005121.png)
    