package dao;
import bean.Student;
import java.util.List;
import java.util.Map;
public interface IStudentDao {
    // 增加学生
    public void insertStudent(Student student);
    // 增加新学生并返回id
    public void insertStudentCacheIdNoReturn(Student student);
    // 增加新学生并返回id返回result
    public int insertStudentCacheId(Student student);

    // 根据id删除学生
    public void deleteStudentById(int id);
    // 更新学生的信息
    public void updateStudent(Student student);

    // 返回所有学生的信息List
    public List<Student> selectAllStudents();
    // 返回所有学生的信息Map
    public Map<String, Object> selectAllStudentsMap();

    // 根据id查找学生
    public Student selectStudentById(int id);
    // 根据名字查找学生，模糊查询
    public List<Student>selectStudentsByName(String name);
}
