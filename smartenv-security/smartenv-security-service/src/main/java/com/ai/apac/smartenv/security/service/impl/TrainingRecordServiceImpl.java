/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.security.service.impl;

import com.ai.apac.smartenv.security.constant.SecurityConstant;
import com.ai.apac.smartenv.security.dto.TrainingRecordDTO;
import com.ai.apac.smartenv.security.entity.TrainingAttach;
import com.ai.apac.smartenv.security.entity.TrainingObject;
import com.ai.apac.smartenv.security.entity.TrainingRecord;
import com.ai.apac.smartenv.security.service.ITrainingAttachService;
import com.ai.apac.smartenv.security.service.ITrainingObjectService;
import com.ai.apac.smartenv.security.vo.TrainingAttachVO;
import com.ai.apac.smartenv.security.vo.TrainingObjectVO;
import com.ai.apac.smartenv.security.vo.TrainingRecordVO;
import com.ai.apac.smartenv.security.mapper.TrainingRecordMapper;
import com.ai.apac.smartenv.security.service.ITrainingRecordService;
import com.ai.apac.smartenv.security.wrapper.TrainingAttachWrapper;
import com.ai.apac.smartenv.security.wrapper.TrainingObjectWrapper;
import com.ai.apac.smartenv.security.wrapper.TrainingRecordWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 培训记录表 服务实现类
 *
 * @author Blade
 * @since 2020-08-20
 */
@Service
@AllArgsConstructor
public class TrainingRecordServiceImpl extends BaseServiceImpl<TrainingRecordMapper, TrainingRecord> implements ITrainingRecordService {

    private ITrainingAttachService attachService;

    private ITrainingObjectService objectService;

    @Override
    public TrainingRecordVO getRecord(Long id) throws Exception {
        if (id == null) {
            return null;
        }
        TrainingRecord record = this.getById(id);
        if (record == null) {
            return null;
        }
        TrainingRecordVO recordVO = TrainingRecordWrapper.build().entityVO(record);
        // 附件分类
        attachClassify(recordVO);

        return recordVO;
    }

    @Override
    public IPage<TrainingRecordVO> selectTrainingRecordPage(Query query, TrainingRecordDTO trainingRecord) throws Exception {
        QueryWrapper<TrainingRecord> queryWrapper = new QueryWrapper<>();
        // 主题
        if (StringUtils.isNotBlank(trainingRecord.getTrainingTopic())) {
            queryWrapper.like(
                    "training_topic", trainingRecord.getTrainingTopic());
        }
        // 组织人
        if (StringUtils.isNotBlank(trainingRecord.getOrganizer())) {
            queryWrapper.like(
                    "organizer", trainingRecord.getOrganizer());
        }
        // 主讲人
        if (StringUtils.isNotBlank(trainingRecord.getSpeaker())) {
            queryWrapper.like(
                    "speaker", trainingRecord.getSpeaker());
        }
        // 培训查询的开始时间
        if (trainingRecord.getTrainingQueryStartTime() != null) {
            queryWrapper.ge(
                    "training_start_time", new Timestamp(trainingRecord.getTrainingQueryStartTime()));
        }
        // 培训查询的结束时间
        if (trainingRecord.getTrainingQueryEndTime() != null) {
            queryWrapper.le(
                    "training_start_time", new Timestamp(trainingRecord.getTrainingQueryEndTime()));
        }
        // 记录查询的开始时间
        if (trainingRecord.getRecordStartTime() != null) {
            queryWrapper.ge(
                    "create_time", new Timestamp(trainingRecord.getRecordStartTime()));
        }
        // 记录查询的结束时间
        if (trainingRecord.getRecordEndTime() != null) {
            queryWrapper.le(
                    "create_time", new Timestamp(trainingRecord.getRecordEndTime()));
        }
        // 培训类型
        if (trainingRecord.getTrainingTypeId() != null) {
            queryWrapper.eq(
                    "training_type_id", trainingRecord.getTrainingTypeId());
        }

        IPage<TrainingRecord> resultPage = this.page(Condition.getPage(query), queryWrapper);
        IPage<TrainingRecordVO> recordVOIPage = TrainingRecordWrapper.build().pageVO(resultPage);
        if (CollectionUtils.isNotEmpty(recordVOIPage.getRecords())) {
            for (TrainingRecordVO recordVO : recordVOIPage.getRecords()) {
                attachClassify(recordVO);
            }
        }
        return recordVOIPage;
    }

