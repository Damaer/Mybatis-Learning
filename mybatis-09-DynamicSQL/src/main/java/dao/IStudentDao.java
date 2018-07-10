package dao;

import bean.Student;

import java.util.List;
import java.util.Map;

public interface IStudentDao {
    public void insertStudent(Student student);
    public void insertStudentCacheId(Student student);

    public void deleteStudentById(int id);
    public void updateStudent(Student student);


    public List<Student> selectAllStudents();
    public Map<String, Object> selectAllStudentsMap();

    public Student selectStudentById(int id);
    public List<Student>selectStudentsByName(String name);
    public List<Student>selectStudentByNameAndAge(Map<String,Object>map);
    public List<Student>selectStudentByCondition(String name,int age,double score);
    public List<Student>selectStudentByDynamicSQL(Student student);
    public List<Student>selectStudentByDynamicSQLWhere(Student student);
    public List<Student>selectStudentByDynamicSQLChoose(Student student);
    public List<Student>selectStudentByDynamicSQLForeachArray(Object[]studentIds);
    public List<Student>selectStudentByDynamicSQLForeachList(List<Integer>studentIds);
    public List<Student>selectStudentByDynamicSQLForeachListStudent(List<Student>students);
    public List<Student>selectStudentByDynamicSQLfragment(List<Student>students);
}
