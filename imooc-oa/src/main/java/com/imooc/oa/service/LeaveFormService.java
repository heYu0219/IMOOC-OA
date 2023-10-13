package com.imooc.oa.service;

import com.imooc.oa.dao.EmployeeDao;
import com.imooc.oa.dao.LeaveFormDao;
import com.imooc.oa.dao.NoticeDao;
import com.imooc.oa.dao.ProcessFlowDao;
import com.imooc.oa.entity.Employee;
import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.entity.Notice;
import com.imooc.oa.entity.ProcessFlow;
import com.imooc.oa.service.exception.BussinessException;
import com.imooc.oa.utils.MybatisUtils;
import javafx.geometry.NodeOrientation;
import org.apache.ibatis.session.SqlSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请假单流程服务
 */
public class LeaveFormService {
    /**
     *创建请假单
     * @param form 前端输入的表单对象
     * @return 持久化后的请假单对象
     */
    public LeaveForm createLeaveForm(LeaveForm form){
        //确保数据要么一次性提交要么全部回滚
        LeaveForm savedForm =(LeaveForm) MybatisUtils.executeUpdate(sqlSession -> {
            //1.持久化form表单数据,8级以下员工表单状态为process,8级(总经理)状态为approved
            EmployeeDao employeedao =sqlSession.getMapper(EmployeeDao.class);
            Employee employee= employeedao.selectById(form.getEmployeeId());
            if(employee.getLevel()==8){
                form.setState("approved");
            }else {
                form.setState("process");
            }
            LeaveFormDao leaveFormDao=sqlSession.getMapper(LeaveFormDao.class);
            leaveFormDao.insert(form);

            //2.增加第一条流程数据,说明表单已提交,状态为complete
            ProcessFlowDao processFlowDao=sqlSession.getMapper(ProcessFlowDao.class);
            ProcessFlow processFlow =new ProcessFlow();
            processFlow.setFormId(form.getFormId());
            processFlow.setOperatorId(employee.getEmployeeId());
            processFlow.setAction("apply");
            processFlow.setCreateTime(new Date());
            processFlow.setState("complete");
            processFlow.setIsLast(0);
            processFlow.setOrderNo(1);
//            processFlow.setOperatorId(employee.getEmployeeId());
            processFlowDao.insert(processFlow);

            //3.分情况创建其余流程数据
            //3.1 7级以下员工,生成部门经理审批任务,请假时间大于36(在bussinessConstant中定义为常量，可以随时改变)小时,还需生成总经理审批任务
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH时");
            NoticeDao noticeDao=sqlSession.getMapper(NoticeDao.class);

            if(employee.getLevel()<7){
                Employee dmanager=employeedao.selectLeader(employee);//查询上级对象
                //部门 经理审批
                ProcessFlow flow=new ProcessFlow();
                flow.setFormId(form.getFormId());
                flow.setOperatorId(dmanager.getEmployeeId());
                flow.setAction("audit");
                flow.setCreateTime(new Date());
                flow.setOrderNo(2);
                flow.setState("process");
                Long diff=form.getEndTime().getTime()-form.getStartTime().getTime();//起始时间和结束时间之间的毫秒数
                float hours=diff/(1000*60*60)*1f;//小时
                if(hours>=BussinessConstants.MANAGER_AUDIT_HOURS){//总经理任务
                    flow.setIsLast(0);//还需总经理审批 ，不是最后一个节点
                    processFlowDao.insert(flow);
                    Employee manager=employeedao.selectLeader(dmanager);//获得总经理对象
                    ProcessFlow flow1=new ProcessFlow();//生成总经理的审批流程
                    flow1.setFormId(form.getFormId());
                    flow1.setOperatorId(manager.getEmployeeId());
                    flow1.setAction("audit");
                    flow1.setCreateTime(new Date());
                    flow1.setOrderNo(3);
                    flow1.setState("ready");
                    flow1.setIsLast(1);
                    processFlowDao.insert(flow1);
                }else {
                    flow.setIsLast(1);
                    processFlowDao.insert(flow);
                }
                //请假单已提交消息
                String noticeContent=String.format("您的请假申请[%s-%s]已提交，请等待上级审批。",
                        sdf.format(form.getStartTime()),sdf.format(form.getEndTime()));
                noticeDao.insert(new Notice(form.getEmployeeId(), noticeContent));//表单提交

                //通知部门经理审批消息
                noticeContent=String.format("%s-%s提起的请假申请[%s-%s]，请尽快审批",
                        employee.getTitle(),employee.getName(),sdf.format(form.getStartTime()),sdf.format(form.getEndTime()));
                noticeDao.insert(new Notice(dmanager.getEmployeeId(), noticeContent));
            }else if(employee.getLevel()==7){
                //3.2 7级员工,生成总经理审批任务
                Employee manager=employeedao.selectLeader(employee);
                ProcessFlow flow2=new ProcessFlow();
                flow2.setFormId(form.getFormId());
                flow2.setOperatorId(manager.getEmployeeId());
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setState("process");//任务流程中的第二个节点
                flow2.setOrderNo(2);
                flow2.setIsLast(1);//整个工作流程中的最后一个节点
                processFlowDao.insert(flow2);
                //请假单已提交消息
                String noticeContent=String.format("您的请假申请[%s-%s]已提交，请等待上级审批。",
                        sdf.format(form.getStartTime()),sdf.format(form.getEndTime()));
                noticeDao.insert(new Notice(form.getEmployeeId(), noticeContent));//表单提交

                //通知总经理审批消息
                noticeContent=String.format("%s-%s提起的请假申请[%s-%s]，请尽快审批",
                        employee.getTitle(),employee.getName(),sdf.format(form.getStartTime()),sdf.format(form.getEndTime()));
                noticeDao.insert(new Notice(manager.getEmployeeId(), noticeContent));



            }else if(employee.getLevel()==8){
                //3.3 8级员工,生成总经理审批任务,系统自动通过
                ProcessFlow flow2=new ProcessFlow();
                flow2.setFormId(form.getFormId());
                flow2.setOperatorId(employee.getEmployeeId());//审批人就是总经理自己
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setAuditTime(new Date());//自动审批
                flow2.setReason("自动通过");
                flow2.setResult("approved");
                flow2.setState("complete");//任务流程中的第二个节点
                flow2.setOrderNo(2);
                flow2.setIsLast(1);//整个工作流程中的最后一个节点
                processFlowDao.insert(flow2);
                //总经理本人请假
                String noticeContent=String.format("您的提起的请假申请[%s-%s]系统已自动通过",
                        employee.getTitle(),employee.getName(),sdf.format(form.getStartTime()),sdf.format(form.getEndTime()));
                noticeDao.insert(new Notice(employee.getEmployeeId(), noticeContent));


            }
            return form;
        });
        return savedForm;
    }

