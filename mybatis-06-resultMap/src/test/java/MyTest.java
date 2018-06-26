import bean.Student;
import dao.IStudentDao;
import dao.StudentDaoImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class MyTest {
	private IStudentDao dao;
	@Before
	public void Before(){
		dao=new StudentDaoImpl();
	}
    /*
     * 查询列表
     *
     */
    @Test
    public void testselectList(){
        List<Student> students=dao.selectAllStudents();
        if(students.size()>0){
            for(Student student:students){
                System.out.println(student);
            }
        }
    }
    /*
     * 通过id来查询student
     *
     */
    @Test
    public void testselectStudentById(){
        Student student=dao.selectStudentById(1);
        System.out.println(student);
    }

}
