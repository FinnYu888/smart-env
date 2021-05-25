package com.ai.apac.smartenv.flow.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FlowInfoNodeVO implements Serializable {

    private String nodeCode;
    private String nodeName;
    private FlowTaskAllotVO flowTaskAllot;
}
