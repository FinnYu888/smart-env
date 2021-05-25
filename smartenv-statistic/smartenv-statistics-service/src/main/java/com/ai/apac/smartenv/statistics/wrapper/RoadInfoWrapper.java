package com.ai.apac.smartenv.statistics.wrapper;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.common.enums.WorkAreaLevelEnum;
import com.ai.apac.smartenv.statistics.entity.VehicleWorkStatResult;
import com.ai.apac.smartenv.statistics.vo.RoadInfoVO;
import com.ai.apac.smartenv.statistics.vo.WorkInfoVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description 道路数据视图包装类
 * @Date 2021/1/12 5:29 下午
 **/
public class RoadInfoWrapper {

    public static RoadInfoWrapper build() {
        return new RoadInfoWrapper();
    }

    public RoadInfoVO entityVO(Integer workAreaLevel, List<VehicleWorkStatResult> vehicleWorkStatResultList) {
        if (CollUtil.isEmpty(vehicleWorkStatResultList)) {
            return null;
        }

        RoadInfoVO roadInfoVO = new RoadInfoVO();
        roadInfoVO.setRoadLevel(WorkAreaLevelEnum.getDescByValue(workAreaLevel));

        //由于会有多个项目,因此需要将多个项目的规划面积加起来
        Map<String, String> workAreaAcreageMap = new HashMap<>();

        Double totalWorkAreaAcreage = 0.0;
        for (String workAreaAcreageStr : workAreaAcreageMap.values()) {
            totalWorkAreaAcreage = totalWorkAreaAcreage + Double.valueOf(workAreaAcreageStr);
        }
        //单位是万平米
        roadInfoVO.setTotalArea(String.valueOf(totalWorkAreaAcreage / 10000));

        List<WorkInfoVO> workInfoList = new ArrayList<WorkInfoVO>();

        return roadInfoVO;
    }
}
