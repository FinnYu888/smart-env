package com.ai.apac.smartenv.websocket.module.polymerization.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import com.ai.apac.smartenv.websocket.module.polymerization.dto.PolymerizationDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PolymerizationVO  extends WebSocketDTO implements Serializable {

    List<PolymerizationDTO> entityList;
}
