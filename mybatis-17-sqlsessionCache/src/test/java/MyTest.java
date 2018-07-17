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
	public void testInsert(){

		Student student=new Student("enasden",21,94.6);
		System.out.println("student="+student);
		dao.insertStudent(student);
		System.out.println("student="+student);

		sqlSession.commit();
	}
	@Test
	public void testinsertStudentCacheId(){
		Student student=new Student("helloworld",14,94.6);
		System.out.println("student="+student);
		dao.insertStudentCacheId(student);;
		System.out.println("student="+student);
	}
	@Test
	public void testdeleteStudentById(){
		dao.deleteStudentById(20);

	}
	@Test
	public void testUpdate(){
		Student student=new Student("lallalalla",14,94.6);
		student.setId(1);
		dao.updateStudent(student);

	}

	@Test
	public void testselectList(){
		List<Student>students=dao.selectAllStudents();
		if(students.size()>0){
			for(Student student:students){
				System.out.println(student);
			}
		}
	}
	@Test
	public void testselectStudentById(){
		Student student=dao.selectStudentById(1);
		System.out.println(student);
	}

	@Test
	public void testselectStudentByName(){
		List<Student>students=dao.selectStudentsByName("hello");
		if(students.size()>0){
			for(Student student:students)
				System.out.println(student);
		}
		
	}
	
	
	@Test
	public void testselectStudentByNameAndAge(){
		Student stu=new Student("lallal", 1212, 40);
		Map<String,Object>map=new HashMap<String, Object>();
		map.put("nameCon", "abc");
		map.put("ageCon", 14);
		map.put("stu", stu);
		List<Student>students=dao.selectStudentByNameAndAge(map);
		if(students.size()>0){
			for(Student student:students)
				System.out.println(student);
		}
		
	}
	@After
	public void after(){
		if(sqlSession!=null){
			sqlSession.close();
		}
	}
	
}