    public List<Map> getLeaveFormList(String pfstate,Long operatorId){
        return (List<Map>) MybatisUtils.executeQuery(sqlSession -> {
            LeaveFormDao leaveFormDao=sqlSession.getMapper(LeaveFormDao.class);
            List<Map> formList=leaveFormDao.selectByParams(pfstate,operatorId);
            return formList;
        });
    }

    public void audit(Long formId,Long operatorId,String result,String reason){
        MybatisUtils.executeUpdate(sqlSession -> {
            //1、无论同意/驳回，当前任务状态变更为complete
            ProcessFlowDao processFlowDao=sqlSession.getMapper(ProcessFlowDao.class);
            List<ProcessFlow> flowList=processFlowDao.selectByFormId(formId);
            if(flowList.size()==0){
                throw new BussinessException("PF001","无效的审批流程");
            }
            List<ProcessFlow> processList=flowList.stream()
                    .filter(p->p.getOperatorId()==operatorId && p.getState().equals("process"))
                    .collect(Collectors.toList());
            ProcessFlow process=null;
            if(processList.size()==0){
                throw new BussinessException("PF002","未找到待处理任务");
            }else {
                process=processList.get(0);
                process.setState("complete");
                process.setResult(result);
                process.setReason(reason);
                process.setAuditTime(new Date());
                processFlowDao.update(process);
            }

            LeaveFormDao leaveFormDao=sqlSession.getMapper(LeaveFormDao.class);
            LeaveForm form=leaveFormDao.selectById(formId);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH时");
            EmployeeDao employeeDao=sqlSession.getMapper(EmployeeDao.class);
            Employee employee=employeeDao.selectById(form.getEmployeeId());//请假员工的信息
            Employee operator=employeeDao.selectById(operatorId);//经办人的信息
            NoticeDao noticeDao=sqlSession.getMapper(NoticeDao.class);
            //2、如果当前任务是最后一个节点，代表流程结束，更新请假单状态为对应的approved/refused
            if(process.getIsLast()==1){
                form.setState(result);
                leaveFormDao.update(form);

                String strResult=null;
                if(result.equals("approved")){
                    strResult="批准";
                }else if(result.equals("refused")){
                    strResult="驳回";
                }
                String noticeContent=String.format("您的请假申请[%s-%s]%s%s已%S，审批意见：%s,审批流程已结束",
                        sdf.format(form.getStartTime()),sdf.format(form.getEndTime()),
                        operator.getTitle(),operator.getName(),strResult,reason );//发给表单提交人的通知
                noticeDao.insert(new Notice(form.getEmployeeId(),noticeContent));

                noticeContent=String.format("%s-%s提起的请假申请[%s-%s],您已%s，审批意见：%s,审批流程已结束",
                        employee.getTitle(),employee.getName(),sdf.format(form.getStartTime()),sdf.format(form.getEndTime()),
                        strResult,reason);//发给审批人的通知
                noticeDao.insert(new Notice(operator.getEmployeeId(), noticeContent));

            }else{
                List<ProcessFlow> readyList=flowList.stream().filter(p->p.getState().equals("ready")).collect(Collectors.toList());
                //3、如果当前任务不是最后一个节点且审批通过，那下一个节点的状态从ready变为process
                if(result.equals("approved")){
                    ProcessFlow readyProcess=readyList.get(0);
                    readyProcess.setState("process");
                    processFlowDao.update(readyProcess);

                    //消息1: 通知表单提交人,部门经理已经审批通过,交由上级继续审批
                    String noticeContent1 = String.format("您的请假申请[%s-%s]%s%s已批准,审批意见:%s ,请继续等待上级审批" ,
                            sdf.format(form.getStartTime()) , sdf.format(form.getEndTime()),
                            operator.getTitle() , operator.getName(),reason);
                    noticeDao.insert(new Notice(form.getEmployeeId(),noticeContent1));

                    //消息2: 通知总经理有新的审批任务
                    String noticeContent2 = String.format("%s-%s提起请假申请[%s-%s],请尽快审批" ,
                            employee.getTitle() , employee.getName() , sdf.format( form.getStartTime()) , sdf.format(form.getEndTime()));
                    noticeDao.insert(new Notice(readyProcess.getOperatorId(),noticeContent2));

                    //消息3: 通知部门经理(当前经办人),员工的申请单你已批准,交由上级继续审批
                    String noticeContent3 = String.format("%s-%s提起请假申请[%s-%s]您已批准,审批意见:%s,申请转至上级领导继续审批" ,
                            employee.getTitle() , employee.getName() , sdf.format( form.getStartTime()) , sdf.format(form.getEndTime()), reason);
                    noticeDao.insert(new Notice(operator.getEmployeeId(),noticeContent3));


                }else if(result.equals("refused")){
                    //4、如果当前任务不是最后一个节点且审批驳回，则后续所有的任务状态变为cancel，请假单状态变为refused
                    for (ProcessFlow p:readyList){
                        p.setState("cancel");
                        processFlowDao.update(p);
                    }
                    form.setState("refused");
                    leaveFormDao.update(form);
                    //消息1: 通知申请人表单已被驳回
                    String noticeContent1 = String.format("您的请假申请[%s-%s]%s%s已驳回,审批意见:%s,审批流程已结束" ,
                            sdf.format(form.getStartTime()) , sdf.format(form.getEndTime()),
                            operator.getTitle() , operator.getName(),reason);
                    noticeDao.insert(new Notice(form.getEmployeeId(),noticeContent1));

                    //消息2: 通知经办人表单"您已驳回"
                    String noticeContent2 = String.format("%s-%s提起请假申请[%s-%s]您已驳回,审批意见:%s,审批流程已结束" ,
                            employee.getTitle() , employee.getName() , sdf.format( form.getStartTime()) , sdf.format(form.getEndTime()), reason);
                    noticeDao.insert(new Notice(operator.getEmployeeId(),noticeContent2));

                }

            }
            return null;
        });
    }
}
