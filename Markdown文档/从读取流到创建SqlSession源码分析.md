我们使用sqlSession之前，需要去获取配置文件，获取InputStream输入流，通过SqlSessionFactoryBuilder获取sqlSessionFactory对象，从而获取sqlSession。
``` java
InputStream is = Resources.getResourceAsStream("mybatis.xml");
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
SqlSession sqlSession = sqlSessionFactory.openSession();
```
#### Resources.getResourceAsStream("mybatis.xml")到底做了什么？
1.首先我们来看`InputStream is = Resources.getResourceAsStream("mybatis.xml");`这句话到底替我们干了什么，下面可以看出在里面调用了另一个内部方法,resource是全局配置的文件名：
```
public static InputStream getResourceAsStream(String resource) throws IOException {
    // 从这里字面意思是传一个空的类加载器进去，还有全局配置文件名，从方法名的意思就是
    // 将配置文件读取，转化成输入流
    return getResourceAsStream((ClassLoader)null, resource);
  }
```
2.跟进方法中，我们可以知道在里面调用ClassLoaderWrapper类的一个实例对象的getResourceAsStream()方法，这个classLoaderWrapper怎么来的呢？这个是Resources.class的一个成员属性,那么这个ClassLoaderWrapper是什么东西呢？<br>
在Resources.class中我们只是使用`private static ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();`创建一个classLoaderWrapper对象。
ClassLoaderWrapper其实是一个ClassLoader(类加载器)的包装类，其中包含了几个ClassLoader对象，一个defaultClassLoader，一个systemClassLoader，通过内部控制，可以确保返回正确的类加载器给系统使用。我们可以当成一个mybatis自定义过的类加载器。
``` java
public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
    InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
    if (in == null) {
      throw new IOException("Could not find resource " + resource);
    } else {
      return in;
    }
  }
```
3.我们可以看出调用了下面这个内部方法,里面调用了封装的方法，一个是获取当前的类加载器，另一个是传进来的文件名：
```
  public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
    return this.getResourceAsStream(resource, this.getClassLoaders(classLoader));
  }
```
4.查看getClassLoaders这个方法，可以看到里面初始化了一个类加载器的数组，里面有很多个类加载器，包括默认的类加载器，当前线程的上下文类加载器，系统类加载器等。
``` java
ClassLoader[] getClassLoaders(ClassLoader classLoader) {
    return new ClassLoader[]{classLoader, this.defaultClassLoader, Thread.currentThread().getContextClassLoader(), this.getClass().getClassLoader(), this.systemClassLoader};
  }
```
5.进入`getResourceAsStream(String resource, ClassLoader[] classLoader)`这个方法内部,我们可以看到里面遍历所有的类加载器，使用类加载器来加载获取InputStream对象，我们可以知道里面是选择第一个适合的类加载器，如果我们不传类加载器进去，那么第一个自己定义的类加载器就是null，那么就会默认选择第二个默认类加载器，而且我们可以知道如果文件名前面没有加“/”，获取到空对象的话，会自动加上“/”再访问一遍：
``` java
 InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
    ClassLoader[] arr$ = classLoader;
    int len$ = classLoader.length;
    for(int i$ = 0; i$ < len$; ++i$) {
      ClassLoader cl = arr$[i$];
      if (null != cl) {
        InputStream returnValue = cl.getResourceAsStream(resource);
        if (null == returnValue) {
          returnValue = cl.getResourceAsStream("/" + resource);
        }
        if (null != returnValue) {
          return returnValue;
        }
      }
    }
    return null;
  }
```
6.我们进入类加载器加载资源文件的代码中,我们可以看到首先获取全路径的url，然后再调用openStream()：
``` java
public InputStream getResourceAsStream(String name) {
    URL url = getResource(name);
    try {
        return url != null ? url.openStream() : null;
    } catch (IOException e) {
        return null;
    }
}
```
6.1.我们跟进getResource(name)这个方法，我们可以看到里面都是调用parent的getResource()方法，如果已经是父加载器，那么就使用`getBootstrapResource(name)`获取，如果获取出来是空的，再根据`getBootstrapResource(name)`方法获取。
``` java
public URL getResource(String name) {
        URL url;
        if (parent != null) {
            url = parent.getResource(name);
        } else {
            url = getBootstrapResource(name);
        }
        if (url == null) {
            url = findResource(name);
        }
        return url;
    }
```
6.1.1我们跟进去`getBootstrapResource(name);`
``` java
private static URL getBootstrapResource(String name) {
        URLClassPath ucp = getBootstrapClassPath();
        Resource res = ucp.getResource(name);
        return res != null ? res.getURL() : null;
    }
```
6.1.1.1我们看到`getBootstrapClassPath()`这个方法，这个方法的里面调用了引入的包，读取的是类加载器的加载路径,这个方法到此为止，再深入就回不去了：)。
``` java
static URLClassPath getBootstrapClassPath() {
        return sun.misc.Launcher.getBootstrapClassPath();
    }
```
6.1.1.2 我们看`ucp.getResource(name)`这个方法，我们可以看到在里面调用了这个方法,这个方法主要是查找缓存，然后遍历找到第一个符合条件的加载器来获取resource，到此我们不再深究下去，得往上一层回头看：
```
public Resource getResource(String var1, boolean var2) {
    if (DEBUG) {
      System.err.println("URLClassPath.getResource(\"" + var1 + "\")");
    }

    int[] var4 = this.getLookupCache(var1);

    URLClassPath.Loader var3;
    for(int var5 = 0; (var3 = this.getNextLoader(var4, var5)) != null; ++var5) {
      Resource var6 = var3.getResource(var1, var2);
      if (var6 != null) {
        return var6;
      }
    }

    return null;
  }
```
我们知道getBootstrapResource(name)里面主要是url（文件资源的路径），然后使用`url.openStream()`去获取stream流：
``` java
public final InputStream openStream() throws java.io.IOException {
        return openConnection().getInputStream();
    }
```
我们来看openConnection()方法,里面调用的是一个抽象方法，获取的是一个URLConnection（url连接对象）：
``` java
public URLConnection openConnection() throws java.io.IOException {
        return handler.openConnection(this);
    }
```
再看getInputStream()这个方法，我们可以看到这是一个接口方法，我们找到FileURLConnection的这个方法,这是一个单线程处理文件URL的inputstream的方法：
```
  public synchronized InputStream getInputStream() throws IOException {
    this.connect();
    if (this.is == null) {
      if (!this.isDirectory) {
        throw new FileNotFoundException(this.filename);
      }
      FileNameMap var3 = java.net.URLConnection.getFileNameMap();
      StringBuffer var4 = new StringBuffer();
      if (this.files == null) {
        throw new FileNotFoundException(this.filename);
      }
      Collections.sort(this.files, Collator.getInstance());
      for(int var5 = 0; var5 < this.files.size(); ++var5) {
        String var6 = (String)this.files.get(var5);
        var4.append(var6);
        var4.append("\n");
      }
      this.is = new ByteArrayInputStream(var4.toString().getBytes());
    }
    return this.is;
  }
```
到这里，整个获取inputstream的过程已经结束，只要把返回值往上一层返回就可以得到这个配置文件所需要的inputstream。

