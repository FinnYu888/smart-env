package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.ParseException;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: ITrackClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/21
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/21  2:16    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_OMNIC_NAME,
        fallback = ITrackClientFallback.class
)
public interface ITrackClient {
    String client="/client";

    String getBigdataTrack=client+"/get-bigdata-track";

    //获取历史轨迹

    @GetMapping(getBigdataTrack)
    R<TrackPositionDto> getBigdataTrack(@RequestParam Long entityId, @RequestParam Long entityType, @RequestParam Long startTime, @RequestParam Long endTime) throws IOException, ParseException;



}
