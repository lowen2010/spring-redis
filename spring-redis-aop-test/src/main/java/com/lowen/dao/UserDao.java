package com.lowen.dao;

import java.util.List;

import com.lowen.entity.UserInfo;

public interface UserDao {
	public void insert(UserInfo userInfo);
	public void updateById(UserInfo userInfo);
	public UserInfo queryById(UserInfo userInfo);
	public List<UserInfo> queryByName(UserInfo userInfo);
}
