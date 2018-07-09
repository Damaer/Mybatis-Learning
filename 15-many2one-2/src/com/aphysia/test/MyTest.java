package com.aphysia.test;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aphysia.beans.Minister;
import com.aphysia.dao.IMinisterDao;
import com.aphysia.utils.MyBatisUtils;

public class MyTest {
	private IMinisterDao dao;
	private SqlSession sqlSession;
	@Before
	public void Before(){
		sqlSession=MyBatisUtils.getSqlSession();
		dao=sqlSession.getMapper(IMinisterDao.class);
	}
	@Test
	public void TestselectMinisterById(){
		Minister minister=dao.selectMInisterById(2);
		System.out.println(minister);
	}
	@After
	public void after(){
		if(sqlSession!=null){
			sqlSession.close();
		}
	}
	
}
