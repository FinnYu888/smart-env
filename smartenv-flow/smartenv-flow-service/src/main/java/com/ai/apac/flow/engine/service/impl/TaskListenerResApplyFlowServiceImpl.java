package com.ai.apac.flow.engine.service.impl;

import com.ai.apac.flow.business.service.FlowBusinessService;
import com.ai.apac.flow.engine.service.IFlowTaskAllotService;
import com.ai.apac.flow.engine.service.IFlowTaskPendingService;
import com.ai.apac.flow.engine.service.ITaskListenerResApplyFLowService;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.flow.entity.FlowTaskPending;
import com.ai.apac.smartenv.flow.feign.IFlowClient;
import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.feign.IResOrderClient;
import com.ai.apac.smartenv.inventory.vo.ResInfoApplyVO;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.delegate.DelegateTask;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("taskListenerResApplyFlow")
public  class TaskListenerResApplyFlowServiceImpl implements ITaskListenerResApplyFLowService {
    @Autowired
    IResOrderClient orderClient;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IFlowTaskAllotService flowTaskAllotService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IHomeDataClient homeDataClient;
    @Autowired
    FlowBusinessService flowBusinessService;
    @Autowired
    IFlowTaskPendingService flowTaskPendingService;

    @Autowired
    private IDataChangeEventClient dataChangeEventClient;


    @Override
    public void applyBefore(DelegateTask task, Long orderid, int status) {

        ResOrderMilestoneVO orderMilestone = new ResOrderMilestoneVO();
        orderMilestone.setTaskId(task.getId());
        orderMilestone.setTaskDefineName(task.getTaskDefinitionKey());
        orderMilestone.setOrderId(orderid);
        orderMilestone.setDoneResult(Func.toStr(status));
        orderMilestone.setProcessInstanceId(task.getProcessInstanceId());
         orderClient.createOrderMilestone(orderMilestone).getData();
    }

    @Override
    public void applyAfters(DelegateTask task, Long orderid,String applyResult,String remark) {
        ResOrderMilestoneVO orderMilestone = new ResOrderMilestoneVO();
        orderMilestone.setOrderId(orderid);
        /*orderMilestone.setAssignmentId(assignmentId);
        orderMilestone.setAssignmentName(assignmentName);*/
        orderMilestone.setDoneResult(applyResult);
        orderMilestone.setDoneRemark(remark);
        orderMilestone.setTaskDefineName(task.getTaskDefinitionKey());
        orderMilestone.setTaskId(task.getId());
        orderMilestone.setProcessInstanceId(task.getProcessInstanceId());
         orderClient.updateOrderMilestone(orderMilestone).getData();
    }

