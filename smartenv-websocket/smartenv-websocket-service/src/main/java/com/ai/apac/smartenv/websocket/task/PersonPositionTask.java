package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description 人员实时位置跟踪
 * @Date 2020/2/25 19:28 下午
 **/
@Getter
@Setter
@Slf4j
public class PersonPositionTask extends BaseTask implements Runnable {


    public PersonPositionTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void run() {
        runTask();
    }

    /**
     * 具体的执行方法,由子类实现
     */
    @Override
    public R<PersonMonitorVO> execute() {
        PersonMonitorVO personMonitorVO = new PersonMonitorVO();
        personMonitorVO.setTopicName(getWebsocketTask().getTopic());
        personMonitorVO.setActionName(getWebsocketTask().getTaskType());
        personMonitorVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
        R<PersonMonitorVO> result = null;
        try {
            Map<String, Object> params = validParams();
            String personIds = params.get("personIds") == null ? null : (String) params.get("personIds");
            Integer status = params.get("status") == null ? null : (Integer) params.get("status");


            String tenantIdStr = params.get("tenantId") == null ? null : (String) params.get("tenantId");
            Boolean isBigScreen = params.get("isBigScreen") == null ? false : (boolean) params.get("isBigScreen");
            Boolean isEasyV = params.get("isEasyV") == null ? false : (boolean) params.get("isEasyV");

            String regionId = params.get("regionId") == null ? null : (String) params.get("regionId");
            BaiduMapUtils.CoordsSystem coordsSystem = BaiduMapUtils.CoordsSystem.BD09LL;
            Integer coord = (Integer) params.get("coordsSystem");
            coordsSystem = coord == null ? BaiduMapUtils.CoordsSystem.BD09LL : BaiduMapUtils.CoordsSystem.getCoordsSystem(coord);


            //# region  当前区域应该放在外面

            String message = null;
            List<String> personIdList = null;
            if (StringUtils.isBlank(personIds) && status == null && StringUtil.isEmpty(tenantIdStr) && regionId == null) {
                throw new ServiceException("The param should not be empty!");
            } else if (StringUtils.isNotBlank(personIds)) {//根据车辆ID查询
                message = "查询人员ID查询实时位置成功";
                personIdList = Func.toStrList(personIds);
            } else if (StringUtil.isNotBlank(tenantIdStr) && isBigScreen && isEasyV) {
                message = "查询人员ID查询实时位置成功";
                personIdList = new ArrayList<>();
                List<String> tenantIds = Func.toStrList(tenantIdStr);
                Future<List<String>> personEasyVList = getPersonService().getPersonEasyVList(tenantIds);
                if (personEasyVList != null || personEasyVList.get() != null) {
                    personIdList=personEasyVList.get();
                }

            } else if (StringUtil.isNotBlank(tenantIdStr) && isBigScreen) {
                message = "查询人员ID查询实时位置成功";
                personIdList = new ArrayList<>();
                List<String> tenantIds = Func.toStrList(tenantIdStr);
                for (String tenantId : tenantIds) {
                    Future<List<String>> personByWorkareaIdsAndStatus = getPersonService().getPersonByWorkareaIdsAndStatus(tenantId);
                    if (personByWorkareaIdsAndStatus == null || personByWorkareaIdsAndStatus.get() == null) {
                        continue;
                    } else {
                        personIdList.addAll(personByWorkareaIdsAndStatus.get());
                    }
                }

            } else if (status != null) {//根据状态查询
                Future<List<OmnicPersonInfo>> dataResult = getPersonService().getPersonByStatus(status, getTenantId());
                if (dataResult != null && dataResult.get() != null) {
                    List<OmnicPersonInfo> personInfoList = dataResult.get();
                    if (personInfoList.size() > 0) {
                        personIdList = new ArrayList<String>();
                        for (OmnicPersonInfo personInfo : personInfoList) {
                            personIdList.add(String.valueOf(personInfo.getId()));
                        }
                    }
                }
                message = "查询人员状态查询实时位置成功";
            } else if (StringUtil.isNotBlank(regionId)) {
                List<Long> personIdsByRegionId = getPersonService().getPersonIdsByRegionId(regionId);
                personIdList = new ArrayList<>();
                List<String> finalPersonIdList = personIdList;
                personIdsByRegionId.forEach(personId -> {
                    finalPersonIdList.add(personId.toString());
                });

            }
            //# region end


            List<PersonMonitorInfoVO> personMonitorInfoVOList = new ArrayList<PersonMonitorInfoVO>();
            if (personIdList == null || personIdList.size() == 0) {
                result = R.data(null, "没有符合条件的人员");
                return result;
            }

            personMonitorInfoVOList = getPersonService().getPersonMonitorInfo(personIdList, coordsSystem);


            for (String personId : personIdList) {
                /**
                 * 将人员ID存入Redis,便于指定推送策略
                 */
                getWebSocketTaskService().createEntityTask(this.getWebsocketTask(), personId);
            }
            if (isEasyV) {
                getWebSocketTaskService().createEasyVTask(getWebsocketTask());
            }

            log.debug("================推送人员实时位置================");
            if (personMonitorInfoVOList.size() == 0) {
                return R.data(null, "没有符合条件的人员");
            }
            personMonitorVO.setPersonList(personMonitorInfoVOList);

            result = R.data(personMonitorVO);
            result.setMsg(message);
        } catch (Exception ex) {
            result = R.data(null);
            result.setCode(ResultCode.FAILURE.getCode());
            result.setMsg(ResultCode.FAILURE.getMessage());

            return result;
//            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }

}
