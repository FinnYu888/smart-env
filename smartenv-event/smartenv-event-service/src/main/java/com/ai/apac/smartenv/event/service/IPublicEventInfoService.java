package com.ai.apac.smartenv.event.service;

import com.ai.apac.smartenv.event.dto.PublicEventInfoDTO;
import com.ai.apac.smartenv.event.entity.PublicEventInfo;
import com.ai.apac.smartenv.event.vo.EventAllInfoVO;
import com.ai.apac.smartenv.event.vo.PublicEventInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;

import java.util.List;

/**
 * @author qianlong
 * @Description 公众上报事件Service
 * @Date 2020/12/17 3:36 下午
 **/
public interface IPublicEventInfoService extends BaseService<PublicEventInfo> {

    /**
     * 保存事件信息
     * @param publicEventInfoDTO
     * @return
     */
    boolean savePublicEventInfo(PublicEventInfoDTO publicEventInfoDTO);

    PublicEventInfoVO getEventDetail(Long eventId);

    void confirmPublicEvent(EventAllInfoVO eventInfoVO, Integer coordsType);
//
//    List<PublicEventInfo> getEventByTenant(String tenantId);
//
//
//    IPage<PublicEventInfo> getEventByWechatId(String wechatId, Query query);
}
