package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.service.IDeviceExtService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: DeviceExtClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/7
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/7  1:52    panfeng          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class DeviceExtClient implements IDeviceExtClient {

    @Autowired
    private IDeviceExtService deviceExtService;


    @Override
    @GetMapping(API_GET_BY_ATTR)
    public R<DeviceExt> getByAttrId(Long deviceId, Long attrId) {
        DeviceExt deviceExt=new DeviceExt();
        deviceExt.setAttrValue("3");
        deviceExt.setDeviceId(deviceId);
        deviceExt.setAttrId(attrId);
        Wrapper<DeviceExt> wrapper= Condition.getQueryWrapper(deviceExt);
        List<DeviceExt> list = deviceExtService.list(wrapper);
        if (CollectionUtil.isNotEmpty(list)){
            return R.data(list.get(0));
        }
        return R.data(deviceExt);
    }
}
