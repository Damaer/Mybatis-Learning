package dao;

import bean.Student;
import org.apache.ibatis.session.SqlSession;
import utils.MyBatisUtils;
import java.util.List;

public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
    public List<Student> selectAllStudents() {
        List<Student> students ;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            students = sqlSession.selectList("selectAllStudents");
            //查询不用修改，所以不用提交事务
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return students;
    }

    public Student selectStudentById(int id) {
        Student student=null;
        try {
            sqlSession=MyBatisUtils.getSqlSession();
            student=sqlSession.selectOne("selectStudentById",id);
            sqlSession.commit();
        } finally{
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
        return student;
    }
}