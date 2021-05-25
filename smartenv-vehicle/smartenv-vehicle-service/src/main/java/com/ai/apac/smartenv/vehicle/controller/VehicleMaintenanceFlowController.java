package com.ai.apac.smartenv.vehicle.controller;


import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.feign.IDictBizClient;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrder;
import com.ai.apac.smartenv.vehicle.service.IVehicleMaintOrderService;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintOrderApproveVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleMaintTypeVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("vechile-follows")
@AllArgsConstructor
@Api(value = "车辆维修申请流程", tags = "车辆维修申请流程")
public class VehicleMaintenanceFlowController {
    private IVehicleMaintOrderService vehicleMaintOrderService;
    private IPersonClient personClient;
    private IDictBizClient dictBizClient;
    private IDictClient dictClient;
    @ApiLog(value = "获取车辆维保类型树")
    @GetMapping("/vechileMainTypeTree")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取车辆维保类型树", notes = "获取车辆维保类型树")
    public R<List<VehicleMaintTypeVO>> vechileMainTypeTree() {
        List<Dict> maintTypeList = dictClient.getList(VehicleConstant.Maint_Type.CODE).getData();
        if (null != maintTypeList && maintTypeList.size()>0) {
            List<VehicleMaintTypeVO> dictVOS = new ArrayList<>();
            for (Dict dict : maintTypeList) {
                VehicleMaintTypeVO dictVO = new VehicleMaintTypeVO();
                List<VehicleMaintTypeVO> voList = null;
                BeanUtil.copyProperties(dict,dictVO);
                List<Dict> dictList = dictClient.getList(dict.getDictKey()).getData();
                if (CollectionUtil.isNotEmpty(dictList)) {
                    voList = new ArrayList<>();
                    for (Dict dict1 : dictList) {
                        VehicleMaintTypeVO dictVO1 = new VehicleMaintTypeVO();
                        BeanUtil.copyProperties(dict1,dictVO1);
                        dictVO1.setChileType(null);
                        voList.add(dictVO1);
                    }
                    dictVO.setChileType(voList);
                }

                dictVOS.add(dictVO);
            }
            return R.data(dictVOS);
        }
        return R.data(null);
    }
    @ApiLog(value = "车辆维修申请提交")
    @PostMapping("/vechileMainApply")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "车辆维修申请提交", notes = "VehicleMaintOrder")
    public R<Long> vechileMainApply(@Valid @RequestBody VehicleMaintOrder vehicleMaintOrder) {

        vehicleMaintOrderService.saveVehcileMaintOrder(vehicleMaintOrder,null,null);
        return R.data(vehicleMaintOrder.getId());
    }
    @ApiLog(value = "车辆维修车队长审批")
    @PostMapping("/vechileMainCaptainApprove")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "车辆维修车队长审批", notes = "maintOrderApproveVO")
    public R<Long> vechileMainCaptainApprove(@Valid @RequestBody VehicleMaintOrderApproveVO maintOrderApproveVO) {
        vehicleMaintOrderService.vechileMainFirstApprove(maintOrderApproveVO);
        return R.status(true);
    }
    @ApiLog(value = "车辆维保预算")
    @PostMapping("/vechileMainBudget")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "车辆维保预算", notes = "maintOrderApproveVO")
    public R<Long> vechileMainBudget(@Valid @RequestBody VehicleMaintOrder vehicleMaintOrder) {
        vehicleMaintOrderService.vechileMainBudget(vehicleMaintOrder);
        return R.status(true);
    }
    @ApiLog(value = "车辆经理审批")
    @PostMapping("/vechileMainManageApprove")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "车辆经理审批", notes = "VehicleMaintOrderApproveVO")
    public R<Long> vechileMainManageApprove(@Valid @RequestBody VehicleMaintOrderApproveVO maintOrderApproveVO)  {
        vehicleMaintOrderService.vechileMainManageApprove(maintOrderApproveVO);
        return R.status(true);
    }
    @ApiLog(value = "申请人维修完成确认")
    @PostMapping("/vechileMainToFinish")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "申请人维修完成确认", notes = "maintOrderApproveVO")
    public R<Long> vechileMainToFinish(@Valid @RequestBody VehicleMaintOrder vehicleMaintOrder)  {
        vehicleMaintOrderService.vechileMainToFinish(vehicleMaintOrder);
        return R.status(true);
    }
    @GetMapping("/cancelOrder")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "取消流程申请", notes = "传入vehicleMaintOrder")
    public R cancelOrder(@ApiParam(value = "订单id", required = true) @RequestParam String id) {
        vehicleMaintOrderService.cancelOrder(id);
        return R.status(true);
    }
}
