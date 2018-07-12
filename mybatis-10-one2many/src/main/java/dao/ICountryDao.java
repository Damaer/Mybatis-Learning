package dao;

import bean.Country;

public interface ICountryDao {
	Country selectCountryById(int cid);
}
