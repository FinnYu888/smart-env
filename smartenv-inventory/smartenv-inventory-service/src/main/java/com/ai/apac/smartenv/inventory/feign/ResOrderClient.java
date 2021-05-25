package com.ai.apac.smartenv.inventory.feign;

import com.ai.apac.smartenv.common.constant.InventoryConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.service.*;
import com.ai.apac.smartenv.inventory.vo.ResInfoApplyVO;
import com.ai.apac.smartenv.inventory.vo.ResOrder4HomeVO;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@ApiIgnore
@RestController
@AllArgsConstructor
public class ResOrderClient implements IResOrderClient {
    @Autowired
    IResOrderMilestoneService orderMilestoneService;
    @Autowired
    IResOrderService resOrderService;
    @Autowired
    IResOperateService operateService;

    @Autowired
    IResInfoService resInfoService;
    @Autowired
    IResManageService resManageService;
    @Override
    @PostMapping(API_UPDATE_ORDERMILESTONE)
    public R updateOrderMilestone( @RequestBody ResOrderMilestoneVO resOrderMilestone) {
        resOrderService.updateOrderStatus(resOrderMilestone.getOrderId(),resOrderMilestone.getProcessInstanceId(),Func.toInt(resOrderMilestone.getDoneResult()));
        return R.status(orderMilestoneService.updateOrderMilestoneByCond(resOrderMilestone));
    }

    @Override
    @PostMapping(API_CREATE_ORDERMILESTONE)
    public R createOrderMilestone(ResOrderMilestoneVO resOrderMilestone) {
        //更新订单状态为待审批
        ResOrder resOrder = resOrderService.getById(resOrderMilestone.getOrderId());
        if (null == resOrder) {
            throw new ServiceException(resManageService.getExceptionMsg(InventoryConstant.ExceptionMsg.KEY_INVENTORY_ORDER));
        }
        resOrder.setOrderStatus(Func.toInt(resOrderMilestone.getDoneResult()));
        resOrder.setWorkflowId(resOrderMilestone.getProcessInstanceId());

        resOrderService.updateById(resOrder);
        resOrderMilestone.setTenantId(resOrder.getTenantId());
        resOrderMilestone.setUpdateTime(TimeUtil.addOrMinusMinutes(TimeUtil.getSysDate().getTime(),10));
        resOrderMilestone.setIsDeleted(0);
        resOrderMilestone.setStatus(1);
        resOrderMilestone.setCreateDept(resOrder.getCreateDept());
        resOrderMilestone.setCreateTime(resOrder.getUpdateTime());
        resOrderMilestone.setUpdateUser(resOrder.getUpdateUser());
        orderMilestoneService.saveOrderMileStone(resOrderMilestone);
        return R.status(true);
    }
    @Override
    @PostMapping(API_CREATE_ORDER)
    public R<ResOrder> resApplyOrder( ResInfoApplyVO infoApplyVO) {

        return R.data(resOrderService.resApplySubmitOrder(infoApplyVO));
    }

    @Override
    public R updateDeliverOrderMilestone(ResOrderMilestoneVO resOrderMilestone) {
        resOrderService.updateOrderStatus(resOrderMilestone.getOrderId(),resOrderMilestone.getProcessInstanceId(),Func.toInt(resOrderMilestone.getDoneResult()));
        return R.status(orderMilestoneService.updateDeliverOrderMilestone(resOrderMilestone));
    }

    @Override
    public R resDeliveryRecord(@RequestParam("orderId") Long orderId) {
        return R.status(resInfoService.resDeliveryRecord(orderId));
    }

    @Override
    public R<List<ResOrder4HomeVO>> getlastOrders(String tenantId,String userId) {
        return R.data(resOrderService.lastResOrders(tenantId,userId));
    }

    @Override
    public R<ResOrder> getApplyOrder(Long orderId) {

        return R.data(resOrderService.getById(orderId));
    }
}
