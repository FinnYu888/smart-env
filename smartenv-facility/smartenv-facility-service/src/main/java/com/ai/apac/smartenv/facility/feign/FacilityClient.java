package com.ai.apac.smartenv.facility.feign;

import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.entity.GarbageAmountDaily;
import com.ai.apac.smartenv.facility.service.IFacilityAsyncService;
import com.ai.apac.smartenv.facility.service.IFacilityInfoService;
import com.ai.apac.smartenv.facility.service.IFacilityTranstationDetailService;
import com.ai.apac.smartenv.facility.vo.FacilityInfoExtVO;
import com.ai.apac.smartenv.facility.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.facility.vo.LastDaysRegionGarbageAmountVO;
import com.ai.apac.smartenv.system.cache.SysCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@ApiIgnore
@RestController
@AllArgsConstructor
public class FacilityClient implements IFacilityClient {

    private IFacilityInfoService facilityInfoService;

    private IFacilityTranstationDetailService facilityTranstationDetailService;

    private IDictClient dictClient;

    private ISysClient sysClient;

    private IFacilityAsyncService facilityAsyncService;


    @Override
    public R<Integer> countFacilityByTenantId(String tenantId, String deviceStatus) {
        QueryWrapper<FacilityInfo> wrapper = new QueryWrapper<FacilityInfo>();
        wrapper.lambda().eq(FacilityInfo::getTenantId,tenantId);
        return R.data(facilityInfoService.count(wrapper));
    }

    @Override
    public R<Boolean> facilityInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.data(facilityAsyncService.thirdFacilityInfoAsync(datasList,tenantId,actionType,true));
    }

    /**
     * 根据facilityId找设施
     * @param facilityId
     * @return
     */
    @Override
    @GetMapping(API_GET_FACILITY_BY_ID)
    public R<FacilityInfo> getFacilityInfoById(@RequestParam("facilityId") Long facilityId) {
        return R.data(facilityInfoService.getById(facilityId));
    }

    @Override
    @GetMapping(API_GET_FACILITY_DETAIL_BY_ID)
    public R<FacilityInfoExtVO> getFacilityDetailById(Long facilityId) {
        FacilityInfo query = new FacilityInfo();
        query.setId(facilityId);
        return R.data(facilityInfoService.getFacilityDetail(query));
    }

    @Override
    @GetMapping(API_GET_UNREGION_FACILITY)
    public R<List<FacilityInfo>> getUnRegionFacility() {
        QueryWrapper<FacilityInfo> queryWrapper = new QueryWrapper<FacilityInfo>();
        queryWrapper.lambda().isNotNull(FacilityInfo::getLng);
        queryWrapper.lambda().isNull(FacilityInfo::getRegionId);
        return R.data(facilityInfoService.list(queryWrapper));
    }

    @Override
    public R updateFacilityInfo(FacilityInfo facilityInfo) {
        return R.data(facilityInfoService.updateById(facilityInfo));
    }

    @Override
    public R<List<LastDaysGarbageAmountVO>> getLastDaysGarbageAmount(Integer days, String tenantId) {
        List<LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList = new ArrayList<LastDaysGarbageAmountVO>();
        List<Dict> dictList = dictClient.getList("GarbageType").getData();
        Timestamp endDate = TimeUtil.getSysDate();
        Timestamp startDate = TimeUtil.getStartTime(TimeUtil.addOrMinusDays(endDate.getTime(),-days+1));
        dictList.forEach(dict -> {
            LastDaysGarbageAmountVO lastDaysGarbageAmountVO = new LastDaysGarbageAmountVO();
            List<GarbageAmountDaily> garbageAmountDailyList = facilityTranstationDetailService.lastDaysGarbageAmount(dict.getDictKey(),TimeUtil.getYYYY_MM_DD_HH_MM_SS(startDate),TimeUtil.getYYYY_MM_DD_HH_MM_SS(endDate),tenantId);
            lastDaysGarbageAmountVO.setGarbageAmountDailyList(garbageAmountDailyList);
            lastDaysGarbageAmountVO.setGarbageTypeId(dict.getDictKey());
            lastDaysGarbageAmountVO.setGarbageTypeName(dict.getDictValue());
            lastDaysGarbageAmountVOList.add(lastDaysGarbageAmountVO);
        });
        return R.data(lastDaysGarbageAmountVOList);
    }

    @Override
    public R<List<LastDaysRegionGarbageAmountVO>> getLastDaysGarbageAmountByRegion(Integer days, String tenantId) {
        Timestamp endDate = TimeUtil.getSysDate();
        Timestamp startDate = TimeUtil.getStartTime(TimeUtil.addOrMinusDays(endDate.getTime(),-days+1));
        List<LastDaysRegionGarbageAmountVO> lastDaysRegionGarbageAmountVOList = facilityTranstationDetailService.lastDaysGarbageAmountGroupByRegion(TimeUtil.getYYYY_MM_DD_HH_MM_SS(startDate),TimeUtil.getYYYY_MM_DD_HH_MM_SS(endDate),tenantId);
        if(ObjectUtil.isNotEmpty(lastDaysRegionGarbageAmountVOList) && lastDaysRegionGarbageAmountVOList.size() > 0){
            lastDaysRegionGarbageAmountVOList.forEach(lastDaysRegionGarbageAmountVO -> {
                String regionId = lastDaysRegionGarbageAmountVO.getRegionId();
                if (StrUtil.isNotBlank(regionId)) {
                    Region region = sysClient.getRegion(Long.parseLong(lastDaysRegionGarbageAmountVO.getRegionId())).getData();
                    if(ObjectUtil.isNotEmpty(region)){
                        lastDaysRegionGarbageAmountVO.setRegionName(region.getRegionName());
                    }
                }
            });
        }
        return R.data(lastDaysRegionGarbageAmountVOList);
    }


    @Override
    @GetMapping(API_GET_ALL_FACILITY)
    public R<List<FacilityInfo>> getAllFacility() {
        QueryWrapper<FacilityInfo> queryWrapper = new QueryWrapper<FacilityInfo>();
        return R.data(facilityInfoService.list(queryWrapper));
    }

    @Override
    public R<Integer> countAllFacility(String tenantId) {
        QueryWrapper<FacilityInfo> queryWrapper = new QueryWrapper<FacilityInfo>();
        queryWrapper.lambda().eq(FacilityInfo::getTenantId,tenantId);
        queryWrapper.lambda().eq(FacilityInfo::getIsDeleted,0);
        return R.data(facilityInfoService.count(queryWrapper));
    }

}
