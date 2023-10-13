package com.imooc.oa.controller;

import com.imooc.oa.entity.Department;
import com.imooc.oa.entity.Employee;
import com.imooc.oa.entity.Node;
import com.imooc.oa.entity.User;
import com.imooc.oa.service.DepartmentService;
import com.imooc.oa.service.EmployeeService;
import com.imooc.oa.service.UserService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "IndexServlet", value = "/index")
public class IndexServlet extends HttpServlet {
    private UserService userService=new UserService();
    private  EmployeeService employeeService=new EmployeeService();
    private DepartmentService departmentService=new DepartmentService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session=request.getSession();
        //得到当前登录的用户对象
        User user=(User) session.getAttribute("login_user");
        //获取当前登录的员工对象
        Employee employee=employeeService.selectById(user.getEmployeeId());
        //获取登录用户可用的功能模块列表
        List<Node> list=userService.selectNodeByUserId(user.getUserId());//执行查询
        //获取员工对应的部门对象
        Department department=departmentService.selectById(employee.getDepartmentId());

        request.setAttribute("nodeList",list);//将节点列表放入请求中
        //后面还会用到当前登录的员工对象，所以存储到生存周期较长的session中
        session.setAttribute("current_employee",employee);
        session.setAttribute("current_department",department);
        //请求转发至ftl进行展现
        request.getRequestDispatcher("/index.ftl").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
