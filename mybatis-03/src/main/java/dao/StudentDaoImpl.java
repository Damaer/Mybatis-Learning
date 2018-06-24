package dao;

import bean.Student;
import org.apache.ibatis.session.SqlSession;
import utils.MyBatisUtils;


public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
    public void insertStu(Student student) {
        //加载主配置文件
        try {
            sqlSession=MyBatisUtils.getSqlSession();
            sqlSession.insert("mapper1.insertStudent",student);
            sqlSession.commit();
        } finally{
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
    }
}