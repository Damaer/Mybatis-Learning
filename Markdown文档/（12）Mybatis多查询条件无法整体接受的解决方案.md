很多时候，我们需要传入多个参数给sql语句接收，但是如果这些参数整体不是一个对象，那么我们应该怎么做呢？这里有两种解决方案，仅供参考。

## 1.将多个参数封装成为Map
测试接口，我们传入一个`Map`，里面的`value`是一个对象，那么我们可以放字符串，数字，以及一个`student`对象。
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
我们的`sql`接口，传入的是一个`Map`，key为`String`,`value`为对象。
``` java
public List<Student>selectStudentByNameAndAge(Map<String,Object> map);
```
下面是`sql`语句,如果`value`是基本类型的话，我们需要使用`#{}`,里面一定写对应的`key`，如果`value`是一个对象的话，里面我们需要写对应的`key.属性`，比如`#{stu.score}`：
``` sql
<select id="selectStudentByNameAndAge" resultType="Student">
    select id,name,age,score from student where name like '%' #{nameCon} '%' and age> #{ageCon} and score>#{stu.score}
</select>
```

## 2.使用索引接收多个参数
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
在`sql`接口中使用两个参数
``` java
public List<Student>selectStudentByNameAndAgeV2(String name,int age);
```
在sql语句里面可以使用索引号,例如`#{0}`,下标从零开始，与参数一一对应
```xml
    <!-- 接受多个参数 -->
    <select id="selectStudentByNameAndAgeV2" resultType="Student">
        select id,name,age,score from student where name like '%' #{0} '%' and age> #{1}
    </select>
```
个人理解：如果是简单的多参数，比如没有涉及到对象的，可以直接使用索引号就可以了，这样看起来更简单，如果涉及到参数是对象的话，需要使用对象的属性就用不了索引号，需要使用map，如果参数较多的话，使用`map`更加方便，修改的时候也更有优势。

**【作者简介】**：  
秦怀，公众号【**秦怀杂货店**】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。这个世界希望一切都很快，更快，但是我希望自己能走好每一步，写好每一篇文章，期待和你们一起交流。

此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~ 


![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/blog/20201012000828.png)