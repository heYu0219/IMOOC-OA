package com.imooc.oa.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.oa.entity.User;
import com.imooc.oa.service.UserService;
import com.imooc.oa.service.exception.BussinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", value = "/check_login")
public class LoginServlet extends HttpServlet {
    private UserService userService=new UserService();
    Logger logger= LoggerFactory.getLogger(LoginServlet.class);
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        //接收用户输入
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        System.out.println(password);
        Map<String,Object> result=new HashMap<>();
        try {
            //调用业务逻辑
            User user=userService.checkLogin(username,password);
            System.out.println(user);
            HttpSession session=request.getSession();
            //向session存入登陆用户信息，属性名login_user，以确保首页能够获取当前登录的用户
            session.setAttribute("login_user",user);
            result.put("code","0");
            result.put("message","success");
            result.put("redirect_url","/index");
        } catch (BussinessException ex){
            logger.error(ex.getMessage(),ex);
            result.put("code",ex.getCode());
            result.put("message",ex.getMessage());
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
            result.put("code",e.getClass().getSimpleName());
            result.put("message",e.getMessage());
        }
        //返回对应结果
//        System.out.println(result.entrySet());
        String json=JSON.toJSONString(result);
//        System.out.println(json);
        response.getWriter().println(json);
    }
}
