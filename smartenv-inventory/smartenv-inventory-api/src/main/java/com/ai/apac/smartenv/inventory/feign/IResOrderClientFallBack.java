package com.ai.apac.smartenv.inventory.feign;

import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.vo.ResInfoApplyVO;
import com.ai.apac.smartenv.inventory.vo.ResOrder4HomeVO;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IResOrderClientFallBack implements IResOrderClient {


    @Override
    public R updateOrderMilestone(ResOrderMilestoneVO resOrderMilestone) {
        return R.fail("更新里程碑信息失败");
    }

    @Override
    public R createOrderMilestone(ResOrderMilestoneVO resOrderMilestone) {
        return R.fail("创建里程碑信息失败");
    }

    @Override
    public R<ResOrder> resApplyOrder(ResInfoApplyVO infoApplyVO) { return R.fail("申请订单提交失败");
    }

    @Override
    public R updateDeliverOrderMilestone(ResOrderMilestoneVO orderMilestone) {
        return R.fail("更新里程碑信息失败");
    }

    @Override
    public R resDeliveryRecord(Long orderId) {
        return R.fail("物资领用失败");
    }

    @Override
    public R<List<ResOrder4HomeVO>> getlastOrders(String tenantId, String userId) {
        return R.fail("查询最近垃圾收集吨数失败");
    }

    @Override
    public R<ResOrder> getApplyOrder(Long orderId) {
        return R.fail("获取订单失败");
    }
}
