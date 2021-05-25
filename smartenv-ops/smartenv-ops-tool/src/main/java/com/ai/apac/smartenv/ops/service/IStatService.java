package com.ai.apac.smartenv.ops.service;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/12/20 10:02 上午
 **/
public interface IStatService {

    /**
     * 生成垃圾收运数据
     *
     * @param min
     * @param max
     * @param areaCode
     */
    void importTrashData(Integer min, Integer max, String areaCode);

    /**
     * 生成违规告警数据
     *
     * @param min
     * @param max
     * @param total
     * @param areaCode
     */
    void importIllegalBehavior(Integer min, Integer max, Integer total, String areaCode);

    /**
     * 生成区域告警数量统计
     *
     * @param min
     * @param max
     * @param areaCode
     */
    void importAlarmCount(Integer min, Integer max, String areaCode);

    /**
     * 生成区域违规行为分析
     *
     * @param min
     * @param max
     * @param areaCode
     */
    void importKpi(Integer min, Integer max, String areaCode);

    /**
     * 生成区域工作完成率
     *
     * @param vehicleMax
     * @param personMax
     * @param areaCode
     */
    void importAreaWorkInfo(Integer vehicleMax, Integer personMax, String areaCode);
}
