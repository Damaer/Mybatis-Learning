# Mybatis-Learning
这是学习mybatis的demo以及总结，会从最基本的项目构建开始，包括Junit测试，log4j等等。

#### **1.[（一）Mybatis入门之第一个程序](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/%EF%BC%88%E4%B8%80%EF%BC%89Mybatis%E5%85%A5%E9%97%A8%E4%B9%8B%E7%AC%AC%E4%B8%80%E4%B8%AA%E7%A8%8B%E5%BA%8F.md)**<br>
主要是介绍了什么是框架，介绍mybatis，演示从0到搭建一个实现插入功能的mybatis demo，同时包含test测试以及日志。

- [Mybatis一定要commit才能成功修改数据库么？](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/Mybatis%E4%B8%80%E5%AE%9A%E8%A6%81%E4%BD%BF%E7%94%A8commit%E6%89%8D%E8%83%BD%E6%88%90%E5%8A%9F%E4%BF%AE%E6%94%B9%E6%95%B0%E6%8D%AE%E5%BA%93%E4%B9%88%EF%BC%9F.md)

- [Mybatis关于创建SqlSession源码分析](https://github.com/Damaer/Mybatis-Learning/blob/master/Markdown%E6%96%87%E6%A1%A3/Mybatis%E5%85%B3%E4%BA%8E%E5%88%9B%E5%BB%BASqlSession%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90.md)

#### **2.[（二）Mybatis多个mapper文件以及namespace命名问题]()**<br>
多个mapper文件时，如果id相同则使用namespace加以区分，否则会报错。
#### **3.[（三）Mybatis使用工具类读取配置文件以及从属性读取DB信息]()**<br>
改进成使用util单例模式来获取SqlSession的实例，节省资源，以及从属性文件读取信息，有利于解耦合。