package com.ai.apac.smartenv.address.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import com.ai.apac.smartenv.address.dto.EventImageDTO;
import com.ai.apac.smartenv.address.entity.AttendanceExportTask;
import com.ai.apac.smartenv.address.entity.EventinfoExportTask;
import com.ai.apac.smartenv.address.entity.TrackExportTask;
import com.ai.apac.smartenv.address.feign.IReverseAddressClient;
import com.ai.apac.smartenv.address.service.*;
import com.ai.apac.smartenv.address.vo.PersonTrackModelVO;
import com.ai.apac.smartenv.address.vo.VehicleTrackModelVO;
import com.ai.apac.smartenv.arrange.dto.AttendanceExportImageDTO;
import com.ai.apac.smartenv.arrange.entity.ScheduleAttendance;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.OkhttpUtil;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.event.entity.EventAssignedHistory;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.entity.EventMedium;
import com.ai.apac.smartenv.event.feign.IEventAssignedHistoryClient;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.event.feign.IEventMediumClient;
import com.ai.apac.smartenv.omnic.dto.OmnicScheduleAttendanceImageDTO;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.omnic.dto.AttendanceDetailDTO;
import com.ai.apac.smartenv.omnic.feign.IAttendanceClient;
import com.ai.apac.smartenv.omnic.feign.ITrackClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.websocket.feign.INotificationClient;
import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.ai.apac.smartenv.common.utils.CommonUtil.getDistance;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AddressAsyncService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/4
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/4  9:54    panfeng          v1.0.0             ????????????
 */
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
public class AddressAsyncService implements IAddressAsyncService {


    private static Logger logger = LoggerFactory.getLogger(AddressAsyncService.class);
    @Autowired
    private IEventInfoClient eventinfoClient;
    @Autowired
    private IEventMediumClient eventMediumClient;
    @Autowired
    private IEventAssignedHistoryClient eventAssignedHistoryClient;
    @Autowired
    private ISysClient sysClient;
    @Autowired
    @Lazy
    private IEventinfoExportTaskService eventinfoExportTaskService;
    @Autowired
    private IOssClient ossClient;


    @Autowired
    private IDeviceRelClient deviceRelClient;
    @Autowired
    private IDeviceClient deviceClient;

    @Autowired
    private BaiduMapUtils baiduMapUtils;
    @Autowired
    private IReverseAddressClient addressClient;
    @Autowired
    private IAddressService addressService;

    @Autowired
    @Lazy
    private ITrackExportTaskService exportTaskService;
    @Autowired
    private INotificationClient notificationClient;
    @Autowired
    private ITrackClient trackClient;
    @Autowired
    private IScheduleClient scheduleClient;

    @Autowired
    private IVehicleClient vehicleClient;
    @Autowired
    private IWorkareaRelClient workareaRelClient;
    @Autowired
    private IWorkareaClient workareaClient;

    @Autowired
    private IAttendanceClient attendanceClient;

    @Autowired
    private IDictClient dictClient;

    @Autowired
    @Lazy
    private IAttendanceExportTaskService attendanceExportTaskService;


