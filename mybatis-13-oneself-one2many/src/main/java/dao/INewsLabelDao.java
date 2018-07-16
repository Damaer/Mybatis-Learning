package dao;

import java.util.List;

import beans.NewsLabel;

public interface INewsLabelDao {
  List<NewsLabel> selectChildByParentId(int pid);
  List<NewsLabel> selectSelfAndChildByParentId(int pid);
}
