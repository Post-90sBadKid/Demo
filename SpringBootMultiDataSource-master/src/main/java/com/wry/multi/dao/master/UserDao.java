package com.wry.multi.dao.master;

import com.wry.multi.dao.BaseDao;
import com.wry.multi.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
* Title: UserDao
* Description:
* 用户数据接口 
 */
@Mapper
public interface UserDao extends BaseDao<User> {
    
}
