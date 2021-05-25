package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationConditionDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
public class PolymerizationListTask extends BaseTask<PolymerizationVO> implements Runnable {


    public PolymerizationListTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    protected R<PolymerizationVO> execute() {
        // 查询人员/车辆/中转站/垃圾桶/事件。返回给前端
        PolymerizationConditionDTO condition = (PolymerizationConditionDTO) validParams().get("conditionDTO");
        List<PolymerizationDTO> allData = new ArrayList<>();
        ExecutorService executorService = getBaseService().getTaskExecutor();
        String tenantId = getTenantId();

        Future<List<PolymerizationDTO>> vehiclePolymerization = null;
        try {
            vehiclePolymerization = executorService.submit(() -> getPolymerizationService().getVehiclePolymerization(condition, tenantId));
        } catch (Exception e) {
            log.error("初始化车辆信息异常:{}", e.getMessage());
        }
        Future<List<PolymerizationDTO>> personPolymerization = null;
        try {
            personPolymerization = executorService.submit(() -> getPolymerizationService().getPersonPolymerization(condition, tenantId));
        } catch (Exception e) {
            log.error("初始化人员信息异常:{}", e.getMessage());
        }
        Future<List<PolymerizationDTO>> eventPolymerization = null;
        try {
            eventPolymerization = executorService.submit(() -> getPolymerizationService().getEventPolymerization(condition, tenantId));
        } catch (Exception e) {
            log.error("初始化事件信息异常:{}", e.getMessage());
        }
        Future<List<PolymerizationDTO>> ashcanPolymerization = null;
        try {
            ashcanPolymerization = executorService.submit(() -> getPolymerizationService().getAshcanPolymerization(condition, tenantId));
        } catch (Exception e) {
            log.error("初始化垃圾桶信息异常:{}", e.getMessage());
        }
        Future<List<PolymerizationDTO>> transferStationPolymerization = null;
        try {
            transferStationPolymerization = executorService.submit(() -> getPolymerizationService().getTransferStationPolymerization(condition, tenantId));
        } catch (Exception e) {
            log.error("初始化中转站信息异常:{}", e.getMessage());
        }

        Future<List<PolymerizationDTO>> toiletPolymerization = null;
        try {
            toiletPolymerization = executorService.submit(() -> getPolymerizationService().getToiletPolymerization(condition, tenantId));
        } catch (Exception e) {
            log.error("初始化公厕信息异常:{}", e.getMessage());
        }
        try {
            if (vehiclePolymerization != null && vehiclePolymerization.get() != null) {
                List<PolymerizationDTO> vehicleList = vehiclePolymerization.get();
                allData.addAll(vehicleList);
//                for (PolymerizationDTO vehicle : vehicleList) {
//                    try {
//                        List<String> entityIdList = new ArrayList<>();
//                        entityIdList.add(vehicle.getObjID());
//                        String sessionId = getWebsocketTask().getSessionId();
//
//                        if (sessionId != null) {
//                            //将当前车辆的监控任务放入redis
//                            EntityTaskDto entityTask = BeanUtil.copy(getWebsocketTask(), EntityTaskDto.class);
//                            entityTask.setEntityIds(entityIdList);
//                            entityTask.setEntityType(CommonConstant.ENTITY_TYPE.VEHICLE);
//                            getWebSocketTaskService().createEntityTask(entityTask);
//                        }
//                    } catch (Exception e) {
//                        log.error("没有符合条件的车辆:" + vehicle, e);
//                    }
//                }
            }

        } catch (Exception e) {
            log.warn("获取车辆信息失败", e);
        }

        try {
            if (personPolymerization != null && personPolymerization.get() != null) {
                List<PolymerizationDTO> personList = personPolymerization.get();
                allData.addAll(personList);
//
//                for (PolymerizationDTO person : personList) {
////                PersonMonitorInfoVO personMonitorInfoVO = null;
//
//                    try {
//                        List<String> entityIdList = new ArrayList<>();
//                        entityIdList.add(person.getObjID());
//                        //将当前人员的监控任务放入redis
//                        String sessionId = getWebsocketTask().getSessionId();
//                        if (sessionId != null) {
//                            EntityTaskDto entityTask = BeanUtil.copy(getWebsocketTask(), EntityTaskDto.class);
//                            entityTask.setEntityIds(entityIdList);
//                            entityTask.setEntityType(CommonConstant.ENTITY_TYPE.PERSON);
//                            getWebSocketTaskService().createEntityTask(entityTask);
//                        }
//
//
//                    } catch (Exception e) {// 此处异常要在循环内处理。否则会导致一个人员查不到。所有的人员的位置都没有了
//                        log.warn("获取人员位置失败：" + person.getObjID(), e);
//                    }
//
//                }

            }
        } catch (Exception e) {
            log.warn("获取人员信息失败", e);
        }
        try {
            if (eventPolymerization != null && eventPolymerization.get() != null) {
                List<PolymerizationDTO> eventList = eventPolymerization.get();
                allData.addAll(eventList);
//
//                for (PolymerizationDTO eventInfo : eventList) {
//                    try {
//                        List<String> entityIdList = new ArrayList<>();
//                        entityIdList.add(eventInfo.getObjID());
//                        //将当前人员的监控任务放入redis
//                        String sessionId = getWebsocketTask().getSessionId();
//                        if (sessionId != null) {
//                            EntityTaskDto entityTask = BeanUtil.copy(getWebsocketTask(), EntityTaskDto.class);
//                            entityTask.setEntityIds(entityIdList);
//                            entityTask.setEntityType(CommonConstant.ENTITY_TYPE.EVENT);
//                            getWebSocketTaskService().createEntityTask(entityTask);
//                        }
//                    } catch (Exception e) {
//                        log.warn("设置事件wsTask{}异常：{}", eventInfo.getObjID(), e.getMessage());
//                    }
//                }
            }
        } catch (Exception e) {
            log.warn("获取事件信息失败", e);
        }

        try {
            if (ashcanPolymerization != null && ashcanPolymerization.get() != null) {
                List<PolymerizationDTO> ashcanList = ashcanPolymerization.get();
                allData.addAll(ashcanList);
//
//                for (PolymerizationDTO ashcanInfo : ashcanList) {
//                    try {
//                        List<String> entityIdList = new ArrayList<>();
//                        entityIdList.add(ashcanInfo.getObjID());
//                        //将当前人员的监控任务放入redis
//                        String sessionId = getWebsocketTask().getSessionId();
//                        if (sessionId != null) {
//                            EntityTaskDto entityTask = BeanUtil.copy(getWebsocketTask(), EntityTaskDto.class);
//                            entityTask.setEntityIds(entityIdList);
//                            entityTask.setEntityType(CommonConstant.ENTITY_TYPE.ASHCAN);
//                            getWebSocketTaskService().createEntityTask(entityTask);
//                        }
//                    } catch (Exception e) {
//                        log.warn("设置垃圾桶wsTask{}异常：{}", ashcanInfo.getObjID(), e.getMessage());
//                    }
//                }
            }
        } catch (Exception e) {
            log.warn("获取垃圾桶信息失败", e);
        }

        try {
            if (transferStationPolymerization != null && transferStationPolymerization.get() != null) {
                List<PolymerizationDTO> transferStationList = transferStationPolymerization.get();
                allData.addAll(transferStationList);

//                for (PolymerizationDTO transferStationInfo : transferStationList) {
//                    try {
//                        List<String> entityIdList = new ArrayList<>();
//                        entityIdList.add(transferStationInfo.getObjID());
//                        //将当前人员的监控任务放入redis
//                        String sessionId = getWebsocketTask().getSessionId();
//                        if (sessionId != null) {
//                            EntityTaskDto entityTask = BeanUtil.copy(getWebsocketTask(), EntityTaskDto.class);
//                            entityTask.setEntityIds(entityIdList);
//                            entityTask.setEntityType(CommonConstant.ENTITY_TYPE.FACILITY);
//                            getWebSocketTaskService().createEntityTask(entityTask);
//                        }
//                    } catch (Exception e) {
//                        log.warn("设置中转站wsTask{}异常：{}", transferStationInfo.getObjID(), e.getMessage());
//                    }
//                }
            }
        } catch (Exception e) {
            log.warn("获取中转站信息失败", e);
        }

        try {
            if (toiletPolymerization != null && toiletPolymerization.get() != null) {
                List<PolymerizationDTO> toiletList = toiletPolymerization.get();
                allData.addAll(toiletList);
//
//                for (PolymerizationDTO toiletInfo : toiletList) {
//                    try {
//                        List<String> entityIdList = new ArrayList<>();
//                        entityIdList.add(toiletInfo.getObjID());
//                        //将当前人员的监控任务放入redis
//                        String sessionId = getWebsocketTask().getSessionId();
//                        if (sessionId != null) {
//                            EntityTaskDto entityTask = BeanUtil.copy(getWebsocketTask(), EntityTaskDto.class);
//                            entityTask.setEntityIds(entityIdList);
//                            entityTask.setEntityType(CommonConstant.ENTITY_TYPE.TOILET);
//                            getWebSocketTaskService().createEntityTask(entityTask);
//                        }
//                    } catch (Exception e) {
//                        log.warn("设置公厕wsTask{}异常：{}", toiletInfo.getObjID(), e.getMessage());
//                    }
//                }
            }
        } catch (Exception e) {
            log.warn("获取公厕信息失败", e);
        }

        PolymerizationVO polymerizationVO = new PolymerizationVO();
        polymerizationVO.setTopicName(getWebsocketTask().getTopic());
        polymerizationVO.setActionName(getWebsocketTask().getTaskType());
        polymerizationVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
        polymerizationVO.setEntityList(allData);
        return R.data(polymerizationVO);
    }
}
