package com.imooc.oa.service;

import com.imooc.oa.dao.EmployeeDao;
import com.imooc.oa.entity.Employee;
import com.imooc.oa.utils.MybatisUtils;

public class EmployeeService {
    public Employee selectById(Long employeeId){
        return (Employee) MybatisUtils.executeQuery(sqlSession -> {
            //getMapper根据传入的接口自动生成对应接口的实现类
            EmployeeDao employeeDao=sqlSession.getMapper(EmployeeDao.class);
             return employeeDao.selectById(employeeId);
        });
    }
}
