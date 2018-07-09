package com.aphysia.utils;


import static org.hamcrest.CoreMatchers.nullValue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtils {
	/**
	 * 创建SqlsessionFactory消耗的资源比较大，线程安全的，所以可以创建单例的
	 * @return
	 */
/*	public SqlSession getSqlSession(){
		InputStream is;
		try {
			is = Resources.getResourceAsStream("mybatis.xml");
			return new SqlSessionFactoryBuilder().build(is).openSession();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}*/
	/**
	 * 线程不安全是指单例的对象，而且它具有可以修改的属性，那么就会有被同时修改的可能性
	 * @return
	 */
	static private SqlSessionFactory sqlSessionFactory;
	static public SqlSession getSqlSession(){
		InputStream is;
		try {
			is = Resources.getResourceAsStream("mybatis.xml");
			if(sqlSessionFactory==null){
				sqlSessionFactory=new SqlSessionFactoryBuilder().build(is);
			}
			return sqlSessionFactory.openSession();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
