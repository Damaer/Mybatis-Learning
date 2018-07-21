package dao;
import java.util.List;
import java.util.Map;

import beans.Student;

public interface IStudentDao {
    public Student selectStudentById(int id);
    // 测试不同的id的sql
    public Student selectStudentById2(int id);


    // 增加学生
    public void insertStudent(Student student);
    // 根据id删除学生
    public void deleteStudentById(int id);
    // 更新学生的信息
    public void updateStudent(Student student);
}