    private void attachClassify(TrainingRecordVO recordVO) throws Exception {
        // 查附件
        List<TrainingAttachVO> attachVOS = attachService.listTrainingAttachByTrainingRecordId(recordVO.getId());
        if (CollectionUtils.isNotEmpty(attachVOS)) {
            // 图片
            List<TrainingAttachVO> pics = attachVOS.stream().filter(attachVO -> attachVO.getAttachType().equals(SecurityConstant.AttachType.PICTURE)).collect(Collectors.toList());
            recordVO.setPicList(pics);
            // 文档
            List<TrainingAttachVO> docs = attachVOS.stream().filter(attachVO -> attachVO.getAttachType().equals(SecurityConstant.AttachType.DOC)).collect(Collectors.toList());
            recordVO.setDocList(docs);
        }
        //查培训对象
        List<TrainingObjectVO> objectVOS = objectService.listTrainingObjectByTrainingRecordId(recordVO.getId());
        if (CollectionUtils.isNotEmpty(objectVOS)) {
            recordVO.setObjectList(objectVOS);
        }
    }

    @Override
    public boolean saveTrainingRecord(TrainingRecordVO trainingRecordVO) throws Exception {
        // 校验参数
        validTrainingRecord(trainingRecordVO, "new");
        // TODO  处理富文本
        String trainingContent = trainingRecordVO.getTrainingContent();
        TrainingRecord trainingRecord = TrainingRecordWrapper.build().voEntity(trainingRecordVO);
        // 保存
        this.save(trainingRecord);
        long recordId = trainingRecord.getId();

        // 附件
        List<TrainingAttach> attachList = new ArrayList<>();
        // 图片
        List<TrainingAttachVO> picList = trainingRecordVO.getPicList();
        if (CollectionUtils.isNotEmpty(picList)) {
            picList.forEach(trainingAttachDTO -> {
                trainingAttachDTO.setTrainingRecordId(recordId);
                trainingAttachDTO.setAttachType(SecurityConstant.AttachType.PICTURE);
            });
            attachList.addAll(TrainingAttachWrapper.build().listVOEntity(picList));
        }
        // 文件
        List<TrainingAttachVO> docList = trainingRecordVO.getDocList();
        if (CollectionUtils.isNotEmpty(docList)) {
            docList.forEach(trainingAttachDTO -> {
                trainingAttachDTO.setTrainingRecordId(recordId);
                trainingAttachDTO.setAttachType(SecurityConstant.AttachType.DOC);
            });
            attachList.addAll(TrainingAttachWrapper.build().listVOEntity(docList));
        }
        // 批量保存附件
        if (CollectionUtils.isNotEmpty(attachList)) {
            attachService.saveBatch(attachList);
        }

        // 培训对象，类型现在只有人
        List<TrainingObjectVO> objectList = trainingRecordVO.getObjectList();
        if (CollectionUtils.isNotEmpty(objectList)) {
            objectList.forEach(trainingObjectVO -> {
                trainingObjectVO.setTrainingRecordId(recordId);
                trainingObjectVO.setObjectType(SecurityConstant.TrainingObjectType.PERSON);
            });
            // 批量保存培训对象
            objectService.saveBatch(TrainingObjectWrapper.build().listVOEntity(objectList));
        }
        return true;
    }

