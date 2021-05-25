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
package com.ai.apac.smartenv.workarea.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.facility.entity.AshcanInfo;
import com.ai.apac.smartenv.facility.feign.IAshcanClient;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.omnic.feign.ITrackClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.workarea.entity.*;
import com.ai.apac.smartenv.workarea.service.*;
import com.ai.apac.smartenv.workarea.vo.UserVO;
import com.ai.apac.smartenv.workarea.vo.WorkareaInfoVO;
import com.ai.apac.smartenv.workarea.mapper.WorkareaInfoMapper;
import com.ai.apac.smartenv.workarea.vo.WorkareaViewVO;
import com.ai.apac.smartenv.workarea.wrapper.WorkareaInfoWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 工作区域信息 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
//@AllArgsConstructor
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
public class WorkareaInfoServiceImpl extends BaseServiceImpl<WorkareaInfoMapper, WorkareaInfo> implements IWorkareaInfoService {

    @Autowired
    private IWorkareaNodeService workareaNodeService;
    @Autowired
    private IWorkareaRelService workareaRelService;
    @Autowired
    private IDeviceRelClient deviceRelClient;
    @Autowired
    private IDeviceClient deviceClient;
    @Autowired
    private IEventInfoClient eventInfoClient;
    @Autowired
    private IAshcanClient ashcanClient;

    @Autowired
    private IPersonVehicleRelClient personVehicleRelClient;
    @Autowired
    private BaiduMapUtils baiduMapUtils;
    @Autowired
    private CoordsTypeConvertUtil coordsTypeConvertUtil;
    @Lazy
    @Autowired
    private IWorkareaAsyncService workareaAsyncService;

    @Autowired
    private ITrackClient trackClient;

    @Autowired
    private IWorkareaPathwayService workareaPathwayService;



    @Override
    public IPage<WorkareaInfoVO> selectWorkareaInfoPage(IPage<WorkareaInfoVO> page, WorkareaInfoVO workareaInfo) {
        return page.setRecords(baseMapper.selectWorkareaInfoPage(page, workareaInfo));
    }



    @Override
    public boolean saveOrUpdateDetail(WorkareaDetail workareaDetail) throws ServiceException {
        boolean status = false;
        String optFlag = BigDataHttpClient.OptFlag.ADD;
        //分别存储对应对象信息
        try {
            WorkareaInfo workareaInfo = workareaDetail.getWorkareaInfo();
            workareaInfo.setRegionId(workareaInfo.getDivision());
            WorkareaNode[] workareaNodes = workareaDetail.getWorkareaNodes();
            List<WorkareaPathway> workareaPathways = workareaDetail.getWorkareaPathways();

            coordsTypeConvertUtil.fromWebConvert(Arrays.asList(workareaNodes));

            if (workareaInfo.getId() != null && workareaInfo.getId() != 0L) { // edit
                workareaInfo.setUpdateTime(new Date());
                this.updateById(workareaInfo);
                optFlag = BigDataHttpClient.OptFlag.EDIT;
            } else {  // add
                workareaInfo.setVehicleCount(0L);
                workareaInfo.setPersonCount(0L);
                this.save(workareaInfo);
            }

            JSONArray nodes = new JSONArray();
            // 每次先删除已有的nodes


            workareaNodeService.remove(new QueryWrapper<WorkareaNode>().eq("workarea_id", workareaInfo.getId()));
            workareaPathwayService.remove(new QueryWrapper<WorkareaPathway>().eq("workarea_id",workareaInfo.getId()));
            // 删除完再新增
            for (WorkareaNode workareaNode : workareaNodes) {
                workareaNode.setWorkareaId(workareaInfo.getId()); // 取基本信息表主键
                workareaNodeService.save(workareaNode);
                JSONObject node = new JSONObject();
                node.put("isDeleted", workareaNode.getIsDeleted());
                node.put("latitudinal", workareaNode.getLatitudinal());
                node.put("longitude", workareaNode.getLongitude());
                node.put("nodeId", workareaNode.getId()); // 取node信息表主键
                node.put("nodeSequence", workareaNode.getNodeSeq());
                node.put("status", workareaNode.getStatus());
                nodes.add(node);
            }

            // 将前端使用的导航途径点存入数据库便于编辑
            if (CollectionUtil.isNotEmpty(workareaPathways)){
                workareaPathways.forEach(workareaPathway -> workareaPathway.setWorkareaId(workareaInfo.getId()));
                workareaPathwayService.saveBatch(workareaPathways);
            }

            //调用大数据
            JSONObject param = new JSONObject();
            param.put("optFlag", optFlag);
            param.put("areaAddress", workareaInfo.getAreaAddress());
            param.put("areaId", workareaInfo.getId());// 取基本信息表主键
            param.put("areaName", workareaInfo.getAreaName());
            param.put("areaType", workareaInfo.getWorkAreaType());
            param.put("isDeleted", workareaInfo.getIsDeleted());
            param.put("regionId", workareaInfo.getRegionId());
            param.put("tenant_id", workareaInfo.getTenantId());
            param.put("nodes", nodes);
            BigDataHttpClient.postDataToBigData("/smartenv-api/sync/region", param.toString());

            status = true;


        } catch (Exception e) {
//            log.error(e.getMessage());
            log.error(e.getMessage(),e);
            throw new ServiceException("保存区域信息失败！");
        }

        return status;
    }

