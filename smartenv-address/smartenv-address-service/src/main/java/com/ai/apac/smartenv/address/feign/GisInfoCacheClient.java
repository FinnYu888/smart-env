package com.ai.apac.smartenv.address.feign;

import com.ai.apac.smartenv.address.dto.CoordsAllSystem;
import com.ai.apac.smartenv.address.service.IGisInfoCacheService;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: GisInfoCacheClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/29
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/29 15:22    panfeng          v1.0.0             修改原因
 */

@ApiIgnore
@RestController
@AllArgsConstructor
public class GisInfoCacheClient implements IGisInfoCacheClient {

    @Autowired
    IGisInfoCacheService gisInfoCacheService;


    @Override
    @PostMapping(SAVE_ORUPDATE_COORDS_ALL_SYSTEM_LIST)
    public R saveOrupdateCoordsAllSystemList(@RequestParam BaiduMapUtils.CoordsSystem coordsSystem,@RequestBody List<CoordsAllSystem> coordsAllSystem) {
        return R.data(gisInfoCacheService.saveOrupdateCoordsAllSystemList(coordsSystem,coordsAllSystem));
    }


    @Override
    @PostMapping(SAVE_OR_UPDATE)
    public R saveOrUpdate(@RequestParam BaiduMapUtils.CoordsSystem coordsSystem,@RequestBody CoordsAllSystem coordsAllSystem) {
        return R.data(gisInfoCacheService.saveOrUpdate(coordsSystem,coordsAllSystem));
    }

    @Override
    @PostMapping(GET_COORDS_ALL_SYSTEM)
    public R<CoordsAllSystem> getCoordsAllSystem(@RequestParam BaiduMapUtils.CoordsSystem coordsSystem, @RequestBody Coords coords) {
        return R.data(gisInfoCacheService.getCoordsAllSystem(coordsSystem,coords));
    }

    @Override
    @PostMapping(GET_COORDS)
    public R<Coords> getCoords(@RequestParam BaiduMapUtils.CoordsSystem source, @RequestParam BaiduMapUtils.CoordsSystem target,@RequestBody Coords coords) {
        return R.data(gisInfoCacheService.getCoords(source,target,coords));
    }
}
