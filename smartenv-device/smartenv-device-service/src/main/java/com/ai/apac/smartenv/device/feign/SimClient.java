package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.entity.SimRel;
import com.ai.apac.smartenv.device.service.ISimInfoService;
import com.ai.apac.smartenv.device.service.ISimRelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @ClassName SimClient
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/28 20:09
 * @Version 1.0
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class SimClient  implements ISimClient {

    private ISimInfoService simInfoService;

    private ISimRelService simRelService;

    @Override
    public R<SimInfo> getSimByDeviceId(Long id) {
       return  R.data(simInfoService.getSimByDeviceId(id));
    }

    @Override
    public R<SimRel> getSimRelBySimCode2(String simCode2) {
        return R.data(simInfoService.getSimInfoBySimCode2(simCode2));
    }

    @Override
    public R<SimInfo> getSimBySimCode(String simCode) {
        QueryWrapper<SimInfo> queryWrapper = new QueryWrapper<SimInfo>();
        queryWrapper.lambda().eq(SimInfo::getSimCode, simCode);
        SimInfo simInfo = simInfoService.getOne(queryWrapper);
        return R.data(simInfo);
    }

    @Override
    public R<SimInfo> getSimBySimNumber(String simNumber) {
        QueryWrapper<SimInfo> queryWrapper = new QueryWrapper<SimInfo>();
        queryWrapper.lambda().eq(SimInfo::getSimNumber, simNumber);
        SimInfo simInfo = simInfoService.getOne(queryWrapper);
        return R.data(simInfo);
    }


}