    private void validTrainingRecord(TrainingRecordVO trainingRecord, String flag) throws Exception {
        if (StringUtils.isNotBlank(flag) && flag.equals("update")) {
            if (trainingRecord.getId() == null) throw new Exception("更新操作记录Id不能为空");
        }
        String trainingTopic = trainingRecord.getTrainingTopic();
        if (StringUtils.isBlank(trainingTopic)) throw new ServiceException("培训主题不能为空");
        String organizer = trainingRecord.getOrganizer();
        if (StringUtils.isBlank(organizer)) throw new ServiceException("组织人能为空");
        String speaker = trainingRecord.getSpeaker();
        if (StringUtils.isBlank(speaker)) throw new ServiceException("主讲人不能为空");
        Long trainingTypeId = trainingRecord.getTrainingTypeId();
        if (trainingTypeId == null) throw new ServiceException("培训类型必选");
    }

    @Override
    public boolean updateTrainingRecord(TrainingRecordVO trainingRecordVO) throws Exception {
        // 校验参数
        validTrainingRecord(trainingRecordVO, "update");
        // TODO  处理富文本
        String trainingContent = trainingRecordVO.getTrainingContent();
        TrainingRecord trainingRecord = TrainingRecordWrapper.build().voEntity(trainingRecordVO);
        // 保存
        this.updateById(trainingRecord);
        long recordId = trainingRecord.getId();

        // 已有附件全部删除
        List<TrainingAttachVO> oldAttaches = attachService.listTrainingAttachByTrainingRecordId(recordId);
        if (CollectionUtils.isNotEmpty(oldAttaches)) {
            List<Long> ids = oldAttaches.stream().map(TrainingAttach::getId).collect(Collectors.toList());
            attachService.removeByIds(ids);
        }
        // 附件
        List<TrainingAttach> attachList = new ArrayList<>();
        // 图片
        List<TrainingAttachVO> picList = trainingRecordVO.getPicList();
        if (CollectionUtils.isNotEmpty(picList)) {
            picList.forEach(trainingAttachDTO -> {
                trainingAttachDTO.setTrainingRecordId(recordId);
                trainingAttachDTO.setAttachType(SecurityConstant.AttachType.PICTURE);
            });
            attachList.addAll(TrainingAttachWrapper.build().listVOEntity(picList));
        }
        // 文件
        List<TrainingAttachVO> docList = trainingRecordVO.getDocList();
        if (CollectionUtils.isNotEmpty(docList)) {
            docList.forEach(trainingAttachDTO -> {
                trainingAttachDTO.setTrainingRecordId(recordId);
                trainingAttachDTO.setAttachType(SecurityConstant.AttachType.DOC);
            });
            attachList.addAll(TrainingAttachWrapper.build().listVOEntity(docList));
        }
        // 批量保存附件
        if (CollectionUtils.isNotEmpty(attachList)) {
            attachService.saveBatch(attachList);
        }

        // 已关联人员全部删除
        List<TrainingObjectVO> oldObjects = objectService.listTrainingObjectByTrainingRecordId(recordId);
        if (CollectionUtils.isNotEmpty(oldObjects)) {
            List<Long> ids = oldObjects.stream().map(TrainingObject::getId).collect(Collectors.toList());
            objectService.removeByIds(ids);
        }
        // 培训对象，类型现在只有人
        List<TrainingObjectVO> objectList = trainingRecordVO.getObjectList();
        if (CollectionUtils.isNotEmpty(objectList)) {
            objectList.forEach(trainingObjectVO -> {
                trainingObjectVO.setTrainingRecordId(recordId);
                trainingObjectVO.setObjectType(SecurityConstant.TrainingObjectType.PERSON);
            });
            // 批量保存培训对象
            objectService.saveBatch(TrainingObjectWrapper.build().listVOEntity(objectList));
        }
        return true;
    }

    @Override
    public boolean removeTrainingRecord(Long recordId) {
        attachService.removeByTrainingRecordId(recordId);
        objectService.removeByTrainingRecordId(recordId);
        return this.removeById(recordId);
    }
}
