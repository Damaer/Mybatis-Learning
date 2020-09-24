package dao;


import model.Student;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DBUtil;
/**
 * 操作学生表的dao类
 * @author 秦怀
 * 下面均使用预编译的方法
 */
public class StudentDao {
    //将连接定义为单例
    private static Connection connection = DBUtil.getConnection();
    // 添加新的学生
    public void addStudent(Student student){
        String sql ="insert into student(name,age,score) "+
                "values(?,?,?)";
        boolean result = false;
        try {
            // 将sql传进去预编译
            PreparedStatement preparedstatement = connection.prepareStatement(sql);
            // 下面把参数传进去
            preparedstatement.setString(1, student.getName());
            preparedstatement.setInt(2, student.getAge());
            preparedstatement.setDouble(3, student.getScore());
            preparedstatement.execute();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("创建数据库连接失败");
        }
    }
    // 更新学生信息
    public void updateStudent(Student student){
        String sql = "update student set name = ? ,age =?,score = ? where id = ? ";
        boolean result = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, student.getName());
            preparedStatement.setInt(2, student.getAge());
            preparedStatement.setDouble(3, student.getScore());
            preparedStatement.setInt(4, student.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("连接数据库失败");
        }
    }
    // 根据id删除一个学生
    public void deleteStudent(int id){
        String sql = "delete from student where id = ?";
        boolean result = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            result=preparedStatement.execute();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    // 根据id查询学生
    public Student selectStudent(int id){
        String sql ="select * from student where id =?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Student student = new Student();
            // 一条也只能使用resultset来接收
            while(resultSet.next()){
                student.setId(resultSet.getInt("id"));
                student.setName(resultSet.getString("name"));
                student.setAge(resultSet.getInt("age"));
                student.setScore(resultSet.getDouble("score"));
            }
            return student;
        } catch (SQLException e) {
            // TODO: handle exception
        }
        return null;
    }
    // 查询所有学生，返回List
    public List<Student> selectStudentList(){
        List<Student>students  = new ArrayList<Student>();
        String sql ="select * from student ";
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            // 不能把student在循环外面创建，要不list里面六个对象都是一样的，都是最后一个的值，
            // 因为list add进去的都是引用
            // Student student = new Student();
            while(resultSet.next()){
                Student student = new Student();
                student.setId(resultSet.getInt(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setScore(resultSet.getDouble(4));
                students.add(student);

            }
        } catch (SQLException e) {
            // TODO: handle exception
        }
        return students;
    }
}
