package com.ai.apac.smartenv.event.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.event.dto.PublicEventInfoDTO;
import com.ai.apac.smartenv.event.entity.*;
import com.ai.apac.smartenv.event.mapper.EventMediumMapper;
import com.ai.apac.smartenv.event.mapper.PublicEventInfoMapper;
import com.ai.apac.smartenv.event.service.*;
import com.ai.apac.smartenv.event.vo.EventAllInfoVO;
import com.ai.apac.smartenv.event.vo.EventMediumVO;
import com.ai.apac.smartenv.event.vo.PublicEventInfoVO;
import com.ai.apac.smartenv.event.wrapper.EventMediumWrapper;
import com.ai.apac.smartenv.event.wrapper.PublicEventInfoWrapper;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/12/17 3:43 下午
 **/
@Service
@AllArgsConstructor
public class PublicEventInfoServiceImpl extends BaseServiceImpl<PublicEventInfoMapper, PublicEventInfo> implements IPublicEventInfoService {

    private EventMediumMapper eventMediumMapper;
    private IEventMediumService eventMediumService;
    private IOssClient ossClient;

    private IProjectClient projectClient;

    private IEventInfoService eventInfoService;

    private IPersonUserRelClient personUserRelClient;

    private IUserClient userClient;

    private ISysClient sysClient;
    private IPersonClient personClient;

    private BaiduMapUtils baiduMapUtils;

    private IEventAssignedHistoryService historyService;

    private IEventInfoKpiRelService eventInfoKpiRelService;


    @Autowired
    private IDataChangeEventClient dataChangeEventClient;



    /**
     * 基本业务校验
     *
     * @param publicEventInfoDTO
     */
    private void basicValid(PublicEventInfoDTO publicEventInfoDTO) {

    }

