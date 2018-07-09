package dao;

import bean.Student;
import java.util.List;
import java.util.Map;

public interface IStudentDao {

    public List<Student>selectStudentByNameAndAge(Map<String,Object> map);

    public List<Student>selectStudentByNameAndAgeV2(String name,int age);
}
