package dao;

import beans.Country;

public interface ICountryDao {
	Country selectCountryById(int cid);
}