    @Async("trackThreadPool")
    @Override
    public void exportExcelToOss(TrackExportTask exportTask, BladeUser user) {
        TrackExportTask updateTrack = new TrackExportTask();

        Long entityId = exportTask.getEntityId();
        Integer entityType = exportTask.getEntityType();
        try {
            String condition = exportTask.getExportCondition();
            JSONObject jsonObject = JSON.parseObject(condition);
            Long conditionBeginTime = jsonObject.getLong("conditionBeginTime");
            Long conditionEndTime = jsonObject.getLong("conditionEndTime");
            Long accStatus = jsonObject.getLong("accStatus");
            logger.info(Thread.currentThread().getName() + "----exportExcelToOss------------------");

            List modelList = new ArrayList<>();
            //????????????????????????
            SimpleDateFormat dateFormat = new SimpleDateFormat(BigDataHttpClient.bigDataTimeFormat);
            TrackExportTask query = new TrackExportTask();
            query.setEntityId(exportTask.getEntityId());
            query.setEntityType(exportTask.getEntityType());
            query.setExportCondition(exportTask.getExportCondition());
            QueryWrapper<TrackExportTask> wrapper = Condition.getQueryWrapper(query);
            wrapper.ne("id", exportTask.getId());
            wrapper.eq("export_status", AddressConstant.ExportStatus.EXPORTED);
            wrapper.isNotNull("file_path");
            List<TrackExportTask> list = exportTaskService.list(wrapper);

            updateTrack.setExportStatus(AddressConstant.ExportStatus.EXPORTED);
            updateTrack.setId(exportTask.getId());
            if (CollectionUtil.isNotEmpty(list)) {
                TrackExportTask task = list.get(0);
                updateTrack.setFilePath(task.getFilePath());
            } else {//????????????
                List<DeviceInfo> deviceInfo = null;
                if (CommonConstant.ENTITY_TYPE.PERSON.equals(exportTask.getEntityType().longValue())) {
                    deviceInfo = deviceClient.getForTrack(entityId, CommonConstant.ENTITY_TYPE.PERSON, VehicleConstant.PERSON_POSITION_DEVICE_TYPE, conditionBeginTime, conditionEndTime).getData();

                } else {
                    deviceInfo = deviceClient.getForTrack(entityId, CommonConstant.ENTITY_TYPE.VEHICLE, VehicleConstant.VEHICLE_POSITION_DEVICE_TYPE, conditionBeginTime, conditionEndTime).getData();

                }
                Sheet sheet1 = null;
                if (CollectionUtil.isNotEmpty(deviceInfo)) {


                    //??????????????????????????????
                    TrackPositionDto trackPositionDto = trackClient.getBigdataTrack(entityId, entityType.longValue(), conditionBeginTime, conditionEndTime).getData();
                    if (trackPositionDto != null) {
                        //????????????????????????????????????????????????????????????????????????

                        List<Coords> result = new ArrayList<>();
                        trackPositionDto.getTracks().forEach(track -> {
                            Coords coords = new Coords();
                            coords.setLatitude(track.getLat());
                            coords.setLongitude(track.getLng());
                            result.add(coords);
                        });


                        //?????????????????????????????????????????????
                        //????????????????????????????????????????????????????????????????????????
                        TrackPositionDto.Position last = trackPositionDto.getTracks().get(trackPositionDto.getTracks().size() - 1);


                        Coords lastCoords = null;
                        TrackPositionDto.Position lastPosition = null;
                        List<Dict> device_status = dictClient.getList("BIGDATA_DEVICE_STATUS").getData();
                        Map<String, Dict> dictMap = new HashMap<>();
                        device_status.forEach(dict -> dictMap.put(dict.getDictKey(), dict));


                        for (int i = 0; i < trackPositionDto.getTracks().size(); i++) {
                            TrackPositionDto.Position position = trackPositionDto.getTracks().get(i);
                            Coords coords1 = result.get(i);
                            position.setLat(coords1.getLatitude());
                            position.setLng(coords1.getLongitude());
                            position.setLastOnlineTime(DateUtil.format(new Date(Long.parseLong(position.getEventTime())), "yyyy-MM-dd hh:mm:ss"));
                            // ??????????????????
                            if (lastCoords == null || getDistance(Double.parseDouble(coords1.getLongitude()), Double.parseDouble(coords1.getLatitude()), Double.parseDouble(lastCoords.getLongitude()), Double.parseDouble(lastCoords.getLatitude())) >= 5) {
                                //??????MongoDB?????????????????????MongoDB????????????????????????????????????????????????????????????MongoDB
                                BaiduMapReverseGeoCodingResult reverseGeoCoding = addressService.getAddress(coords1);
                                if (reverseGeoCoding == null || reverseGeoCoding.getResult() == null) {
                                    reverseGeoCoding = baiduMapUtils.getReverseGeoCoding(coords1, BaiduMapUtils.CoordsSystem.BD09LL);
                                    reverseGeoCoding.setBaiduCoords(coords1);
                                    addressService.saveAddress(reverseGeoCoding);
                                }
                                lastCoords = coords1;
                                lastPosition = position;
                                position.setAddress(reverseGeoCoding.getResult().getFormatted_address());
                            } else {
                                position.setAddress(lastPosition.getAddress());
                            }
                            if (CommonConstant.ENTITY_TYPE.PERSON.equals(exportTask.getEntityType().longValue())) {
                                PersonTrackModelVO vehicleTrackModelVO = new PersonTrackModelVO();

                                String eventTime = position.getEventTime();

                                Boolean needWork = scheduleClient.checkNeedWork(entityId, "2", new Date(Long.parseLong(eventTime))).getData();
                                if (!needWork) {
                                    position.setWorkStatus(VehicleStatusEnum.OFF_LINE.getDesc());
                                } else if (position.getAccStatus().equals("1")) {
                                    position.setWorkStatus(VehicleStatusEnum.ON_LINE.getDesc());
                                } else {
                                    position.setWorkStatus(VehicleStatusEnum.OFFLINE_ALARM.getDesc());
                                }


                                position.setEventTime(DateUtil.format(new Date(Long.parseLong(position.getEventTime())), "yyyy-MM-dd HH:mm:ss"));
                                if (position.getAccStatus().equals("0")) {
                                    position.setAccStatus("??????");
                                } else {
                                    position.setAccStatus("??????");

                                }

                                vehicleTrackModelVO.setAccStatuslable(position.getAccStatus());


                                BeanUtil.copyProperties(position, vehicleTrackModelVO);
                                modelList.add(vehicleTrackModelVO);

                            } else if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(exportTask.getEntityType().longValue())) {
                                VehicleTrackModelVO vehicleTrackModelVO = new VehicleTrackModelVO();

                                String eventTime = position.getEventTime();

                                Boolean needWork = scheduleClient.checkNeedWork(entityId, "1", new Date(Long.parseLong(eventTime))).getData();
                                if (!needWork) {
                                    position.setWorkStatus(VehicleStatusEnum.OFF_LINE.getDesc());
                                } else if (position.getAccStatus().equals("0")) {
                                    position.setWorkStatus(VehicleStatusEnum.ON_LINE.getDesc());
                                } else {
                                    position.setWorkStatus(VehicleStatusEnum.OFFLINE_ALARM.getDesc());
                                }


                                position.setEventTime(DateUtil.format(new Date(Long.parseLong(position.getEventTime())), "yyyy-MM-dd HH:mm:ss"));
//                                if (position.getAccStatus().equals("0")) {
//                                    position.setAccStatus("??????");
//                                } else if (position.getAccStatus().equals("1")) {
//                                    position.setAccStatus("????????????");
//                                } else if (position.getAccStatus().equals("2")) {
//                                    position.setAccStatus("????????????");
//                                } else {
//                                    position.setAccStatus("??????");
//                                }

                                if (position.getAccStatus()==null){
                                    position.setAccStatus("??????");

                                }else{

                                    position.setAccStatus(dictMap.get(position.getAccStatus()).getDictValue());
                                }


                                vehicleTrackModelVO.setPlateNumber(exportTask.getEntityName());
                                vehicleTrackModelVO.setAccStatus(position.getAccStatus());
                                BeanUtil.copyProperties(position, vehicleTrackModelVO);
                                modelList.add(vehicleTrackModelVO);
                            }


                        }

                        if (CommonConstant.ENTITY_TYPE.PERSON.equals(exportTask.getEntityType().longValue())) {

                            sheet1 = new Sheet(1, 0, PersonTrackModelVO.class);

                        } else if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(exportTask.getEntityType().longValue())) {

                            sheet1 = new Sheet(1, 0, VehicleTrackModelVO.class);
                        }
                    }
                }
                StringBuffer buffer = new StringBuffer();
                if (CommonConstant.ENTITY_TYPE.PERSON.equals(exportTask.getEntityType().longValue())) {
                    buffer.append("??????????????????-");

                } else if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(exportTask.getEntityType().longValue())) {
                    buffer.append("??????????????????-");
                }
                buffer.append(exportTask.getEntityName());
                buffer.append("-");
                buffer.append(dateFormat.format(exportTask.getExportTime()));
                buffer.append(".xlsx");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ExcelWriter writer = new ExcelWriter(byteArrayOutputStream, ExcelTypeEnum.XLSX);
                if (sheet1 == null) {
                    sheet1 = new Sheet(1, 0, PersonTrackModelVO.class);
                    sheet1.setSheetName("sheet1");
                    sheet1 = new Sheet(1, 0, null);
                    List<List<String>> head = new ArrayList<>();
                    List<String> head1 = new ArrayList<>();
                    head1.add("????????????????????????");
                    head.add(head1);
                    sheet1.setHead(head);
                } else {
                    sheet1.setSheetName("sheet1");
                    writer.write(modelList, sheet1);
                    writer.finish();

                }
                byte[] bytes = byteArrayOutputStream.toByteArray();
                String stringData = Base64Utils.encodeToString(bytes);
                String data = ossClient.putBase64Stream(AddressConstant.BUCKET, buffer.toString(), stringData).getData();
                updateTrack.setFilePath(data);

            }
        } catch (Exception e) {
            logger.error("????????????", e);
            updateTrack.setExportStatus(AddressConstant.ExportStatus.EXPORT_FIELD);
        }
        exportTaskService.updateById(updateTrack);//??????


        NotificationInfo notityInfo = new NotificationInfo();
        notityInfo.setUserId(user.getUserId().toString());
        notityInfo.setBroadCast(false);
        notityInfo.setPathType(WebSocketConsts.NotificationPathType.INNER_LINK);
        notityInfo.setLevel(WebSocketConsts.NotificationLevel.INFO);
        notityInfo.setCategory(WebSocketConsts.NotificationCategory.TASK);
        if (CommonConstant.ENTITY_TYPE.PERSON.longValue() == entityType.longValue()) {
            notityInfo.setPath(WebSocketConsts.NotificationPath.PERSON_HISTORY_TRACK);
            notityInfo.setTitle("??????????????????????????????????????????");
            notityInfo.setContent("??????????????????????????????????????????,??????????????????????????????????????????");
        } else if (CommonConstant.ENTITY_TYPE.VEHICLE.longValue() == entityType.longValue()) {
            notityInfo.setPath(WebSocketConsts.NotificationPath.VEHICLE_HISTORY_TRACK);
            notityInfo.setTitle("??????????????????????????????????????????");
            notityInfo.setContent("??????????????????????????????????????????,??????????????????????????????????????????");
        }
        notificationClient.pushNotification(notityInfo);
    }

    //*************************************************   attendance export ***************************************************************************************
    @Override
    @Async("trackThreadPool")
    public void exportAttendance(AttendanceExportTask exportTask, BladeUser user) {
        String docxFile = null;
        AttendanceExportTask update = getAllIdTags(exportTask);
        update.setId(exportTask.getId());
        try {
            List<ScheduleAttendance> attendancesList = selectAllAttendance(exportTask);
            //????????????????????????????????????
            //????????????
            docxFile = exportAttendance(attendancesList, exportTask);
            update.setExportStatus(AddressConstant.ExportStatus.EXPORTED);
            update.setFilePath(docxFile);
        } catch (Exception e) {
            logger.error("????????????", e);
            update.setExportStatus(AddressConstant.ExportStatus.EXPORT_FIELD);
        } finally {
            attendanceExportTaskService.updateById(update);
            NotificationInfo notityInfo = new NotificationInfo();
            notityInfo.setUserId(user.getUserId().toString());
            notityInfo.setBroadCast(false);
            notityInfo.setPathType(WebSocketConsts.NotificationPathType.INNER_LINK);
            notityInfo.setLevel(WebSocketConsts.NotificationLevel.INFO);
            notityInfo.setCategory(WebSocketConsts.NotificationCategory.TASK);
            notityInfo.setPath(WebSocketConsts.NotificationPath.ATTANCEDENT);
            notityInfo.setTitle("????????????????????????????????????");
            notityInfo.setContent("????????????????????????????????????,??????????????????????????????");
            notificationClient.pushNotification(notityInfo);
        }

    }


    public AttendanceExportTask getAllIdTags(AttendanceExportTask exportTask) {
        AttendanceExportTask newExportTask = new AttendanceExportTask();
        List<Long> regionIds = new ArrayList<>();
        List<Long> deptIds = new ArrayList<>();
        List<Long> entityCategoryIds = new ArrayList<>();

        List<ScheduleAttendance> attendancesList = selectAllAttendance(exportTask);
        attendancesList.forEach(attendance -> {
            if (!deptIds.contains(attendance.getDeptId())) {
                deptIds.add(attendance.getDeptId());
            }
            if (!entityCategoryIds.contains(attendance.getEntityCategoryId())) {
                entityCategoryIds.add(attendance.getEntityCategoryId());
            }

            if (!regionIds.contains(attendance.getRegionId())) {
                regionIds.add(attendance.getRegionId());
            }

        });
        String regionIdString = StringUtils.arrayToDelimitedString(regionIds.toArray(), ",");
        String deptIdString = StringUtils.arrayToDelimitedString(deptIds.toArray(), ",");
        String entityCategoryString = StringUtils.arrayToDelimitedString(entityCategoryIds.toArray(), ",");
        newExportTask.setId(exportTask.getId());
        newExportTask.setRegionIdTags(regionIdString);
        newExportTask.setDeptIdTags(deptIdString);
        newExportTask.setCategoryIdTags(entityCategoryString);

        return newExportTask;
    }

    /**
     * ??????word ??????
     * @param scheduleAttendances
     * @param exportTask
     * @return
     * @throws Exception
     */
    public String exportAttendance(List<ScheduleAttendance> scheduleAttendances, AttendanceExportTask exportTask) throws Exception {

        if (CollectionUtil.isEmpty(scheduleAttendances)) {
            return null;
        }
        List<AttendanceDetailDTO> detailDTOS = new ArrayList<>();
        List<JasperPrint> prints = new ArrayList<>();
        //?????????????????????????????????JasperPrint
        for (ScheduleAttendance attendance : scheduleAttendances) {

            // ??????????????????
            AttendanceDetailDTO attendanceDetailDTO = attendanceClient.getAttendanceDetail(attendance).getData();
            detailDTOS.add(attendanceDetailDTO);

            //?????????list
            // ??????Jasper ????????????
            JasperPrint print = exportAttendance(attendanceDetailDTO);
            prints.add(print);

        }
        JRAbstractExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
        try {
            exporter.exportReport();
            byte[] bytes = bos.toByteArray();
            String encodeData = Base64.encode(bytes);
            String fileName = "????????????-" + DateUtil.format(exportTask.getExportTime(), "YYYY-MM-DD_HH:mm:ss:SSS") + ".docx";
            //?????????oss
            String path = ossClient.putBase64Stream(CommonConstant.BUCKET, fileName, encodeData).getData();
            return path;

        } catch (JRException e) {
            e.printStackTrace();
        }


        return null;
    }


    public JasperPrint exportAttendance(AttendanceDetailDTO attendanceDetailDTO) throws Exception {
        List<AttendanceExportImageDTO> attendanceExportImageDTOS = vehicleImageDTOS(attendanceDetailDTO.getScheduleAttendanceDetails());


        String lineImagePath = attendanceDetailDTO.getPersonLineImagePath();
        byte[] lineImageByte;
        InputStream nullImage = AttendanceExportTaskServiceImpl.class.getClassLoader().getResourceAsStream("jasper/nullImage.png");
        if (StringUtil.isEmpty(lineImagePath)){
            byte[] nullImageData = StreamUtils.copyToByteArray(nullImage);
            lineImageByte = nullImageData;
        }else {
            String lineImageURL = ossClient.getObjectLink(CommonConstant.BUCKET, lineImagePath).getData();
            lineImageByte = OkhttpUtil.download(lineImageURL);
            //????????????????????????????????????????????????????????????,?????????????????????
            if(null==lineImageByte||0==lineImageByte.length){
                byte[] nullImageData = StreamUtils.copyToByteArray(nullImage);
                lineImageByte = nullImageData;
            }
        }

//        InputStream jrxml = AttendanceExportTaskServiceImpl.class.getClassLoader().getResourceAsStream("jasper/VehicleDayTrack.jrxml");
//        JasperReport report = JasperCompileManager.compileReport(jrxml);

        //?????????????????????Jasper??????
        Map<String, Object> parameter = new HashMap<>();
//        parameter.put("day", attendanceDetailDTO.getScheduleDate());
//        parameter.put("vehicle", attendanceDetailDTO.getEntityName());
//        parameter.put("schedule", DateUtil.format(attendanceDetailDTO.getScheduleBeginTime(), "HH:mm") + "-" + DateUtil.format(attendanceDetailDTO.getScheduleEndTime(), "HH:mm"));
//        parameter.put("workAreaInfo", attendanceDetailDTO.getWorkareaName());
//        parameter.put("startPoint", StringUtil.isNotBlank(attendanceDetailDTO.getWorkBeginPosition().getAddress()) ? attendanceDetailDTO.getWorkBeginPosition().getAddress() : "??????");
//        parameter.put("line", attendanceDetailDTO.getPathWay());
//        parameter.put("endPoint", StringUtil.isNotBlank(attendanceDetailDTO.getWorkEndPosition().getAddress()) ? attendanceDetailDTO.getWorkEndPosition().getAddress() : "??????");
//        parameter.put("trackImage", new ByteArrayInputStream(lineImageByte));
        //# init

        parameter.put("title_work_real","");  // ????????????????????????????????????????????????????????????????????????"??????????????????&???????????????"
        parameter.put("title_work_image",""); // ?????????????????????????????????"???????????????"??????????????????????????????????????????
        parameter.put("title_workarea_start",""); // ??????????????????????????????????????????????????????????????????"?????????"
        parameter.put("title_workarea_pathway","");// ?????????????????????????????????????????????????????????"?????????"
        parameter.put("title_line_image",""); // ??????????????????????????????????????????????????????????????????"?????????"
        parameter.put("title_schedule_name","");// ???????????????????????????????????????????????????"?????????"


        parameter.put("title_day", "??????");
        parameter.put("day", attendanceDetailDTO.getScheduleDate());
        parameter.put("title_entity_name","?????????");// ??????????????????????????????"?????????"
        parameter.put("title_position_name","?????????");// ????????????????????????????????????????????????????????????"?????????"
        parameter.put("title_dept_name","?????????"); // ??????????????????????????????????????????????????????????????????"?????????"
        parameter.put("title_vehicle_name","?????????");// ??????????????????????????????????????????????????????????????????"?????????"
        parameter.put("entityName",attendanceDetailDTO.getEntityName());// ????????????
        parameter.put("position",attendanceDetailDTO.getPersonPositionName()); // ?????????????????????????????????????????????null
        parameter.put("dept",attendanceDetailDTO.getDeptName()); // ?????????????????????????????????????????????null


        //????????????
        if (attendanceDetailDTO.getScheduleBeginTime()!=null&&attendanceDetailDTO.getScheduleEndTime()!=null){
            parameter.put("title_schedule_name","?????????");// ???????????????????????????????????????????????????"?????????"
            String begin = DateUtil.format(attendanceDetailDTO.getScheduleBeginTime(), "HH:mm");
            String end = DateUtil.format(attendanceDetailDTO.getScheduleEndTime(), "HH:mm");
            String scheduleStr=begin+"-"+end;
            if ( attendanceDetailDTO.getBreaksBeginTime()!=null&&attendanceDetailDTO.getBreaksEndTime()!=null){
                String breakBegin = DateUtil.format( attendanceDetailDTO.getBreaksBeginTime(), "HH:mm");
                String breakEnd = DateUtil.format(attendanceDetailDTO.getBreaksEndTime(), "HH:mm");
                scheduleStr+="(?????????"+breakBegin+"-"+breakEnd+")";
            }
            parameter.put("schedule",scheduleStr);// ?????????????????????8:00???18:00 ????????????11:00???12:00)
        }
        InputStream jrxml = AttendanceExportTaskServiceImpl.class.getClassLoader().getResourceAsStream("jasper/Attendance_line.jrxml");

        if (CollectionUtil.isNotEmpty(attendanceDetailDTO.getPositions())){
            parameter.put("title_work_real","??????????????????&???????????????");  // ????????????????????????????????????????????????????????????????????????"??????????????????&???????????????"
            parameter.put("title_work_image","???????????????"); // ?????????????????????????????????"???????????????"??????????????????????????????????????????
            parameter.put("title_workarea_start","?????????"); // ??????????????????????????????????????????????????????????????????"?????????"
            parameter.put("title_workarea_pathway","?????????");// ?????????????????????????????????????????????????????????"?????????"
            parameter.put("title_workarea_end","?????????");// ?????????????????????????????????????????????????????????"?????????"
            parameter.put("title_line_image","???????????????"); // ??????????????????????????????????????????????????????????????????"?????????"

            parameter.put("workarea_start",attendanceDetailDTO.getWorkBeginPosition().getAddress()); // ???????????????????????????????????????null
            parameter.put("work_area_pathway",attendanceDetailDTO.getPathWay()); // ???????????????????????????????????????null
            parameter.put("workarea_end",attendanceDetailDTO.getWorkEndPosition().getAddress()); // ???????????????????????????????????????null
            parameter.put("line_image_1", new ByteArrayInputStream(lineImageByte)); // ?????????1??????????????????????????????nullImage

        }else {
            jrxml=AttendanceExportTaskServiceImpl.class.getClassLoader().getResourceAsStream("jasper/Attendance_no_line.jrxml");
        }
        JasperReport report = JasperCompileManager.compileReport(jrxml);
//        parameter.put("vehicle",""); // ????????????????????????????????????????????????null
        JRDataSource datasource = new JRBeanCollectionDataSource(attendanceExportImageDTOS, false);
        JasperPrint print = JasperFillManager.fillReport(report, parameter, datasource);
        return print;
    }


    public List<AttendanceExportImageDTO> vehicleImageDTOS(List<OmnicScheduleAttendanceImageDTO> imageDTOS) throws Exception {
        List<AttendanceExportImageDTO> attendanceExportImageDTOS = new ArrayList<>();
        Iterator<OmnicScheduleAttendanceImageDTO> iterator = imageDTOS.iterator();

        InputStream nullImage = AttendanceExportTaskServiceImpl.class.getClassLoader().getResourceAsStream("jasper/nullImage.png");

        byte[] nullImageData = StreamUtils.copyToByteArray(nullImage);


        if (iterator.hasNext()) {
            //?????????
            while (iterator.hasNext()) {
                AttendanceExportImageDTO attendanceExportImageDTO = new AttendanceExportImageDTO();
                OmnicScheduleAttendanceImageDTO dto1 = iterator.next();
                String image1Path = dto1.getImagePath();
                String image1Url = ossClient.getObjectLink(CommonConstant.BUCKET, image1Path).getData();
                ByteArrayOutputStream image1Output = new ByteArrayOutputStream();
                HttpUtil.download(image1Url, image1Output, true);
                byte[] image1Byte = image1Output.toByteArray();
                if(null==image1Byte||0==image1Byte.length){
                    image1Byte = nullImageData;
                }

                attendanceExportImageDTO.setImage1(new ByteArrayInputStream(image1Byte));
                attendanceExportImageDTO.setImage1Detail(DateUtil.format(dto1.getUploadTime(), "HH:mm") + dto1.getAddress());
                if(ArrangeConstant.GO_OFF_WORK_FLAG.ATTED.equals(dto1.getGoOffWorkFlag())){
                    attendanceExportImageDTO.setImage1Title("??????");
                }else {
                    attendanceExportImageDTO.setImage1Title("??????");
                }

                if (iterator.hasNext()) {
                    OmnicScheduleAttendanceImageDTO dto2 = iterator.next();
                    String image2Path = dto2.getImagePath();
                    String image2Url = ossClient.getObjectLink(CommonConstant.BUCKET, image2Path).getData();
                    ByteArrayOutputStream image2Output = new ByteArrayOutputStream();
                    HttpUtil.download(image2Url, image2Output, true);
                    byte[] image2Byte = image2Output.toByteArray();
                    if(null==image2Byte||0==image2Byte.length){
                        image2Byte = nullImageData;
                    }
                    attendanceExportImageDTO.setImage2(new ByteArrayInputStream(image2Byte));
                    attendanceExportImageDTO.setImage2Detail(DateUtil.format(dto2.getUploadTime(), "HH:mm") + dto2.getAddress());
                    if(ArrangeConstant.GO_OFF_WORK_FLAG.ATTED.equals(dto2.getGoOffWorkFlag())){
                        attendanceExportImageDTO.setImage2Title("??????");
                    }else {
                        attendanceExportImageDTO.setImage2Title("??????");
                    }
                } else {
                    attendanceExportImageDTO.setImage2(new ByteArrayInputStream(nullImageData));
                    attendanceExportImageDTO.setImage2Detail("");
                }

                attendanceExportImageDTOS.add(attendanceExportImageDTO);
            }
        } else {
            AttendanceExportImageDTO attendanceExportImageDTO = new AttendanceExportImageDTO();
            attendanceExportImageDTO.setImage1(new ByteArrayInputStream(nullImageData));
            attendanceExportImageDTO.setImage1Detail("");
            attendanceExportImageDTO.setImage1Title("");
            attendanceExportImageDTO.setImage2(new ByteArrayInputStream(nullImageData));
            attendanceExportImageDTO.setImage1Title("");
            attendanceExportImageDTO.setImage2Detail("");


        }

        return attendanceExportImageDTOS;
    }

    public List<ScheduleAttendance> selectAllAttendance(AttendanceExportTask exportTask) {
        logger.info(Thread.currentThread().getName() + "----exportWordToOss------------------");
        String exportCondition = exportTask.getExportCondition();
        JSONObject jsonObject = JSON.parseObject(exportCondition);
        JSONArray conditionIds = jsonObject.getJSONArray("conditionIds");


        String conditionDate = jsonObject.getString("conditionDate");
        Long conditionRegionId = jsonObject.getLong("conditionRegionId");
        Long conditionVehicleCategoryId = jsonObject.getLong("conditionVehicleCategoryId");
        Long conditionDeptId = jsonObject.getLong("conditionDeptId");
        String plateNumber = jsonObject.getString("plateNumber");
        List<ScheduleAttendance> result = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(conditionIds)) {
            conditionIds.forEach(attendanceIdObj -> {
                if (attendanceIdObj instanceof String) {
                    String attendanceId = attendanceIdObj.toString();
                    ScheduleAttendance attendanceQuery = new ScheduleAttendance();
                    attendanceQuery.setId(Long.parseLong(attendanceId));
                    List<ScheduleAttendance> attendances = scheduleClient.getAttendance(attendanceQuery).getData();
                    result.addAll(attendances);
                }
            });
        } else {
            ScheduleAttendance query = new ScheduleAttendance();
            query.setRegionId(conditionRegionId);
            query.setEntityCategoryId(conditionVehicleCategoryId);
            query.setDeptId(conditionDeptId);
            query.setEntityName(plateNumber);
            query.setAttendanceStatus(ArrangeConstant.AttendanceStatus.ATTED);
            List<ScheduleAttendance> attendanceList = scheduleClient.getAttendanceByDate(query, conditionDate).getData();
            result.addAll(attendanceList);
        }
        return result;
    }


    //*************************************************   event export ***************************************************************************************
    @Async("trackThreadPool")
    @Override
    public void exportEventinfo(EventinfoExportTask exportTask, List<Long> eventIds) {
        //?????????????????????
        try {
            List<EventInfo> eventInfoList = new ArrayList<>();
            String name = "";
            String belongIds = "";
            if (CollectionUtil.isNotEmpty(eventIds)) {

                for (Long eventId : eventIds) {
                    EventInfo eventById = eventinfoClient.getEventInfoByEventId(eventId).getData();
                    eventInfoList.add(eventById);
                    if (eventById.getBelongArea() != null && eventById.getBelongArea() != 0L) {
                        if ((name == null || "".equals(name)) && ("-1".equals(belongIds) || "".equals(belongIds))) {
                            belongIds = eventById.getBelongArea().toString();
                            name = sysClient.getRegion(eventById.getBelongArea()).getData().getRegionName();
                        } else {
                            String regionName = sysClient.getRegion(eventById.getBelongArea()).getData().getRegionName();
                            if (org.apache.commons.lang.StringUtils.isNotBlank(regionName)) {
                                if (!name.contains(regionName)) {
                                    name += "," + sysClient.getRegion(eventById.getBelongArea()).getData().getRegionName();
                                }
                            }
                            if (!belongIds.contains(eventById.getBelongArea().toString())) {
                                belongIds += "," + eventById.getBelongArea().toString();
                            }

                        }
                    }

                }
                exportTask.setBelongArea(belongIds);
                exportTask.setBelongAreaName(name);
            }


            String docxFile = exportEventinfo(eventInfoList, exportTask.getId());
            exportTask.setExportStatus(AddressConstant.ExportStatus.EXPORTED);
            exportTask.setFilePath(docxFile);
        } catch (Exception e) {
            exportTask.setErrorLog(e.getMessage().length() > 2555 ? e.getMessage().substring(0, 2555) : e.getMessage());
            exportTask.setExportStatus(AddressConstant.ExportStatus.EXPORT_FIELD);
        } finally {
            eventinfoExportTaskService.updateById(exportTask);
        }
    }

    public String exportEventinfo(List<EventInfo> eventInfoList, Long eventExportId) throws Exception {

        if (CollectionUtil.isEmpty(eventInfoList)) {
            return null;
        }
        List<JasperPrint> prints = new ArrayList<>();
        for (int i=0;i < eventInfoList.size(); i++) {
            try {
                String title = "";
                if(i == 0) { //??????title
                    title = "??????????????????";
                }
                JasperPrint print = exportEventinfo(eventInfoList.get(i),title);
                prints.add(print);
            } catch (JRException e) {
                throw new Exception(e.getMessage());

            }
        }
        JRAbstractExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
        try {
            exporter.exportReport();
            byte[] bytes = bos.toByteArray();
            String encodeData = Base64.encode(bytes);
            String fileName = "??????????????????" + eventExportId + ".docx";

            String path = ossClient.putBase64Stream(CommonConstant.BUCKET, fileName, encodeData).getData();
            return path;

        } catch (JRException e) {
            e.printStackTrace();
        }


        return null;
    }


    public JasperPrint exportEventinfo(EventInfo eventInfo,String title) throws JRException, UnsupportedEncodingException {
        List<EventImageDTO> list = convertEventImages(eventInfo);
        InputStream jrxml = EventInfoExportTaskServiceImpl.class.getClassLoader().getResourceAsStream("jasper/Eventinfo.jrxml");
        JasperReport report = JasperCompileManager.compileReport(jrxml);

        //?????????????????????Jasper??????
        Map<String, Object> parameter = new HashMap<>();
        if(org.apache.commons.lang.StringUtils.isNotBlank(title)) {
            parameter.put("TITLE",title);
        }

        parameter.put("DATE", TimeUtil.getYYYYMMDD(eventInfo.getCreateTime()));
        parameter.put("EVENT", DictCache.getValue("event_type", String.valueOf(eventInfo.getEventType())));
        parameter.put("ADDRESS", eventInfo.getEventAddress());
        parameter.put("EVENT_DESC", eventInfo.getEventDesc());

        JRDataSource datasource = new JRBeanCollectionDataSource(list, false);
        JasperPrint print = JasperFillManager.fillReport(report, parameter, datasource);
        return print;
    }


    public List<EventImageDTO> convertEventImages(EventInfo eventInfo) throws UnsupportedEncodingException {
        List<EventImageDTO> eventImageDTOS = new ArrayList<>();
        //????????????????????????
        List<EventMedium> eventMediumList = eventMediumClient.listEventMediumById(eventInfo.getId(),EventConstant.MediumDetailType.PRE_CHECK).getData();
        EventImageDTO eventImageDTO = new EventImageDTO();
        if (eventMediumList != null && eventMediumList.size() > 0) {
            ByteArrayOutputStream image1Output = new ByteArrayOutputStream();
            String url1 = ossClient.getObjectLink(CommonConstant.BUCKET, URLDecoder.decode(eventMediumList.get(0).getMediumUrl(), "UTF-8")).getData();
            HttpUtil.download(url1, image1Output, true);
            byte[] image1Byte = image1Output.toByteArray();
            eventImageDTO.setImage1(new ByteArrayInputStream(image1Byte));
        }
        if (eventMediumList.size() > 1) {
            ByteArrayOutputStream image2Output = new ByteArrayOutputStream();
            String url2 = ossClient.getObjectLink(CommonConstant.BUCKET, URLDecoder.decode(eventMediumList.get(1).getMediumUrl(), "UTF-8")).getData();
            HttpUtil.download(url2, image2Output, true);
            byte[] image2Byte = image2Output.toByteArray();
            eventImageDTO.setImage2(new ByteArrayInputStream(image2Byte));
        }
        if (eventMediumList.size() > 2) {
            ByteArrayOutputStream image5Output = new ByteArrayOutputStream();
            String url3 = ossClient.getObjectLink(CommonConstant.BUCKET, URLDecoder.decode(eventMediumList.get(2).getMediumUrl(), "UTF-8")).getData();

            HttpUtil.download(url3, image5Output, true);
            byte[] image5Byte = image5Output.toByteArray();
            eventImageDTO.setImage5(new ByteArrayInputStream(image5Byte));
        }
        //????????????????????????
        List<EventAssignedHistory> eventAssignedHistoryList = eventAssignedHistoryClient.listEventAssignedHistoryById(eventInfo.getId(), CommonConstant.ASSIGNED_HISTORY_TYPE.CHECK, CommonConstant.CHECK_RESULT.QUALIFIED).getData();
        if (eventAssignedHistoryList != null && eventAssignedHistoryList.size() > 0) {

            List<EventMedium> eventMediumList4Result = eventMediumClient.listEventMediumByAssignedId(eventAssignedHistoryList.get(0).getId()).getData();
            if (eventMediumList4Result != null && eventMediumList4Result.size() > 0) {
                ByteArrayOutputStream image3Output = new ByteArrayOutputStream();
                String url4 = ossClient.getObjectLink(CommonConstant.BUCKET, URLDecoder.decode(eventMediumList4Result.get(0).getMediumUrl(), "UTF-8")).getData();

                HttpUtil.download(url4, image3Output, true);
                byte[] image3Byte = image3Output.toByteArray();
                eventImageDTO.setImage3(new ByteArrayInputStream(image3Byte));
            }
            if (eventMediumList4Result != null && eventMediumList4Result.size() > 1) {
                ByteArrayOutputStream image4Output = new ByteArrayOutputStream();
                String url5 = ossClient.getObjectLink(CommonConstant.BUCKET, URLDecoder.decode(eventMediumList4Result.get(1).getMediumUrl(), "UTF-8")).getData();

                HttpUtil.download(url5, image4Output, true);
                byte[] image4Byte = image4Output.toByteArray();
                eventImageDTO.setImage4(new ByteArrayInputStream(image4Byte));
            }
            if (eventMediumList4Result != null && eventMediumList4Result.size() > 2) {
                ByteArrayOutputStream image6Output = new ByteArrayOutputStream();
                String url6 = ossClient.getObjectLink(CommonConstant.BUCKET, URLDecoder.decode(eventMediumList4Result.get(2).getMediumUrl(), "UTF-8")).getData();

                HttpUtil.download(url6, image6Output, true);
                byte[] image6Byte = image6Output.toByteArray();
                eventImageDTO.setImage6(new ByteArrayInputStream(image6Byte));
            }
        }else {
            List<EventMedium> eventMediumListAfter = eventMediumClient.listEventMediumById(eventInfo.getId(),EventConstant.MediumDetailType.AFTER_CHECK).getData();
            if (eventMediumListAfter != null && eventMediumListAfter.size() > 0) {
                ByteArrayOutputStream image1Output = new ByteArrayOutputStream();
                String url1 = ossClient.getObjectLink(CommonConstant.BUCKET, URLDecoder.decode(eventMediumListAfter.get(0).getMediumUrl(), "UTF-8")).getData();
                HttpUtil.download(url1, image1Output, true);
                byte[] image1Byte = image1Output.toByteArray();
                eventImageDTO.setImage3(new ByteArrayInputStream(image1Byte));
            }
            if (eventMediumListAfter.size() > 1) {
                ByteArrayOutputStream image2Output = new ByteArrayOutputStream();
                String url2 = ossClient.getObjectLink(CommonConstant.BUCKET, URLDecoder.decode(eventMediumListAfter.get(1).getMediumUrl(), "UTF-8")).getData();
                HttpUtil.download(url2, image2Output, true);
                byte[] image2Byte = image2Output.toByteArray();
                eventImageDTO.setImage4(new ByteArrayInputStream(image2Byte));
            }
            if (eventMediumListAfter.size() > 2) {
                ByteArrayOutputStream image5Output = new ByteArrayOutputStream();
                String url3 = ossClient.getObjectLink(CommonConstant.BUCKET, URLDecoder.decode(eventMediumListAfter.get(2).getMediumUrl(), "UTF-8")).getData();

                HttpUtil.download(url3, image5Output, true);
                byte[] image5Byte = image5Output.toByteArray();
                eventImageDTO.setImage6(new ByteArrayInputStream(image5Byte));
            }
        }

        eventImageDTOS.add(eventImageDTO);
        return eventImageDTOS;
    }

//    public static void main(String[] args) {
//        //????????????????????????
//        try {
//            String path = "http://10.21.33.235:9001/smartenv/upload/20200601/QQ%E5%9B%BE%E7%89%8720190603115105.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minio/20200601/us-east-1/s3/aws4_request&X-Amz-Date=20200601T030658Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=a255583532c57417acad6957157fa7847b0a1ec0afc64480ea4754dcea831a52";
//            String str= URLDecoder.decode(path,"UTF-8");
//            //????????????
//            System.out.println("path?????????:"+str);
//            System.out.println("-----------------");
//            String str1= URLEncoder.encode("???","UTF-8");
//            //????????????
//            System.out.println("???????????????:"+(URLEncoder.encode(str1,"UTF-8")));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
}
