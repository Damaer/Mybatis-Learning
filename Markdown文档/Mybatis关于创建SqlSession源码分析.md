##### 1.为什么我们使用SQLSessionFactoryBuilder的时候不需要自己关闭流？
我们看我们的代码：
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
当我们使用`inputStream = Resources.getResourceAsStream("mybatis.xml");`的时候，我们并需要去关闭inputstream，我们可以查看源码，首先看到`SqlSessionFactoryBuilder().build()`这个方法：
``` java
    // 将inputstream传递进去，调用了另一个分装的build()方法
    public SqlSessionFactory build(InputStream inputStream) {
        return this.build((InputStream)inputStream, (String)null, (Properties)null);
    }
```
跟进去，我们再来看另一个build方法，里面有一个finally模块，无论怎么样都会执行close方法，所以这就是为什么我们在使用的时候为什么不用关闭inputstream的原因：
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
                // 关闭流
                inputStream.close();
            } catch (IOException var13) {
                ;
            }

        }
        return var5;
    }
```
##### 2.Sqlsession是如何创建的？
语句里面执行代码：
``` java
sqlSession=sqlSessionFactory.openSession();
```
我们需要查看源码，我们发现opensession是sqlSessionFactory的一个接口方法，sqlSessionFactory是一个接口。
``` java
public interface SqlSessionFactory {
    // 在这里只贴出了一个方法，其他的就不贴了
    SqlSession openSession();
    }
```
idea选中该方法,`ctrl + alt +B`,我们可以发现有DefaultSqlSessionFactory,和SqlSessionManager这两个类实现了SqlSessionFactory这个接口
<br>![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-2/75470738.jpg)<br>
那么我们需要跟进去DefaultSqlSessionFactory这个类的openSesseion方法,在里面调用了一个封装好的方法：openSessionFromDataSource()
``` java
    public SqlSession openSession() {
        return this.openSessionFromDataSource(this.configuration.getDefaultExecutorType(), (TransactionIsolationLevel)null, false);
    }
```
当然在`DefaultSqlSessionFactory`这个类里面还有一个方法，参数是autoCommit，也就是可以指定是否自动提交：
``` java
    public SqlSession openSession(boolean autoCommit) {
        return this.openSessionFromDataSource(this.configuration.getDefaultExecutorType(), (TransactionIsolationLevel)null, autoCommit);
    }
