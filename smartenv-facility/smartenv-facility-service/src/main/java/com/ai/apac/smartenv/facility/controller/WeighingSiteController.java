package com.ai.apac.smartenv.facility.controller;

import cn.hutool.core.date.DatePattern;
import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.facility.dto.BasicWeighingSitePolymerizationDTO;
import com.ai.apac.smartenv.facility.dto.WeighingSiteMetaDataDTO;
import com.ai.apac.smartenv.facility.dto.WeighingSiteRecordDTO;
import com.ai.apac.smartenv.facility.service.IWeighingSiteService;
import com.ai.apac.smartenv.facility.vo.WeighingSiteAllPolymerization;
import com.ai.apac.smartenv.facility.vo.WeighingSiteRocordVO;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WeighingSiteController
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/3
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/3  15:35    panfeng          v1.0.0             修改原因
 */

@RestController
@AllArgsConstructor
@RequestMapping("/weighingSite")
@Api(value = "称重点详情", tags = "称重点管理")
public class WeighingSiteController {

    @Autowired
    private IWeighingSiteService weighingSiteService;


    @Autowired
    private MongoTemplate mongoTemplate;


    @PostMapping("/importWeighingSiteRecord")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "导入称重记录", notes = "传入Excel")
    public R<String> importWeighingSiteRecord(@RequestParam("file") MultipartFile excel, BladeUser bladeUser) throws Exception {
        BufferedInputStream inputStream;
        try {
            inputStream = new BufferedInputStream(excel.getInputStream());

            List<Object> datas = EasyExcelFactory.read(inputStream, new Sheet(1, 1));
            if (CollectionUtil.isEmpty(datas)) {
                return R.fail("导入文件为空");
            }
            List<WeighingSiteRecordDTO> weighingSiteRecordDTOS = new ArrayList<>();

            //获取每一行的数据
            for (Object rowData : datas) {
                List<String> params = new ArrayList<>();

                //获取每一行中每一列的数据
                List<?> rowList = (List<?>) rowData;
                for (Object o : rowList) {
                    params.add(String.class.cast(o));
                }

                //设值
                WeighingSiteRecordDTO weighingSiteRecordDTO = new WeighingSiteRecordDTO();
                weighingSiteRecordDTO.setWeighingSiteId(1334421361593966595L); // 先写死
                weighingSiteRecordDTO.setCompanyId("1336292693825642498");// 公司ID，后面从blade 中取


                weighingSiteRecordDTO.setWeighingRecordId(params.get(0));
                weighingSiteRecordDTO.setFreightName(params.get(1));
                weighingSiteRecordDTO.setFreightSpec(params.get(2));
                weighingSiteRecordDTO.setGrossWeightTime(params.get(3) == null ? null : DateUtil.parse(params.get(3), DatePattern.NORM_DATETIME_PATTERN));
                weighingSiteRecordDTO.setGrossWeight(params.get(4) == null ? null : Double.parseDouble(params.get(4)));
                weighingSiteRecordDTO.setTareTime(params.get(5));
                weighingSiteRecordDTO.setTare(params.get(6) == null ? null : Double.parseDouble(params.get(6)));
                weighingSiteRecordDTO.setActualWeight(params.get(7) == null ? null : Double.parseDouble(params.get(7)));
                weighingSiteRecordDTO.setWeighingTime(params.get(8) == null ? null : DateUtil.parse(params.get(8), DatePattern.NORM_DATETIME_PATTERN));
                weighingSiteRecordDTO.setDeduction(params.get(9) == null ? null : Double.parseDouble(params.get(9)));
                weighingSiteRecordDTO.setNetWeight(params.get(10) == null ? null : Double.parseDouble(params.get(10)));

                weighingSiteRecordDTO.setRadioFrequencyCardNo(params.get(11));
                weighingSiteRecordDTO.setShipper(params.get(12));
                weighingSiteRecordDTO.setReceivingUnit(params.get(13));
                weighingSiteRecordDTO.setTransportUnit(params.get(14));
                weighingSiteRecordDTO.setPlateNumber(params.get(15));
                weighingSiteRecordDTO.setDrivingLicenseNumber(params.get(16));
                weighingSiteRecordDTO.setDriver(params.get(17));
                weighingSiteRecordDTO.setRemark(params.get(18));
                weighingSiteRecordDTO.setWeighman(params.get(19));
                weighingSiteRecordDTO.setWeighingSiteNo(params.get(20));
                weighingSiteRecordDTOS.add(weighingSiteRecordDTO);
            }

            weighingSiteService.batchImportWeighingSiteData(weighingSiteRecordDTOS);

        } catch (IOException e) {
            return R.fail("导入失败");
        }


        return R.success("success");

    }

    @GetMapping("/getCompany")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取收集单位", notes = "获取收集单位")
    public R<List<String>> getCompany() {
        List<WeighingSiteMetaDataDTO> weighingSiteMetaData = mongoTemplate.findAll(WeighingSiteMetaDataDTO.class, "WeighingSiteMetaData");
        if (CollectionUtil.isEmpty(weighingSiteMetaData)) {
            return R.data(null);
        }
        List<String> transportUnitList = weighingSiteMetaData.get(0).getTransportUnitList();
        return R.data(transportUnitList);
    }

    @GetMapping("/getShipper")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取垃圾收集区域", notes = "获取垃圾收集区域")
    public R<List<String>> getShipper() {
        List<WeighingSiteMetaDataDTO> weighingSiteMetaData = mongoTemplate.findAll(WeighingSiteMetaDataDTO.class, "WeighingSiteMetaData");
        if (CollectionUtil.isEmpty(weighingSiteMetaData)) {
            return R.data(null);
        }
        List<String> shipperList = weighingSiteMetaData.get(0).getShipperList();
        return R.data(shipperList);
    }

    @GetMapping("/getWeighingSiteRecords")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取垃圾收集数据", notes = "获取垃圾收集数据")
    public R<IPage<WeighingSiteRecordDTO>> getWeighingSiteRecords(WeighingSiteRocordVO weighingSiteRecordDTO, org.springblade.core.mp.support.Query queryp) {

        Query query = new Query();

        if (StringUtil.isNotBlank(weighingSiteRecordDTO.getWeighingRecordId())) {
            query.addCriteria(Criteria.where("weighingRecordId").regex("^.*" + weighingSiteRecordDTO.getWeighingRecordId() + ".*$"));
        }
        if (StringUtil.isNotBlank(weighingSiteRecordDTO.getTransportUnit())) {
            query.addCriteria(Criteria.where("transportUnit").is(weighingSiteRecordDTO.getTransportUnit()));
        }
        if (StringUtil.isNotBlank(weighingSiteRecordDTO.getShipper())) {
            query.addCriteria(Criteria.where("shipper").is(weighingSiteRecordDTO.getShipper()));
        }
        if (weighingSiteRecordDTO.getStartTime() != null && weighingSiteRecordDTO.getEndTime() != null) {
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where("weighingTime").gte(weighingSiteRecordDTO.getStartTime()),
                    Criteria.where("weighingTime").lte(weighingSiteRecordDTO.getEndTime())
            );

            query.addCriteria(criteria);

        }


        long count = mongoTemplate.count(query, WeighingSiteRecordDTO.class);
        Pageable pageAble = PageRequest.of(queryp.getCurrent(), queryp.getSize() + 1, Sort.by(Sort.Order.desc("weighingTime")));
        query.with(pageAble);


        IPage<WeighingSiteRecordDTO> page = Condition.getPage(queryp);

        List<WeighingSiteRecordDTO> weighingSiteRecordDTOS = mongoTemplate.find(query, WeighingSiteRecordDTO.class);
        page.setRecords(weighingSiteRecordDTOS);
        page.setSize(queryp.getSize());
        page.setCurrent(queryp.getCurrent());
        page.setTotal(count);
        return R.data(page);
    }

    @GetMapping("/getWeighingSiteAllPolymerizationData")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取所有垃圾收集聚合数据", notes = "获取所有垃圾收集聚合数据")
    public R<WeighingSiteAllPolymerization> getWeighingSiteAllPolymerizationData() {
        List<BasicWeighingSitePolymerizationDTO> companyLastWeekPolymerizationData = mongoTemplate.findAll(BasicWeighingSitePolymerizationDTO.class, FacilityConstant.COMPANY_LAST_WEEK_POLYMERIZATION_DATA);
        Query query = new Query();
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        query.addCriteria(Criteria.where("weighingTime").gte(instance.getTime()));
        long count = mongoTemplate.count(query, WeighingSiteRecordDTO.class);
        WeighingSiteAllPolymerization weighingSiteAllPolymerization = new WeighingSiteAllPolymerization();
        weighingSiteAllPolymerization.setRubbishCount(count);
        Map<String, List<BasicWeighingSitePolymerizationDTO>> collect = companyLastWeekPolymerizationData.stream().collect(Collectors.groupingBy(BasicWeighingSitePolymerizationDTO::getDate));
        String format = DateUtil.format(DateUtil.now(), "MM-dd");
        weighingSiteAllPolymerization.setRubbishWeight(CollectionUtil.isNotEmpty(collect.get(format)) ? collect.get(format).get(0).getValue() : 0);
        return R.data(weighingSiteAllPolymerization);
    }

