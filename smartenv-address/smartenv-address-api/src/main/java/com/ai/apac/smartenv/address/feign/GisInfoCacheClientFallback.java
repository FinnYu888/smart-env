package com.ai.apac.smartenv.address.feign;

import com.ai.apac.smartenv.address.dto.CoordsAllSystem;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import org.springblade.core.tool.api.R;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: GisInfoCacheClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/29
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/29 15:20    panfeng          v1.0.0             修改原因
 */

public class GisInfoCacheClientFallback implements IGisInfoCacheClient{

    @Override
    public R saveOrupdateCoordsAllSystemList(BaiduMapUtils.CoordsSystem coordsSystem, List<CoordsAllSystem> coordsAllSystem) {
        return R.fail("接收数据失败");
    }

    @Override
    public R saveOrUpdate(BaiduMapUtils.CoordsSystem coordsSystem, CoordsAllSystem coordsAllSystem) {
        return R.fail("接收数据失败");
    }


    @Override
    public R<CoordsAllSystem> getCoordsAllSystem(BaiduMapUtils.CoordsSystem coordsSystem, Coords coords) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<Coords> getCoords(BaiduMapUtils.CoordsSystem source, BaiduMapUtils.CoordsSystem target, Coords coords) {
        return R.fail("接收数据失败");
    }
}
