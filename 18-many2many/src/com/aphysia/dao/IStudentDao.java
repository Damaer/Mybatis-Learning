package com.aphysia.dao;

import com.aphysia.beans.Student;

public interface IStudentDao {
	Student selectStudentById(int id);
}
