注：代码已托管在`GitHub`上，地址是：`https://github.com/Damaer/Mybatis-Learning` ，项目是`mybatis-13-oneself-one2many`，需要自取，需要配置`maven`环境以及`mysql`环境(`sql`语句在`resource`下的`test.sql`中)，觉得有用可以点个小星星。

`docsify`文档地址在：`https://damaer.github.io/Mybatis-Learning/#/`

所谓**自关联查询**，是指自己既然充当一方，又充当多方。比如新闻栏目的数据表，自己可以是父栏目，也可以是多方，子栏目。在数据表里面实现就是一张表，有一个外键`pid`，用来表示该栏目的父栏目，一级栏目没有父栏目的，可以将其外键设置为0。

`DB`表如下：  
![](https://img-blog.csdnimg.cn/img_convert/29192b55571a01e02f992bad110400da.png)

## 查询指定栏目的所有子孙栏目
查询指定目录的所有子孙目录，我们需要使用递归的思想，查出当前栏目之后，需要将当前栏目的`id`作为下一级栏目的`pid`。

实体类`NewsLabel.java`,使用一对多的关系：
```java
import java.util.Set;

public class NewsLabel {
	private Integer id;
	private String name;
	private Set<NewsLabel>children;
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
	public Set<NewsLabel> getChildren() {
		return children;
	}
	public void setChildren(Set<NewsLabel> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "NewsLabel [id=" + id + ", name=" + name + ", children="
				+ children + "]";
	}
	
}
```

定义`sql`接口:
``` java
public interface INewsLabelDao {
	List<NewsLabel> selectChildByParentId(int pid);
}
```

`mapper.xml`文件，在递归里面使用本身`sql`：
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper 
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="dao.INewsLabelDao">
	<resultMap type="beans.NewsLabel" id="newsLabelMapper">
		<id column="id" property="id"/>
		<result column="name" property="name"/>
		<collection property="children" 
				ofType="NewsLabel"
				select="selectChildByParentId"
				column="id"/>
	</resultMap>
	<select id="selectChildByParentId" resultMap="newsLabelMapper">
		select id,name from newslabel where pid=#{xxx}
	</select>
</mapper>
```


测试类`MyTest.java`:
``` java
public class MyTest {
  private INewsLabelDao dao;
  private SqlSession sqlSession;
  @Before
  public void Before(){
    sqlSession=MyBatisUtils.getSqlSession();
    dao=sqlSession.getMapper(INewsLabelDao.class);
  }
  @Test
  public void TestselectMinisterById(){
    List<NewsLabel>children=dao.selectChildByParentId(2);
    for(NewsLabel newsLabel:children){
      System.out.println(newsLabel);
    }
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
``` shell
NewsLabel [id=3, name=NBA, children=[NewsLabel [id=5, name=火箭, children=[]], NewsLabel [id=6, name=湖人, children=[]]]]
NewsLabel [id=4, name=CBA, children=[NewsLabel [id=7, name=北京金瓯, children=[]], NewsLabel [id=8, name=浙江广夏, children=[]], NewsLabel [id=9, name=青岛双星, children=[]]]]
```
这样的写法只能选出子孙栏目，不能将自己的信息输出。

## 查询指定目录以及指定子孙目录

添加一个`sql`的接口：
``` java
List<NewsLabel> selectSelfAndChildByParentId(int pid);
```
`mapper`文件里面实现,在`resultMap`里面递归调用另一个`sql`，最外层的`sql`只执行一次，这样就可以实现查询自身一次，递归查询子孙栏目的功能：
```xml
    <!--  筛选出自己以及子孙栏目-->
    <select id="selectChildByParentId2" resultMap="newsLabelMapper2">
		select id,name from newslabel where pid=#{ooo}
	</select>
    <resultMap type="beans.NewsLabel" id="newsLabelMapper2">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <collection property="children"
                    ofType="NewsLabel"
                    select="selectChildByParentId2"
                    column="id"/>
    </resultMap>
    <select id="selectSelfAndChildByParentId" resultMap="newsLabelMapper2">
		select id,name from newslabel where id=#{xxx}
	</select>
```


单元测试：
``` java
  @Test
  public void TestselectSelfAndChildrenLabelById(){
    List<NewsLabel> children = dao.selectSelfAndChildByParentId(2);
    for (NewsLabel newsLabel : children) {
      System.out.println(newsLabel);
    }
  }
```
结果：
```shell
[service] 2018-07-16 11:17:16,667 - org.apache.ibatis.transaction.jdbc.JdbcTransaction -450  [main] DEBUG org.apache.ibatis.transaction.jdbc.JdbcTransaction  - Setting autocommit to false on JDBC Connection [com.mysql.jdbc.JDBC4Connection@5bb21b69]
[service] 2018-07-16 11:17:16,669 - dao.INewsLabelDao.selectSelfAndChildByParentId -452  [main] DEBUG dao.INewsLabelDao.selectSelfAndChildByParentId  - ==>  Preparing: select id,name from newslabel where id=? 
[service] 2018-07-16 11:17:16,704 - dao.INewsLabelDao.selectSelfAndChildByParentId -487  [main] DEBUG dao.INewsLabelDao.selectSelfAndChildByParentId  - ==> Parameters: 2(Integer)
[service] 2018-07-16 11:17:16,722 - dao.INewsLabelDao.selectChildByParentId2 -505  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ====>  Preparing: select id,name from newslabel where pid=? 
[service] 2018-07-16 11:17:16,723 - dao.INewsLabelDao.selectChildByParentId2 -506  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ====> Parameters: 2(Integer)
[service] 2018-07-16 11:17:16,726 - dao.INewsLabelDao.selectChildByParentId2 -509  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ======>  Preparing: select id,name from newslabel where pid=? 
[service] 2018-07-16 11:17:16,726 - dao.INewsLabelDao.selectChildByParentId2 -509  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ======> Parameters: 3(Integer)
[service] 2018-07-16 11:17:16,727 - dao.INewsLabelDao.selectChildByParentId2 -510  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========>  Preparing: select id,name from newslabel where pid=? 
[service] 2018-07-16 11:17:16,728 - dao.INewsLabelDao.selectChildByParentId2 -511  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========> Parameters: 5(Integer)
[service] 2018-07-16 11:17:16,729 - dao.INewsLabelDao.selectChildByParentId2 -512  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - <========      Total: 0
[service] 2018-07-16 11:17:16,732 - dao.INewsLabelDao.selectChildByParentId2 -515  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========>  Preparing: select id,name from newslabel where pid=? 
[service] 2018-07-16 11:17:16,732 - dao.INewsLabelDao.selectChildByParentId2 -515  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========> Parameters: 6(Integer)
[service] 2018-07-16 11:17:16,733 - dao.INewsLabelDao.selectChildByParentId2 -516  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - <========      Total: 0
[service] 2018-07-16 11:17:16,734 - dao.INewsLabelDao.selectChildByParentId2 -517  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - <======      Total: 2
[service] 2018-07-16 11:17:16,734 - dao.INewsLabelDao.selectChildByParentId2 -517  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ======>  Preparing: select id,name from newslabel where pid=? 
[service] 2018-07-16 11:17:16,734 - dao.INewsLabelDao.selectChildByParentId2 -517  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ======> Parameters: 4(Integer)
[service] 2018-07-16 11:17:16,736 - dao.INewsLabelDao.selectChildByParentId2 -519  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========>  Preparing: select id,name from newslabel where pid=? 
[service] 2018-07-16 11:17:16,736 - dao.INewsLabelDao.selectChildByParentId2 -519  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========> Parameters: 7(Integer)
[service] 2018-07-16 11:17:16,738 - dao.INewsLabelDao.selectChildByParentId2 -521  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - <========      Total: 0
[service] 2018-07-16 11:17:16,738 - dao.INewsLabelDao.selectChildByParentId2 -521  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========>  Preparing: select id,name from newslabel where pid=? 
[service] 2018-07-16 11:17:16,739 - dao.INewsLabelDao.selectChildByParentId2 -522  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========> Parameters: 8(Integer)
[service] 2018-07-16 11:17:16,741 - dao.INewsLabelDao.selectChildByParentId2 -524  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - <========      Total: 0
[service] 2018-07-16 11:17:16,742 - dao.INewsLabelDao.selectChildByParentId2 -525  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========>  Preparing: select id,name from newslabel where pid=? 
[service] 2018-07-16 11:17:16,742 - dao.INewsLabelDao.selectChildByParentId2 -525  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - ========> Parameters: 9(Integer)
[service] 2018-07-16 11:17:16,743 - dao.INewsLabelDao.selectChildByParentId2 -526  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - <========      Total: 0
[service] 2018-07-16 11:17:16,744 - dao.INewsLabelDao.selectChildByParentId2 -527  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - <======      Total: 3
[service] 2018-07-16 11:17:16,744 - dao.INewsLabelDao.selectChildByParentId2 -527  [main] DEBUG dao.INewsLabelDao.selectChildByParentId2  - <====      Total: 2
[service] 2018-07-16 11:17:16,745 - dao.INewsLabelDao.selectSelfAndChildByParentId -528  [main] DEBUG dao.INewsLabelDao.selectSelfAndChildByParentId  - <==      Total: 1
NewsLabel [id=2, name=体育新闻, children=[NewsLabel [id=3, name=NBA, children=[NewsLabel [id=6, name=湖人, children=[]], NewsLabel [id=5, name=火箭, children=[]]]], NewsLabel [id=4, name=CBA, children=[NewsLabel [id=7, name=北京金瓯, children=[]], NewsLabel [id=8, name=浙江广夏, children=[]], NewsLabel [id=9, name=青岛双星, children=[]]]]]]

```

**此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~**

**技术之路不在一时，山高水长，纵使缓慢，驰而不息。**

**公众号：秦怀杂货店**

![](https://img-blog.csdnimg.cn/img_convert/7d98fb66172951a2f1266498e004e830.png)