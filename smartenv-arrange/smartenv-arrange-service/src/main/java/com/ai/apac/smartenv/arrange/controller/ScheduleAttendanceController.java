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
package com.ai.apac.smartenv.arrange.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import com.ai.apac.smartenv.arrange.dto.ScheduleAttendanceDTO;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleAttendanceDetail;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.service.IScheduleAttendanceDetailService;
import com.ai.apac.smartenv.arrange.service.IScheduleObjectService;
import com.ai.apac.smartenv.arrange.service.IScheduleService;
import com.ai.apac.smartenv.arrange.vo.ScheduleAttendanceDetailVO;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.omnic.dto.AttendanceDetailDTO;
import com.ai.apac.smartenv.omnic.feign.IAttendanceClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaNodeClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.arrange.entity.ScheduleAttendance;
import com.ai.apac.smartenv.arrange.vo.ScheduleAttendanceVO;
import com.ai.apac.smartenv.arrange.wrapper.ScheduleAttendanceWrapper;
import com.ai.apac.smartenv.arrange.service.IScheduleAttendanceService;
import org.springblade.core.boot.ctrl.BladeController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 打卡记录表 控制器
 *
 * @author Blade
 * @since 2020-05-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("/scheduleattendance")
@Api(value = "打卡记录表", tags = "打卡记录表接口")
@Slf4j
public class ScheduleAttendanceController extends BladeController {

    private IScheduleAttendanceService scheduleAttendanceService;
    private IScheduleAttendanceDetailService scheduleAttendanceDetailService;
    private IAttendanceClient attendanceClient;

    private IScheduleObjectService objectService;

    private IScheduleService scheduleService;


    private IPersonClient personClient;


    private IPersonUserRelClient personUserRelClient;

    private IPersonVehicleRelClient personVehicleRelClient;
    private IWorkareaClient workareaClient;

    private IWorkareaRelClient workareaRelClient;

    private IWorkareaNodeClient workareaNodeClient;

    private ISysClient sysClient;

    private IVehicleClient vehicleClient;

    private IEntityCategoryClient entityCategoryClient;

    private IOssClient ossClient;


    /**
     * 详情
     */
    @GetMapping("/vehicle/detail/{scheduleAttendanceId}")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入scheduleAttendance")
    public R<AttendanceDetailDTO> detail(@PathVariable Long scheduleAttendanceId) {
        AttendanceDetailDTO detailDTO = null;
        try {
            ScheduleAttendance detail = scheduleAttendanceService.getById(scheduleAttendanceId);
            detailDTO = attendanceClient.getAttendanceDetail(detail).getData();
        } catch (Exception e) {
            return R.fail("查询详情失败");
        }
        return R.data(detailDTO);
    }

