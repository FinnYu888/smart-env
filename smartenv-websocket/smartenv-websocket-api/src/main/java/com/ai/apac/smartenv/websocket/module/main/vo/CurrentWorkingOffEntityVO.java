package com.ai.apac.smartenv.websocket.module.main.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName CurrentWorkingOffEntityVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/28 17:07
 * @Version 1.0
 */
@Data
public class CurrentWorkingOffEntityVO extends WebSocketDTO {

    List<WorkingOffEntityVO> workingOffEntityVOList;
}
