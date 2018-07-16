package dao;

import bean.Student;

public interface IStudentDao {
	Student selectStudentById(int id);
}
