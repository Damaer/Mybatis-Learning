在之前的代码中我们的运行过程再梳理一下,首先我们执行Test，调用dao接口方法
<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-27/31254729.jpg)<br>
接口的定义:<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-27/18077943.jpg)<br>
调用接口的实现类方法：<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-27/24791381.jpg)<br>
最后才是调用真正的sql:<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-27/14184681.jpg)<br>
那么我们想是不是可以减少一步呢？我们不用自己实现接口，只需要将接口的名字和mapper文件的namespace对应起来，将接口里面的方法名与sql语句标签的id对应起来是不是就可以了呢？**事实上，mybatis提供了这样的做法，这就是mapper动态代理。**<br>
#### mapper动态代理的例子
首先主配置文件（Mybatis.xml），在里面配置数据库连接信息，注册需要扫描的mapper文件:<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-29/77037679.jpg)<br>
定义数据库查询的接口，里面每一个接口的名字很重要，需要和mapper里面每一条sql对应起来：<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-29/91175625.jpg)<br>
定义mapper文件（namespace要写我们定义的接口，而每条sql的id与我们的接口方法名字对应起来）:<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-29/80749780.jpg)<br>
那我们在使用的时候:<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-29/17855493.jpg)<br>
我们在前面还写到过一个selectStudentMap方法，但是里面调用的是和SelectList一样的sql，在接口的实现类里面我们自己处理了一下，但是现在使用自动实现的话，底层只会调用SelectOne或者SelectList方法，所以这个方法会报错，如果接受类型是list，那么框架会自动使用selectList方法，否则就会选择selectOne()这个方法。<br>
在这里我们使用的是返回的是map，所以自动选择返回selectOne()方法，那么就会报错。如果我们需要使用自动返回map的话，可以自己定一个map，或者返回list之后再处理，这个知识点后面再介绍，有兴趣可以访问：[mybatis的mapper返回map结果集](https://blog.csdn.net/lei32323/article/details/72831093)
#### mapper动态代理的原理
打一个断点在sqlSession.getMapper()方法上：<br>
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-30/99377126.jpg)<br>
我们可以看到执行下面的接口方法(接口`SqlSession`的方法)
``` java
<T> T getMapper(Class<T> var1);
```
这是一个接口，我们可以看到实现接口的有两个类，一个是DefaultSqlSession,一个是SqlSessionManager，我们需要看的是`DefaultSqlSession`下面的接口：
```
 public <T> T getMapper(Class<T> type) {
    return this.configuration.getMapper(type, this);
  }
```
我们知道，在创建sqlsession的时候，confiiguration这个配置对象已经创建完成。跟进去,这是使用mapper注册器对象的getMapper方法，将当前的sqlSession对象传递进去：
```
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    return mapperRegistry.getMapper(type, sqlSession);
  }
```
我们跟进去源码，可以发现里面使用`knownMappers.get(type)`来获取mapper代理工厂，这个konwnMappers是一个hashMap，这个hashMap里面已经初始化了mapperProxyFactory对象了，获取到工厂对象之后，再去使用sqlSession实例化：
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
实例化的时候，使用了mapper动态代理：
``` java
public T newInstance(SqlSession sqlSession) {
    final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
    return newInstance(mapperProxy);
  }
protected T newInstance(MapperProxy<T> mapperProxy) {
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }
```
从下面的debug结果中我们可以看到，这是动态代理的结果：<br>
![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-7-4/74424148.jpg)