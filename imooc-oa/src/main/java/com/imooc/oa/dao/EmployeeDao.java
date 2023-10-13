package com.imooc.oa.dao;

import com.imooc.oa.entity.Employee;
import org.apache.ibatis.annotations.Param;

public interface EmployeeDao {
    public Employee selectById(Long employeeId);

    public Employee selectLeader(@Param("emp") Employee employee);
}
