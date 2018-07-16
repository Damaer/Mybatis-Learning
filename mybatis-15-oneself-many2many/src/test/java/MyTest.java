import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.Student;
import dao.IStudentDao;
import utils.MyBatisUtils;

public class MyTest {
  private IStudentDao dao;
  private SqlSession sqlSession;
  @Before
  public void Before(){
    sqlSession=MyBatisUtils.getSqlSession();
    dao=sqlSession.getMapper(IStudentDao.class);
  }
  @Test
  public void TestselectMinisterById(){
    Student student=dao.selectStudentById(1);
    System.out.println(student);
  }
  @After
  public void after(){
    if(sqlSession!=null){
      sqlSession.close();
    }
  }

}

