package dao;

import beans.NewsLabel;

import java.util.List;

public interface INewsLabelDao {
  NewsLabel selectParentByParentId(int pid);
}
