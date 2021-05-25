package com.ai.apac.smartenv.inventory.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.entity.ResOrderMilestone;
import com.ai.apac.smartenv.inventory.vo.ResInfoApplyVO;
import com.ai.apac.smartenv.inventory.vo.ResOrder4HomeVO;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient( value = ApplicationConstant.APPLICATION_INVENTORY_NAME,
        fallback = IResOrderClientFallBack.class
)
public interface IResOrderClient {
    String API_PREFIX = "/client";
    String API_UPDATE_ORDERMILESTONE = API_PREFIX + "/updateOrderMilestone";
    String API_CREATE_ORDERMILESTONE = API_PREFIX + "/createOrderMilestone";
    String API_CREATE_ORDER = API_PREFIX + "/submitResOrder";
    String API_UPDATE_DELIVERMILESTONE = API_PREFIX + "/updateDeliverOrderMilestone";
    String API_DELIEVERY_RECORD =API_PREFIX + "/resDelieveryRecord";
    String API_LAST_ORDERS =API_PREFIX + "last-orders";
    String GET_APPLY_ORDER = API_PREFIX +"/get-apply-order";
    /**
    * 更新订单里程碑信息
    */
    @PostMapping(value = API_UPDATE_ORDERMILESTONE)
    R updateOrderMilestone(@RequestBody ResOrderMilestoneVO resOrderMilestone);

    /**
     * 创建订单里程碑信息
     */
    @PostMapping(value = API_CREATE_ORDERMILESTONE)
    R createOrderMilestone(@RequestBody ResOrderMilestoneVO resOrderMilestone);


    @PostMapping(value = API_CREATE_ORDER)
    R<ResOrder> resApplyOrder( @RequestBody ResInfoApplyVO infoApplyVO) ;
    /**
     * 更新物流发货milestone
     */
    @PostMapping(value = API_UPDATE_DELIVERMILESTONE)
    R updateDeliverOrderMilestone(@RequestBody ResOrderMilestoneVO orderMilestone);
    /**
    * 物资领用记录
    */
    @GetMapping(value = API_DELIEVERY_RECORD)
    R resDeliveryRecord(@RequestParam("orderId") Long orderId);

    @GetMapping(value = API_LAST_ORDERS)
    R<List<ResOrder4HomeVO>> getlastOrders(@RequestParam("tenantId") String tenantId,@RequestParam("userId") String userId);
    /**
    * 查询订单信息
    * @author 66578
    */
    @GetMapping(value = GET_APPLY_ORDER)
    R<ResOrder> getApplyOrder(@RequestParam("orderId")Long orderId);
}
