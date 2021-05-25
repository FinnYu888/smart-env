package com.ai.apac.smartenv.address.feign;

import com.ai.apac.smartenv.address.dto.CoordsAllSystem;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IGisInfoCacheClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/29
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/29 15:20    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_ADDR_NAME,
        fallback = GisInfoCacheClientFallback.class
)
public interface IGisInfoCacheClient {

    String client = "/client/gisInfoCache/";

    String SAVE_ORUPDATE_COORDS_ALL_SYSTEM_LIST = client + "saveOrupdateCoordsAllSystemList";
    String SAVE_OR_UPDATE = client + "saveOrUpdate";
    String GET_COORDS_ALL_SYSTEM = client + "getCoordsAllSystem";
    String GET_COORDS = client + "getCoords";


    @PostMapping(SAVE_ORUPDATE_COORDS_ALL_SYSTEM_LIST)
    R saveOrupdateCoordsAllSystemList(@RequestParam BaiduMapUtils.CoordsSystem coordsSystem,@RequestBody  List<CoordsAllSystem> coordsAllSystem);

    @PostMapping(SAVE_OR_UPDATE)
    R saveOrUpdate(@RequestParam BaiduMapUtils.CoordsSystem coordsSystem,@RequestBody CoordsAllSystem coordsAllSystem);

    @PostMapping(GET_COORDS_ALL_SYSTEM)
    R<CoordsAllSystem> getCoordsAllSystem(@RequestParam BaiduMapUtils.CoordsSystem coordsSystem, @RequestBody Coords coords);

    @PostMapping(GET_COORDS)
    R<Coords> getCoords(@RequestParam BaiduMapUtils.CoordsSystem source, @RequestParam BaiduMapUtils.CoordsSystem target,@RequestBody  Coords coords);
}
