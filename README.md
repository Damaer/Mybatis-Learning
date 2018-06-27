# Mybatis-Learning
这是学习mybatis的demo以及总结，会从最基本的项目构建开始，包括Junit测试，log4j等等。

#### **1.[（一）Mybatis入门之第一个程序](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/%EF%BC%88%E4%B8%80%EF%BC%89Mybatis%E5%85%A5%E9%97%A8%E4%B9%8B%E7%AC%AC%E4%B8%80%E4%B8%AA%E7%A8%8B%E5%BA%8F.md)**<br>
主要是介绍了什么是框架，介绍mybatis，演示从0到搭建一个实现插入功能的mybatis demo，同时包含test测试以及日志。

- [Mybatis一定要commit才能成功修改数据库么？](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/Mybatis%E4%B8%80%E5%AE%9A%E8%A6%81%E4%BD%BF%E7%94%A8commit%E6%89%8D%E8%83%BD%E6%88%90%E5%8A%9F%E4%BF%AE%E6%94%B9%E6%95%B0%E6%8D%AE%E5%BA%93%E4%B9%88%EF%BC%9F.md)

- [Mybatis关于创建SqlSession源码分析](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/Mybatis%E5%85%B3%E4%BA%8E%E5%88%9B%E5%BB%BASqlSession%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90.md)

#### **2.[（二）Mybatis多个mapper文件以及namespace命名问题](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/%EF%BC%88%E4%BA%8C%EF%BC%89Mybatis%E5%A4%9A%E4%B8%AAmapper%E6%96%87%E4%BB%B6%E4%BB%A5%E5%8F%8Anamespace%E5%91%BD%E5%90%8D%E9%97%AE%E9%A2%98.md)**<br>
多个mapper文件时，如果id相同则使用namespace加以区分，否则会报错。
#### **3.[（三）Mybatis使用工具类读取配置文件以及从属性读取DB信息](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/%EF%BC%88%E4%B8%89%EF%BC%89Mybatis%E4%BD%BF%E7%94%A8%E5%B7%A5%E5%85%B7%E7%B1%BB%E8%AF%BB%E5%8F%96%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6%E4%BB%A5%E5%8F%8A%E4%BB%8E%E5%B1%9E%E6%80%A7%E8%AF%BB%E5%8F%96DB%E4%BF%A1%E6%81%AF.md)**<br>
改进成使用util单例模式来获取SqlSession的实例，节省资源，以及从属性文件读取信息，有利于解耦合。
#### **4.[（四）关于Mybatis别名定义](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/%EF%BC%88%E5%9B%9B%EF%BC%89%E5%85%B3%E4%BA%8EMybatis%E5%88%AB%E5%90%8D%E5%AE%9A%E4%B9%89.md)**<br>
定义别名能让我们快速简洁的看出类型信息，省去许多写路径名的麻烦。
#### **5.[（五）Mybatis增删改查demo](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/%EF%BC%88%E4%BA%94%EF%BC%89Mybatis%E5%A2%9E%E5%88%A0%E6%94%B9%E6%9F%A5demo.md)**<br>
完整的增删改查的demo。
#### **6.[（六）Mybatis插入数据返回主键id](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/%EF%BC%88%E5%85%AD%EF%BC%89Mybatis%E6%8F%92%E5%85%A5%E6%95%B0%E6%8D%AE%E8%BF%94%E5%9B%9E%E4%B8%BB%E9%94%AEid.md)**<br>
如何在插入数据后返回主键id信息，这个在实际开发中比较多见。