    @Override
    public boolean workAreaInfo2BigData(String tenantId) throws IOException {
        // 根据租户查询所有工作区域
        String optFlag = BigDataHttpClient.OptFlag.EDIT;
        List<WorkareaInfo> workareaInfoList = this.list(new QueryWrapper<WorkareaInfo>().eq("tenant_id", tenantId));
        if (workareaInfoList != null && workareaInfoList.size() > 0) {
            for (WorkareaInfo workareaInfo : workareaInfoList) {
                JSONArray nodes = new JSONArray();
                List<WorkareaNode> workareaNodes = workareaNodeService.list(new QueryWrapper<WorkareaNode>().eq("workarea_id", workareaInfo.getId()));
                for (WorkareaNode workareaNode : workareaNodes) {
                    workareaNode.setWorkareaId(workareaInfo.getId()); // 取基本信息表主键
                    JSONObject node = new JSONObject();
                    node.put("isDeleted", workareaNode.getIsDeleted());
                    node.put("latitudinal", workareaNode.getLatitudinal());
                    node.put("longitude", workareaNode.getLongitude());
                    node.put("nodeId", workareaNode.getId()); // 取node信息表主键
                    node.put("nodeSequence", workareaNode.getNodeSeq());
                    node.put("status", workareaNode.getStatus());
                    nodes.add(node);
                }

                //调用大数据
                JSONObject param = new JSONObject();
                param.put("optFlag", optFlag);
                param.put("areaAddress", workareaInfo.getAreaAddress());
                param.put("areaId", workareaInfo.getId());// 取基本信息表主键
                param.put("areaName", workareaInfo.getAreaName());
                param.put("areaType", workareaInfo.getWorkAreaType());
                param.put("isDeleted", workareaInfo.getIsDeleted());
                param.put("regionId", workareaInfo.getRegionId());
                param.put("tenant_id", workareaInfo.getTenantId());
                param.put("nodes", nodes);
                BigDataHttpClient.postDataToBigData("/smartenv-api/sync/region", param.toString());
            }
        }


        return true;
    }

