package com.aphysia.test;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;





import com.aphysia.beans.NewsLabel;
import com.aphysia.dao.INewsLabelDao;
import com.aphysia.utils.MyBatisUtils;

public class MyTest {
	private INewsLabelDao dao;
	private SqlSession sqlSession;
	@Before
	public void Before(){
		sqlSession=MyBatisUtils.getSqlSession();
		dao=sqlSession.getMapper(INewsLabelDao.class);
	}
	@Test
	public void TestselectMinisterById(){
		NewsLabel children=dao.selectNewsLabelById(2);
		System.out.println(children);
	}
	@After
	public void after(){
		if(sqlSession!=null){
			sqlSession.close();
		}
	}
	
}