    /**
     * 保存事件信息
     *
     * @param publicEventInfoDTO
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean savePublicEventInfo(PublicEventInfoDTO publicEventInfoDTO) {
        basicValid(publicEventInfoDTO);
        PublicEventInfo publicEventInfo = BeanUtil.copy(publicEventInfoDTO, PublicEventInfo.class);
        BladeUser bladeUser = AuthUtil.getUser();
        if (bladeUser != null && bladeUser.getUserId() != null) {
            publicEventInfo.setCreateDept(Long.valueOf(bladeUser.getDeptId()));
            publicEventInfo.setCreateUser(bladeUser.getUserId());
            publicEventInfo.setUpdateUser(bladeUser.getUserId());
        }
        publicEventInfo.setCreateTime(new Date());
        publicEventInfo.setUpdateTime(new Date());
        publicEventInfo.setIsDeleted(0);
        publicEventInfo.setTenantId("000000");
        //需要做相应的处理来加上租户，实在加不上的，租户为空

        try {
            Coords coords = new Coords();
            coords.setLatitude(publicEventInfoDTO.getLatitudinal());
            coords.setLongitude(publicEventInfoDTO.getLongitude());
            BaiduMapReverseGeoCodingResult reverseGeoCoding = BaiduMapUtils.getReverseGeoCoding(coords, BaiduMapUtils.CoordsSystem.GC02);
            Integer adcode = reverseGeoCoding.getResult().getAddressComponent().getAdcode();
            publicEventInfo.setAdcode(adcode.toString());
            List<Project> projectList = null;
            // 通过adcode 获取到所属 的租户，租户可能会管理多级，所以按照三级来查询，区县-> 地级市—> 省 的级别来查询。
            // 上面的adcode 为6位数的区县代码。如果要查询市，将最后两位置为0。如果要查询省，将最后四位置为0
            R<List<Project>> districtProject = projectClient.getProjectByAdcode(adcode.longValue());
            if (districtProject != null && districtProject.getData() != null) {
                projectList = districtProject.getData();
            }
            Long cityCode = Long.valueOf(adcode.toString().substring(0, 4).concat("00"));
            if (CollectionUtil.isEmpty(projectList)) {
                R<List<Project>> cityProject = projectClient.getProjectByAdcode(cityCode);
                projectList = cityProject.getData();
            }
            Long provinceCode = Long.valueOf(adcode.toString().substring(0, 2).concat("0000"));
            if (CollectionUtil.isEmpty(projectList)) {
                R<List<Project>> provinceProject = projectClient.getProjectByAdcode(provinceCode);
                projectList = provinceProject.getData();
            }
            Region addressRegion = null;
            Project project = null;

            for (Project pro : projectList) {
                String projectCode = pro.getProjectCode();
                Region regionByAddress = eventInfoService.getRegionByAddress(publicEventInfoDTO.getLatitudinal(), publicEventInfoDTO.getLongitude(), projectCode);
                if (regionByAddress != null) {
                    addressRegion = regionByAddress;
                    project = pro;
                    publicEventInfo.setTenantId(project.getProjectCode());
                    publicEventInfo.setBelongArea(addressRegion.getId());
                    publicEventInfo.setBelongAreaName(addressRegion.getRegionName());
                    break;
                }
            }
        } catch (Exception e) {
            log.error("获取地址失败");
        }

        publicEventInfo.setStatus(EventConstant.PublicEventStatus.HANDLE_0);
        baseMapper.insert(publicEventInfo);

        if (CollUtil.isNotEmpty(publicEventInfoDTO.getEventMediumList())) {
            publicEventInfoDTO.getEventMediumList().stream().forEach(eventMediumDTO -> {
                EventMedium eventMedium = BeanUtil.copy(eventMediumDTO, EventMedium.class);
                eventMedium.setEventInfoId(publicEventInfo.getId());
                eventMedium.setMediumDetailType(EventConstant.MediumDetailType.PRE_CHECK);// 整改前
                eventMedium.setIsDeleted(0);
                eventMediumMapper.insert(eventMedium);
            });
        }
        return true;
    }


    @Override
    public PublicEventInfoVO getEventDetail(Long eventId) {

        PublicEventInfo publicEventInfo = new PublicEventInfo();
        publicEventInfo.setId(eventId);
        QueryWrapper<PublicEventInfo> queryWrapper = new QueryWrapper<>(publicEventInfo);

        PublicEventInfo one = getOne(queryWrapper);
        PublicEventInfoVO publicEventInfoVO = PublicEventInfoWrapper.build().entityVO(one);
//        EventMedium eventMediumQueryEntity = new EventMedium();
//        eventMediumQueryEntity.setEventInfoId(eventId);
        QueryWrapper<EventMedium> mediumQuery = new QueryWrapper<>();
        mediumQuery.eq("event_info_id", eventId);
        List<EventMedium> eventMedia = eventMediumService.list(mediumQuery);

        List<EventMediumVO> eventMediaVOList = eventMedia.stream().map(eventMedium -> {
            EventMediumVO copy = EventMediumWrapper.build().entityVO(eventMedium);
            try {
                R<String> objectLink = ossClient.getObjectLink(CommonConstant.BUCKET, eventMedium.getMediumUrl());
                if (objectLink != null) {
                    copy.setMediumUrl(objectLink.getData());
                }
            } catch (Exception e) {
                return null;
            }
            return copy;
        }).filter(eventMediumVO -> eventMediumVO != null).collect(Collectors.toList());
        Map<Integer, List<EventMediumVO>> eventMediaMap = eventMediaVOList.stream().collect(Collectors.groupingBy(EventMedium::getMediumType));
        publicEventInfoVO.setPreEventMediumList(eventMediaMap.get(EventConstant.MediumDetailType.PRE_CHECK));
        publicEventInfoVO.setAfterEventMediumList(eventMediaMap.get(EventConstant.MediumDetailType.AFTER_CHECK));
        return publicEventInfoVO;
    }


    @Override
    public void confirmPublicEvent(EventAllInfoVO eventInfoVO, Integer coordsType) {
        BladeUser user = AuthUtil.getUser();

        EventInfo preEventInfo = eventInfoVO.getEventInfo();
        Long publicEventId = eventInfoVO.getEventInfo().getId();
        EventInfo eventInfo = new EventInfo();
        PublicEventInfo publicEventInfo = getById(publicEventId);
        BeanUtil.copy(publicEventInfo, eventInfo);
        eventInfo.setId(null);
        eventInfo.setTenantId(user.getTenantId());
        eventInfo.setStatus(EventConstant.Event_Status.HANDLE_1);
        PublicEventInfo update=new PublicEventInfo();


        PersonUserRel reportPersonUserRel = personUserRelClient.getRelByUserId(user.getUserId()).getData();
        if (reportPersonUserRel != null && reportPersonUserRel.getId() != null) {
            Person personInfo = PersonCache.getPersonById(null, Long.valueOf(reportPersonUserRel.getPersonId()));
            eventInfo.setReportPersonId(reportPersonUserRel.getPersonId());
            eventInfo.setReportPersonName(personInfo.getPersonName());
        }

        if (StringUtil.isNotBlank(preEventInfo.getHandlePersonId()) ) {
            Person personInfo = PersonCache.getPersonById(null, Long.valueOf(Long.valueOf(preEventInfo.getHandlePersonId())));
            eventInfo.setHandlePersonId(preEventInfo.getHandlePersonId());
            eventInfo.setHandlePersonName(personInfo.getPersonName());

            update.setHandlePersonId(preEventInfo.getHandlePersonId());
            update.setHandlePersonName(personInfo.getPersonName());
//            eventInfo.setExt1(personInfo.getId().toString());
        }


        if (coordsType != null && !BaiduMapUtils.CoordsSystem.GC02.equals(coordsType) && publicEventInfo.getLatitudinal() != null && publicEventInfo.getLongitude() != null) {
            List<Coords> coordsList = new ArrayList<>();
            Coords coords = new Coords();
            coords.setLatitude(publicEventInfo.getLatitudinal());
            coords.setLongitude(publicEventInfo.getLongitude());
            coordsList.add(coords);
            if (BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType).equals(BaiduMapUtils.CoordsSystem.BD09LL)) {
                List<Coords> resultList = null;
                resultList = baiduMapUtils.baiduMapllToGC02All(coordsList);
                if (CollectionUtil.isNotEmpty(resultList)) {
                    Coords coords1 = resultList.get(0);
                    eventInfo.setLongitude(coords1.getLongitude());
                    eventInfo.setLatitudinal(coords1.getLatitude());
                }
            }
        }

        eventInfoService.save(eventInfo);



        update.setId(publicEventId);
        update.setStatus(EventConstant.PublicEventStatus.HANDLE_1);
        update.setEventId(eventInfo.getId());
        updateById(update);

        EventMedium mediumEntity = new EventMedium();
        mediumEntity.setEventInfoId(publicEventId);
        QueryWrapper<EventMedium> mediumWrapper = new QueryWrapper<>(mediumEntity);
        List<EventMedium> list = eventMediumService.list(mediumWrapper);
        list.forEach(eventMedium -> {
            eventMedium.setId(null);
            eventMedium.setEventInfoId(eventInfo.getId());
            eventMediumService.save(eventMedium);
        });
        //保存历史指派记录
        EventAssignedHistory history = new EventAssignedHistory();
        if (StringUtils.isNotBlank(eventInfo.getHandlePersonId())) {
            history.setAssignedPersonId(eventInfo.getHandlePersonId());
            history.setAssignedPersonName(eventInfo.getHandlePersonName());
        } else {
            if (StringUtils.isNotBlank(eventInfo.getExt1())) {
                history.setAssignedPersonId(eventInfo.getExt1());
                history.setAssignedPersonName(PersonCache.getPersonById(null, Long.valueOf(eventInfo.getExt1())).getPersonName());
            }

        }


        history.setEventInfoId(eventInfo.getId());
//			history.setHandleAdvice(eventInfoVO.getEventInfo().getHandleAdvice());
        history.setType(EventConstant.Type.ASSIGN); //1-指派，2-检查
        historyService.save(history);


        List<EventInfoKpiRel> eventInfoKpiRelList = eventInfoVO.getEventInfoKpiRelList();

        if (CollectionUtil.isNotEmpty(eventInfoKpiRelList)) {
            eventInfoKpiRelList.forEach(eventInfoKpiRel -> {
                eventInfoKpiRel.setEventInfoId(eventInfoVO.getEventInfo().getId());
                eventInfoKpiRel.setTenantId(eventInfoVO.getEventInfo().getTenantId());
            });
            eventInfoKpiRelService.saveBatch(eventInfoKpiRelList);
        }

        List<Long> personIdList = new ArrayList<>();


        List<String> userIdList = new ArrayList<String>();
        if (StringUtils.isNotBlank(eventInfoVO.getEventInfo().getHandlePersonId())) {

            personIdList.add(Long.valueOf(eventInfoVO.getEventInfo().getHandlePersonId()));
        }
        if (eventInfoVO.getEventInfo().getReportPersonId() != null) {
            if (reportPersonUserRel != null && reportPersonUserRel.getUserId() != null) {
                userIdList.add(reportPersonUserRel.getUserId().toString());
            }
            personIdList.add(Long.valueOf(eventInfoVO.getEventInfo().getReportPersonId()));
        }
        if (StringUtils.isNotBlank(eventInfoVO.getEventInfo().getExt1())) {//带上租户管理员
            PersonUserRel adminUserRel = PersonUserRelCache.getRelByPersonId(Long.valueOf(eventInfoVO.getEventInfo().getExt1()));
            if (adminUserRel != null && adminUserRel.getUserId() != null) {
                userIdList.add(adminUserRel.getUserId().toString());
            }
            personIdList.add(Long.valueOf(eventInfoVO.getEventInfo().getExt1()));
        }
        String userIds = ArrayUtil.toString(userIdList).replace("[", "").replaceAll("]", "");

        //把事件message保存mongo
        eventInfoService.eventMessage2Mongo(eventInfo);
        String tenantId = AuthUtil.getTenantId();
        ThreadUtil.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                // 发送页面通知
                eventInfoService.sendNotice(eventInfo, userIds);
            }
        }));

        ThreadUtil.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                //发送微信公众号消息
                eventInfoService.sendWechatMessage(personIdList, eventInfo);
            }
        }));

        ThreadUtil.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                //发送数据库变更通知消息
                BaseDbEventDTO dbeventDto = new BaseDbEventDTO();
                dbeventDto.setTenantId(tenantId);
                dbeventDto.setEventType(DbEventConstant.EventType.INSPECT_EVENT);
                dbeventDto.setEventObject(eventInfo.getId());
                dataChangeEventClient.doDbEvent(dbeventDto);
            }
        }));
    }

}
