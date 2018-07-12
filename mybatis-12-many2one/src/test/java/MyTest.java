import bean.Country;
import bean.Minister;
import dao.IMinisterDao;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.MyBatisUtils;

public class MyTest {
  private IMinisterDao dao;
  private SqlSession sqlSession;
  @Before
  public void Before(){
    sqlSession=MyBatisUtils.getSqlSession();
    dao=sqlSession.getMapper(IMinisterDao.class);
  }
  @Test
  public void TestselectMinisterById(){
    Minister minister = dao.selectMinisterById(1);
    System.out.println(minister);
  }

  @Test
  public void TestselectMinisterById2(){
    Minister minister = dao.selectMinisterById2(1);
    System.out.println(minister);
  }


  @After
  public void after(){
    if(sqlSession!=null){
      sqlSession.close();
    }
  }

}
