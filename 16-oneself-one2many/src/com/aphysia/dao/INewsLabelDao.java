package com.aphysia.dao;

import java.util.List;
import com.aphysia.beans.NewsLabel;
public interface INewsLabelDao {
	List<NewsLabel> selectChildByParentId(int pid);
}
