import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import beans.Country;
import dao.ICountryDao;
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
		System.out.println(country.getCid());
		// System.out.println(country.getMinisters());
	}
	@After
	public void after(){
		if(sqlSession!=null){
			sqlSession.close();
		}
	}
	
}
