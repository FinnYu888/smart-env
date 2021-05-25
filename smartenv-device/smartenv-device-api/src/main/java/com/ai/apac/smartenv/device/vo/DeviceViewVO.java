package com.ai.apac.smartenv.device.vo;

import com.ai.apac.smartenv.device.entity.DeviceChannel;
import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 记录设备VIEW信息视图实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceViewVO对象", description = "记录设备视图信息")
public class DeviceViewVO extends DeviceInfo {

    List<DeviceExt> deviceExtList;

    List<DeviceChannel> deviceChannelList;

    String entityCategoryName;


    String deviceFactoryName;

    String simCode;

    String simNumber;

    String simId;


}
