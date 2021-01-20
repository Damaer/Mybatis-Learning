注：代码已托管在`GitHub`上，地址是：`https://github.com/Damaer/Mybatis-Learning` ，项目是`mybatis-14-oneself-many2one`，需要自取，需要配置maven环境以及`mysql`环境(`sql`语句在`resource`下的`test.sql`中)，觉得有用可以点个小星星。

`docsify`文档地址在：`https://damaer.github.io/Mybatis-Learning/#/`

现在有个数据库查询需求，给出当前新闻栏目的`id`，希望查出父辈栏目，父辈的父辈栏目等等信息。

数据表设计如下：
![](https://img-blog.csdnimg.cn/img_convert/29192b55571a01e02f992bad110400da.png)

实体类设计：
```java
package beans;

import java.util.Set;

public class NewsLabel {
  private Integer id;
  private String name;
  private NewsLabel parent;
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
  public NewsLabel getParent() {
    return parent;
  }
  @Override
  public String toString() {
    return "NewsLabel [id=" + id + ", name=" + name + ", parent=" + parent
            + "]";
  }
  public void setParent(NewsLabel parent) {
    this.parent = parent;
  }

}
```

sql查询接口定义：
```java
public interface INewsLabelDao {
  NewsLabel selectParentByParentId(int pid);
}
```

`sql`定义在`mapper.xml`文件中,可以看到，我们查询的时候调用的是`id`为`“selectParentByParentId”`的sql，返回结果做了一个映射（`resultMap`），`resultMap`的`id`是`“newsLabelMapper”`,`“newsLabelMapper”`中除了`id`映射和`name`映射，还有一个`<association/>`,里面定义的是关联关系定义。
- `property="parent"`：表示映射的属性是`parent`
- `javaType="NewsLabel"`:表示映射的类型是`NewsLabel`
- `column="pid"`:使用`pid`作为参数传递进去再次查询。
- `select="selectParentByParentId"`:查询 `parent` 属性执行的语句​

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="dao.INewsLabelDao">
    <resultMap type="NewsLabel" id="newsLabelMapper">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <association property="parent"
                     javaType="NewsLabel"
                     select="selectParentByParentId"
                     column="pid"/>
    </resultMap>
    <select id="selectParentByParentId" resultMap="newsLabelMapper">
		select id,name,pid from newslabel where id=#{xxx}
	</select>
</mapper>
```

测试类：
```java
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
    NewsLabel children=dao.selectParentByParentId(7);

    System.out.println(children);

  }
  @After
  public void after(){
    if(sqlSession!=null){
      sqlSession.close();
    }
  }

}
```


查询出来结果：
```bash
[service] 2018-07-16 11:54:10,123 - dao.INewsLabelDao.selectParentByParentId -683  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - ==>  Preparing: select id,name,pid from newslabel where id=? 
[service] 2018-07-16 11:54:10,154 - dao.INewsLabelDao.selectParentByParentId -714  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - ==> Parameters: 7(Integer)
[service] 2018-07-16 11:54:10,174 - dao.INewsLabelDao.selectParentByParentId -734  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - ====>  Preparing: select id,name,pid from newslabel where id=? 
[service] 2018-07-16 11:54:10,174 - dao.INewsLabelDao.selectParentByParentId -734  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - ====> Parameters: 4(Integer)
[service] 2018-07-16 11:54:10,181 - dao.INewsLabelDao.selectParentByParentId -741  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - ======>  Preparing: select id,name,pid from newslabel where id=? 
[service] 2018-07-16 11:54:10,181 - dao.INewsLabelDao.selectParentByParentId -741  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - ======> Parameters: 2(Integer)
[service] 2018-07-16 11:54:10,183 - dao.INewsLabelDao.selectParentByParentId -743  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - ========>  Preparing: select id,name,pid from newslabel where id=? 
[service] 2018-07-16 11:54:10,183 - dao.INewsLabelDao.selectParentByParentId -743  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - ========> Parameters: 0(Integer)
[service] 2018-07-16 11:54:10,184 - dao.INewsLabelDao.selectParentByParentId -744  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - <========      Total: 0
[service] 2018-07-16 11:54:10,184 - dao.INewsLabelDao.selectParentByParentId -744  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - <======      Total: 1
[service] 2018-07-16 11:54:10,184 - dao.INewsLabelDao.selectParentByParentId -744  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - <====      Total: 1
[service] 2018-07-16 11:54:10,184 - dao.INewsLabelDao.selectParentByParentId -744  [main] DEBUG dao.INewsLabelDao.selectParentByParentId  - <==      Total: 1
NewsLabel [id=7, name=北京金瓯, parent=NewsLabel [id=4, name=CBA, parent=NewsLabel [id=2, name=体育新闻, parent=null]]]
```

**此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~**

**技术之路不在一时，山高水长，纵使缓慢，驰而不息。**

**公众号：秦怀杂货店**

![](https://img-blog.csdnimg.cn/img_convert/7d98fb66172951a2f1266498e004e830.png)