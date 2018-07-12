import bean.Country;
import dao.ICountryDao;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.MyBatisUtils;

public class MyTest {
  private ICountryDao dao;
  private SqlSession sqlSession;
  @Before
  public void Before(){
    sqlSession=MyBatisUtils.getSqlSession();
    dao=sqlSession.getMapper(ICountryDao.class);
  }
  @Test
  public void TestselectCountryById(){
    Country country=dao.selectCountryById(1);
    System.out.println(country);
  }

  @Test
  public void TestselectCountryById2(){
    Country country=dao.selectCountryById2(1);
    System.out.println(country);
  }


  @After
  public void after(){
    if(sqlSession!=null){
      sqlSession.close();
    }
  }

}
