#### 1.使用工具类获取sqlSession实例对象
在上一个demo中，处理了多个namespace的问题，那么我们可以看到代码还是会有一定的冗余，比如下面这段代码中我们每一个增删改查操作都需要读取一遍配置文件：
```
public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
	public void insertStu(Student student) {
		try {
			InputStream inputStream;
			inputStream = Resources.getResourceAsStream("mybatis.xml");
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
			sqlSession=sqlSessionFactory.openSession();
			sqlSession.insert("mapper1.insertStudent",student);
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
我们的思路应该是写一个工具类来替我们获取配置文件的信息，只要返回一个sqlSession实例就可以了。所以就有了MyBatisUtils.class,下面这样的方式，只要使用`sqlSession=MyBatisUtils.getSqlSession();`就可以获取到sqlsession的实例。
```
public class MyBatisUtils {
    public SqlSession getSqlSession(){
        InputStream is;
        try {
            is = Resources.getResourceAsStream("mybatis.xml");
            return new SqlSessionFactoryBuilder().build(is).openSession();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
```
但是以上的方式并不是最好的，还是会浪费资源，如果sqlsession已经存在了，这段代码还是会去创建一个新的实例对象。我们知道sqlsession没有可修改的属性，是线程安全的，所以我们需要把它改写成单例模式。
``` java
public class MyBatisUtils {
    static private SqlSessionFactory sqlSessionFactory;
    // 单例模式
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
使用的时候只需要获取即可
```
sqlSession=MyBatisUtils.getSqlSession();
```
#### 2.DB配置改造成读取配置文件
现在我们需要将DB使用配置文件读取，不是用xml配置，很多人会问，为什么这样做，有人可能会回答是因为改动的时候容易改，但是xml改动的时候不是挺容易改么？<br>
其实，写到属性文件的原因与上面的一样，就是为了更好改，要是上线了需要该数据库我们只需要改动`<properties  resource="jdbc_mysql.properties">`这一个地方就可以了，xml文件就变得更加简洁清晰了。

原来在mybatis.xml文件里配置的是：
``` 
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
  		<!-- 配置运行环境 -->
  		<!-- default 表示默认使用哪一个环境，可以配置多个，比如开发时的测试环境，上线后的正式环境等 -->
  		<environments default="mysqlEM">
  			<environment id="mysqlEM">
                <!--事务管理器-->
  				<transactionManager type="JDBC">		
  				</transactionManager>
                <!--数据源-->
  				<dataSource type="POOLED">
  					<property name="driver" value="com.mysql.jdbc.Driver"/>
  					<property name="url" value="jdbc:mysql://127.0.0.1:3306/test"/>
  					<property name="username" value="root"/>
  					<property name="password" value="123456"/>
  				</dataSource>
  			</environment>
  		</environments>
  		<!-- 注册映射文件 -->
  		<mappers>
  			<mapper resource="mapper/mapper.xml"/>
            <mapper resource="mapper/mapper1.xml"/>
  		</mappers>
  </configuration>
```
现在我们定义一个jdbc-mysql.properties文件，将数据库连接的属性直接写进属性文件里(我们可以有好几个不一样的.properties文件,配置着不同的数据库)：
```
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/test
jdbc.user=root
jdbc.password=123456
```
将mybatis.xml改造成(注意下面需要配置属性文件，然后才能在environment标签里面使用，直接使用key就可以了，属性文件配置是按照key-value的模式配置的)：
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
        <!-- 配置数据库文件 -->
        <properties  resource="jdbc_mysql.properties">
        </properties>
  		<!-- 配置运行环境 -->
  		<!-- default 表示默认使用哪一个环境，可以配置多个，比如开发时的测试环境，上线后的正式环境等 -->
  		<environments default="mysqlEM">
            <environment id="mysqlEM">
                <transactionManager type="JDBC">
                </transactionManager>
                <environment type="POOLED">
                    <property name="driver" value="${jdbc.driver}"/>
                    <property name="url" value="${jdbc.url}"/>
                    <property name="username" value="${jdbc.user}"/>
                    <property name="password" value="${jdbc.password}"/>
                </dataSource>
            </environment>
  		</environments>
  		<!-- 注册映射文件 -->
  		<mappers>
  			<mapper resource="mapper/mapper.xml"/>
            <mapper resource="mapper/mapper1.xml"/>
  		</mappers>
  </configuration>
```