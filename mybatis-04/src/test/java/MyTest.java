import bean.Student;
import dao.IStudentDao;
import dao.StudentDaoImpl;
import org.junit.Before;
import org.junit.Test;

public class MyTest {
	private IStudentDao dao;
	@Before
	public void Before(){
		dao=new StudentDaoImpl();
	}
	@Test
	public void testInsert(){
		Student student=new Student("1ADAS",23,100);
		dao.insertStu(student);
	}
}
