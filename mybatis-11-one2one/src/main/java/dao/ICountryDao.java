package dao;

import bean.Country;

public interface ICountryDao {
	Country selectCountryById(int cid);
	Country selectCountryById2(int cid);
}
