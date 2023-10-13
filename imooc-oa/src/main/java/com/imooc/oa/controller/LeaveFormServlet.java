package com.imooc.oa.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.entity.User;
import com.imooc.oa.service.LeaveFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.CredentialException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LeaveFormServlet", value = "/leave/*")
public class LeaveFormServlet extends HttpServlet {
    private LeaveFormService leaveFormService=new LeaveFormService();
    private Logger logger= LoggerFactory.getLogger(LeaveFormService.class);
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
         //http://localhost/leave/create  根据最后一个"/"后的单词确定要处理的逻辑
        String uri=request.getRequestURI();
        String methodName=uri.substring(uri.lastIndexOf("/")+1);
        if(methodName.equals("create")){
            this.create(request, response);
        }else if(methodName.equals("list")){
            this.getLeaveFormList(request,response);
        }else if(methodName.equals("audit")){
            this.audit(request,response);
        }
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //接收各项请假单数据
        HttpSession session=request.getSession();
        User user=(User) session.getAttribute("login_user");//获取当前登录的用户
        String formType=request.getParameter("formType");
        String strStartTime=request.getParameter("startTime");
        String strEndTime=request.getParameter("endTime");
        String reason=request.getParameter("reason");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH");

        Map result=new HashMap();
        try {
            LeaveForm leaveForm=new LeaveForm();
            leaveForm.setEmployeeId(user.getEmployeeId());
            leaveForm.setStartTime(sdf.parse(strStartTime));
            leaveForm.setEndTime(sdf.parse(strEndTime));
            leaveForm.setFormType(Integer.parseInt(formType));
            leaveForm.setReason(reason);
            leaveForm.setCreateTime(new Date());

            //调用业务逻辑方法
            leaveFormService.createLeaveForm(leaveForm);
            result.put("code","0");
            result.put("message","success");
        } catch (ParseException e) {
            logger.error("请假申请异常",e);
            result.put("code",e.getClass().getSimpleName());
            result.put("message",e.getMessage());
//            throw new RuntimeException(e);
        }
        //组织响应数据
        String json= JSON.toJSONString(result);
        response.getWriter().println(json);
    }

    /**
     * 查询需要审核的请假单列表
     * @param request
     * @param response
     * @throws IOException
     */
    private void getLeaveFormList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user=(User) request.getSession().getAttribute("login_user");
        List<Map> formList=leaveFormService.getLeaveFormList("process", user.getEmployeeId());
        Map result=new HashMap();
        result.put("code","0");
        result.put("msg","");
        result.put("count",formList.size());
        result.put("data",formList);
        String json=JSON.toJSONString(result);
        response.getWriter().println(json);
    }

    /**
     * 处理审批操作
     * @param request
     * @param response
     */
    private void audit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String formId=request.getParameter("formId");
        String result=request.getParameter("result");
        String reason=request.getParameter("reason");
        User user=(User) request.getSession().getAttribute("login_user");
        Map mpResult=new HashMap();
        try {
            leaveFormService.audit(Long.parseLong(formId), user.getEmployeeId(), result,reason);
            mpResult.put("code","0");
            mpResult.put("message","success");
        } catch (NumberFormatException e) {
            logger.error("请假单审核失败",e);
            mpResult.put("code",e.getClass().getSimpleName());
            mpResult.put("message",e.getMessage());
            throw new RuntimeException(e);
        }
        String json=JSON.toJSONString(mpResult);
        response.getWriter().println(json);
    }

}
