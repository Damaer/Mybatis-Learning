package dao;

import bean.Student;

import java.util.List;
import java.util.Map;

public interface IStudentDao {
    // 返回所有学生的信息List
    public List<Student> selectAllStudents();
    // 根据id查找学生
    public Student selectStudentById(int id);

}
