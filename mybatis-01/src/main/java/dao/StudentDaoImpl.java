package dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bean.Student;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
	public void insertStu(Student student) {
		try {
			InputStream inputStream;
			inputStream = Resources.getResourceAsStream("mybatis.xml");
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
			sqlSession=sqlSessionFactory.openSession();
			sqlSession.insert("insertStudent",student);
			sqlSession.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
		    if(sqlSession!=null){
		        sqlSession.close();
            }
        }
	}
}
