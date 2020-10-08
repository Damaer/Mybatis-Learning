package action;


import java.util.List;

import dao.StudentDao;

import model.Student;

public class StudentAction {

    /**
     * @param args
     */
    public static void main(String[] args) {
        StudentDao studentDao = new StudentDao();
        // TODO Auto-generated method stub
        System.out.println("========================查询所有学生========================");
        List<Student> students =studentDao.selectStudentList();
        for(int i=0;i<students.size();i++){
            System.out.println(students.get(i).toString());
        }
        /*System.out.println("========================修改学生信息========================");
        Student stu2 = new Student("Jam",20,98.4);
        stu2.setId(2);
        studentDao.updateStudent(stu2);
        System.out.println("========================通过id查询学生========================");
        Student student = studentDao.selectStudent(2);
        System.out.println(student.toString());
        System.out.println("========================增加学生========================");
        Student stu = new Student("new name",20,98.4);
        studentDao.addStudent(stu);
        System.out.println("========================删除学生信息========================");
        studentDao.deleteStudent(4);
        System.out.println("========================查询所有学生========================");
        students =studentDao.selectStudentList();
        for(int i=0;i<students.size();i++){
            System.out.println(students.get(i).toString());
        }*/
    }

}
