package com.imooc.oa.service;

import com.imooc.oa.dao.DepartmentDao;
import com.imooc.oa.dao.EmployeeDao;
import com.imooc.oa.entity.Department;
import com.imooc.oa.entity.Employee;
import com.imooc.oa.utils.MybatisUtils;

public class DepartmentService {
    /**
     * 按编号得到部门对象
     * @param departmentId 部门编号
     * @return 部门对象  null代表部门不存在
     */
    public Department selectById(Long departmentId){
        return (Department) MybatisUtils.executeQuery(sqlSession -> {
            //getMapper根据传入的接口自动生成对应接口的实现类
            DepartmentDao employeeDao=sqlSession.getMapper(DepartmentDao.class);
             return employeeDao.selectById(departmentId);
        });
    }
}
