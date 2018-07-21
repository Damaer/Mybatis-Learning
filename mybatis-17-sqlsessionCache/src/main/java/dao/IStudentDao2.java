package dao;

import beans.Student;

public interface IStudentDao2 {
    //不同的namespace下的相同id的测试
    public Student selectStudentById(int id);
}
