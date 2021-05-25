package com.ai.apac.smartenv.job.scheduler;

import com.ai.apac.smartenv.assessment.entity.KpiTplDef;
import com.ai.apac.smartenv.assessment.entity.KpiTplDetail;
import com.ai.apac.smartenv.assessment.entity.StaffKpiIns;
import com.ai.apac.smartenv.assessment.entity.StaffKpiInsDetail;
import com.ai.apac.smartenv.assessment.feign.IAssessmentClient;
import com.ai.apac.smartenv.assessment.vo.KpiTargetLostPointsStaVO;
import com.ai.apac.smartenv.assessment.vo.KpiTargetLostPointsVO;
import com.ai.apac.smartenv.common.constant.AssessmentConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.protostuff.CollectionSchema.MessageFactories.Collection;

/**
 * @ClassName kpiTargetLostPointsCountTask
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/21 15:21
 * @Version 1.0
 */
@Component
@AllArgsConstructor
public class kpiTargetLostPointsCountTask {

    private IAssessmentClient assessmentClient;

    private MongoTemplate mongoTemplate;

    //@Scheduled(cron = "0 0 0 * * ? ")
    public void kpiTargetLostPointsCount() {
        KpiTargetLostPointsStaVO kpiTargetLostPointsStaVO = new  KpiTargetLostPointsStaVO();
        List<KpiTargetLostPointsVO> kpiTargetLostPointsVOList = new ArrayList<KpiTargetLostPointsVO>();
        kpiTargetLostPointsStaVO.setDays(30);
        kpiTargetLostPointsStaVO.setTenantId("000000");
        kpiTargetLostPointsStaVO.setStaTime(TimeUtil.getYYYYMMDDHHMMSS(TimeUtil.getSysDate()));
        kpiTargetLostPointsStaVO.setKpiTargetLostPointsVOList(kpiTargetLostPointsVOList);
        Map<String,KpiTargetLostPointsVO> KpiTargetLostPointsVOMap = new HashMap<String,KpiTargetLostPointsVO>();
        List<StaffKpiIns> staffKpiInsList = assessmentClient.listEndKpiTargetRecently(30,"000000").getData();

        if(ObjectUtil.isNotEmpty(staffKpiInsList) && staffKpiInsList.size() > 0){
            staffKpiInsList.forEach(staffKpiIns -> {
                Long kpiInsId = staffKpiIns.getId();
                List<StaffKpiInsDetail> staffKpiInsDetailList = assessmentClient.listKpiInsDatailsByKpiInsId(kpiInsId,"000000").getData();
                staffKpiInsDetailList.forEach(staffKpiInsDetail -> {
                    Double score = Double.parseDouble(staffKpiInsDetail.getScore());
                    Long kpiTplDetailId = staffKpiInsDetail.getKpiTplDetailId();
                    KpiTplDetail kpiTplDetail = assessmentClient.getKpiTplDetailById(kpiTplDetailId).getData();
                    Long kpiTplId = kpiTplDetail.getKpiTplId();
                    KpiTplDef kpiTplDef = assessmentClient.getKpiTplDefById(kpiTplId).getData();
                    Long kpiId = kpiTplDetail.getKpiId();
                    String kpiName = kpiTplDetail.getKpiName();
                    Integer weight = kpiTplDetail.getWeighting();
                    Integer totalScore = kpiTplDef.getTotalScore();
                    if(totalScore*weight/100 > score){
                        if(ObjectUtil.isNotEmpty(KpiTargetLostPointsVOMap.get(kpiId.toString()))){
                            KpiTargetLostPointsVO kpiTargetLostPointsVO = KpiTargetLostPointsVOMap.get(kpiId.toString());
                            kpiTargetLostPointsVO.setLostPoints(kpiTargetLostPointsVO.getLostPoints()+totalScore*weight/100 - score);
                        }else{
                        KpiTargetLostPointsVO kpiTargetLostPointsVO = new KpiTargetLostPointsVO();
                        kpiTargetLostPointsVO.setKpiId(kpiTplDetail.getKpiId());
                        kpiTargetLostPointsVO.setKpiName(kpiTplDetail.getKpiName());
                        kpiTargetLostPointsVO.setLostPoints(totalScore*weight/100 - score);
                            KpiTargetLostPointsVOMap.put(kpiId.toString(),kpiTargetLostPointsVO);
                        }
                    }
                });
            });
            kpiTargetLostPointsVOList = new ArrayList<KpiTargetLostPointsVO>(KpiTargetLostPointsVOMap.values());
            kpiTargetLostPointsStaVO.setKpiTargetLostPointsVOList(kpiTargetLostPointsVOList);
        }
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("tenantId").is("000000"));
        mongoTemplate.findAndRemove(query,KpiTargetLostPointsStaVO.class,"KpiTargetLostPointsSta");
        mongoTemplate.save(kpiTargetLostPointsStaVO,"KpiTargetLostPointsSta");

    }
}
