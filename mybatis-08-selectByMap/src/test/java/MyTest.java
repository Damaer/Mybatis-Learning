import bean.Student;
import dao.IStudentDao;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;
import utils.MyBatisUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTest {
  private IStudentDao dao;
  private SqlSession sqlSession;

  @Before
  public void Before() {
    sqlSession = MyBatisUtils.getSqlSession();
    dao = sqlSession.getMapper(IStudentDao.class);
  }

  @After
  public void after() {
    if (sqlSession != null) {
      sqlSession.close();
    }
  }
  @Test
  public void testselectStudentByNameAndAge(){
      Student stu=new Student("lallal", 1212, 40);
      Map<String,Object> map=new HashMap<String, Object>();
      map.put("nameCon", "hello");
      map.put("ageCon", 14);
      map.put("stu", stu);
      List<Student>students=dao.selectStudentByNameAndAge(map);
      if(students.size()>0){
          for(Student student:students)
              System.out.println(student);
      }
  }

    /**
     * 使用索引号
     */
  @Test
  public void testselectStudentByNameAndAgeV2(){
        Student stu=new Student("lallal", 1212, 40);
        List<Student>students=dao.selectStudentByNameAndAgeV2("hello",13);
        if(students.size()>0){
            for(Student student:students)
                System.out.println(student);
        }
    }
}
