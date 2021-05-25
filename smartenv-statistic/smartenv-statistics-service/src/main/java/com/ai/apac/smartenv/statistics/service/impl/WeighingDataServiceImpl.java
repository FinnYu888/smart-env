package com.ai.apac.smartenv.statistics.service.impl;

import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.facility.dto.BasicWeighingSitePolymerizationDTO;
import com.ai.apac.smartenv.facility.dto.WeighingSiteRecordDTO;
import com.ai.apac.smartenv.statistics.service.IWeighingDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WeighingSiteServiceImpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/12/3
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/3  16:45    panfeng          v1.0.0             修改原因
 */
@Service
@AllArgsConstructor
@Slf4j
public class WeighingDataServiceImpl implements IWeighingDataService {

    @Autowired
    private MongoTemplate mongoTemplate;




    /**
     * 生成某一个区域最近一个月的垃圾收集聚合数据
     * @param regionName
     * @return
     */
//    public Boolean weighingSiteRegionLastMonthPolymerizationData(Long regionName){
//
//        Query query=new Query();
//        query.addCriteria(Criteria.where(""));
//        List<WeighingSiteRecordDTO> weighingSiteRecordDTOS = mongoTemplate.find(query, WeighingSiteRecordDTO.class);
//
//        return null;
//    }
    /**
     * 生成所有区域最7天的垃圾收集聚合数据
     * @return
     * @param companyId
     */
    @Override
    public Boolean weighingSiteAllRegionLastMonthPolymerizationData(String companyId){
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR,23);
        instance.set(Calendar.MINUTE,59);
        instance.set(Calendar.SECOND,59);

        instance.add(Calendar.DAY_OF_MONTH,-8);

        mongoTemplate.dropCollection(FacilityConstant.REGION_LAST_WEEK_POLYMERIZATION_DATA);

        Query query=new Query();
        Criteria criteria=new Criteria();
        criteria.andOperator(Criteria.where("weighingTime").gt(instance.getTime()),
                new Criteria().orOperator(
                        Criteria.where("transportUnit").regex("^.*" + "龙马" + ".*$"),
                        Criteria.where("transportUnit").regex("^.*" + "侨银" + ".*$"),
                        Criteria.where("transportUnit").regex("^.*" + "中环洁" + ".*$")
                ));
//        query.addCriteria(Criteria.where("weighingTime").gt(instance.getTime()).orOperator());
        query.addCriteria(criteria);
        query.with(Sort.by("weighingTime").descending());
        List<WeighingSiteRecordDTO> companyName1WeighingSiteRecordDTOS = mongoTemplate.find(query, WeighingSiteRecordDTO.class);
        Map<String, List<WeighingSiteRecordDTO>> company1 = companyName1WeighingSiteRecordDTOS.stream().collect(Collectors.groupingBy(weighingSiteRecordDTO -> DateUtil.format(weighingSiteRecordDTO.getWeighingTime(), "MM-dd")));
        Set<String> comyany1DateStrs = company1.keySet();

        List<String> collect = comyany1DateStrs.stream().sorted().collect(Collectors.toList());

        for (String  company1Str:collect ) {
            List<WeighingSiteRecordDTO> weighingSiteRecordDTOS = company1.get(company1Str);

            double sum = weighingSiteRecordDTOS.stream().mapToDouble(WeighingSiteRecordDTO::getNetWeight).sum();
            BasicWeighingSitePolymerizationDTO basicWeighingSitePolymerizationDTO=new BasicWeighingSitePolymerizationDTO();
            basicWeighingSitePolymerizationDTO.setDate(company1Str);
            basicWeighingSitePolymerizationDTO.setObj("全部");
            basicWeighingSitePolymerizationDTO.setValue(sum/1000);
            basicWeighingSitePolymerizationDTO.setCreateTime(DateUtil.now());
            basicWeighingSitePolymerizationDTO.setCompanyId(companyId);
            mongoTemplate.insert(basicWeighingSitePolymerizationDTO, FacilityConstant.REGION_LAST_WEEK_POLYMERIZATION_DATA);
        }

        return true;
    }

    /**
     * 生成某一个项目（租户）最近一个月的垃圾收集聚合数据
     * @param projectName
     * @return
     */