    /**
     * 分页 打卡记录表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入scheduleAttendance")
    public R<IPage<ScheduleAttendanceVO>> list(ScheduleAttendanceDTO scheduleAttendance, Query query) {

        scheduleAttendance.setAttendanceStatus(ArrangeConstant.AttendanceStatus.ATTED);


        ScheduleAttendance copy = BeanUtil.copy(scheduleAttendance, ScheduleAttendance.class);
        QueryWrapper<ScheduleAttendance> queryWrapper = Condition.getQueryWrapper(copy);

        if (StringUtil.isNotBlank(scheduleAttendance.getEntityName())) {
            queryWrapper = queryWrapper.like("entity_name", scheduleAttendance.getEntityName());
            copy.setEntityName(null);
        }

        String workDate = scheduleAttendance.getWorkDate();
        if (StringUtil.isNotBlank(workDate)) {
			/*ScheduleObject scheduleObj = new ScheduleObject();
			scheduleObj.setScheduleDate(LocalDate.parse(workDate));
			QueryWrapper<ScheduleObject> scheduleObjectQuery = new QueryWrapper<>(scheduleObj);
			List<ScheduleObject> list = objectService.list(scheduleObjectQuery);
			List<Long> objList = new ArrayList<>();
			list.forEach(scheduleObject -> objList.add(scheduleObject.getId()));
			if (CollectionUtil.isNotEmpty(objList)){
			    queryWrapper.in("schedule_object_id", objList);
			}else {
			    R.data(null);
			}*/
            // 排班记录可能会删除
            queryWrapper.likeRight("work_start_time", workDate);
        }

        IPage<ScheduleAttendance> pages = scheduleAttendanceService.page(Condition.getPage(query), queryWrapper);
        IPage<ScheduleAttendanceVO> scheduleAttendanceVOIPage = ScheduleAttendanceWrapper.build().pageVO(pages);
        scheduleAttendanceVOIPage.getRecords().forEach(attendance -> {

            ScheduleAttendanceDetail entity = new ScheduleAttendanceDetail();
            entity.setScheduleAttendanceId(attendance.getId());
            QueryWrapper<ScheduleAttendanceDetail> detailQuery = new QueryWrapper<>(entity);
            List<ScheduleAttendanceDetail> list = scheduleAttendanceDetailService.list(detailQuery);


//            ScheduleObject scheduleObject = objectService.getById(attendance.getScheduleObjectId());
            ScheduleObject scheduleObject = objectService.getByIdWithDel(attendance.getScheduleObjectId());// 排班记录可能会删除
            if (scheduleObject != null && scheduleObject.getScheduleDate() != null) {
                attendance.setScheduleDate(scheduleObject.getScheduleDate().toString());
            }

            Date beginTime = null;
            Date endTime = null;
            for (ScheduleAttendanceDetail attendanceDetail : list) {
                if (ArrangeConstant.GO_OFF_WORK_FLAG.ATTED.equals(attendanceDetail.getGoOffWorkFlag())) {
                    //如果已经打卡，则取打卡时间，如果没有打卡，则取上班时间
                    if (attendanceDetail.getAttendanceStatus() != null && ArrangeConstant.AttendanceStatus.ATTED.equals(attendanceDetail.getAttendanceStatus())) {
                        beginTime = attendanceDetail.getUploadTime();
                    } else {
                        beginTime = attendanceDetail.getScheduleTime();
                    }
                } else if (ArrangeConstant.GO_OFF_WORK_FLAG.NOT_ATTED.equals(attendanceDetail.getGoOffWorkFlag())) {
                    //如果已经打卡，则取打卡时间，如果没有打卡，则取上班时间
                    if (attendanceDetail.getAttendanceStatus() != null && ArrangeConstant.AttendanceStatus.ATTED.equals(attendanceDetail.getAttendanceStatus())) {
                        endTime = attendanceDetail.getUploadTime();
                    } else {
                        endTime = attendanceDetail.getScheduleTime();
                    }
                }
            }
            String begin = DateUtil.format(beginTime, "HH:mm");
            String end = DateUtil.format(endTime, "HH:mm");


            attendance.setScheduleTime(begin + "-" + end);
        });


        return R.data(scheduleAttendanceVOIPage);
    }


    /**
     * 自定义分页 打卡记录表
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "自定义分页", notes = "传入scheduleAttendance")
    public R<IPage<ScheduleAttendanceVO>> page(ScheduleAttendanceVO scheduleAttendance, Query query) {
        IPage<ScheduleAttendanceVO> pages = scheduleAttendanceService.selectScheduleAttendancePage(Condition.getPage(query), scheduleAttendance);
        return R.data(pages);
    }

    /**
     * 更新11101504 除了驾驶员也可以打卡，取人员的排班
     *
     * @param date
     * @return
     */
    @GetMapping("/attendanceList")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "根据日期获取打卡列表，如果没有则生成", notes = "传入scheduleAttendance")
    public R<List<ScheduleAttendanceVO>> attendanceList(String date) {
        // 通过User 取人员，通过人员取车辆，通过车辆取排班
        BladeUser user = getUser();
        PersonUserRel personUserRel = personUserRelClient.getRelByUserId(user.getUserId()).getData();
        Long vehicleId = null;
        String scheduleEntityType = "";
        List<ScheduleObject> scheduleObjectList = new ArrayList<ScheduleObject>();
        if (personUserRel != null && personUserRel.getId() != null) {
            Long personId = personUserRel.getPersonId();
//            List<PersonVehicleRel> personVehicleRels = personVehicleRelClient.getVehicleByPersonId(personId).getData();
//            if (CollectionUtil.isNotEmpty(personVehicleRels)) {
//                PersonVehicleRel personVehicleRel = personVehicleRels.get(0);
//                vehicleId = personVehicleRel.getVehicleId();
//                ScheduleObject query = new ScheduleObject();
//                query.setScheduleDate(LocalDate.parse(date));
//                query.setEntityId(vehicleId);
//                query.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
//                scheduleObjectList = objectService.list(Condition.getQueryWrapper(query));
//                scheduleEntityType = ArrangeConstant.ScheduleObjectEntityType.VEHICLE;
//            }else{
            //更新11101504 除了驾驶员也可以打卡，取人员的排班
            // 只有人员可以打卡，驾驶员打卡后自动绑定车辆
            ScheduleObject query = new ScheduleObject();
            query.setScheduleDate(LocalDate.parse(date));
            query.setEntityId(personId);
            query.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
            scheduleObjectList = objectService.list(Condition.getQueryWrapper(query));
            scheduleEntityType = ArrangeConstant.ScheduleObjectEntityType.PERSON;
//            }
        }
        if (ObjectUtil.isEmpty(scheduleObjectList) || scheduleObjectList.size() == 0) {

            return R.fail("当前登录人员今天没有排班/车辆排班");
        }

        List<Long> scheduleObjectIdList = scheduleObjectList.stream().map(ScheduleObject::getId).collect(Collectors.toList());

        //如果当天有排班，查看是否有打卡列表。如果没有，则生成
        QueryWrapper<ScheduleAttendance> wrapper = new QueryWrapper<ScheduleAttendance>();

        wrapper.lambda().in(ScheduleAttendance::getScheduleObjectId, scheduleObjectIdList);
        wrapper.orderByAsc("work_start_time");
        List<ScheduleAttendance> list = scheduleAttendanceService.list(wrapper);


        if (CollectionUtil.isEmpty(list)) {
            for (ScheduleObject scheduleObject : scheduleObjectList) {
                generalVehicleScheduleAttanceList(scheduleObject, scheduleEntityType);
            }
        }

        list = scheduleAttendanceService.list(wrapper);
        List<ScheduleAttendanceVO> scheduleAttendanceVOS = ScheduleAttendanceWrapper.build().listVO(list);

        scheduleAttendanceVOS.forEach(scheduleAttendance -> {
            ScheduleAttendanceDetail detailWrapper = new ScheduleAttendanceDetail();
            detailWrapper.setScheduleAttendanceId(scheduleAttendance.getId());
            List<ScheduleAttendanceDetail> attendanceDetails = scheduleAttendanceDetailService.list(Condition.getQueryWrapper(detailWrapper));
            scheduleAttendance.setDetails(attendanceDetails);
        });
        return R.data(scheduleAttendanceVOS);
    }

    public int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }


    @PostMapping("/vehicleAttendance")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "打卡,需要传入拍照信息", notes = "传入scheduleAttendance")
    public R<Boolean> vehicleAttendance(ScheduleAttendanceDetailVO attendanceDetail, @RequestParam MultipartFile uploadFile) {
        attendanceDetail.setAttendanceStatus(ArrangeConstant.AttendanceStatus.ATTED);
        if (attendanceDetail.getUploadTimeStamp() != null) {
            attendanceDetail.setUploadTime(new Date(attendanceDetail.getUploadTimeStamp()));
        }
        try {
//            Font font = new Font("宋体", Font.PLAIN, 16);  //水印字体
            System.setProperty("java.awt.headless","true");
            InputStream resourceAsStream = ScheduleAttendance.class.getClassLoader().getResourceAsStream("fonts/simhei.ttf");
            Font font = ImgUtil.createFont(resourceAsStream);
            font=font.deriveFont(Font.BOLD,18);

            Date date = new Date(attendanceDetail.getUploadTimeStamp());
            String format = DateUtil.format(date, DateUtil.PATTERN_DATETIME);
            Coords coords = new Coords();
            coords.setLatitude(attendanceDetail.getLat());
            coords.setLongitude(attendanceDetail.getLng());
            BaiduMapReverseGeoCodingResult reverseGeoCoding = BaiduMapUtils.getReverseGeoCoding(coords);


            String formatted_address="未知位置";
            if (reverseGeoCoding!=null&&reverseGeoCoding.getResult()!=null){
                formatted_address = reverseGeoCoding.getResult().getFormatted_address();//水印内容
            }


            Color color = new Color(153,204,204, 118);   // 水印颜色

            BufferedImage buImage = ImageIO.read(uploadFile.getInputStream());
            int width = buImage.getWidth(); //图片宽
            int height = buImage.getHeight(); //图片高

            BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(buImage, 0, 0, width, height, null);
            g.setColor(color); //水印颜色
            g.setFont(font); //水印字体

            int x = width -g.getFontMetrics(g.getFont()).charsWidth(formatted_address.toCharArray(), 0, formatted_address.length())-10;
            int y = height - g.getFontMetrics(g.getFont()).getHeight()-10;
            g.drawString(formatted_address, x, y); //水印位置

            int x1 = width -g.getFontMetrics(g.getFont()).charsWidth(format.toCharArray(), 0, format.length())-10;
            g.drawString(format, x1, y- g.getFontMetrics(g.getFont()).getHeight()-5); //水印位置
            g.dispose(); //释放资源s

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            System.out.println("添加水印完成");

//            ImgUtil.pressText(uploadFile.getInputStream(), byteArrayOutputStream, waterMarkContent, new Color(255, 255, 255, 0), font, 10, 10, 0f);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            String encode = Base64.encode(bytes);
            String data = ossClient.putBase64Stream(CommonConstant.BUCKET, "attendance-" + System.currentTimeMillis() + ".png", encode).getData();
            attendanceDetail.setImagePath(data);

        } catch (Exception e) {
            log.error("给图片添加水印失败",e);
        }


        ScheduleAttendanceDetail detailServiceById = scheduleAttendanceDetailService.getById(attendanceDetail.getId());
        Long scheduleAttendanceId = detailServiceById.getScheduleAttendanceId();
        ScheduleAttendance attendance = scheduleAttendanceService.getById(scheduleAttendanceId);
        //what?
        ScheduleAttendance update = new ScheduleAttendance();
        update.setId(attendance.getId());
        update.setAttendanceStatus(ArrangeConstant.AttendanceStatus.ATTED);
        scheduleAttendanceService.updateById(update);


        return R.data(scheduleAttendanceDetailService.updateById(attendanceDetail));
    }


    public List<ScheduleAttendanceVO> generalVehicleScheduleAttanceList(ScheduleObject scheduleObject, String scheduleEntityType) {
        Schedule schedule = scheduleService.getById(scheduleObject.getScheduleId());
        Date scheduleBeginTime = schedule.getScheduleBeginTime();
        Date breaksBeginTime = schedule.getBreaksBeginTime();
        Date breaksEndTime = schedule.getBreaksEndTime();
        Date scheduleEndTime = schedule.getScheduleEndTime();
        ScheduleAttendanceDetail begin = new ScheduleAttendanceDetail();
        ScheduleAttendanceDetail end = new ScheduleAttendanceDetail();
        begin.setAttendanceStatus(ArrangeConstant.AttendanceStatus.NOT_ATTED);
        end.setAttendanceStatus(ArrangeConstant.AttendanceStatus.NOT_ATTED);
        begin.setScheduleTime(convertTodayTime(scheduleBeginTime));
        end.setScheduleTime(convertTodayTime(scheduleEndTime));

        Long entityId = scheduleObject.getEntityId();

        Long entityType = CommonConstant.ENTITY_TYPE.VEHICLE;
        if (ArrangeConstant.ScheduleObjectEntityType.PERSON.equals(scheduleEntityType)) {
            entityType = CommonConstant.ENTITY_TYPE.PERSON;
        }

        List<WorkareaRel> workareaRels = workareaRelClient.getByEntityIdAndType(entityId, entityType).getData();
        WorkareaNode beginNode = null;
        WorkareaNode endNode = null;
        WorkareaInfo workareaInfo = null;
        if (CollectionUtil.isNotEmpty(workareaRels)) {
            WorkareaRel workareaRel = workareaRels.get(0);
            List<WorkareaNode> workareaNodes = workareaNodeClient.queryNodeByWorkareaId(workareaRel.getWorkareaId()).getData();
            if (CollectionUtil.isNotEmpty(workareaNodes)) {
                beginNode = workareaNodes.get(0);
                endNode = workareaNodes.get(workareaNodes.size() - 1);
            }

            workareaInfo = workareaClient.getWorkInfoById(workareaRel.getWorkareaId()).getData();
        }

//        if (beginNode==null){
//            throw  new RuntimeException("路线规划为空");
//        }

        Region region = null;
        if (workareaInfo != null) {
            region = sysClient.getRegion(workareaInfo.getId()).getData();
        }

        Long deptId = null;
        Dept dept = null;
        String entityName = "";
        Long entityCategoryId = null;
        EntityCategory entityCategory = null;

        if (ArrangeConstant.ScheduleObjectEntityType.VEHICLE.equals(scheduleEntityType)) {
            VehicleInfo vehicleInfo = vehicleClient.getVehicleInfoById(scheduleObject.getEntityId()).getData();
            if (vehicleInfo != null) {
                entityName = vehicleInfo.getPlateNumber();
                deptId = vehicleInfo.getDeptId();
                dept = sysClient.getDept(deptId).getData();
                entityCategoryId = vehicleInfo.getEntityCategoryId();
                if (ObjectUtil.isNotEmpty(entityCategoryId)) {
                    entityCategory = entityCategoryClient.getCategory(entityCategoryId).getData();
                }
            }
        }

        if (ArrangeConstant.ScheduleObjectEntityType.PERSON.equals(scheduleEntityType)) {
            Person person = personClient.getPerson(scheduleObject.getEntityId()).getData();
            if (ObjectUtil.isNotEmpty(person)) {
                entityName = person.getPersonName();
                deptId = person.getPersonDeptId();
                dept = sysClient.getDept(deptId).getData();
                entityCategoryId = person.getEntityCategoryId();
                if (ObjectUtil.isNotEmpty(entityCategoryId)) {
                    entityCategory = entityCategoryClient.getCategory(entityCategoryId).getData();
                }
            }
        }


        //如果上面没有执行，这里会报空指针，（工作区域没绑定）
        begin.setScheduleAreaLat(beginNode == null ? null : beginNode.getLatitudinal());
        end.setScheduleAreaLat(endNode == null ? null : endNode.getLatitudinal());
        begin.setScheduleAreaLng(beginNode == null ? null : beginNode.getLongitude());
        end.setScheduleAreaLng(endNode == null ? null : endNode.getLongitude());
        begin.setGoOffWorkFlag(ArrangeConstant.GO_OFF_WORK_FLAG.ATTED);
        end.setGoOffWorkFlag(ArrangeConstant.GO_OFF_WORK_FLAG.NOT_ATTED);
        List<ScheduleAttendanceVO> attendanceVOS = new ArrayList<>();
        ScheduleAttendanceVO firstAttendance = new ScheduleAttendanceVO();
        List<ScheduleAttendanceDetail> firstAttendanceDetails = new ArrayList<>();


        firstAttendance.setScheduleDate(scheduleObject.getScheduleDate().toString());
        // 所有属性

        firstAttendance.setWorkareaId(workareaInfo == null ? null : workareaInfo.getId());
        firstAttendance.setWorkareaName(workareaInfo == null ? null : workareaInfo.getAreaName());
        firstAttendance.setRegionId(workareaInfo == null ? null : workareaInfo.getRegionId());
        firstAttendance.setRegionName(region == null ? null : region.getRegionName());
        firstAttendance.setEntityId(entityId);
        firstAttendance.setEntityName(entityName);
        firstAttendance.setEntityCategoryId(entityCategoryId);
        firstAttendance.setEntityType(entityType);
        firstAttendance.setDeptId(deptId);
        firstAttendance.setDeptName(dept == null ? null : dept.getDeptName());
        firstAttendance.setEntityCategoryName(entityCategory == null ? null : entityCategory.getCategoryName());
        firstAttendance.setAttendanceStatus(ArrangeConstant.AttendanceStatus.NOT_ATTED);
        firstAttendance.setDetails(firstAttendanceDetails);
        attendanceVOS.add(firstAttendance);
        if (breaksBeginTime != null && breaksEndTime != null) {
            ScheduleAttendanceDetail breakBegin = new ScheduleAttendanceDetail();
            ScheduleAttendanceDetail breakEnd = new ScheduleAttendanceDetail();
            breakBegin.setAttendanceStatus(ArrangeConstant.AttendanceStatus.NOT_ATTED);
            breakEnd.setAttendanceStatus(ArrangeConstant.AttendanceStatus.NOT_ATTED);
            breakBegin.setScheduleTime(convertTodayTime(breaksBeginTime));
            breakEnd.setScheduleTime(convertTodayTime(breaksEndTime));

            breakBegin.setScheduleAreaLat(beginNode == null ? null : beginNode.getLatitudinal());
            breakEnd.setScheduleAreaLat(endNode == null ? null : endNode.getLatitudinal());

            breakBegin.setScheduleAreaLng(beginNode == null ? null : beginNode.getLongitude());
            breakEnd.setScheduleAreaLng(endNode == null ? null : endNode.getLongitude());

            breakBegin.setGoOffWorkFlag(ArrangeConstant.GO_OFF_WORK_FLAG.NOT_ATTED);
            breakEnd.setGoOffWorkFlag(ArrangeConstant.GO_OFF_WORK_FLAG.ATTED);

            firstAttendanceDetails.add(begin);
            firstAttendanceDetails.add(breakBegin);

            firstAttendance.setWorkStartTime(begin.getScheduleTime());
            firstAttendance.setWorkEndTime(breakBegin.getScheduleTime());


            ScheduleAttendanceVO secondAttendance = new ScheduleAttendanceVO();
            List<ScheduleAttendanceDetail> secondAttendanceDetails = new ArrayList<>();
            BeanUtils.copyProperties(firstAttendance, secondAttendance);

            secondAttendanceDetails.add(breakEnd);
            secondAttendanceDetails.add(end);
            secondAttendance.setDetails(secondAttendanceDetails);
            // 所有属性

            secondAttendance.setWorkStartTime(breakEnd.getScheduleTime());
            secondAttendance.setWorkEndTime(end.getScheduleTime());

            attendanceVOS.add(secondAttendance);
        } else {
            firstAttendanceDetails.add(begin);
            firstAttendanceDetails.add(end);
            firstAttendance.setWorkStartTime(begin.getScheduleTime());
            firstAttendance.setWorkEndTime(end.getScheduleTime());
        }
        attendanceVOS.forEach(attendanceVO -> {
            attendanceVO.setScheduleObjectId(scheduleObject.getId());
            boolean save = scheduleAttendanceService.save(attendanceVO);
            if (CollectionUtil.isNotEmpty(attendanceVO.getDetails())) {
                attendanceVO.getDetails().forEach(detail -> {
                    detail.setScheduleAttendanceId(attendanceVO.getId());
                    scheduleAttendanceDetailService.save(detail);
                });
            }
        });

        return attendanceVOS;

    }


    /**
     * 将指定时间转成今天对应的时间
     *
     * @return
     */
    private Date convertTodayTime(Date date) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        Calendar today = Calendar.getInstance();
        dateCal.set(Calendar.YEAR, today.get(Calendar.YEAR));
        dateCal.set(Calendar.MONTH, today.get(Calendar.MONTH));
        dateCal.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
        return dateCal.getTime();
    }

}
