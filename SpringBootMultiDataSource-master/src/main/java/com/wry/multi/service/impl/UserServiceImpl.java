package com.wry.multi.service.impl;

import com.wry.multi.dao.BaseDao;
import com.wry.multi.dao.master.UserDao;
import com.wry.multi.pojo.User;
import com.wry.multi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
* Title: UserServiceImpl
* Description: 
* 用户操作实现类 
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {
	@Autowired
	private UserDao userDao;
	
	@Override
	protected BaseDao<User> getMapper() {
		return this.userDao;
	}
	
}
