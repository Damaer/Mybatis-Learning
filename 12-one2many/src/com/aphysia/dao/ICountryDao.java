package com.aphysia.dao;

import com.aphysia.beans.Country;

public interface ICountryDao {
	Country selectCountryById(int cid);
}
