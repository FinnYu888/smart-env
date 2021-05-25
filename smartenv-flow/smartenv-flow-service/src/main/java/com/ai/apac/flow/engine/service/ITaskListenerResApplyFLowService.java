package com.ai.apac.flow.engine.service;

import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.vo.ResInfoApplyVO;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import org.flowable.task.api.Task;
import org.flowable.task.service.delegate.DelegateTask;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;

import java.util.Map;

/**
* 物资申请流程任务监听
*/
public interface ITaskListenerResApplyFLowService {
    /**
    * 领导审批前置任务
    */
    public void applyBefore(DelegateTask task, Long orderid, int status);
    /**
     * 领导审批后置任务
     */
     public void applyAfters(DelegateTask task, Long orderid, String applyResult,String remark);

     /*
     *
     */
    public void deliverBefores(DelegateTask task,Long orderId,int status);

    public R<ResOrder> resApplyOrder(ResInfoApplyVO infoApplyVO);


    /**
    * 领导审批节点
    * @author 66578
    */
    void resLeaderApply(ResOrderMilestoneVO orderMileStoneVO);

    /**
    * w物资出库
    * @author 66578
    */
    void resDelieveryApply(ResOrderMilestoneVO orderMileStoneVO);
}