//
//    @GetMapping("/getLast7DayAllComyany")
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "获取所有公司最近7天的垃圾收集聚合数据(大屏使用)", notes = "生成所有公司最7天的垃圾收集聚合数据")
//    public List<BasicWeighingSitePolymerizationDTO> getLast7DayAllComyany() {
//        // 接入基础数据以后加入公司的条件查询
//        List<BasicWeighingSitePolymerizationDTO> companyLastWeekPolymerizationData = mongoTemplate.findAll(BasicWeighingSitePolymerizationDTO.class, FacilityConstant.COMPANY_LAST_WEEK_POLYMERIZATION_DATA);
//
//        return companyLastWeekPolymerizationData;
//    }
//
//
//    @GetMapping("/weighingDataAllRegionByComyany")
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "获取所有区域最7天的垃圾收集聚合数据（公众号使用）", notes = "生成所有公司最7天的垃圾收集聚合数据")
//    public R<List<BasicWeighingSitePolymerizationDTO>> weighingDataAllRegionByComyany(@RequestParam(required = true) String companyId) {
//
//        List<BasicWeighingSitePolymerizationDTO> companyLastWeekPolymerizationData = mongoTemplate.findAll(BasicWeighingSitePolymerizationDTO.class, FacilityConstant.REGION_LAST_WEEK_POLYMERIZATION_DATA);
//        return R.data(companyLastWeekPolymerizationData);
//    }
//
//
//    @PutMapping("/weighingSiteAllRegionLastWeekPolymerizationData")
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "生成所有区域最7天的垃圾收集聚合数据", notes = "生成所有公司最7天的垃圾收集聚合数据")
//    public R<String> weighingSiteAllRegionLastWeekPolymerizationData() {
//
//        weighingSiteService.weighingSiteAllRegionLastMonthPolymerizationData();
//        return R.success("success");
//    }
//
//
//    @PutMapping("/weighingSiteAllCompanyLastWeekPolymerizationData")
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "生成所有公司最7天的垃圾收集聚合数据", notes = "生成所有公司最7天的垃圾收集聚合数据")
//    public R<String> weighingSiteAllCompanyLastWeekPolymerizationData() {
//
//        weighingSiteService.weighingSiteAllCompanyLastWeekPolymerizationData();
//        return R.success("success");
//    }


}
