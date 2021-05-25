package com.ai.apac.smartenv.ops.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import com.ai.apac.smartenv.facility.vo.FacilityTranstationDetailVO;
import com.ai.apac.smartenv.ops.service.ITranstationMockService;
import com.ai.apac.smartenv.ops.util.HttpServiceUtil;
import com.ai.apac.smartenv.ops.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/20 9:14 下午
 **/
@Slf4j
public class TransationMockServiceImpl implements ITranstationMockService {

    static final String SERVER_URL = "http://www.asiainfo.tech:31203/";

    static final String IMPORT_TRASH_WEIGHT_URL = SERVER_URL + "smartenv-facility/facilityManage/receiveTranstationInfo";

    /**
     * 导入中转站垃圾称重数据
     *
     * @param facilityTranstationDetailVO
     */
    @Override
    public void importTranstationData(FacilityTranstationDetailVO facilityTranstationDetailVO) {
        String token = TokenUtil.getAuthToken("dean_admin", "123456");
        String res = HttpServiceUtil.postRestful(IMPORT_TRASH_WEIGHT_URL, facilityTranstationDetailVO, token);
        log.info("导入中转站垃圾称重数据结果:{}", res);
    }

    public void batchImportTrashWeight(Long facilityId) {
        List<String> dayList = StatServiceImpl.getDayListOfMonth();
        dayList.stream().forEach(day -> {
            List<FacilityTranstationDetailVO> dataList = new ArrayList<>();
            DateTime dateTime = new DateTime(day + " 12:34:23", DatePattern.NORM_DATETIME_FORMAT);
            for (int i = 1; i <= 4; i++) {
//                System.out.println(i);
                FacilityTranstationDetailVO facilityTranstationDetailVO = new FacilityTranstationDetailVO();
                facilityTranstationDetailVO.setFacilityId(facilityId);
                facilityTranstationDetailVO.setGarbageWeight(String.valueOf(RandomUtil.randomInt(2, 8)));
                facilityTranstationDetailVO.setGarbageType(String.valueOf(i));
                facilityTranstationDetailVO.setTransferTime(new Timestamp(dateTime.getTime()));
                importTranstationData(facilityTranstationDetailVO);
            }
        });
    }

//    public static void main(String[] args) {
////        FacilityTranstationDetailVO facilityTranstationDetailVO = new FacilityTranstationDetailVO();
////        facilityTranstationDetailVO.setFacilityId(1283963058048901121L);
////        facilityTranstationDetailVO.setGarbageWeight(String.valueOf(RandomUtil.randomInt(8)));
////        facilityTranstationDetailVO.setGarbageType("3");
////        facilityTranstationDetailVO.setTransferTime(new Timestamp(System.currentTimeMillis()));
//
////        TransationMockServiceImpl transationMockService = new TransationMockServiceImpl();
////        transationMockService.batchImportTrashWeight(1285389113936896001L);
//    }
}