```
我们再跟进去源码,我们会发现有一个参数是`autoCommit`，也就是自动提交，我们可以看到上一步传值是false，也就是不会自动提交，通过configuration（主配置）获取environment（运行环境），然后通过environment（环境）开启和获取一个事务工厂，通过事务工厂获取事务对象Transaction，通过事务对象创建一个执行器executor，Executor是一个接口，实现类有比如SimpleExecutor，BatchExecutor，ReuseExecutor，所以我们下面代码里的execType，是指定它的类型，生成指定类型的Executor，把引用给接口对象，有了执行器之后就可以return一个DefaultSqlSession对象了。
``` java
    private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
        Transaction tx = null;
        DefaultSqlSession var8;
        try {
            // configuration是主配置文件
            Environment environment = this.configuration.getEnvironment();
            // 获取事务工厂，事务管理器可以使jdbc之类的
            TransactionFactory transactionFactory = this.getTransactionFactoryFromEnvironment(environment);
            // 获取事务对象Transaction
            tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
            // 通过事务对象创建一个执行器executor
            Executor executor = this.configuration.newExecutor(tx, execType);
            // DefaultSqlSession是SqlSession实现类，创建一个DefaultSqlSession并返回
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
我们跟` var8 = new DefaultSqlSession(this.configuration, executor, autoCommit);`这句代码,我们这是初始化函数赋值于各个成员变量，我们发现里面有一个dirty成员，这是干什么用的呢？从名字上来讲我们理解是脏的，这里既然设置为false，那就是不脏的意思。那到底什么是脏呢？**脏是指内存里面的数据与数据库里面的数据存在不一致的问题，如果一致就是不脏的**
后面会解释这个dirty的作用之处，到这里一个SqlSession就创建完成了。
``` java
    public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
        this.configuration = configuration;
        this.executor = executor;
        this.dirty = false;
        this.autoCommit = autoCommit;
    }
```
##### 3.增删改是怎么执行的
我们使用到这句代码：
``` java
sqlSession.insert("insertStudent",student);
```
我们发现同样是接口方法，上面我们知道SqlSession其实是DefaultSqlSession所实现的接口，那么我们跟进去DefaultSqlSession的insert()方法，我们发现其实inset方法底层也是实现了update这个方法，同样的delete方法在底层也是调用了update这个方法，**增，删，改本质上都是改**。
``` java
public int insert(String statement, Object parameter) {
    return this.update(statement, parameter);
}
public int update(String statement) {
    return this.update(statement, (Object)null);
}
```
那么我们现在跟进去update方法中，dirty变成ture，表明即将改数据，所以数据库数据与内存中数据不一致了，statement是我们穿过来的id，这样就可以通过id拿到statement的对象，然后就通过执行器执行修改的操作：
``` java
    public int update(String statement, Object parameter) {
        int var4;
        try {
            // dirty变成ture，表明数据和数据库数据不一致，需要更新
            this.dirty = true;
            // 通过statement的id把statement从配置中拿到映射关系
            MappedStatement ms = this.configuration.getMappedStatement(statement);
            // 执行器执行修改的操作
            var4 = this.executor.update(ms, this.wrapCollection(parameter));
        } catch (Exception var8) {
            throw ExceptionFactory.wrapException("Error updating database.  Cause: " + var8, var8);
        } finally {
            ErrorContext.instance().reset();
        }
        return var4;
    }
```
##### 4.SqlSession.commit()为什么可以提交事务(transaction)？
首先，我们使用到的源码，同样选择DefaultSqlSession这个接口的方法,我们发现commit里面调用了另一个commit方法，传进去一个false的值：
``` java
    public void commit() {
        this.commit(false);
    }
```
我们跟进去，发现上面传进去的false是变量force，里面调用了一个`isCommitOrRollbackRequired(force)`方法，执行的结果返回给commit方法当参数。
``` java
public void commit(boolean force) {
    try {
        this.executor.commit(this.isCommitOrRollbackRequired(force));
        // 提交之后dirty置为false，因为数据库与内存的数据一致了。
        this.dirty = false;
    } catch (Exception var6) {
        throw ExceptionFactory.wrapException("Error committing transaction.  Cause: " + var6, var6);
    } finally {
        ErrorContext.instance().reset();
    }
}
```
我们跟进去`isCommitOrRollbackRequired(force)`这个方法,这个方法从命名上是**需要提交还是回滚**的意思。在前面我们知道autoCommit是false，那么取反之后就是true，关于dirty我们知道前面我们执行过insert()方法，insert的底层调用了update方法，将dirty置为true，表示即将修改数据，那我们知道`!this.autoCommit && this.dirty`的值就是true，那么就短路了，所以整个表达式的值就是true。
``` java
private boolean isCommitOrRollbackRequired(boolean force) {
    return !this.autoCommit && this.dirty || force;
}
```
返回上一层的，我们知道`this.isCommitOrRollbackRequired(force)`的返回值是true。
```
this.executor.commit(this.isCommitOrRollbackRequired(force));
```
跟进去commit方法,这个commit方法是一个接口方法，实现接口的有BaseExecutor，还有CachingExecutor，我们选择BaseExecutor这个接口实现类:
``` java
// required是true
public void commit(boolean required) throws SQLException {
    // 如果已经 关闭，那么就没有办法提交，抛出异常
    if (this.closed) {
        throw new ExecutorException("Cannot commit, transaction is already closed");
    } else {
        this.clearLocalCache();
        this.flushStatements();
        // 如果required是true，那么就提交事务
        if (required) {
            this.transaction.commit();
        }
    }
}
```
##### 5.为什么sqlsession关闭就不需要回滚了？
假如我们在上面已经提交过了，那么dirty的值就为false。我们使用的是`sqlSession.close();`，跟进去源码，同样是接口，我们跟DefaoultSqlsession的方法，同样调用了isCommitOrRollbackRequired()这个方法：
``` java
    public void close() {
        try {
            this.executor.close(this.isCommitOrRollbackRequired(false));
            this.dirty = false;
        } finally {
            ErrorContext.instance().reset();
        }
    }
```
我们跟进去isCommitOrRollbackRequired(false)这个方法,我们知道force传进来的值是false,autoCommit是false（只要我们使用无参的`sqlSessionFactory.openSession();`），取反之后**！autoCommit**是true，但是dirty已经是false,所以`!this.autoCommit && this.dirty`的值是false，那么force也是false，所以整一个表达式就是false：
``` java
    private boolean isCommitOrRollbackRequired(boolean force) {
        return !this.autoCommit && this.dirty || force;
    }
```
我们返回上一层，executor.close()方法，参数是false：
``` java
this.executor.close(this.isCommitOrRollbackRequired(false));
```
跟进去close()方法,forceRollback的值是false,我们发现有一个`this.rollback(forceRollback)`：
``` java
public void close(boolean forceRollback) {
        try {
            try {
                this.rollback(forceRollback);
            } finally {
                // 最后如果事务不为空，那么我们就关闭事务
                if (this.transaction != null) {
                    this.transaction.close();
                }
            }
        } catch (SQLException var11) {
            log.warn("Unexpected exception on closing transaction.  Cause: " + var11);
        } finally {
            this.transaction = null;
            this.deferredLoads = null;
            this.localCache = null;
            this.localOutputParameterCache = null;
            this.closed = true;
        }
    }
```
我们跟进去rollback()这个方法,我们可以发现required是fasle，所以` this.transaction.rollback();`是不会执行的，这个因为我们在前面做了提交了，所以是不用回滚的：
``` java
     public void rollback(boolean required) throws SQLException {
        if (!this.closed) {
            try {
                this.clearLocalCache();
                this.flushStatements(true);
            } finally {
                if (required) {
                    this.transaction.rollback();
                }

            }
        }

    }
```
假如我们现在执行完insert()方法，但是没有使用commit(),那么现在的dirty就是true，也就是数据库数据与内存的数据不一致。我们再执行close()方法的时候，dirty是true，!this.autoCommit是true，那么整个表达式就是true。
``` java
    private boolean isCommitOrRollbackRequired(boolean force) {
        return !this.autoCommit && this.dirty || force;
    }
```
返回上一层，close的参数就会变成true
``` java
this.executor.close(this.isCommitOrRollbackRequired(false));
```
close()方法里面调用了` this.rollback(forceRollback);`，参数为true，我们跟进去,可以看到确实执行了回滚：
``` java
     public void rollback(boolean required) throws SQLException {
        if (!this.closed) {
            try {
                this.clearLocalCache();
                this.flushStatements(true);
            } finally {
                if (required) {
                    this.transaction.rollback();
                }

            }
        }

    }
```
所以只要我们执行了提交（commit），那么关闭的时候就不会执行回滚，只要没有提交事务，就会发生回滚，所以里面的dirty是很重要的。