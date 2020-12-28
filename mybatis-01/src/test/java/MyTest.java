import bean.Student;
import dao.IStudentDao;
import dao.StudentDaoImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class MyTest {
	private IStudentDao dao;
	@Before
	public void Before(){
		dao=new StudentDaoImpl();
	}
	@Test
	public void testInsert(){
		Student student=new Student("heihei",30,90);
		dao.insertStu(student);
	}
}