    @Override
    public boolean removeAllInfo(List<Long> ids) throws ServiceException {
        boolean status = false;
        try {

            for (Long id : ids) {
                List<AshcanInfo> ashcanInfos = ashcanClient.listAshcanInfoByid(id).getData();
                if (ashcanInfos != null && ashcanInfos.size() > 0) {
                    throw new ServiceException("当前区域或路线正在垃圾桶规划中使用");
                }

                List<EventInfo> eventInfos = eventInfoClient.listEventInfoById(id).getData();
                if (eventInfos != null && eventInfos.size() > 0) {
                    for (EventInfo eventInfo : eventInfos) {
                        if (eventInfo.getStatus() != 3) {
                            throw new ServiceException("当前区域或路线正在事件中使用");
                        }
                    }

                }
                try {
                    workareaRelService.remove(new QueryWrapper<WorkareaRel>().eq("workarea_id", id));
                    workareaNodeService.remove(new QueryWrapper<WorkareaNode>().eq("workarea_id", id));
                } catch (Exception e) {
                    throw new ServiceException("删除失败!");
                }

            }
            this.deleteLogic(ids);

            status = true;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        return status;
    }

    @Override
    public List<WorkareaInfoVO> getWorkAreaInfoPages(WorkareaRel workareaRel, WorkareaInfo areaInfo) throws ServiceException {
        List<WorkareaInfoVO> workareaInfoList = new ArrayList<>();
        QueryWrapper<WorkareaRel> queryWrapper = new QueryWrapper<WorkareaRel>();
        if (workareaRel.getEntityId() != null && workareaRel.getEntityId() != 0L) {
            queryWrapper.eq("entity_id", workareaRel.getEntityId());
        }
        if (workareaRel.getEntityType() != null && workareaRel.getEntityType() != 0L) {
            queryWrapper.eq("entity_type", workareaRel.getEntityType());
        }
        if (workareaRel.getTenantId() != null) {
            queryWrapper.eq("tenant_id", workareaRel.getTenantId());
        }
        queryWrapper.orderByDesc("update_time");
        List<WorkareaRel> workareaRelInfos = null;
        if (workareaRel.getIsDeleted() != null && workareaRel.getIsDeleted() == 1) { // 查历史
            workareaRelInfos = workareaRelService.selectWorkareaRelHList(workareaRel);
        } else {
            workareaRelInfos = workareaRelService.list(queryWrapper);
        }
        if (workareaRelInfos != null && workareaRelInfos.size() > 0) {
            for (WorkareaRel workareaRelInfo : workareaRelInfos) {
                boolean flag = false;
                WorkareaInfo workareaInfo = this.getById(workareaRelInfo.getWorkareaId());
                if (workareaInfo == null) {
                    WorkareaInfo area = new WorkareaInfo();
                    area.setId(workareaRelInfo.getWorkareaId());
                    area.setIsDeleted(1);
                    workareaInfo = this.selectHWorkareaInfo(area);
                    if (workareaInfo == null || workareaInfo.getId() == null) {
                        throw new ServiceException("No data by id:" + workareaRelInfo.getWorkareaId());
                    }
                }
                workareaInfo.setUpdateTime(workareaRelInfo.getUpdateTime());
                WorkareaInfoVO workareaInfoVO = WorkareaInfoWrapper.build().entityVO(workareaInfo);
                workareaInfoVO.setRelId(workareaRelInfo.getId());
                if (validParams(areaInfo)) {
                    boolean tip = true; // 在过滤数据时避免错乱，如果tip=false表示当前数据已被过滤掉，下面的条件不用继续筛选
                    if (areaInfo.getDivision() != null && tip) {
                        if (workareaInfo.getDivision().equals(areaInfo.getDivision())) {
                            flag = true;
                        } else {
                            flag = false;
                            tip = false;
                        }
                    }
//					if(areaInfo.getIsDeleted() !=null && tip) {
//						if(workareaInfo.getIsDeleted().equals(areaInfo.getIsDeleted())) {
//							flag = true;
//						}else {
//							flag = false;
//							tip = false;
//						}
//					}
                    if (areaInfo.getWorkAreaType() != null && tip) {
                        if (workareaInfo.getWorkAreaType().equals(areaInfo.getWorkAreaType())) {
                            flag = true;
                        } else {
                            flag = false;
                            tip = false;
                        }
                    }

                    if (areaInfo.getAreaType() != null && tip) {
                        if (workareaInfo.getAreaType().equals(areaInfo.getAreaType())) {
                            flag = true;
                        } else {
                            flag = false;
                            tip = false;
                        }
                    }

                    if (areaInfo.getAreaAddress() != null && tip) {
                        if (workareaInfo.getAreaAddress().equals(areaInfo.getAreaAddress())) {
                            flag = true;
                        } else {
                            flag = false;
                            tip = false;
                        }
                    }

                    if (areaInfo.getAreaName() != null && tip) {
                        if (workareaInfo.getAreaName().equals(areaInfo.getAreaName()) || workareaInfo.getAreaName().contains(areaInfo.getAreaName())) {
                            flag = true;
                        } else {
                            flag = false;
                        }
                    }
                    if (flag) {
                        workareaInfoList.add(workareaInfoVO);
                    }
                } else { // 只根据实体类型查询
                    workareaInfoList.add(workareaInfoVO);
                }


            }
        }

        List<WorkareaInfoVO> results = new ArrayList<WorkareaInfoVO>(new HashSet<WorkareaInfoVO>(workareaInfoList));
        if (results != null && results.size() > 0) {
            for (WorkareaInfoVO result : results) {
                result.setNodes(coordsTypeConvertUtil.toWebConvert(workareaNodeService.list(new QueryWrapper<WorkareaNode>().eq("workarea_id", result.getId()))));
            }
        }
        return results;
    }

    @Override
    public WorkareaInfo selectHWorkareaInfo(WorkareaInfo workareaInfo) throws ServiceException {
        return baseMapper.selectHWorkareaInfo(workareaInfo.getIsDeleted(), workareaInfo.getId());
    }


    /**
     * 因为车辆路线变动而粗发的驾驶员的路线变动
     *
     * @param entityId
     * @param workAreaId
     * @param flag
     */
    @Override
    public void syncDriverWorkAreaRel(Long entityId, Long workAreaId, String flag) {
        List<WorkareaRel> workareaRelList = new ArrayList<WorkareaRel>();
        List<PersonVehicleRel> personVehicleRelList = personVehicleRelClient.getPersonVehicleRels(entityId).getData();
        if (personVehicleRelList.size() > 0) {
            personVehicleRelList.forEach(personVehicleRel -> {
                if (ObjectUtil.isNotEmpty(personVehicleRel.getPersonId())) {
                    //发现一个驾驶员，把他给解绑了
                    WorkareaRel workareaRel = new WorkareaRel();
                    workareaRel.setEntityId(personVehicleRel.getPersonId());
                    workareaRel.setEntityType(1l);
                    workareaRel.setWorkareaId(workAreaId);
                    if ("2".equals(flag)) {
                        //新增绑定
                        workareaRelList.add(workareaRel);
                    } else {
                        List<WorkareaRel> workareaRels = workareaRelService.list(Condition.getQueryWrapper(workareaRel));
                        //再次确认这人还在这儿磨洋工
                        if (workareaRels.size() > 0) {
                            workareaRelList.addAll(workareaRels);
                        }
                    }

                }
            });
        }
        bindOrUnbind(workareaRelList, AuthUtil.getUser());
    }

    @Override
    public void syncDriverWorkAreaRelAsync(Long entityId, Long workAreaId, String flag, BladeUser bladeUser) {
        List<WorkareaRel> workareaRelList = new ArrayList<WorkareaRel>();
        List<PersonVehicleRel> personVehicleRelList = personVehicleRelClient.getPersonVehicleRels(entityId).getData();
        if (personVehicleRelList.size() > 0) {
            personVehicleRelList.forEach(personVehicleRel -> {
                if (ObjectUtil.isNotEmpty(personVehicleRel.getPersonId())) {
                    //发现一个驾驶员，把他给解绑了
                    WorkareaRel workareaRel = new WorkareaRel();
                    workareaRel.setEntityId(personVehicleRel.getPersonId());
                    workareaRel.setEntityType(1l);
                    workareaRel.setWorkareaId(workAreaId);
                    if ("2".equals(flag)) {
                        //新增绑定
                        workareaRelList.add(workareaRel);
                    } else {
                        List<WorkareaRel> workareaRels = workareaRelService.list(Condition.getQueryWrapper(workareaRel));
                        //再次确认这人还在这儿磨洋工
                        if (workareaRels.size() > 0) {
                            workareaRelList.addAll(workareaRels);
                        }
                    }

                }
            });
        }
//    	bindOrUnbind(workareaRelList);
        if (!workareaRelList.isEmpty()) {
            workareaAsyncService.bindOrUnbindAsync(workareaRelList, bladeUser);
        }
    }


	@Override
    public boolean bindOrUnbind(List<WorkareaRel> workareaRelList, BladeUser bladeUser) throws ServiceException {
		boolean checkAsync = true;
    	if (bladeUser == null) {
    		bladeUser = AuthUtil.getUser();
		}
    	if (StringUtil.isBlank(bladeUser.getUserName())) {
    		checkAsync = false;// 给驾驶员绑定多个车辆的时候，短时间内多个异步任务，不校验处理中
		}
        if (workareaRelList != null && !workareaRelList.isEmpty()) {
			/*if (checkAsync) {
				for (WorkareaRel workareaRel : workareaRelList) {
					Long entityId = workareaRel.getEntityId();
					String entityType = String.valueOf(workareaRel.getEntityType());
					String asyncEntity = WorkareaCache.getAsyncEntity(entityId, entityType);
					if (asyncEntity != null) {
						if (WorkareaConstant.WorkareaRelEntityType.PERSON.equals(entityType)) {
							Person person = PersonCache.getPersonById(bladeUser.getTenantId(), entityId);
							throw new ServiceException(person.getPersonName() + ", 等等......人员规划处理中，请稍后重试");
						} else if (WorkareaConstant.WorkareaRelEntityType.VEHICLE.equals(entityType)) {
							VehicleInfo vehicleInfo = VehicleCache.getVehicleById(bladeUser.getTenantId(), entityId);
							throw new ServiceException(vehicleInfo.getPlateNumber() + ", 等等......车辆规划处理中，请稍后重试");
						}
					}
				}
				for (WorkareaRel workareaRel : workareaRelList) {
					WorkareaCache.putAsyncEntity(workareaRel.getEntityId(), String.valueOf(workareaRel.getEntityType()));
				}
			}*/
            workareaAsyncService.bindOrUnbindAsync(workareaRelList, bladeUser);
        }
        return true;
    }

    @Override
    public boolean bindWorkareas(List<String> workareaIds, WorkareaRel workareaRel) throws ServiceException {
        boolean status = false;
        if (workareaIds != null && workareaIds.size() > 0) {
            long entityType = workareaRel.getEntityType();
            for (String workareaId : workareaIds) {
                // 更新已绑定数量

                WorkareaInfo workareaInfo = this.getById(Long.valueOf(workareaId));
                // 绑定解绑时需要更新绑定数量
                if (workareaInfo == null) {
                    throw new ServiceException("No data by id:" + workareaId);
                }
                if (entityType == 1L) { // 人员
                    workareaInfo.setPersonCount((long) (1 + (workareaInfo.getPersonCount() == null ? 0 : workareaInfo.getPersonCount().intValue())));
                } else if (entityType == 2L) { // 车辆
                    workareaInfo.setVehicleCount((long) (1 + (workareaInfo.getVehicleCount() == null ? 0 : workareaInfo.getVehicleCount().intValue())));
                }
                workareaInfo.setUpdateTime(new Date());
                this.updateById(workareaInfo);
                WorkareaRel areaRel = new WorkareaRel();
                areaRel.setWorkareaId(Long.valueOf(workareaId));
                areaRel.setEntityId(workareaRel.getEntityId());
                areaRel.setEntityType(workareaRel.getEntityType());
                areaRel.setEntityCategoryId(workareaRel.getEntityCategoryId());
                workareaRelService.save(areaRel);


                //调用大数据
                JSONObject param = new JSONObject();
                List<String> deviceIdList = new ArrayList<>();
                R<List<DeviceRel>> deviceRels = deviceRelClient.getEntityRels(workareaRel.getEntityId(), workareaRel.getEntityType() == 1 ? 5 : workareaRel.getEntityType());
                if (deviceRels.getData() != null && !deviceRels.getData().isEmpty() && deviceRels.getData().get(0).getId() > 0) {
                    for (DeviceRel datum : deviceRels.getData()) {
                        String devicecode = deviceClient.getDeviceById(String.valueOf(datum.getDeviceId())).getData().getDeviceCode();
                        deviceIdList.add(devicecode);
                    }
                }
                if (deviceIdList.size() > 0) {
                    param.put("deviceId", deviceIdList.toArray());
                    param.put("areaId", workareaId);
                    param.put("optFlag", BigDataHttpClient.OptFlag.ADD);
                    try {
                        BigDataHttpClient.postDataToBigData("/smartenv-api/sync/deviceAreaRel", param.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                if (workareaRel.getEntityType() == 2L) {
                    //把这车上的驾驶员也绑了
                    syncDriverWorkAreaRel(workareaRel.getEntityId(), Long.parseLong(workareaId), "2");
                }
                status = true;
            }
        }
        return status;
    }

    @Override
    public boolean reBindWorkarea(List<String> ids, WorkareaRel workareaRel) throws ServiceException {
        boolean status = false;
        if (ids != null && ids.size() > 0) {
            for (String id : ids) {
                WorkareaInfo workareaInfo = this.getById(Long.valueOf(id));
                if (workareaInfo.getIsDeleted() != null && workareaInfo.getIsDeleted() == 1) { // 解绑
                    List<String> workareaIds = new ArrayList<>();
                    ids.add(String.valueOf(workareaInfo.getId()));
                    unbindWorkareas(workareaIds, workareaRel);
                } else {
                    List<String> workareaIds = new ArrayList<>();
                    ids.add(String.valueOf(workareaInfo.getId()));
                    bindWorkareas(workareaIds, workareaRel);
                }
                status = true;
            }
        }
        return status;
    }

    @Override
    public boolean unbindWorkareas(List<String> ids, WorkareaRel workareaRel) throws ServiceException {
        boolean status = false;
        if (ids != null && ids.size() > 0) {
            long entityType = workareaRel.getEntityType();
            for (String workareaId : ids) {
                // 更新已绑定数量

                WorkareaInfo workareaInfo = this.getById(Long.valueOf(workareaId));
                // 绑定解绑时需要更新绑定数量
                if (workareaInfo == null) {
                    throw new ServiceException("No data by id:" + workareaId);
                }
                if (entityType == 1L) { // 人员
                    int personCount = (workareaInfo.getPersonCount() == null ? 0 : workareaInfo.getPersonCount().intValue()) - 1;
                    workareaInfo.setPersonCount((long) (Math.max(personCount, 0)));
                } else if (entityType == 2L) { // 车辆
                    int vehicle = workareaInfo.getVehicleCount() == null ? 0 : workareaInfo.getVehicleCount().intValue() - 1;
                    workareaInfo.setVehicleCount((long) (Math.max(vehicle, 0)));
                }
                workareaInfo.setUpdateTime(new Date());
                this.updateById(workareaInfo);

                //调用大数据
                JSONObject param = new JSONObject();
                List<String> deviceIdList = new ArrayList<>();
                R<List<DeviceRel>> deviceRels = deviceRelClient.getEntityRels(workareaRel.getEntityId(), workareaRel.getEntityType() == 1 ? 5 : workareaRel.getEntityType());
                if (deviceRels.getData() != null && !deviceRels.getData().isEmpty() && deviceRels.getData().get(0).getId() > 0) {
                    for (DeviceRel datum : deviceRels.getData()) {
                        String devicecode = deviceClient.getDeviceById(String.valueOf(datum.getDeviceId())).getData().getDeviceCode();
                        deviceIdList.add(devicecode);
                    }
                }
                if (deviceIdList.size() > 0) {
                    param.put("deviceId", deviceIdList.toArray());
                    param.put("areaId", workareaId);
                    param.put("optFlag", BigDataHttpClient.OptFlag.REMOVE);
                    try {
                        BigDataHttpClient.postDataToBigData("/smartenv-api/sync/deviceAreaRel", param.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
            workareaRelService.removeById(workareaRel.getId());
            if (workareaRel.getEntityType() == 2L) {
                //把这车上的驾驶员也解绑了
                syncDriverWorkAreaRel(workareaRel.getEntityId(), workareaRel.getWorkareaId(), "1");
            }

            status = true;
        }
        return status;
    }

    @Override
    public List<WorkareaViewVO> getAreaListByPersonId(String entityId, String tenantId) throws ServiceException {
        List<WorkareaViewVO> workareaViewVOS = new ArrayList<>();
        List<WorkareaRel> workareaRelList = workareaRelService.list(new QueryWrapper<WorkareaRel>().eq("entity_id", entityId));
        if (workareaRelList != null && workareaRelList.size() > 0) {
            for (WorkareaRel workareaRel : workareaRelList) {
                WorkareaViewVO workareaViewVO = new WorkareaViewVO();
                WorkareaInfo workareaInfo = this.getById(workareaRel.getWorkareaId());
                if (workareaInfo == null) {
                    throw new ServiceException("no data by id:" + workareaRel.getWorkareaId());
                }
                workareaViewVO.setWorkareaId(workareaInfo.getId());
                workareaViewVO.setWorkareaName(workareaInfo.getAreaName());
                workareaViewVO.setAreaType(workareaInfo.getAreaType());
                workareaViewVOS.add(workareaViewVO);
            }
        }
        return workareaViewVOS;
    }

    private boolean validParams(WorkareaInfo areaInfo) throws ServiceException {
        boolean result = true;
        if (areaInfo.getDivision() == null && areaInfo.getAreaName() == null && areaInfo.getWorkAreaType() == null
                && areaInfo.getId() == null && areaInfo.getRegionId() == null && areaInfo.getAreaType() == null
                && areaInfo.getAreaAddress() == null && areaInfo.getArea() == null && areaInfo.getLength() == null
                && areaInfo.getWidth() == null) {
            result = false;
        }
        return result;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public Boolean unbindWorkarea(Long entityId, Long entityType) {
        List<WorkareaRel> workareaRelList = workareaRelService.list(new QueryWrapper<WorkareaRel>().eq("entity_id", entityId).eq("entity_type", entityType));
        if (workareaRelList != null && !workareaRelList.isEmpty()) {
            workareaRelList.forEach(workareaRel -> {
                List<String> ids = new ArrayList<>();
                ids.add(String.valueOf(workareaRel.getWorkareaId()));
                unbindWorkareas(ids, workareaRel);
            });
        }
        return true;
    }

    @Override
    public Boolean syncDriverWorkArea(Long entityId,Long personId,String flag, BladeUser bladeUser) {
        if ("1".equals(flag)) {
            //驾驶员解绑路线
            List<WorkareaRel> workareaRels = workareaRelService.list(new QueryWrapper<WorkareaRel>().eq("entity_id", personId).eq("entity_type", 1));
            // 当前车辆路线
            List<WorkareaRel> veihcleWorkareas = workareaRelService.list(new QueryWrapper<WorkareaRel>().eq("entity_id", entityId).eq("entity_type", 2));
            HashMap<Long, Integer> veihcleWorkareaMap = new HashMap<>();
            if (veihcleWorkareas != null) {
            	for (WorkareaRel veihcleWorkarea : veihcleWorkareas) {
            		veihcleWorkareaMap.put(veihcleWorkarea.getWorkareaId(), 0);
            	}
			}
            // 只解绑当前车辆的路线
            if (workareaRels != null) {
            	Iterator<WorkareaRel> iterator = workareaRels.iterator();
            	while (iterator.hasNext()) {
            		WorkareaRel next = iterator.next();
            		Long workareaId = next.getWorkareaId();
            		if (!veihcleWorkareaMap.containsKey(workareaId)) {
            			continue;
            		}
            		if (veihcleWorkareaMap.get(workareaId) > 0) {
            			continue;
            		}
            		veihcleWorkareaMap.put(workareaId, 1);
            		iterator.remove();
            	}
            	this.bindOrUnbind(workareaRels, bladeUser);
			}
        } else {
            //驾驶员绑定路线
            List<WorkareaRel> workareaRels_ = new ArrayList<WorkareaRel>();
            List<WorkareaRel> workareaRels = workareaRelService.list(new QueryWrapper<WorkareaRel>().eq("entity_id", entityId).eq("entity_type", 2));
            workareaRels.forEach(workareaRel -> {
                WorkareaRel workareaRel1 = new WorkareaRel();
                BeanUtil.copy(workareaRel, workareaRel1);
                workareaRel1.setEntityId(personId);
                workareaRel1.setEntityType(1l);
                workareaRel1.setId(null);
                workareaRels_.add(workareaRel1);
            });
            this.bindOrUnbind(workareaRels_, bladeUser);
        }
        return true;
    }

    @Override
    public List<UserVO> eventPerson(String workareaId) {
        List<UserVO> userList = new ArrayList<>();
        WorkareaInfo workareaInfo = this.getById(workareaId);
        if (ObjectUtil.isNotEmpty(workareaInfo) && ObjectUtil.isNotEmpty(workareaInfo.getAreaHead())) {
            Person user = PersonCache.getPersonById(null, workareaInfo.getAreaHead());
            if (user != null && user.getId() != null) {
                UserVO userVO = new UserVO();
                userVO.setDeptId(user.getPersonDeptId());
                userVO.setName(user.getPersonName());
                userVO.setId(user.getId());
                userVO.setJobNumber(user.getJobNumber());
                userList.add(userVO);
            }
        }
        return userList;
    }


    /**
     * 根据历史轨迹生成
     * @param workareaInfo
     * @param entityType
     * @param entityId
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    @Override
    @Async
    public Boolean addWorkareaInfoByTrack(WorkareaInfo workareaInfo, Long entityType, Long entityId, Long startTime, Long endTime) throws Exception {
        boolean status=false;
        try {
            //查询实体对应的历史轨迹设备
            List<DeviceInfo> deviceInfo = null;
            if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(entityType)) {
                deviceInfo = deviceClient.getForTrack(entityId, CommonConstant.ENTITY_TYPE.VEHICLE, VehicleConstant.VEHICLE_POSITION_DEVICE_TYPE, startTime, endTime).getData();
            } else if (CommonConstant.ENTITY_TYPE.PERSON.equals(entityType)) {
                deviceInfo = deviceClient.getForTrack(entityId, CommonConstant.ENTITY_TYPE.PERSON, VehicleConstant.PERSON_POSITION_DEVICE_TYPE, startTime, endTime).getData();

            } else {
                throw new ServiceException("无法查到匹配的设备信息");
            }
            if (CollectionUtil.isEmpty(deviceInfo)) {
                throw new ServiceException("无法查到匹配的设备信息");
            }
            //查询历史轨迹，
            TrackPositionDto trackPositionDto = trackClient.getBigdataTrack(entityId, entityType.longValue(), startTime, endTime).getData();
            if (trackPositionDto == null) {
                throw new ServiceException("无法查询到有效的历史轨迹");
            }
            List<TrackPositionDto.Position> tracks = trackPositionDto.getTracks();
            if (CollectionUtil.isEmpty(tracks)){
                throw new ServiceException("无法查询到有效的历史轨迹");
            }
            // 转为 coordList  便于进行筛点
            List<Coords> coordList = tracks.stream().map(track -> {
                Coords coords = new Coords();
                coords.setLatitude(track.getLat());
                coords.setLongitude(track.getLng());
                return coords;
            }).collect(Collectors.toList());

            //进行两次筛选，筛选后的结果就是规划区域。第一次筛选规则为进行前后距离判断，如果距离小于15米，直接筛选掉
            //第二次筛选规则为角度判断，如果角度大于173度，那么看起来就像是一条直线。也直接筛选掉
            List<Coords> result = BaiduMapUtils.filterLineByDistance(coordList, 50);
//            result = BaiduMapUtils.filterLineByMaxAngleByNear(result, 180);
            //得到的是百度的坐标系。需要转为GC02 存表
            result=coordsTypeConvertUtil.coordsConvert(BaiduMapUtils.CoordsSystem.BD09LL, BaiduMapUtils.CoordsSystem.GC02,result);


            AtomicLong index= new AtomicLong(1L);
            List<WorkareaNode> nodeList = result.stream().map((coords) -> {
                WorkareaNode node = new WorkareaNode();
                node.setLatitudinal(coords.getLatitude());
                node.setLongitude(coords.getLongitude());
                node.setNodeSeq(index.getAndIncrement());
                return node;
            }).collect(Collectors.toList());
            //保存
            workareaInfo.setDivision(workareaInfo.getRegionId());
            workareaInfo.setVehicleCount(0L);
            workareaInfo.setPersonCount(0L);
            this.save(workareaInfo);


            JSONArray nodes = new JSONArray();
            for (WorkareaNode workareaNode : nodeList) {
                workareaNode.setWorkareaId(workareaInfo.getId()); // 取基本信息表主键
                workareaNode.setTenantId(workareaInfo.getTenantId());
                workareaNodeService.save(workareaNode);
                JSONObject node = new JSONObject();
                node.put("isDeleted", workareaNode.getIsDeleted());
                node.put("latitudinal", workareaNode.getLatitudinal());
                node.put("longitude", workareaNode.getLongitude());
                node.put("nodeId", workareaNode.getId()); // 取node信息表主键
                node.put("nodeSequence", workareaNode.getNodeSeq());
                node.put("status", workareaNode.getStatus());
                nodes.add(node);
            }
            //调用大数据
            JSONObject param = new JSONObject();
            param.put("optFlag", BigDataHttpClient.OptFlag.ADD);
            param.put("areaAddress", workareaInfo.getAreaAddress());
            param.put("areaId", workareaInfo.getId());// 取基本信息表主键
            param.put("areaName", workareaInfo.getAreaName());
            param.put("areaType", workareaInfo.getWorkAreaType());
            param.put("isDeleted", workareaInfo.getIsDeleted());
            param.put("regionId", workareaInfo.getRegionId());
            param.put("tenant_id", workareaInfo.getTenantId());
            param.put("nodes", nodes);
            BigDataHttpClient.postDataToBigData("/smartenv-api/sync/region", param.toString());
             status = true;
        } catch (ServiceException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw new ServiceException("保存区域信息失败！");
        } catch (ParseException e) {
            log.warn(e.getMessage());
            throw new ServiceException("保存区域信息失败！");
        } catch (JSONException e) {
            log.warn(e.getMessage());
            throw new ServiceException("保存区域信息失败！");
        }
        return status;


    }

    @Override
    public Boolean batchChangeRegion4WorkArea(List<Long> areaIds, Long targetRegionId,String regionManager) {
        for (Long areaId : areaIds) {
            WorkareaInfo workareaInfo = this.getById(areaId);
            workareaInfo.setRegionId(targetRegionId);
            workareaInfo.setDivision(targetRegionId);
            workareaInfo.setAreaHead(Long.valueOf(regionManager));
            workareaInfo.setUpdateTime(new Date());
            this.updateById(workareaInfo);
        }
        return true;
    }
}
