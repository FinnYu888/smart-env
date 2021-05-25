package com.ai.apac.smartenv.green.dto.mongo;

import com.ai.apac.smartenv.device.dto.mongo.GreenScreenDeviceDTLDTO;
import com.ai.apac.smartenv.device.dto.mongo.GreenScreenDeviceDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName GreenScreenGreenAreaDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 17:47
 * @Version 1.0
 */
@Data
public class GreenScreenGreenAreaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    String area;
    String deviceId;
    String greenAreaId;
    String greenArea;
    String greenAreaName;
    String greenPer;
    String treeNum;
    String lawnArea;
    List<GreenScreenDeviceDTLDTO> indexList;
    List<GreenScreenNodeDTO> nodes;
}
