package com.ai.apac.smartenv.common.constant;

/**
 * @author qianlong
 * @Description 手表指令定义
 * @Date 2020/5/15 9:22 上午
 **/
public interface WatchCommandConst {

    //设置手表上传时间间隔
    String UPLOAD = "UPLOAD";

    //拨打电话
    String CALL = "CALL";

    //发送短语
    String MESSAGE = "MESSAGE";

    //服务端接收到文字后转语音并向手表发送
    String SEND_TEXT_TO_VOICE = "T2V";

    //设置SOS号码
    String SOS = "SOS";

    //设置电话本
    String PHONE_BOOK = "PHB";

    //设置心率协议
    String HR_SET = "hrtstart";

    //获取手表定位信息
    String GET_LOCATION = "CR";
}
