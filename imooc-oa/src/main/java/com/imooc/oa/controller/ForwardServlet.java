package com.imooc.oa.controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "ForwardServlet", value = "/forward/*")
public class ForwardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri=request.getRequestURI();
        // /forward/form
        // /forward/a/b/c/form
        String subUri=uri.substring(1);//从第一个"/"后截取字符串
        String page=subUri.substring(subUri.indexOf("/"));//获得第一次出现"/"(包括)后的字符串：/form or /a/b/c/form
        request.getRequestDispatcher(page+".ftl").forward(request,response);
    }


}
