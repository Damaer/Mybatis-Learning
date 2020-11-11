很多时候，我们需要传入多个参数给sql语句接收，但是如果这些参数整体不是一个对象，那么我们应该怎么做呢？这里有两种解决方案，仅供参考。
#### 1.将多个参数封装成为Map
测试接口，我们传入一个Map，里面的value是一个对象，那么我们可以放字符串，数字，以及一个student对象。
``` java
  @Test
  public void testselectStudentByNameAndAge(){
      Student stu=new Student("lallal", 1212, 40);
      Map<String,Object> map=new HashMap<String, Object>();
      map.put("nameCon", "hello");
      map.put("ageCon", 14);
      map.put("stu", stu);
      List<Student>students=dao.selectStudentByNameAndAge(map);
      if(students.size()>0){
          for(Student student:students)
              System.out.println(student);
      }
  }
```
我们的sql接口，传入的是一个Map，key为String,value为对象。
``` java
public List<Student>selectStudentByNameAndAge(Map<String,Object> map);
```
下面是sql语句,如果value是基本类型的话，我们需要使用#{},里面一定写对应的key，如果value是一个对象的话，里面我们需要写对应的`key.属性`，比如`#{stu.score}`：
``` sql
<select id="selectStudentByNameAndAge" resultType="Student">
    select id,name,age,score from student where name like '%' #{nameCon} '%' and age> #{ageCon} and score>#{stu.score}
</select>
```

#### 2.使用索引接收多个参数
我们的测试类如下，传入两个参数：
``` java
  @Test
  public void testselectStudentByNameAndAgeV2(){
        Student stu=new Student("lallal", 1212, 40);
        List<Student>students=dao.selectStudentByNameAndAgeV2("hello",14);
        if(students.size()>0){
            for(Student student:students)
                System.out.println(student);
        }
    }
```
在sql接口中使用两个参数
```
public List<Student>selectStudentByNameAndAgeV2(String name,int age);
```
在sql语句里面可以使用索引号,例如#{0},下标从零开始，与参数一一对应
```
    <!-- 接受多个参数 -->
    <select id="selectStudentByNameAndAgeV2" resultType="Student">
        select id,name,age,score from student where name like '%' #{0} '%' and age> #{1}
    </select>
```
个人理解：如果是简单的多参数，比如没有涉及到对象的，可以直接使用索引号就可以了，这样看起来更简单，如果涉及到参数是对象的话，需要使用对象的属性就用不了索引号，需要使用map，如果参数较多的话，使用map更加方便，修改的时候也更有优势。