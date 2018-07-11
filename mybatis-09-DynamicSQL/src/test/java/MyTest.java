import bean.Student;
import dao.IStudentDao;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.MyBatisUtils;

import java.util.ArrayList;
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
  /** 插入测试 */
  @Test
  public void testInsert() {
    /** 要是没有select id，这样是不会自动获取id的，id会一直为空 */
    Student student = new Student("sam", 11, 94.6);
    System.out.println("插入前：student=" + student);
    dao.insertStudent(student);
    System.out.println("插入后：student=" + student);
    /** 不知道为什么sqlSession.commit不用执行也可以提交到数据库 */
    sqlSession.commit();
  }
  /** 测试插入后获取id */
  @Test
  public void testinsertStudentCacheId() {
    Student student = new Student("helloworld", 14, 94.6);
    System.out.println("插入前：student=" + student);
    dao.insertStudentCacheId(student);
    ;
    System.out.println("插入后：student=" + student);
  }
  /*
   * 测试删除
   *
   */
  @Test
  public void testdeleteStudentById() {
    dao.deleteStudentById(20);
  }
  /*
   * 测试修改，一般我们业务里面不这样写，一般是查询出来student再修改
   *
   */
  @Test
  public void testUpdate() {
    Student student = new Student("la67768la", 14, 94.6);
    student.setId(4);
    dao.updateStudent(student);
  }
  /*
   * 查询列表
   *
   */
  @Test
  public void testselectList() {
    List<Student> students = dao.selectAllStudents();
    if (students.size() > 0) {
      for (Student student : students) {
        System.out.println(student);
      }
    }
  }
  /*
   * 查询列表装成map
   * 动态代理里面不能用，因为mapper里面没有id为selectAllStudentsMap的配置
   */
  /*@Test
  public void testselectMap(){
  	Map<String,Object>students=dao.selectAllStudentsMap();
  	//有相同的名字的会直接替换掉之前查出来的，因为是同一个key
  	System.out.println(students.get("helloworld"));
  	System.out.println(students.get("1ADAS"));
  }*/
  /*
   * 通过id来查询student
   *
   */
  @Test
  public void testselectStudentById() {
    Student student = dao.selectStudentById(19);
    System.out.println(student);
  }
  /*
   * 通过模糊查询student的名字
   *
   */
  @Test
  public void testselectStudentByName() {
    List<Student> students = dao.selectStudentsByName("abc");
    if (students.size() > 0) {
      for (Student student : students) System.out.println(student);
    }
  }

  @Test
  public void testselectStudentByNameAndAge() {
    Student stu = new Student("lallal", 1212, 40);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("nameCon", "abc");
    map.put("ageCon", 14);
    map.put("stu", stu);
    List<Student> students = dao.selectStudentByNameAndAge(map);
    if (students.size() > 0) {
      for (Student student : students) System.out.println(student);
    }
  }

  @Test
  public void testselectStudentByConditon() {
    List<Student> students = dao.selectStudentByCondition("abc", 14, 40.0);
    if (students.size() > 0) {
      for (Student student : students) System.out.println(student);
    }
  }

  @Test
  public void testselectStudentByDynamicSQL() {
    Student student = new Student("", 0, 40.0);
    List<Student> students = dao.selectStudentByDynamicSQL(student);
    if (students.size() > 0) {
      for (Student stu : students) System.out.println(stu);
    }
  }

  @Test
  public void testselectStudentByDynamicSQLWhere() {
    Student student = new Student("hello", 14, 40.0);
    List<Student> students = dao.selectStudentByDynamicSQLWhere(student);
    if (students.size() > 0) {
      for (Student stu : students) System.out.println(stu);
    }
  }

  @Test
  public void testselectStudentByDynamicSQLChoose() {
    // Student student=new Student("", 14, 40.0);
    // Student student=new Student("abc", 14, 40.0);
    Student student = new Student("hello", 14, 40.0);
    List<Student> students = dao.selectStudentByDynamicSQLChoose(student);
    if (students.size() > 0) {
      for (Student stu : students) System.out.println(stu);
    }
  }

  @Test
  public void testselectStudentByDynamicSQLForeachArray() {
    Object[] studentIds = new Object[] {1, 2, 3};
    List<Student> students = dao.selectStudentByDynamicSQLForeachArray(studentIds);
    if (students.size() > 0) {
      for (Student stu : students) System.out.println(stu);
    }
  }

  @Test
  public void testselectStudentByDynamicSQLForeachList() {
    List<Integer> studentIds = new ArrayList<Integer>();
    studentIds.add(1);
    studentIds.add(3);
    List<Student> students = dao.selectStudentByDynamicSQLForeachList(studentIds);
    if (students.size() > 0) {
      for (Student stu : students) System.out.println(stu);
    }
  }

  @Test
  public void testselectStudentByDynamicSQLForeachListStudent() {
    Student stu1 = new Student();
    stu1.setId(1);
    Student stu2 = new Student();
    stu2.setId(2);
    Student stu3 = new Student();
    stu3.setId(3);
    List<Student> stus = new ArrayList<Student>();
    stus.add(stu1);
    stus.add(stu2);
    stus.add(stu3);
    List<Student> students = dao.selectStudentByDynamicSQLForeachListStudent(stus);
    if (students.size() > 0) {
      for (Student stu : students) System.out.println(stu);
    }
  }

  @Test
  public void testselectStudentByDynamicSQLfragment() {
    Student stu1 = new Student();
    stu1.setId(1);
    Student stu2 = new Student();
    stu2.setId(2);
    Student stu3 = new Student();
    stu3.setId(3);
    List<Student> stus = new ArrayList<Student>();
    stus.add(stu1);
    stus.add(stu2);
    stus.add(stu3);
    List<Student> students = dao.selectStudentByDynamicSQLfragment(stus);
    if (students.size() > 0) {
      for (Student stu : students) System.out.println(stu);
    }
  }

  @After
  public void after() {
    if (sqlSession != null) {
      sqlSession.close();
    }
  }
}
