[TOC]
## 1.回顾Mybatis执行sql的流程
在之前的代码中我们的运行过程再梳理一下,首先我们执行Test，调用dao接口方法

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-27/31254729.jpg)

接口的定义:
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-27/18077943.jpg)

调用接口的实现类方法：
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-27/24791381.jpg)

最后才是调用真正的sql:
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-27/14184681.jpg)


上面的代码是在接口实现类里面自己去执行id，查找并执行mapper文件里面的sql，**那么我们想是不是可以减少一步呢？**

如果我们不用自己实现接口，只需要将接口的名字和mapper文件的namespace对应起来，将接口里面的方法名与sql语句标签的id对应起来是不是就可以了呢？

**事实上，mybatis提供了这样的做法，这就是mapper动态代理。**

## 2.mapper动态代理怎么写？
首先主配置文件（`Mybatis.xml`），在里面配置数据库连接信息，注册需要扫描的`mapper`文件:

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-29/77037679.jpg)

定义数据库查询的接口，里面每一个接口的名字很重要，需要和`mapper`里面每一条`sql`对应起来：

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-29/91175625.jpg)

定义`mapper`文件(**namespace是接口的全限定类名**):
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-29/80749780.jpg)

那我们在使用的时候，需要使用`sqlSession.getMapper()`方法，里面传入的是接口，意思是通过**接口的全限定名**，也就是前面在`mapper.xml`文件里面配置的命名空间`nameSpace`,这样一来，就是获取到了代理类，将`dao`和`mapper.xml`文件关联起来了，而每条`sql`的`id`与我们的接口方法名字对应起来）

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-29/17855493.jpg)

我们在前面还写到过一个`selectStudentMap()`方法，但是里面调用的是和`SelectList()`一样的`sql`，在接口的实现类里面我们自己处理了一下，但是现在使用自动实现的话，底层只会调用`SelectOne()`或者`SelectList()`方法，所以这个方法会报错，如果接受类型是`list`，那么框架会自动使用`selectList()`方法，否则就会选择`selectOne()`这个方法。

在这里我们使用的是返回的是`map`，所以自动选择返回`selectOne()`方法，那么就会报错。如果我们需要使用自动返回`map`的话，可以自己定一个`map`，或者返回`list`之后再处理，这个知识点后面再介绍，有兴趣可以访问：[mybatis的mapper返回map结果集](https://blog.csdn.net/lei32323/article/details/72831093)

## 3.mapper动态代理怎么做的？
打一个断点在`sqlSession.getMapper()`方法上：

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-30/99377126.jpg)


我们可以看到执行下面的接口方法(接口`SqlSession`的方法)
``` java
<T> T getMapper(Class<T> var1);
```
这是一个接口，我们可以看到实现接口的有两个类，一个是`DefaultSqlSession`,一个是`SqlSessionManager`，我们需要看的是`DefaultSqlSession`下面的接口：

``` java
 public <T> T getMapper(Class<T> type) {
    return this.configuration.getMapper(type, this);
  }
```

我们知道，在创建`sqlsession`的时候，`confiiguration`这个配置对象已经创建完成。跟进去,这是使用`mapper`注册器对象的`getMapper()`方法，将当前的`sqlSession`对象传递进去：

```java
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    return mapperRegistry.getMapper(type, sqlSession);
  }
```
我们跟进去源码，可以发现里面使用`knownMappers.get(type)`来获取`mapper`代理工厂，这个`konwnMappers`是一个`hashMap`，这个`hashMap`里面已经初始化了`mapperProxyFactory`对象了，获取到工厂对象之后，再去使用`sqlSession`实例化：

``` java
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
    if (mapperProxyFactory == null) {
      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    }
    try {
      return mapperProxyFactory.newInstance(sqlSession);
    } catch (Exception e) {
      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
  }
```
实例化的时候，使用了`mapper`动态代理：
``` java
public T newInstance(SqlSession sqlSession) {
    final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
    return newInstance(mapperProxy);
  }
protected T newInstance(MapperProxy<T> mapperProxy) {
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }
```
从下面的debug结果中我们可以看到，这是动态代理的结果,我们看到的是`dao`，但是动态代理对这个`dao`做了增强，实则是一个`mapperProxy`。

![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-7-4/74424148.jpg)

**【作者简介】**：  
秦怀，公众号【**秦怀杂货店**】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。这个世界希望一切都很快，更快，但是我希望自己能走好每一步，写好每一篇文章，期待和你们一起交流。

此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~ 


![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/blog/20201012000828.png)