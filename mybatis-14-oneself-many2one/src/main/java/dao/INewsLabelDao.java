package dao;

import beans.NewsLabel;

import java.util.List;

public interface INewsLabelDao {
  List<NewsLabel> selectChildByParentId(int pid);
  List<NewsLabel> selectSelfAndChildByParentId(int pid);
}
