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
            // 读取配置文件，配置文件中配置了数据库以及需要扫描的mapper文件
            inputStream = Resources.getResourceAsStream("mybatis.xml");
            // 创建sqlsession的工厂，可以理解为这个工厂已经扫描完我们的mapper文件
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            // 打开一次会话
            sqlSession = sqlSessionFactory.openSession();
            // 执行一次insert操作，insertStudent是我们在mapper文件中配置的id，只有一个文件的时候，可以这么写，是唯一的，student是需要传进去的参数
            sqlSession.insert("insertStudent", student);
            // 需要提交会话内容
            sqlSession.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sqlSession != null) {
                // 执行完之后需要关闭会话，节省系统资源
                sqlSession.close();
            }
        }
    }
}
