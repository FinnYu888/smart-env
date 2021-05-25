package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import org.springblade.core.tool.api.R;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: ITrackClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/21
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/21  2:16    panfeng          v1.0.0             修改原因
 */
public class ITrackClientFallback implements ITrackClient {
    @Override
    public R<TrackPositionDto> getBigdataTrack(Long entityId, Long entityType, Long startTime, Long endTime) {
        return R.fail("获取消息失败");
    }


}