//    public Boolean weighingSiteProjectLastMonthPolymerizationData(Long projectName){
//        return null;
//    }
    /**
     * 生成所有项目（租户）最近7天的垃圾收集聚合数据
     * @return
     */
//    public Boolean weighingSiteAllProjectLastMonthPolymerizationData(){
//
//        return null;
//    }

    /**
     * 生成某一个公司最7天的垃圾收集聚合数据
     * @param companyName
     * @return
     */
    @Override
    public Boolean weighingSiteCompanyLastMonthPolymerizationData(String companyName){

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR,23);
        instance.set(Calendar.MINUTE,59);
        instance.set(Calendar.SECOND,59);

        instance.add(Calendar.DAY_OF_MONTH,-8);


        mongoTemplate.dropCollection(FacilityConstant.COMPANY_LAST_WEEK_POLYMERIZATION_DATA);

        Query query=new Query();
        query.addCriteria(Criteria.where("transportUnit").regex("^.*" + companyName + ".*$").and("weighingTime").gt(instance.getTime()));
        List<WeighingSiteRecordDTO> companyName1WeighingSiteRecordDTOS = mongoTemplate.find(query, WeighingSiteRecordDTO.class);
        Map<String, List<WeighingSiteRecordDTO>> company1 = companyName1WeighingSiteRecordDTOS.stream().collect(Collectors.groupingBy(weighingSiteRecordDTO -> DateUtil.format(weighingSiteRecordDTO.getWeighingTime(),"MM-dd")));
        Set<String> comyany1DateStrs = company1.keySet();
        for (String  company1Str:comyany1DateStrs ) {
            List<WeighingSiteRecordDTO> weighingSiteRecordDTOS = company1.get(company1Str);
            double sum = weighingSiteRecordDTOS.stream().mapToDouble(WeighingSiteRecordDTO::getNetWeight).sum();
            BasicWeighingSitePolymerizationDTO basicWeighingSitePolymerizationDTO=new BasicWeighingSitePolymerizationDTO();
            basicWeighingSitePolymerizationDTO.setDate(company1Str);
            basicWeighingSitePolymerizationDTO.setObj(companyName);
            basicWeighingSitePolymerizationDTO.setValue(sum/1000);
            basicWeighingSitePolymerizationDTO.setCreateTime(new Date());
            mongoTemplate.insert(basicWeighingSitePolymerizationDTO, FacilityConstant.COMPANY_LAST_WEEK_POLYMERIZATION_DATA);
        }

        return true;
    }
    /**
     * 生成所有公司最7天的垃圾收集聚合数据
     * @return
     * @param companyId
     */
    @Override
    public Boolean weighingSiteAllCompanyLastWeekPolymerizationData(String companyId){
        String companyName1="龙马";
        String companyName2="侨银";
        String companyName3="中环洁";

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR,23);
        instance.set(Calendar.MINUTE,59);
        instance.set(Calendar.SECOND,59);

        instance.add(Calendar.DAY_OF_MONTH,-8);


        mongoTemplate.dropCollection(FacilityConstant.COMPANY_LAST_WEEK_POLYMERIZATION_DATA);

        Query query=new Query();
        query.addCriteria(Criteria.where("transportUnit").regex("^.*" + companyName1 + ".*$").and("weighingTime").gt(instance.getTime()));
        List<WeighingSiteRecordDTO> companyName1WeighingSiteRecordDTOS = mongoTemplate.find(query, WeighingSiteRecordDTO.class);
        Map<String, List<WeighingSiteRecordDTO>> company1 = companyName1WeighingSiteRecordDTOS.stream().collect(Collectors.groupingBy(weighingSiteRecordDTO -> DateUtil.format(weighingSiteRecordDTO.getWeighingTime(),"MM-dd")));
        Set<String> comyany1DateStrs = company1.keySet();
        for (String  company1Str:comyany1DateStrs ) {
            List<WeighingSiteRecordDTO> weighingSiteRecordDTOS = company1.get(company1Str);
            double sum = weighingSiteRecordDTOS.stream().mapToDouble(WeighingSiteRecordDTO::getNetWeight).sum();
            BasicWeighingSitePolymerizationDTO basicWeighingSitePolymerizationDTO=new BasicWeighingSitePolymerizationDTO();
            basicWeighingSitePolymerizationDTO.setDate(company1Str);
            basicWeighingSitePolymerizationDTO.setObj(companyName1);
            basicWeighingSitePolymerizationDTO.setValue(sum/1000);
            basicWeighingSitePolymerizationDTO.setCompanyId(companyId);
            basicWeighingSitePolymerizationDTO.setCreateTime(DateUtil.now());
            mongoTemplate.insert(basicWeighingSitePolymerizationDTO, FacilityConstant.COMPANY_LAST_WEEK_POLYMERIZATION_DATA);
        }

        Query query2=new Query();
        query2.addCriteria(Criteria.where("transportUnit").regex("^.*" + companyName2 + ".*$").and("weighingTime").gt(instance.getTime()));
        List<WeighingSiteRecordDTO> companyName2WeighingSiteRecordDTOS = mongoTemplate.find(query2, WeighingSiteRecordDTO.class);


        Map<String, List<WeighingSiteRecordDTO>> company2 = companyName2WeighingSiteRecordDTOS.stream().collect(Collectors.groupingBy(weighingSiteRecordDTO -> DateUtil.format(weighingSiteRecordDTO.getWeighingTime(), "MM-dd")));
        Set<String> comyany2DateStrs = company2.keySet();
        for (String  company1Str:comyany2DateStrs ) {
            List<WeighingSiteRecordDTO> weighingSiteRecordDTOS = company2.get(company1Str);
            double sum = weighingSiteRecordDTOS.stream().mapToDouble(WeighingSiteRecordDTO::getNetWeight).sum();
            BasicWeighingSitePolymerizationDTO basicWeighingSitePolymerizationDTO=new BasicWeighingSitePolymerizationDTO();
            basicWeighingSitePolymerizationDTO.setDate(company1Str);
            basicWeighingSitePolymerizationDTO.setObj(companyName2);
            basicWeighingSitePolymerizationDTO.setValue(sum/1000);
            basicWeighingSitePolymerizationDTO.setCompanyId(companyId);
            basicWeighingSitePolymerizationDTO.setCreateTime(DateUtil.now());
            mongoTemplate.insert(basicWeighingSitePolymerizationDTO,FacilityConstant.COMPANY_LAST_WEEK_POLYMERIZATION_DATA);
        }



        Query query3=new Query();
        query3.addCriteria(Criteria.where("transportUnit").regex("^.*" + companyName3 + ".*$").and("weighingTime").gt(instance.getTime()));
        List<WeighingSiteRecordDTO> companyName3WeighingSiteRecordDTOS = mongoTemplate.find(query3, WeighingSiteRecordDTO.class);

        Map<String, List<WeighingSiteRecordDTO>> company3 = companyName3WeighingSiteRecordDTOS.stream().collect(Collectors.groupingBy(weighingSiteRecordDTO -> DateUtil.format(weighingSiteRecordDTO.getWeighingTime(), "MM-dd")));
        Set<String> comyany3DateStrs = company2.keySet();
        for (String  company1Str:comyany3DateStrs ) {
            List<WeighingSiteRecordDTO> weighingSiteRecordDTOS = company3.get(company1Str);
            double sum = weighingSiteRecordDTOS.stream().mapToDouble(WeighingSiteRecordDTO::getNetWeight).sum();
            BasicWeighingSitePolymerizationDTO basicWeighingSitePolymerizationDTO=new BasicWeighingSitePolymerizationDTO();
            basicWeighingSitePolymerizationDTO.setDate(company1Str);
            basicWeighingSitePolymerizationDTO.setObj(companyName3);
            basicWeighingSitePolymerizationDTO.setValue(sum/1000);
            basicWeighingSitePolymerizationDTO.setCompanyId(companyId);
            basicWeighingSitePolymerizationDTO.setCreateTime(DateUtil.now());
            mongoTemplate.insert(basicWeighingSitePolymerizationDTO,FacilityConstant.COMPANY_LAST_WEEK_POLYMERIZATION_DATA);
        }
        return true;
    }




}
