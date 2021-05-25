package com.ai.apac.smartenv.omnic.service.impl;

import cn.hutool.core.codec.Base64;
import com.ai.apac.smartenv.address.feign.IReverseAddressClient;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleAttendance;
import com.ai.apac.smartenv.arrange.entity.ScheduleAttendanceDetail;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.omnic.dto.OmnicScheduleAttendanceImageDTO;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.omnic.dto.AttendanceDetailDTO;
import com.ai.apac.smartenv.omnic.feign.ITrackClient;
import com.ai.apac.smartenv.omnic.service.VehicleAttendanceDetailService;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.ai.apac.smartenv.common.utils.CommonUtil.getDistance;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleAttendanceDetailServiceImpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/12 19:30     panfeng          v1.0.0             修改原因
 */
@Service
@AllArgsConstructor
public class AttendanceDetailServiceImpl implements VehicleAttendanceDetailService {

    private static Logger logger = LoggerFactory.getLogger(AttendanceDetailServiceImpl.class);


    private IScheduleClient scheduleClient;

    private IVehicleClient vehicleClient;

    private IPersonVehicleRelClient personVehicleRelClient;
    //    @Autowired
//    private ISysClient sysClient;
//    @Autowired
//    private IWorkareaClient workareaClient;
//    @Autowired
//    private IWorkareaRelClient workareaRelClient;
//    @Autowired
//    private IDeviceClient deviceClient;

    private BaiduMapUtils baiduMapUtils;

    private IReverseAddressClient addressClient;

    private IOssClient ossClient;


    private ITrackClient trackClient;


    private MongoTemplate mongoTemplate;


