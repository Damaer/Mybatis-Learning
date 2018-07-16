package utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

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
  private static SqlSessionFactory sqlSessionFactory;

  public static SqlSession getSqlSession() {
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
