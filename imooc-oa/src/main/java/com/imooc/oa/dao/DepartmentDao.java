package com.imooc.oa.dao;

import com.imooc.oa.entity.Department;
import com.imooc.oa.entity.Employee;

public interface DepartmentDao {
    public Department selectById(Long departmentId);
}
