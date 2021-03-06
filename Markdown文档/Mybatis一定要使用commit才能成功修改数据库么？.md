为什么我们有时候不使用commit也能修改数据库成功？

[TOC]
# 1.从数据库的层面上来讲，其实这个主要看你用什么“存储引擎”
像以下的代码就是使用了自动提交的mysql引擎。
```sql
CREATE TABLE `student` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(20) NOT NULL , 
`age` INT NOT NULL , `score` DOUBLE NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM; 
```
如果是不支持事务的引擎，如myisam，则是否commit都没有效的。
如果是支持事务的引擎，如innodb，则有系统参数设置是否自动commit，查看参数如下：
```xml
mysql> show variables like '%autocommit%';
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| autocommit    | ON    |
+---------------+-------+
1 row in set (0.00 sec)
mysql>
```
显示结果为on表示事务自动提交，不用手工去commit,当然，你可以设置其为OFF，然后自己手工去commit。
# 2.使用myIsam引擎，不需要commit

比如下面的代码：
``` java
public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
	public void insertStu(Student student) {
		try {
			InputStream inputStream;
			inputStream = Resources.getResourceAsStream("mybatis.xml");
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
			sqlSession=sqlSessionFactory.openSession();
			sqlSession.insert("insertStudent",student);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
```
我们可以看到已经更新了一行，我们完全不使用commit
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-31/43350162.jpg)
# 3.创建数据表（使用Innodb），也不提交，结果没有插入

``` sql
CREATE TABLE `student` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(20) NOT NULL , 
`age` INT NOT NULL , `score` DOUBLE NOT NULL , PRIMARY KEY (`id`)) ENGINE = Innodb; 
```
我们再执行插入时，发现控制台输出是这样的：

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-31/90272953.jpg)

好像输入也成功了，但是我去数据库看了一下，居然是空的：

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-5-31/12018810.jpg)

那我们将代码换成这样，加入提交事务：
``` java
public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
	public void insertStu(Student student) {
		try {
			InputStream inputStream;
			inputStream = Resources.getResourceAsStream("mybatis.xml");
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
			sqlSession=sqlSessionFactory.openSession();
			sqlSession.insert("insertStudent",student);
			// 提交事务
			sqlSession.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
		    if(sqlSession!=null){
		        sqlSession.close();
            }
        }
	}
}
```
执行代码，我们会发现事务提交成功了，同时我们也关闭数据库了：

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-2/73693242.jpg)

打开数据库，我们会发现，居然没有id为1的记录，为什么直接跳到2了呢？还记不记得之前插入一次但是没有提交，所以问题就在这里。上一次的提交已经写到事务里面了，只是没有提交，所以这一次提交的时候，上一次默认已经占用了那条记录，只是不写进数据库中。有提交就可以回滚，所以要使用回滚的话，可以使用`sqlsession.rollback()`。

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-2/19062569.jpg)

如果我们使用sqlsession.close()的话，我们就不需要使用回滚了。
下面是我把commit去掉，但是留下close的结果，我们可以看到没有commit，但是已经会自动rollback了，所以只要使用`sqlsession.close()`就会自动回滚再关闭。

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-2/80015665.jpg)
