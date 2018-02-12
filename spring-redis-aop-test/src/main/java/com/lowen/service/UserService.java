package com.lowen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowen.CacheEvict;
import com.lowen.Cacheable;
import com.lowen.dao.UserDao;
import com.lowen.entity.UserInfo;

@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;
	
	public void insert(UserInfo userInfo) {
		userDao.insert(userInfo);
	}
	
	@Cacheable(key="userInfo",fieldKey="#id")
	public UserInfo queryById(String id) {
		UserInfo userInfo = new UserInfo();
		userInfo.setId(id);
		userInfo = userDao.queryById(userInfo);
		
		return userInfo;
	}
	
	@CacheEvict(key="userInfo",fieldKey="#userInfo.id")
	public void updateById(UserInfo userInfo) {
		userDao.updateById(userInfo);
	}
	
	@Cacheable(key="userInfo",fieldKey="#name")
	public List<UserInfo> queryByName(String name) {
		UserInfo userInfo = new UserInfo();
		userInfo.setName(name);
		List<UserInfo> list = userDao.queryByName(userInfo);
		return list;
	}

}