#### new SqlSessionFactoryBuilder().build(is)的运行原理
首先SqlSessionFactoryBuilder的无参数构造方法是没有任何操作的：
```
public SqlSessionFactoryBuilder() {
  }
```
那么我们看`build(is)`这个方法,里面调用了一个封装方法，一个是inputstream，一个是string，一个是属性对象：
``` java
public SqlSessionFactory build(InputStream inputStream) {
    return this.build((InputStream)inputStream, (String)null, (Properties)null);
  }
```
跟进去,我们可以看到在里面shiyong了xmlconfigbuilder，也就是xml配置构造器，实例化一个xml配置对象，可想而知，也就是我们的mybatis.xml所对应的配置对象构造器，在里面调用了另一个build()方法：
``` java
  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    SqlSessionFactory var5;
    try {
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      var5 = this.build(parser.parse());
    } catch (Exception var14) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", var14);
    } finally {
      ErrorContext.instance().reset();

      try {
        inputStream.close();
      } catch (IOException var13) {
        ;
      }

    }

    return var5;
  }
```
我们可以看到调用的另一个build方法,也就是使用配置对象构建一个DefaultSqlSessionFactory对象，在上面返回这个对象，也就是我们的sqlsessionFactory。
```
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }
```
#### sqlSessionFactory.openSession()获取sqlSession
我们可以看到其实这个是sqlSessionFactory的一个接口，其实现类是DefaultSqlSessionFactory，那么方法如下：
``` java
  public SqlSession openSession() {
    return this.openSessionFromDataSource(this.configuration.getDefaultExecutorType(), (TransactionIsolationLevel)null, false);
  }
```
我们查看openSessionFromDataSource()这个方法，从名字可以大概知道是从数据源加载Sqlsession，里面可以指定执行器类型，事物隔离级别，还有是否自动提交，如果不设定，那么默认是null以及false，在方法内主要做的是将配置文件对象的环境取出来构造事务工厂，配置执行器等，返回一个DefaultSqlSession的实例。
``` java
  private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    DefaultSqlSession var8;
    try {
      Environment environment = this.configuration.getEnvironment();
      TransactionFactory transactionFactory = this.getTransactionFactoryFromEnvironment(environment);
      tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
      Executor executor = this.configuration.newExecutor(tx, execType);
      var8 = new DefaultSqlSession(this.configuration, executor, autoCommit);
    } catch (Exception var12) {
      this.closeTransaction(tx);
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + var12, var12);
    } finally {
      ErrorContext.instance().reset();
    }
    return var8;
  }
```
到此为止，一个sqlsession对象就根据配置文件创建出来了。