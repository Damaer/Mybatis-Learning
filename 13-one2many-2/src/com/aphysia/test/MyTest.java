package com.aphysia.test;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aphysia.beans.Country;
import com.aphysia.dao.ICountryDao;
import com.aphysia.utils.MyBatisUtils;

public class MyTest {
	private ICountryDao dao;
	private SqlSession sqlSession;
	@Before
	public void Before(){
		sqlSession=MyBatisUtils.getSqlSession();
		dao=sqlSession.getMapper(ICountryDao.class);
	}
	@Test
	public void TestselectCountryById(){
		Country country=dao.selectCountryById(1);
		System.out.println(country);
	}
	@After
	public void after(){
		if(sqlSession!=null){
			sqlSession.close();
		}
	}
	
}
