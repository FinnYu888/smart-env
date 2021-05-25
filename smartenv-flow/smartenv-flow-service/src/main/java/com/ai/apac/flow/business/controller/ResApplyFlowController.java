package com.ai.apac.flow.business.controller;

import com.ai.apac.flow.engine.service.FlowService;
import com.ai.apac.flow.engine.service.IFlowInfoService;
import com.ai.apac.flow.engine.service.IFlowTaskAllotService;
import com.ai.apac.flow.engine.service.ITaskListenerResApplyFLowService;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.feign.IResOrderClient;
import com.ai.apac.smartenv.inventory.vo.ResInfoApplyVO;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("follows")
@AllArgsConstructor
@Api(value = "物资申请管理接口", tags = "物资申请管理接口")
public class ResApplyFlowController {
    ITaskListenerResApplyFLowService resApplyFLowService;
    IResOrderClient orderClient;
    private TaskService taskService;

    private IPersonUserRelClient personUserRelClient;
    private IPersonClient personClient;

    private IHomeDataClient homeDataClient;
    private FlowService flowService;
    private IFlowInfoService flowInfoService;
    /**
     * 物资申请
     */
    @ApiLog(value = "物资申请")
    @PostMapping("/resApplyOrder")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "物资申请提交", notes = "传入ResInfoApplyVO")
    public R<ResOrder> resApplyOrder(@RequestBody ResInfoApplyVO infoApplyVO, BladeUser bladeUser,@RequestHeader(CommonConstant.ACCEPT_CHANNEL_TYPE) String acceptChannelType) {
        //校验流程是否配置审批节点

        if(!flowInfoService.checkFlowInfoConfig(InventoryConstant.Flow_Key.RES_APPLY_FLOW,AuthUtil.getTenantId())) {
            throw new ServiceException("请先检查流程节点人员配置。");
        }
        if(StringUtil.isNotBlank(acceptChannelType) && acceptChannelType.equals(CommonConstant.ACCEPT_CHANNEL_TYPES.MINI_APP)){
            PersonUserRel personUserRel =  personUserRelClient.getRelByUserId(infoApplyVO.getCustId()).getData();
            if(ObjectUtil.isNotEmpty(personUserRel) && ObjectUtil.isNotEmpty(personUserRel.getPersonId())){
                Person person = personClient.getPerson(personUserRel.getPersonId()).getData();
                infoApplyVO.setCustId(personUserRel.getPersonId());
                infoApplyVO.setCustName(person.getPersonName());
            }
        }
        if (null != bladeUser) infoApplyVO.setTenantId(bladeUser.getTenantId());
        return resApplyFLowService.resApplyOrder(infoApplyVO);

    }
    /**
     * 领导审批
     */
    @ApiLog(value = "领导审批")
    @PostMapping("/resLeaderApply")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "领导审批", notes = "传入orderMileStoneVO")
    public R resLeaderApply(@RequestBody ResOrderMilestoneVO orderMileStoneVO, BladeUser bladeUser) {

        resApplyFLowService.resLeaderApply(orderMileStoneVO);

        return R.status(true);
    }
    /**
     * 确认发货
     */
    @ApiLog(value = "确认发货")
    @PostMapping("/resDelieveryApply")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "确认发货", notes = "传入orderMileStoneVO")
    public R resDelieveryApply(@RequestBody ResOrderMilestoneVO orderMileStoneVO) {

        resApplyFLowService.resDelieveryApply(orderMileStoneVO);


        return R.status(true);
    }
    private String getExceptionMsg(String key) {
        String msg = DictBizCache.getValue(InventoryConstant.ExceptionMsg.CODE, key);
        if (StringUtils.isBlank(msg)) {
            msg = key;
        }
        return msg;
    }
    private String getTaskId(String taskId,String processInstId) {

        Task task=taskService.createTaskQuery() // 创建任务查询
                .taskId(taskId) // 根据任务id查询
                .singleResult();
        if (StringUtil.isBlank(taskId) || null == task) {
            //如果task为空，根据processInstId获取任务节点
            Task taskQuery = taskService.createTaskQuery().processInstanceId(processInstId).singleResult();
            if (null == taskQuery|| !InventoryConstant.Flow_Key.RES_FLOW_DELIVERY.equals(taskQuery.getTaskDefinitionKey())) {
                throw new ServiceException(getExceptionMsg(FlowConstant.ExceptionMsg.KEY_TASK_NODE));
            }
            return taskId = taskQuery.getId();

        }
        return taskId;
    }
    @PostMapping("deploy-uploads")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "上传部署流程文件", notes = "传入文件")
    public R deployUploads(@RequestParam List<MultipartFile> files, @RequestParam String category) {

        return R.status(flowService.deployUpload(files, category));
    }
}
