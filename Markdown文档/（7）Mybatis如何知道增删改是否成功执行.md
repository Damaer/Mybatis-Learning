使用mybatis的时候，经常发现一个需求，我怎么知道自己是不是增加/修改/删除数据成功了？

好像执行sql之后都没有结果的。其实不是的，增删改的sql执行之后都会有一个int类型的返回值，表示的意思是这个操作影响的行数。举个例子，如果我们插入一行成功的话，影响的就是一行。如果我们修改一条数据成功的话，那么我们也是影响了一行。如果我们删除一条数据成功的话，那么返回的就是1，表示影响了一行，如果没有删除任何的数据，那么返回值就是0。所以我们经常使用返回值是否大于0来表示是不是修改（增加/更新/删除都算是一种修改）数据成功。

比如我们插入数据的时候：
``` java
<insert id="insertStudentCacheId" parameterType="Student">
    insert into student(name,age,score) values(#{name},#{age},#{score})
</insert>
```
接口定义：
``` java
// 增加新学生并返回id返回result
public int insertStudentCacheId(Student student);
```
接口实现：
``` java
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
```
Test方法：
``` java
    @Test
    public void testinsertStudentCacheId(){
        Student student=new Student("helloworld",17,101);
        int result = dao.insertStudentCacheId(student);
        System.out.println("result:"+result);
    }
```
结果如下：![](http://markdownpicture.oss-cn-qingdao.aliyuncs.com/18-6-25/82414247.jpg)

这样的方式对于update以及删除方法都是有效的，这是因为他们都是属于修改方法，属于读写模式，而select方式是属于只读方式。

**【作者简介】**：  
秦怀，公众号【**秦怀杂货店**】作者，技术之路不在一时，山高水长，纵使缓慢，驰而不息。这个世界希望一切都很快，更快，但是我希望自己能走好每一步，写好每一篇文章，期待和你们一起交流。

此文章仅代表自己（本菜鸟）学习积累记录，或者学习笔记，如有侵权，请联系作者核实删除。人无完人，文章也一样，文笔稚嫩，在下不才，勿喷，如果有错误之处，还望指出，感激不尽~ 


![](https://markdownpicture.oss-cn-qingdao.aliyuncs.com/blog/20201012000828.png)