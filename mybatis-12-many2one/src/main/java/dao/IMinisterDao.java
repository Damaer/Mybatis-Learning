package dao;

import bean.Country;
import bean.Minister;

public interface IMinisterDao {
	Minister selectMinisterById(int mid);
	Minister selectMinisterById2(int mid);
}
