import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import beans.Student;
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
	public void testselectStudentById(){

		// 第一次查询
		Student student=dao.selectStudentById(1);
		System.out.println(student);
		// 第二次查询
		Student student2=dao.selectStudentById(1);
		System.out.println(student2);
		HashMap
	}

	@After
	public void after(){
		if(sqlSession!=null){
			sqlSession.close();
		}
	}
	
}
