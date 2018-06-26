package dao;

import bean.Student;
import org.apache.ibatis.session.SqlSession;
import utils.MyBatisUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDaoImpl implements IStudentDao {
    private SqlSession sqlSession;
    public void insertStudent(Student student) {
        int result;
        //加载主配置文件
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            sqlSession.insert("insertStudent", student);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }

        }
    }
    public void  insertStudentCacheIdNoReturn(Student student) {

        try {
            sqlSession = MyBatisUtils.getSqlSession();
            sqlSession.insert("insertStudentCacheIdNoReturn", student);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
    public int  insertStudentCacheId(Student student) {
        int result;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            result =sqlSession.insert("insertStudentCacheId", student);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return result;
    }

    public void deleteStudentById(int id) {
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            sqlSession.delete("deleteStudentById", id);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    public void updateStudent(Student student) {
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            sqlSession.update("updateStudent", student);
            sqlSession.commit();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

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

    public Map<String, Object> selectAllStudentsMap() {
        Map<String ,Object> map=new HashMap<String, Object>();
        /**
         * 可以写成Map<String ,Student> map=new HashMap<String, Student>();
         */
        try {
            sqlSession=MyBatisUtils.getSqlSession();
            map=sqlSession.selectMap("selectAllStudents", "name");
            //查询不用修改，所以不用提交事务
        } finally{
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
        return map;
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

    public List<Student> selectStudentsByName(String name) {
        List<Student>students=new ArrayList<Student>();
        try {
            sqlSession=MyBatisUtils.getSqlSession();
            students=sqlSession.selectList("selectStudentsByName",name);
            //查询不用修改，所以不用提交事务
        } finally{
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
        return students;
    }
}