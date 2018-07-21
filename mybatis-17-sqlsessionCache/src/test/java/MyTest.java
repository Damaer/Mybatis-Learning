import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.IStudentDao2;
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
		Student student=dao.selectStudentById(17);
		System.out.println(student);
		// 第二次查询
		Student student2=dao.selectStudentById(17);
		System.out.println(student2);
	}
    @Test
    public void testDiffereentId2(){

        // 第一次查询
        Student student=dao.selectStudentById(17);
        System.out.println(student);
        // 第二次查询，测试不同的id，同一个namespace
        Student student2=dao.selectStudentById2(17);
        System.out.println(student2);
    }

    @Test
    public void testDiffereentNamespaceSameId(){

        // 第一次查询
        Student student=dao.selectStudentById(17);
        System.out.println(student);
        // 第二次查询，测试相同的id，不同的namespace
        IStudentDao2 dao2= sqlSession.getMapper(IStudentDao2.class);
        Student student2=dao2.selectStudentById(17);
        System.out.println(student2);
    }


    @Test
    public void test03(){

        // 第一次查询
        Student student=dao.selectStudentById(17);
        System.out.println(student);
        //插入学生
        Student student1 = new Student("12112",12,21.6);
        student1.setId(18);
        //dao.insertStudent(student1);
        //dao.updateStudent(student1);
        dao.deleteStudentById(18);
        student=dao.selectStudentById(17);
        System.out.println(student);
    }
	@After
	public void after(){
		if(sqlSession!=null){
			sqlSession.close();
		}
	}

}
