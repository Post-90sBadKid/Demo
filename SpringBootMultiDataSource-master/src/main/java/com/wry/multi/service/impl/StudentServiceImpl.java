package com.wry.multi.service.impl;

import com.wry.multi.dao.BaseDao;
import com.wry.multi.dao.cluster.StudentDao;
import com.wry.multi.pojo.Student;
import com.wry.multi.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 
* Title: StudentServiceImpl
* Description: 
* 用户操作实现类 
 */
@Service
public class StudentServiceImpl extends BaseServiceImpl<Student> implements StudentService {
	@Autowired
	private StudentDao studentDao;
	
	@Override
	protected BaseDao<Student> getMapper() {
		return this.studentDao;
	}
	
}
