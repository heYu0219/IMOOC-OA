package com.imooc.oa.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.oa.entity.Notice;
import com.imooc.oa.entity.User;
import com.imooc.oa.service.NoticeService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NoticeServlet", value = "/notice/list")
public class NoticeServlet extends HttpServlet {
    NoticeService noticeService=new NoticeService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user=(User) request.getSession().getAttribute("login_user");
        List<Notice> noticeList=noticeService.getNoticeList(user.getEmployeeId());
        Map result=new HashMap();
        result.put("code","0");
        result.put("msg","");
        result.put("count",noticeList.size());
        result.put("data",noticeList);
        String json= JSON.toJSONString(result);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
