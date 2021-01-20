注：代码已托管在`GitHub`上，地址是：`https://github.com/Damaer/Mybatis-Learning` ，项目是`mybatis-12-many2one`，需要自取，需要配置`maven`环境以及`mysql`环境(`sql`语句在`resource`下的`test.sql`中)，觉得有用可以点个小星星。

`docsify`文档地址在：`https://damaer.github.io/Mybatis-Learning/#/`

一对多关联查询：每个国家有很多大臣，领导人，现在我们查询一个领导，希望能将他所在国家的信息连带出来，这就是一对多关联查询。

数据表如下:

![](https://img-blog.csdnimg.cn/img_convert/e257bdd0f452c0220132a6c722b7edc5.png)
![](https://img-blog.csdnimg.cn/img_convert/d23c3cdb58b36d7a9d999d0027d49311.png)


## 创建数据库/表
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
`country`类：
```java
public class Country {
	private Integer cid;
	private String cname;

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

	@Override
	public String toString() {
		return "Country{" +
						"cid=" + cid +
						", cname='" + cname + '\'' +
						'}';
	}
}
```


`Minister`类,在这个类里面使用`Country`当属性。
```java
public class Minister {
	private Integer mid;
	private String mname;
	private Country country;

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

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "Minister{" +
						"mid=" + mid +
						", mname='" + mname + '\'' +
						", country=" + country +
						'}';
	}
}
```


`Mybatis`工具类,获取`sqlsession`实例：
```java
public class MyBatisUtils {

    /*	public SqlSession getSqlSession(){
            InputStream is;
            try {
                is = Resources.getResourceAsStream("mybatis.xml");
                return new SqlSessionFactoryBuilder().build(is).openSession();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }*/
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

在`mybatis.xml`主配置文件里面注册`mapper`文件：
```xml
<mappers>
    <mapper resource="mapper/mapper.xml"/>
</mappers>
```

接口的声明：
```java
public interface IMinisterDao {
	Minister selectMinisterById(int mid);
	Minister selectMinisterById2(int mid);
}
```
`mapper`文件**最重要的部分**,与一对一有点像，两种查询方式，一种是**嵌套结果**，一种是**嵌套查询**，嵌套查询可以使用懒加载模式，比较常用：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper 
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="dao.IMinisterDao">
<!-- 	resultMap 能解决字段和属性不一样的问题 -->
	<!-- 以后用得比较多 ，是因为可以使用延迟加载-->
	<!-- 嵌套查询 -->
	<select id="selectCountryById" resultType="Country">
	select * from country where cid=#{countryId}
	</select>
	<resultMap type="Minister" id="countryMapper">
		<id column="mid" property="mid"/>
		<result column="mname" property="mname"/>
		<!-- country中有一个成员变量是ministers，它的泛型是Minister -->
		<collection property="country"
					ofType="Country"
					select="selectCountryById"
					column="countryId">
		</collection>
	</resultMap>
	<select id="selectMinisterById" resultMap="countryMapper">
		select mid,mname,countryId
		from minister
		where 
		mid=#{mid}
	</select>

    <!-- 嵌套结果-->
    <select id="selectMinisterById2" resultMap="countryMapper2">
        select mid,mname,countryId,cid,cname
		from minister,country
		where
		mid=#{mid} and countryId = cid
    </select>
    <resultMap id="countryMapper2" type="Minister">
        <id column="mid" property="mid"/>
        <result column="mname" property="mname"/>
        <collection property="country" ofType="Country">
            <id property="cid" column="cid"/>
            <result property="cname" column="cname"/>
        </collection>
    </resultMap>
</mapper>
```


单元测试：
```java
public class MyTest {
  private IMinisterDao dao;
  private SqlSession sqlSession;
  @Before
  public void Before(){
    sqlSession=MyBatisUtils.getSqlSession();
    dao=sqlSession.getMapper(IMinisterDao.class);
  }
  @Test
  public void TestselectMinisterById(){
    Minister minister = dao.selectMinisterById(1);
    System.out.println(minister);
  }

  @Test
  public void TestselectMinisterById2(){
    Minister minister = dao.selectMinisterById2(1);
    System.out.println(minister);
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
[service] 2018-07-12 20:07:23,875 - dao.IMinisterDao.selectMinisterById2 -567  [main] DEBUG dao.IMinisterDao.selectMinisterById2  - ==>  Preparing: select mid,mname,countryId,cid,cname from minister,country where mid=? and countryId = cid 
[service] 2018-07-12 20:07:23,909 - dao.IMinisterDao.selectMinisterById2 -601  [main] DEBUG dao.IMinisterDao.selectMinisterById2  - ==> Parameters: 1(Integer)
[service] 2018-07-12 20:07:23,930 - dao.IMinisterDao.selectMinisterById2 -622  [main] DEBUG dao.IMinisterDao.selectMinisterById2  - <==      Total: 1
Minister{mid=1, mname='aaa', country=Country{cid=1, cname='USA'}}
```


**【作者简介】**：  
秦怀，公众号【**秦怀杂货店**】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。这个世界希望一切都很快，更快，但是我希望自己能走好每一步，写好每一篇文章，期待和你们一起交流。

此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~ 

![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/20210107005121.png)
    