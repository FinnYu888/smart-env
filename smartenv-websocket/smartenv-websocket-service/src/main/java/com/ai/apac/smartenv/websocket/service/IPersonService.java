package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.entity.OmnicPersonInfo;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.websocket.common.GetPersonPositionDTO;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorInfoVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonMonitorVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import org.springblade.core.tool.api.R;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author qianlong
 * @Description 人员服务
 * @Date 2020/2/16 4:22 下午
 **/
public interface IPersonService {

    /**
     * 向客户端推送人员状态统计信息
     *
     * @param websocketTask
     */
    void pushPersonStatus(WebsocketTask websocketTask);

    /**
     * 向客户端实时推送人员位置信息
     *
     * @param websocketTask
     */
    void pushPersonPosition(WebsocketTask websocketTask);


    /**
     * 向客户端实时推送当前人员的详细信息
     *
     * @param websocketTask
     */
    void pushPersonDetail(WebsocketTask websocketTask);

    /**
     * 向客户端实时推送当前人员的运行轨迹
     *
     * @param websocketTask
     */
    void pushPersonTrackRealTime(WebsocketTask websocketTask);

    /**
     * 根据租户获取当前人员状态
     *
     * @param tenantId
     * @return
     */
    Future<StatusCount> getStatusCount(String tenantId);

    /**
     * 根据状态查询对应的人员信息
     *
     * @param status
     * @param tenantId
     * @return
     */
    Future<List<OmnicPersonInfo>> getPersonByStatus(Integer status, String tenantId);





    Future<List<String>> getPersonByWorkareaIdsAndStatus(String tenantId);


    Future<List<String>> getPersonEasyVList(List<String> tenantId);

    List<PersonMonitorInfoVO> getPersonMonitorInfo(List<String> personIdList, BaiduMapUtils.CoordsSystem coordsSystem);

    PersonMonitorInfoVO getPersonMonitorInfo(String personId, BaiduMapUtils.CoordsSystem coordsSystem);

    /**
     * 根据人员ID实时获取最新信息
     *
     * @param personId
     * @return
     */
    PersonDetailVO getPersonDetailRealTime(String personId, String tenantId);

    PersonDetailVO getPersonStatusRealTime(String personId, String tenantId);


    /**
     * 获取人员最新位置信息
     *
     * @param personId
     * @param deviceInfo
     * @param trackPosition
     * @param tenantId
     * @return
     */
    PersonMonitorVO getPersonLastInfo(String personId, DeviceInfo deviceInfo, List<PositionDTO> trackPosition, String tenantId);

    List<Long> getPersonIdsByRegionId(String regionId);

    R<PersonMonitorVO> getPersonPosition(GetPersonPositionDTO getPersonPositionDTO);

    PersonDetailVO getPersonDetailInfo(String personId);
}
