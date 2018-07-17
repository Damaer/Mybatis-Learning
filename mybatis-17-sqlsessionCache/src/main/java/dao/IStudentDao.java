package dao;
import java.util.List;
import java.util.Map;

import beans.Student;

public interface IStudentDao {
	public void insertStudent(Student student);
	public void insertStudentCacheId(Student student);
	
	public void deleteStudentById(int id);
	public void updateStudent(Student student);
	

	public List<Student> selectAllStudents();
	public Map<String, Object> selectAllStudentsMap();
	
	public Student selectStudentById(int id);
	public List<Student>selectStudentsByName(String name);
	public List<Student>selectStudentByNameAndAge(Map<String, Object> map);
}
