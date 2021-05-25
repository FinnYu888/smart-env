package com.ai.apac.smartenv.alarm.vo;

import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@Document(collection = AlarmConstant.MONGODB_ALARM_INFO)
public class AlarmInfoMongoDBVO implements Serializable {
    private static final long serialVersionUID = -3564378328281623663L;

    @Indexed
//    @BsonProperty("alarmId")
    private Long alarmId;

    private String uuid;
    
//    @BsonProperty("deviceCode")
    private String deviceCode;

    @Indexed
//    @BsonProperty("entityId")
    private Long entityId;

//    @BsonProperty("entityType")
    private Long entityType;

//    @BsonProperty("entityName")
    private String entityName;

//    @BsonProperty("entityDefine")
    private String entityDefine;

//    @BsonProperty("ruleId")
    private Long ruleId;

//    @BsonProperty("ruleName")
    private String ruleName;

//    @BsonProperty("ruleAlarmLevel")
    private Integer ruleAlarmLevel;

//    @BsonProperty("ruleCategoryId")
    private Long ruleCategoryId;

//    @BsonProperty("ruleCategoryCode")
    private String ruleCategoryCode;

//    @BsonProperty("parentRuleCategoryId")
    private Long parentRuleCategoryId;

    @Indexed
//    @BsonProperty("alarmTime")
    private Long alarmTime;

//    @BsonProperty("longitude")
    private String longitude;

//    @BsonProperty("latitudinal")
    private String latitudinal;

//    @BsonProperty("data")
    private String data;

//    @BsonProperty("isHandle")
    private Integer isHandle;

//    @BsonProperty("alarmMessage")
    private String alarmMessage;

//    @BsonProperty("alarmCheck")
    private Integer alarmCheck;

//    @BsonProperty("checkRemark")
    private String checkRemark;

//    @BsonProperty("informType")
    private String informType;

//    @BsonProperty("tenantId")
    private String tenantId;

//    @BsonProperty("createUser")
    private Long createUser;

//    @BsonProperty("createDept")
    private Long createDept;

//    @BsonProperty("createTime")
    private Long createTime;

//    @BsonProperty("updateUser")
    private Long updateUser;

//    @BsonProperty("updateTime")
    private Long updateTime;

//    @BsonProperty("status")
    private Integer status;

//    @BsonProperty("isDeleted")
    private Integer isDeleted;

//    @BsonProperty("alarmLevel")
    private Integer alarmLevel;

//    @BsonProperty("alarmLevelName")
    private String alarmLevelName;

//    @BsonProperty("isHandleName")
    private String isHandleName;

//    @BsonProperty("alarmName")
    private String alarmName;

//    @BsonProperty("alarmType")
    private Long alarmType;

//    @BsonProperty("alarmTypeName")
    private String alarmTypeName;

//    @BsonProperty("alarmCatalogName")
    private String alarmCatalogName;

    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
//    @BsonProperty("vehicleId")
    private Long vehicleId;

//    @BsonProperty("plateNumber")
    private String plateNumber;

    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
//    @BsonProperty("kindCode")
    private Long kindCode;

//    @BsonProperty("vehicleTypeName")
    private String vehicleTypeName;

    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long vehicleCategoryId;

    private String vehicleCategoryName;
    
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
//    @BsonProperty("personId")
    private Long personId;

//    @BsonProperty("personName")
    private String personName;

//    @BsonProperty("jobNumber")
    private String jobNumber;

//    @BsonProperty("personPositionName")
    private String personPositionName;

    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
//    @BsonProperty("personPositionId")
    private Long personPositionId;

//    @BsonProperty("department")
    private String department;

//    @BsonProperty("deptId")
    private Long deptId;

    private List<String> initiativeAlarmPics;
    
    private Integer initiativeAlarmType;

    private MinicreatAdasAlarmVO adasAlarmVO;

    private MinicreateDsmAlarmVO dsmAlarmVO;
}
