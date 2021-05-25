package com.ai.apac.smartenv.statistics.controller;

import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.facility.dto.BasicWeighingSitePolymerizationDTO;
import com.ai.apac.smartenv.statistics.service.IWeighingDataService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

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
public class WeighingDataController {

    @Autowired
    private IWeighingDataService weighingSiteService;


    @Autowired
    private MongoTemplate mongoTemplate;


    @GetMapping("/getLast7DayAllComyany")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取所有公司最近7天的垃圾收集聚合数据(大屏使用)", notes = "生成所有公司最7天的垃圾收集聚合数据")
    public List<BasicWeighingSitePolymerizationDTO> getLast7DayAllComyany(@RequestParam String companyId) {

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        instance.add(Calendar.DAY_OF_MONTH, -9);
//        mongoTemplate.findall
        // 接入基础数据以后加入公司的条件查询
        Query query = new Query();
        query.addCriteria(Criteria.where("companyId").is(companyId));
        query.addCriteria(Criteria.where("createTime").gte(instance.getTime()));
        List<BasicWeighingSitePolymerizationDTO> companyLastWeekPolymerizationData = mongoTemplate.find(query, BasicWeighingSitePolymerizationDTO.class, FacilityConstant.COMPANY_LAST_WEEK_POLYMERIZATION_DATA);

        Map<String, List<BasicWeighingSitePolymerizationDTO>> basicWeighingSitePolymerizationDTOMap = companyLastWeekPolymerizationData.stream().collect(Collectors.groupingBy(basicWeighingSitePolymerizationDTO -> basicWeighingSitePolymerizationDTO.getDate()));

        List<BasicWeighingSitePolymerizationDTO> result = new ArrayList<>();

        // 先判断沧州
        if (companyId.equals("1336292693825642498")){
            Calendar current = Calendar.getInstance();
            for (int i = 0; i < 7; i++) {
                current.add(Calendar.DAY_OF_MONTH, -1);
                Date time = current.getTime();
                String format = DateUtil.format(time, "MM-dd");
                List<BasicWeighingSitePolymerizationDTO> basicWeighingSitePolymerizationDTOS = basicWeighingSitePolymerizationDTOMap.get(format);
                if (CollectionUtil.isNotEmpty(basicWeighingSitePolymerizationDTOS)){
                    result.addAll(basicWeighingSitePolymerizationDTOS);
                }else {
                    BasicWeighingSitePolymerizationDTO company1=new BasicWeighingSitePolymerizationDTO();
                    BasicWeighingSitePolymerizationDTO company2=new BasicWeighingSitePolymerizationDTO();
                    BasicWeighingSitePolymerizationDTO company3=new BasicWeighingSitePolymerizationDTO();
                    company1.setValue(0D);
                    company2.setValue(0D);
                    company3.setValue(0D);
                    company1.setObj("龙马");
                    company2.setObj("侨银");
                    company3.setObj("中环洁");
                    company1.setDate(format);
                    company2.setDate(format);
                    company3.setDate(format);
                    company1.setCompanyId(companyId);
                    company2.setCompanyId(companyId);
                    company3.setCompanyId(companyId);
                    result.add(company1);
                    result.add(company2);
                    result.add(company3);
                }
            }
        }


        return result;
    }


    @GetMapping("/weighingDataAllRegionByComyany")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取所有区域最7天的垃圾收集聚合数据（公众号使用）", notes = "生成所有公司最7天的垃圾收集聚合数据")
    public R<List<BasicWeighingSitePolymerizationDTO>> weighingDataAllRegionByComyany(@RequestParam String companyId) {

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        instance.add(Calendar.DAY_OF_MONTH, -9);
        // 接入基础数据以后加入公司的条件查询
        Query query = new Query();
        query.addCriteria(Criteria.where("companyId").is(companyId));
        query.addCriteria(Criteria.where("createTime").gte(instance.getTime()));

        List<BasicWeighingSitePolymerizationDTO> companyLastWeekPolymerizationData = mongoTemplate.find(query,BasicWeighingSitePolymerizationDTO.class, FacilityConstant.REGION_LAST_WEEK_POLYMERIZATION_DATA);

        Map<String, List<BasicWeighingSitePolymerizationDTO>> basicWeighingSitePolymerizationDTOMap = companyLastWeekPolymerizationData.stream().collect(Collectors.groupingBy(basicWeighingSitePolymerizationDTO -> basicWeighingSitePolymerizationDTO.getDate()));


        List<BasicWeighingSitePolymerizationDTO> result = new ArrayList<>();
        Calendar current = Calendar.getInstance();
        // 先判断沧州
        if (companyId.equals("1336292693825642498")){
            for (int i = 0; i < 7; i++) {
                current.add(Calendar.DAY_OF_MONTH, -1);
                Date time = current.getTime();
                String format = DateUtil.format(time, "MM-dd");
                List<BasicWeighingSitePolymerizationDTO> basicWeighingSitePolymerizationDTOS = basicWeighingSitePolymerizationDTOMap.get(format);
                if (CollectionUtil.isNotEmpty(basicWeighingSitePolymerizationDTOS)){
                    result.addAll(basicWeighingSitePolymerizationDTOS);
                }else {
                    BasicWeighingSitePolymerizationDTO company1=new BasicWeighingSitePolymerizationDTO();
                    company1.setValue(0D);
                    company1.setObj("全部");
                    company1.setDate(format);
                    company1.setCompanyId(companyId);
                    result.add(company1);

                }
            }
        }

        return R.data(result);
    }


    @PutMapping("/weighingSiteAllRegionLastWeekPolymerizationData")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "生成所有区域最7天的垃圾收集聚合数据", notes = "生成所有公司最7天的垃圾收集聚合数据")
    public R<String> weighingSiteAllRegionLastWeekPolymerizationData(@RequestParam String companyId) {

        weighingSiteService.weighingSiteAllRegionLastMonthPolymerizationData(companyId);
        return R.success("success");
    }


    @PutMapping("/weighingSiteAllCompanyLastWeekPolymerizationData")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "生成所有公司最7天的垃圾收集聚合数据", notes = "生成所有公司最7天的垃圾收集聚合数据")
    public R<String> weighingSiteAllCompanyLastWeekPolymerizationData(@RequestParam String companyId) {

        weighingSiteService.weighingSiteAllCompanyLastWeekPolymerizationData(companyId);
        return R.success("success");
    }


}
