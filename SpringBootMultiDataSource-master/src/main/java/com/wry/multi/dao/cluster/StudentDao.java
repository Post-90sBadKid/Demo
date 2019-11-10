package com.wry.multi.dao.cluster;

import com.wry.multi.dao.BaseDao;
import com.wry.multi.pojo.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
* @Title: StudentDao
* @Description: 
 */
@Mapper
public interface StudentDao extends BaseDao<Student> {

}