    @Override
    public void deliverBefores(DelegateTask task,Long orderId, int status) {
        ResOrderMilestoneVO orderMilestone = new ResOrderMilestoneVO();
        orderMilestone.setOrderId(orderId);
        orderMilestone.setTaskDefineName(task.getTaskDefinitionKey());
        orderMilestone.setTaskId(task.getId());
        orderMilestone.setDoneResult(Func.toStr(status));
        orderMilestone.setProcessInstanceId(task.getProcessInstanceId());
         orderClient.updateDeliverOrderMilestone(orderMilestone).getData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<ResOrder> resApplyOrder(ResInfoApplyVO infoApplyVO) {
        BladeUser user = AuthUtil.getUser();
        if (infoApplyVO != null) {
            infoApplyVO.setCreateUser(user.getUserId());
            infoApplyVO.setCreateDept((Long)Func.toLongList(user.getDeptId()).iterator().next());
            infoApplyVO.setUpdateUser(user.getUserId());
        }
        R<ResOrder> resOrderR = orderClient.resApplyOrder(infoApplyVO);
        if (null != resOrderR && resOrderR.getCode() == ResultCodeConstant.ResponseCode.SUCCESS) {
            //提交审批
            Map param = new HashMap<>();
            param.put("orderId",resOrderR.getData().getId());
            ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(InventoryConstant.Flow_Key.RES_APPLY_FLOW, InventoryConstant.Flow_Key.RES_APPLY_FLOW,param);
            //创建待执行任务
            flowTaskAllotService.createFlowTask(InventoryConstant.Flow_Key.RES_APPLY_FLOW,InventoryConstant.Flow_Key.RES_FLOW_APPLY,resOrderR.getData().getId(),AuthUtil.getTenantId());
            //推送首页任务数据
            dataChangeEventClient.doWebsocketEvent(new BaseWsMonitorEventDTO<String>(WsMonitorEventConstant.EventType.ORDER_EVENT, AuthUtil.getTenantId(), AuthUtil.getTenantId(),null));
        }
        return resOrderR;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resLeaderApply(ResOrderMilestoneVO orderMileStoneVO) {
        String tenantId = AuthUtil.getTenantId();
        //校验有没有待处理任务
        Long orderId = orderMileStoneVO.getOrderId();
        ResOrder resOrder = orderClient.getApplyOrder(orderId).getData();
        if (null == resOrder || InventoryConstant.Order_Status.APPROVE != resOrder.getOrderStatus())
        {
            throw new ServiceException("当前没有待处理的任务。");
        }
        //查询有没有待处理任务
        FlowTaskPending flowTaskPending = flowTaskPendingService.getTodoTask(orderMileStoneVO.getOrderId(),InventoryConstant.Flow_Key.RES_FLOW_APPLY);
        //完成当前任务节点
        String remark = orderMileStoneVO.getDoneRemark();
        String result = orderMileStoneVO.getDoneResult();
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put(InventoryConstant.Flow_Key.RES_APPLY_APPLYRESULT,result);
        resultMap.put(InventoryConstant.Flow_Key.RES_APPLY_APPLYREMARK,remark);
        resultMap.put(InventoryConstant.Flow_Key.RES_APPLY_ASSIGNID,null);
        resultMap.put(InventoryConstant.Flow_Key.RES_APPLY_ASSIGNNAME,"");
        //表中不存在taskId，根据流程实例id获取
        flowBusinessService.completeTask(orderMileStoneVO.getTaskId(),InventoryConstant.Flow_Key.RES_FLOW_APPLY,resOrder.getWorkflowId(),remark,resultMap);

        //更新当前任务为已完成状态
        flowTaskPendingService.finishFlowTaskPending(InventoryConstant.Flow_Key.RES_APPLY_FLOW,InventoryConstant.Flow_Key.RES_FLOW_APPLY,orderMileStoneVO.getOrderId());
        //更新当前milestone为完成
        PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
        Long personId = personUserRel.getPersonId();
        Person person = PersonCache.getPersonById(AuthUtil.getTenantId(),personId);
        ResOrderMilestoneVO applyOrderMilestone = new ResOrderMilestoneVO();
        applyOrderMilestone.setOrderId(orderMileStoneVO.getOrderId());
        applyOrderMilestone.setAssignmentId(Func.toStr(personId));
        applyOrderMilestone.setAssignmentName(person.getPersonName());
        applyOrderMilestone.setDoneResult(result);
        applyOrderMilestone.setDoneRemark(remark);
        applyOrderMilestone.setTaskDefineName(InventoryConstant.Flow_Key.RES_FLOW_APPLY);
        applyOrderMilestone.setTaskId(orderMileStoneVO.getTaskId());
        applyOrderMilestone.setProcessInstanceId(orderMileStoneVO.getProcessInstanceId());
        orderClient.updateOrderMilestone(applyOrderMilestone);

        if (Func.toStr(InventoryConstant.Order_Status.APPROVE_AGREE).equals(result) ) {
            //创建发货milestone
            ResOrderMilestoneVO orderMilestone = new ResOrderMilestoneVO();
            orderMilestone.setTaskDefineName(InventoryConstant.Flow_Key.RES_FLOW_DELIVERY);
            orderMilestone.setOrderId(orderMileStoneVO.getOrderId());
            orderMilestone.setDoneResult(Func.toStr(InventoryConstant.Order_Status.RECEIVE));
            orderClient.createOrderMilestone(orderMilestone).getData();
            flowTaskAllotService.createFlowTask(InventoryConstant.Flow_Key.RES_APPLY_FLOW,InventoryConstant.Flow_Key.RES_FLOW_DELIVERY,orderMileStoneVO.getOrderId(),AuthUtil.getTenantId());

        }

        dataChangeEventClient.doWebsocketEvent(new BaseWsMonitorEventDTO<String>(WsMonitorEventConstant.EventType.ORDER_EVENT, tenantId, tenantId,null));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resDelieveryApply(ResOrderMilestoneVO orderMileStoneVO) {
        //校验有没有待处理任务
        Long orderId = orderMileStoneVO.getOrderId();
        ResOrder resOrder = orderClient.getApplyOrder(orderId).getData();
        if (null == resOrder || InventoryConstant.Order_Status.RECEIVE != resOrder.getOrderStatus())
        {
            throw new ServiceException("当前没有待处理的任务。");
        }
        //查询有没有待处理任务
        FlowTaskPending flowTaskPending = flowTaskPendingService.getTodoTask(orderMileStoneVO.getOrderId(),InventoryConstant.Flow_Key.RES_FLOW_DELIVERY);
        //完成当前任务节点
        String remark = orderMileStoneVO.getDoneRemark();
            String result = Func.toStr(InventoryConstant.Order_Status.FINISH);
         Map<String,Object> resultMap = new HashMap<>();
        resultMap.put(InventoryConstant.Flow_Key.RES_APPLY_APPLYRESULT,result);
        resultMap.put(InventoryConstant.Flow_Key.RES_APPLY_APPLYREMARK,remark);
        BladeUser bladeUser = AuthUtil.getUser();
        resultMap.put(InventoryConstant.Flow_Key.RES_APPLY_ASSIGNID,bladeUser.getAccount());
        resultMap.put(InventoryConstant.Flow_Key.RES_APPLY_ASSIGNNAME,bladeUser.getUserName());
        flowBusinessService.completeTask(orderMileStoneVO.getTaskId(),InventoryConstant.Flow_Key.RES_FLOW_DELIVERY,resOrder.getWorkflowId(),remark,resultMap);

        //更新当前任务为已完成状态
        flowTaskPendingService.finishFlowTaskPending(InventoryConstant.Flow_Key.RES_APPLY_FLOW,InventoryConstant.Flow_Key.RES_FLOW_DELIVERY,orderMileStoneVO.getOrderId());
        //更新当前milestone为完成
        PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(AuthUtil.getUserId());
        Long personId = personUserRel.getPersonId();
        Person person = PersonCache.getPersonById(AuthUtil.getTenantId(),personId);
        ResOrderMilestoneVO applyOrderMilestone = new ResOrderMilestoneVO();
        applyOrderMilestone.setOrderId(orderMileStoneVO.getOrderId());
        applyOrderMilestone.setAssignmentId(Func.toStr(personId));
        applyOrderMilestone.setAssignmentName(person.getPersonName());
        applyOrderMilestone.setDoneResult(result);
        applyOrderMilestone.setDoneRemark(remark);
        applyOrderMilestone.setTaskDefineName(InventoryConstant.Flow_Key.RES_FLOW_DELIVERY);
        applyOrderMilestone.setTaskId(orderMileStoneVO.getTaskId());
        applyOrderMilestone.setProcessInstanceId(orderMileStoneVO.getProcessInstanceId());
        orderClient.updateOrderMilestone(applyOrderMilestone);
        //记录物资领用记录
        R resturn = orderClient.resDeliveryRecord(orderId);
        if (ResultCodeConstant.ResponseCode.SUCCESS != resturn.getCode()) {
            throw new ServiceException(resturn.getMsg());
        }


    }


}
