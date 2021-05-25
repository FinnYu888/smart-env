package com.ai.apac.smartenv.event.controller;

import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.event.cache.PublicEventKpiCache;
import com.ai.apac.smartenv.event.dto.PublicEventInfoDTO;
import com.ai.apac.smartenv.event.entity.PublicEventInfo;
import com.ai.apac.smartenv.event.service.IPublicEventInfoService;
import com.ai.apac.smartenv.event.vo.EventAllInfoVO;
import com.ai.apac.smartenv.event.vo.PublicEventInfoVO;
import com.ai.apac.smartenv.event.wrapper.PublicEventInfoWrapper;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/17 8:41 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("")
@Api(value = "公众事件KPI", tags = "公众事件KPI")
public class PublicEventInfoController {

    @Autowired
    private IProjectClient projectClient;

    @Autowired
    private IPublicEventInfoService publicEventInfoService;

    /**
     * 公众上报事件
     *
     * @param publicEventInfoDTO
     * @return
     */
    @PostMapping("/public/eventInfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "公众上报事件", notes = "公众上报事件")
    public R addPublicEventInfo(@RequestBody PublicEventInfoDTO publicEventInfoDTO) {
        return R.status(publicEventInfoService.savePublicEventInfo(publicEventInfoDTO));
    }


    @GetMapping("/public/getPublicEventBySupervisor")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "巡查员查询事件列表", notes = "巡查员查询事件列表")
    public R<IPage<PublicEventInfoVO>> getPublicEventBySupervisor(PublicEventInfoDTO publicEventInfoDTO, Query query) {
        String tenantId = AuthUtil.getTenantId();
        R<Project> projectByTenantId = projectClient.getProjectByTenantId(tenantId);
        Long adcode = projectByTenantId.getData().getAdcode();
        Long cityCode = Long.valueOf(adcode.toString().substring(0, 4).concat("00"));
        Long provinceCode = Long.valueOf(adcode.toString().substring(0, 2).concat("0000"));

        PublicEventInfo publicEventInfo = new PublicEventInfo();
        publicEventInfo.setEventType(publicEventInfoDTO.getEventType());
        QueryWrapper<PublicEventInfo> wrapper = new QueryWrapper<>(publicEventInfo);
        wrapper.eq("tenant_id", tenantId);

        if (publicEventInfoDTO.getStatus() != null && EventConstant.PublicEventStatus.HANDLE_0.equals(publicEventInfoDTO.getStatus())) {
            wrapper.eq("status", publicEventInfoDTO.getStatus());
            wrapper.or(true,publicEventInfoQueryWrapper -> {
                publicEventInfoQueryWrapper.in("adCode",adcode, cityCode, provinceCode);
                publicEventInfoQueryWrapper.eq("tenant_id", "000000");
                if (publicEventInfoDTO.getStatus() != null && EventConstant.PublicEventStatus.HANDLE_0.equals(publicEventInfoDTO.getStatus())) {
                    publicEventInfoQueryWrapper.eq("status", publicEventInfoDTO.getStatus());
                }
            });
        }else if (publicEventInfoDTO.getStatus() != null && EventConstant.PublicEventStatus.HANDLE_1.equals(publicEventInfoDTO.getStatus())){
            wrapper.in("status", EventConstant.PublicEventStatus.HANDLE_3,EventConstant.PublicEventStatus.HANDLE_1,EventConstant.PublicEventStatus.HANDLE_2);
        }
//        wrapper.or(true).in("adCode", adcode, cityCode, provinceCode).eq("tenant_id", "000000");




        wrapper.orderByDesc("create_time");
        IPage<PublicEventInfo> page = publicEventInfoService.getBaseMapper().selectPage(Condition.getPage(query), wrapper);

        IPage<PublicEventInfoVO> publicEventInfoVOIPage = PublicEventInfoWrapper.build().pageVO(page);
        publicEventInfoVOIPage.getRecords().forEach(publicEventInfoVO -> {
            publicEventInfoVO.setEventTypeName(PublicEventKpiCache.getKpiById(Long.parseLong(publicEventInfoVO.getEventType())).getKpiName());
            publicEventInfoVO.setEventStatusName(DictCache.getValue("public_event_status", publicEventInfoVO.getStatus()));
        });
        return R.data(publicEventInfoVOIPage);
    }

    @PostMapping("/public/canclePublicEvent")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "巡查员取消公共事件", notes = "巡查员取消公共事件")
    public R<String> canclePublicEvent(@RequestBody PublicEventInfoDTO publicEventInfoDTO){
        PublicEventInfo entity=BeanUtil.copy(publicEventInfoDTO,PublicEventInfo.class);
        entity.setStatus(EventConstant.PublicEventStatus.HANDLE_3);
        entity.setTenantId(AuthUtil.getTenantId());
        publicEventInfoService.updateById(entity);

        return R.data(null);
    }


    @PostMapping("/public/confirmPublicEvent")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "巡查员确认公共事件", notes = "巡查员确认公共事件")
    public R<String> confirmPublicEvent(@RequestBody EventAllInfoVO eventInfoVO){
        String coordsTypeStr = WebUtil.getHeader("coordsType");
        Integer coordsType= BaiduMapUtils.CoordsSystem.GC02.value;
        if (StringUtil.isEmpty(coordsTypeStr)){
            coordsType= Integer.parseInt(coordsTypeStr);
        }
        publicEventInfoService.confirmPublicEvent(eventInfoVO,coordsType);
        return R.data(null);
    }






    @GetMapping("/publicEvent/listPublicEventInfoByWechat")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "根据微信ID获取上报的事件", notes = "公众上报事件")
    public R<IPage<PublicEventInfoVO>> listPublicEventInfoByWechat(String wechatId, Query query) {

        PublicEventInfo publicEventInfo = new PublicEventInfo();
        publicEventInfo.setReportPersonId(wechatId);
        QueryWrapper<PublicEventInfo> queryWrapper = new QueryWrapper<>(publicEventInfo);
        queryWrapper.orderByDesc("create_time");
        IPage<PublicEventInfo> page = publicEventInfoService.page(Condition.getPage(query), queryWrapper);
        IPage<PublicEventInfoVO> publicEventInfoVOIPage = PublicEventInfoWrapper.build().pageVO(page);
        publicEventInfoVOIPage.getRecords().forEach(publicEventInfoVO -> {
            publicEventInfoVO.setEventTypeName(PublicEventKpiCache.getKpiById(Long.parseLong(publicEventInfoVO.getEventType())).getKpiName());
            publicEventInfoVO.setEventStatusName(DictCache.getValue("public_event_status", publicEventInfoVO.getStatus()));
        });
        return R.data(publicEventInfoVOIPage);
    }

    @GetMapping("/publicEvent/getDetail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取事件详情", notes = "公众上报事件")
    public R<PublicEventInfoVO> getPublicEventDetail(Long eventId) {
        PublicEventInfoVO eventDetail = publicEventInfoService.getEventDetail(eventId);
        eventDetail.setEventTypeName(PublicEventKpiCache.getKpiById(Long.parseLong(eventDetail.getEventType())).getKpiName());
        eventDetail.setEventStatusName(DictCache.getValue("public_event_status", eventDetail.getStatus()));
        return R.data(eventDetail);
    }


}