    /**
     * 生成考勤打卡详情
     *
     * @param attendance
     * @return
     * @throws Exception
     */
    @Override
    public AttendanceDetailDTO getAttendanceDetail(ScheduleAttendance attendance) throws Exception {
        try {
            Long scheduleObjectId = attendance.getScheduleObjectId();
            ScheduleObject scheduleObject = scheduleClient.getScheduleObjectById(scheduleObjectId).getData();

//            VehicleInfo vehicleById = null;
            Person personById = null;

            personById = PersonCache.getPersonById(null, attendance.getEntityId());

            AttendanceDetailDTO dto = new AttendanceDetailDTO();


            VehicleInfo vehicleInfo=null;
            List<PersonVehicleRel> personVehicleRels = personVehicleRelClient.getVehicleByPersonId(personById.getId()).getData();
            if (CollectionUtil.isNotEmpty(personVehicleRels)){
                vehicleInfo = vehicleClient.getVehicleInfoById(personVehicleRels.get(0).getVehicleId()).getData();
            }
//            VehicleInfo vehicleInfo = vehicleClient.getVehicleInfoById(scheduleObject.getEntityId()).getData();

            //先从MongoDB中取
            Query mongoQuery = new Query();
            mongoQuery.addCriteria(Criteria.where("attendanceId").is(attendance.getId()));
            AttendanceDetailDTO mongoTemplateOne = mongoTemplate.findOne(mongoQuery, AttendanceDetailDTO.class);
            List<ScheduleAttendanceDetail> attendanceDetails = scheduleClient.getAttendanceDetailListByAttendanceId(attendance.getId()).getData();

            Date beginTime = null;
            Date endTime = null;
            logger.info("Mongo 查询结果：" + mongoTemplateOne);

            for (ScheduleAttendanceDetail attendanceDetail : attendanceDetails) {
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
            if (beginTime == null && endTime == null) {
                logger.error("上下班时间不能为空");
                throw new RuntimeException("上下班时间不能为空");
            }
            TrackPositionDto trackPositionDto = null;
            if (vehicleInfo==null) {
                logger.warn("trackClient.getBigdataTrack" + personById.getId() + "       " + CommonConstant.ENTITY_TYPE.PERSON + beginTime.getTime() + "       " + endTime.getTime());
                trackPositionDto = trackClient.getBigdataTrack(personById.getId(), CommonConstant.ENTITY_TYPE.PERSON, beginTime.getTime(), endTime.getTime()).getData();
            } else {
                logger.warn("trackClient.getBigdataTrack" + vehicleInfo.getId() + "       " + CommonConstant.ENTITY_TYPE.VEHICLE + beginTime.getTime() + "       " + endTime.getTime());
            }
            logger.warn(trackPositionDto.toString());
            List<TrackPositionDto.Position> tracks = null;
            if (trackPositionDto != null) {
                tracks = trackPositionDto.getTracks();
                //如果MongoDB 里面有当天的数据并且数据和当前没有发生变化，则直接取MongoDB中的数据
                if (mongoTemplateOne != null) {
                    if (CollectionUtil.isEmpty(trackPositionDto.getTracks()) && CollectionUtil.isEmpty(mongoTemplateOne.getPositions())) {
                        return mongoTemplateOne;
                    } else if (CollectionUtil.isNotEmpty(tracks) && CollectionUtil.isNotEmpty(mongoTemplateOne.getPositions())) {
                        TrackPositionDto.Position position = tracks.get(tracks.size() - 1);
                        String currentTimeLastEventTime = position.getEventTime(); // 最后一个节点的时间
                        TrackPositionDto.Position mongoPosition = mongoTemplateOne.getPositions().get(mongoTemplateOne.getPositions().size() - 1);
                        String eventTime = mongoPosition.getEventTime();// MongoDB 中最后一个节点的时间
                        if (eventTime.equals(currentTimeLastEventTime)) {
                            return mongoTemplateOne;
                        }
                    }
                }
            }


            Schedule schedule = scheduleClient.getScheduleById(scheduleObject.getScheduleId()).getData();
//            Person person = personVehicleRelClient.getDriverByVehicleId(vehicleInfo.getId()).getData();
            //填充DTO中的数据

            BeanUtil.copy(schedule, dto);
            BeanUtil.copy(scheduleObject, dto);
            if (vehicleInfo!=null){
                BeanUtil.copy(vehicleInfo, dto);
                dto.setEntityCategoryName(VehicleCategoryCache.getCategoryById(vehicleInfo.getEntityCategoryId()).getCategoryName());
            }
            BeanUtil.copy(personById, dto);
            BeanUtil.copy(attendance, dto);
            dto.setEntityId(attendance.getEntityId());
            dto.setEntityType(attendance.getEntityType());
            dto.setAttendanceId(attendance.getId());
            dto.setPersonPositionName(StationCache.getStationName(personById.getPersonPositionId()));



            // 填充图片上传坐标的地址（前端发过来的坐标）
            List<OmnicScheduleAttendanceImageDTO> scheduleAttendanceDetailVOS = BeanUtil.copy(attendanceDetails, OmnicScheduleAttendanceImageDTO.class);
            scheduleAttendanceDetailVOS.forEach(omnicScheduleAttendanceImageDTO -> {
                Coords coord = new Coords();
                coord.setLatitude(omnicScheduleAttendanceImageDTO.getLat());
                coord.setLongitude(omnicScheduleAttendanceImageDTO.getLng());
                BaiduMapReverseGeoCodingResult reverseGeoCoding = addressClient.getAddress(coord).getData();
                if (reverseGeoCoding != null || reverseGeoCoding.getResult() != null) {
                    try {
                        reverseGeoCoding = baiduMapUtils.getReverseGeoCoding(coord);
                        reverseGeoCoding.setBaiduCoords(coord);
                        addressClient.saveAddress(reverseGeoCoding);
                        omnicScheduleAttendanceImageDTO.setAddress(reverseGeoCoding.result.getAddressComponent().getStreet());
                    } catch (IOException e) {
                        logger.warn("地址转换失败：" + coord, e);
                    }
                }

            });
            dto.setScheduleAttendanceDetails(scheduleAttendanceDetailVOS);


            if (CollectionUtil.isNotEmpty(tracks)) {

//                OmnicPersonInfo copy = BeanUtil.copy(personById, OmnicPersonInfo.class);
//                List<OmnicPersonInfo> personInfos = new ArrayList<>();
//                personInfos.add(copy);
//                dto.setPersonList(personInfos);
                //先全部转换为坐标
                List<Coords> coords = new ArrayList<>();
                tracks.forEach(position -> {
                    Coords coord = new Coords();
                    coord.setLatitude(position.getLat());
                    coord.setLongitude(position.getLng());
                    coords.add(coord);
                });
                Coords lastCoords = null;
                dto.setPositions(tracks);
                // 填充首尾地址
                if (CollectionUtil.isNotEmpty(tracks)) {

                    //终点地址
                    TrackPositionDto.Position position = tracks.get(tracks.size() - 1);
                    String eventTime = position.getEventTime();
                    Date parse = DateUtil.parse(eventTime, BigDataHttpClient.bigDataTimeFormat);
                    dto.setLastGeneralTime(parse.getTime());
                    dto.setWorkEndPosition(position);

                    Coords coord = new Coords();
                    coord.setLatitude(position.getLat());
                    coord.setLongitude(position.getLng());
                    BaiduMapReverseGeoCodingResult reverseGeoCoding = addressClient.getAddress(coord).getData();
                    if (reverseGeoCoding == null || reverseGeoCoding.getResult() == null) {
                        reverseGeoCoding = baiduMapUtils.getReverseGeoCoding(coord);
                        reverseGeoCoding.setBaiduCoords(coord);
                        addressClient.saveAddress(reverseGeoCoding);
                    }
                    position.setAddress(reverseGeoCoding.result.getFormatted_address());


                    //起点地址
                    TrackPositionDto.Position firstPosition = tracks.get(0);
                    dto.setWorkBeginPosition(firstPosition);

                    Coords firstCoord = new Coords();
                    firstCoord.setLatitude(firstPosition.getLat());
                    firstCoord.setLongitude(firstPosition.getLng());
                    BaiduMapReverseGeoCodingResult firstReverseGeoCoding = addressClient.getAddress(firstCoord).getData();
                    if (firstReverseGeoCoding == null || firstReverseGeoCoding.getResult() == null) {
                        firstReverseGeoCoding = baiduMapUtils.getReverseGeoCoding(firstCoord);
                        firstReverseGeoCoding.setBaiduCoords(firstCoord);
                        addressClient.saveAddress(firstReverseGeoCoding);
                    }
                    firstPosition.setAddress(firstReverseGeoCoding.getResult().getFormatted_address());




                    // 填充所有点的地址
                    List<BaiduMapReverseGeoCodingResult> baiduMapReverseGeoCodingResultList = new ArrayList<>();
                    for (Coords coord1 : coords) {
                        if (lastCoords == null || getDistance(Double.parseDouble(coord1.getLongitude()), Double.parseDouble(coord1.getLatitude()), Double.parseDouble(lastCoords.getLongitude()), Double.parseDouble(lastCoords.getLatitude())) >= 5) {
                            BaiduMapReverseGeoCodingResult reverseGeoCoding1 = addressClient.getAddress(coord1).getData();

                            try {
                                if (reverseGeoCoding1 == null || reverseGeoCoding1.getResult() == null) {
                                    reverseGeoCoding1 = baiduMapUtils.getReverseGeoCoding(coord);
                                    reverseGeoCoding1.setBaiduCoords(coord);
                                    addressClient.saveAddress(reverseGeoCoding1);
                                }
                                lastCoords = coord;
                                baiduMapReverseGeoCodingResultList.add(reverseGeoCoding1);
                            } catch (IOException e) {
                                logger.warn("地址转换失败" + coord);
                            }

                        }
                    }
                    //生成途径地
                    String pathway = generalPathway(baiduMapReverseGeoCodingResultList);
                    logger.info("途径路线:" + pathway);
                    dto.setPathWay(pathway);// 途径
                    String path = null;


                    try {
                        // 生成百度静态图
                        InputStream lineBaiduStaticImage = baiduMapUtils.getLineBaiduStaticImage(coords);

                        byte[] data = new byte[lineBaiduStaticImage.available()];
                        lineBaiduStaticImage.read(data);
                        lineBaiduStaticImage.close();
                        String dataStr = Base64.encode(data);
                        String fileName = "mapLine_" + personById.getPersonName() + "_" + System.currentTimeMillis() + ".png";
                        logger.info("上传Oss--" + fileName);
                        path = ossClient.putBase64Stream(CommonConstant.BUCKET, fileName, dataStr).getData();
                        logger.info("上传Oss成功--" + path);
                        dto.setPersonLineImagePath(path);
                    } catch (Exception e) {
                        logger.error("生成静态图失败！", e);
                    }
                }
            }


            mongoTemplate.save(dto);
            logger.info("上传MongoDB成功--" + dto);
            return dto;

        } catch (Exception e) {
            logger.error("生成详情信息失败！", e);
            throw e;
        }

    }


    /**
     * 生成途径地，相邻的两个相同的地址会被合并。在直行通过十字路口或丁字路口的时候，可能会出现地址被定位到垂直路所在的
     * 路上去了，这个时候要舍弃一个坐标点，也就是说，当某个坐标点前后两个坐标一致的时候，这个坐标点会被舍弃。一致的两个
     * 坐标地址会被合并为一个
     *
     * @return
     */
    public String generalPathway(List<BaiduMapReverseGeoCodingResult> pathway) {
        LinkedList<String> linkedList = new LinkedList<>();


        String lastStreet = null;

        for (int i = 0; i < pathway.size(); i++) {
            String street = pathway.get(i).getResult().getAddressComponent().getStreet();
            if (StringUtil.isBlank(street)) {
                continue;
            }
            if (StringUtil.isBlank(lastStreet)) {
                linkedList.addLast(street);
                lastStreet = street;
            }
            //相邻的两个地址要被忽略掉
            if (lastStreet.equals(street)) {
                continue;
            }
            //解决十字路口（丁字路口）地址不准的问题
            if (i < pathway.size() - 1) {
                String nextStreet = pathway.get(i + 1).getResult().getAddressComponent().getStreet();
                if (nextStreet.equals(lastStreet)) {
                    continue;
                }
            }
            linkedList.addLast(street);
            lastStreet = street;
        }
        String join = StringUtil.join(linkedList, "-");
        return join;
    }


}
