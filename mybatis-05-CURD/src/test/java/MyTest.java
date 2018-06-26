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
    /**
     * 插入测试
     */
    @Test
    public void testInsert(){
        /**
         * 要是没有select id，这样是不会自动获取id的，id会一直为空
         */
        Student student=new Student("hello",14,94.6);
        System.out.println("插入前：student="+student);
        dao.insertStudent(student);
        System.out.println("插入后：student="+student);
    }
    /**
     * 测试插入后获取id
     */
    @Test
    public void testinsertStudentCacheIdNoReturn(){
        Student student=new Student("helloworld",17,101);
        System.out.println("插入前：student="+student);
        dao.insertStudentCacheIdNoReturn(student);
        System.out.println("插入后：student="+student);
    }
    /**
     * 测试插入后获取id
     */
    @Test
    public void testinsertStudentCacheId(){
        Student student=new Student("helloworld",17,101);
        System.out.println("插入前：student="+student);
        int result = dao.insertStudentCacheId(student);
        System.out.println("result:"+result);
        System.out.println("插入后：student="+student);
    }
    /*
     * 测试删除
     *
     */
    @Test
    public void testdeleteStudentById(){
        dao.deleteStudentById(5);

    }
    /*
     * 测试修改，一般我们业务里面不这样写，一般是查询出来student再修改
     *
     */
    @Test
    public void testUpdate(){
        Student student=new Student("lallalalla",14,94.6);
        student.setId(21);
        dao.updateStudent(student);

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
     * 查询列表装成map
     *
     */
    @Test
    public void testselectMap(){
        Map<String,Object> students=dao.selectAllStudentsMap();
        // 有相同的名字的会直接替换掉之前查出来的，因为是同一个key
        System.out.println(students.get("helloworld"));
        System.out.println(students.get("1ADAS"));
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
    /*
     * 通过模糊查询student的名字
     *
     */
    @Test
    public void testselectStudentByName(){
        List<Student>students=dao.selectStudentsByName("abc");
        if(students.size()>0){
            for(Student student:students)
                System.out.println(student);
        }

    }
}
