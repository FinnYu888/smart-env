package com.ai.apac.smartenv.omnic.controller;

import cn.hutool.core.util.RandomUtil;
import com.ai.apac.smartenv.address.entity.GisInfo;
import com.ai.apac.smartenv.address.feign.IAddressClient;
import com.ai.apac.smartenv.address.vo.GisInfoVO;
import com.ai.apac.smartenv.omnic.vo.AssessStatVO;
import com.ai.apac.smartenv.omnic.vo.IllegalBehaviorStatVO;
import com.ai.apac.smartenv.omnic.vo.TrashStatVO;
import com.ai.apac.smartenv.omnic.vo.WorkStatVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/8/26 9:10 上午
 **/
@RestController
@RequestMapping("/easyv/boss")
@AllArgsConstructor
@Api(value = "BOSS视角大屏相关接口", tags = "BOSS视角大屏相关接口")
@Slf4j
public class BossController {

    @Autowired
    private IAddressClient addressClient;

    private static List<String> assessItemList = new ArrayList<String>();

    static {
        assessItemList.add("主干道普扫");
        assessItemList.add("处理响应不及时");
        assessItemList.add("景观道清扫");
        assessItemList.add("河道清洁");
        assessItemList.add("中转站有异味");
        assessItemList.add("小区垃圾桶收运不及时");
        assessItemList.add("零星建筑垃圾清运");
        assessItemList.add("大件废弃物清运");
        assessItemList.add("公厕异味");
        assessItemList.add("中转站周边不干净");
        assessItemList.add("交通护栏清洁");
        assessItemList.add("主干道绿化带清洁");
    }

    /**
     * 根据项目编号、区域编码获取作业分析数据
     *
     * @param projectId
     * @param areaCode
     * @return
     */
    @ApiOperation(value = "区域编码获取作业分析数据", notes = "区域编码获取作业分析数据")
    @GetMapping("bi/workStatInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "areaCode", value = "区域编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "authToken", value = "认证令牌", paramType = "header", dataType = "string")
    })
    @ResponseBody
    public WorkStatVO getWorkStatInfo(String projectId, @RequestParam(required = false) String areaCode) {
        log.info("getWorkStatInfo|||projectId={},areaCode={}", projectId, areaCode);
        WorkStatVO workStatVO = new WorkStatVO();
        workStatVO.setOverviewWorkRate(RandomUtil.randomDouble(0.80, 0.98, 2, RoundingMode.CEILING));
        workStatVO.setPersonWorkRate(RandomUtil.randomDouble(0.80, 0.98, 2, RoundingMode.CEILING));
        workStatVO.setVehicleWorkRate(RandomUtil.randomDouble(0.80, 0.98, 2, RoundingMode.CEILING));
        return workStatVO;
    }

    /**
     * 查询作业违规同期对比数据
     *
     * @param projectId
     * @param areaCode
     * @return
     */
    @ApiOperation(value = "查询作业违规同期对比数据", notes = "查询作业违规同期对比数据")
    @GetMapping("bi/illegalBehaviorStatInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "areaCode", value = "区域编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "authToken", value = "认证令牌", paramType = "header", dataType = "string")
    })
    @ResponseBody
    public List<IllegalBehaviorStatVO> getIllegalBehaviorStatInfo(String projectId, @RequestParam(required = false) String areaCode) {
        log.info("getIllegalBehaviorStatInfo|||projectId={},areaCode={}", projectId, areaCode);
        String today = DateUtil.format(new Date(), DateUtil.PATTERN_DATE);
        Date lastMonthDate = DateUtil.minusMonths(new Date(), 1);
        String lastMonthDateStr = DateUtil.format(lastMonthDate, DateUtil.PATTERN_DATE);

        List<IllegalBehaviorStatVO> dataList = new ArrayList<>();
        List<IllegalBehaviorStatVO> lastMonthDataList = new ArrayList<>();
        lastMonthDataList.add(new IllegalBehaviorStatVO("人员越界", String.valueOf(RandomUtil.randomInt(50, 120)), "上月同期"));
        lastMonthDataList.add(new IllegalBehaviorStatVO("人员滞留", String.valueOf(RandomUtil.randomInt(50, 120)), "上月同期"));
        lastMonthDataList.add(new IllegalBehaviorStatVO("车辆越界", String.valueOf(RandomUtil.randomInt(50, 120)), "上月同期"));
        lastMonthDataList.add(new IllegalBehaviorStatVO("车辆滞留", String.valueOf(RandomUtil.randomInt(50, 120)), "上月同期"));
        lastMonthDataList.add(new IllegalBehaviorStatVO("车辆超速", String.valueOf(RandomUtil.randomInt(50, 120)), "上月同期"));
        lastMonthDataList.add(new IllegalBehaviorStatVO("驾驶行为异常", String.valueOf(RandomUtil.randomInt(50, 120)), "上月同期"));
        dataList.addAll(lastMonthDataList);

        for (int i = 0; i < lastMonthDataList.size(); i++) {
            IllegalBehaviorStatVO illegalBehaviorStatVO = lastMonthDataList.get(i);
            Integer lastValue = Integer.valueOf(illegalBehaviorStatVO.getValue());
            IllegalBehaviorStatVO todayData = new IllegalBehaviorStatVO(illegalBehaviorStatVO.getItem(), String.valueOf(lastValue - RandomUtil.randomInt(1, 30)), "今日");
            dataList.add(todayData);
        }

        return dataList;
    }

    /**
     * 查询垃圾收运数据
     *
     * @param projectId
     * @param areaCode
     * @return
     */
    @ApiOperation(value = "查询垃圾收运数据", notes = "查询垃圾收运数据")
    @GetMapping("bi/trashStatInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "areaCode", value = "区域编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "authToken", value = "认证令牌", paramType = "header", dataType = "string")
    })
    @ResponseBody
    public List<TrashStatVO> getTrashStatInfo(String projectId, @RequestParam(required = false) String areaCode) {
        log.info("getTrashStatInfo|||projectId={},areaCode={}", projectId, areaCode);

        List<TrashStatVO> dataList = new ArrayList<TrashStatVO>();
        //查询最近7天的数据
        for (int i = 7; i >= 1; i--) {
            Date date = DateUtil.minusDays(new Date(), i);
            String dateStr = DateUtil.format(date, "yyyy/MM/dd");
            dataList.add(new TrashStatVO("厨余垃圾", RandomUtil.randomDouble(10.00, 100.00, 2, RoundingMode.CEILING), dateStr));
            dataList.add(new TrashStatVO("可回收垃圾", RandomUtil.randomDouble(10.00, 80.00, 2, RoundingMode.CEILING), dateStr));
            dataList.add(new TrashStatVO("其他垃圾", RandomUtil.randomDouble(10.00, 50.00, 2, RoundingMode.CEILING), dateStr));
        }

        return dataList;
    }

    /**
     * 查询考核问题数据
     *
     * @param projectId
     * @param areaCode
     * @return
     */
    @ApiOperation(value = "查询考核问题数据", notes = "查询考核问题数据")
    @GetMapping("bi/assessStatInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "areaCode", value = "区域编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "authToken", value = "认证令牌", paramType = "header", dataType = "string")
    })
    @ResponseBody
    public List<AssessStatVO> getAssessStatInfo(String projectId, @RequestParam(required = false) String areaCode) {
        log.info("getAssessStatInfo|||projectId={},areaCode={}", projectId, areaCode);

        List<AssessStatVO> dataList = new ArrayList<AssessStatVO>();
        assessItemList.stream().forEach(assessItemName -> {
            dataList.add(new AssessStatVO(assessItemName, RandomUtil.randomInt(10, 200)));
        });
        return dataList;
    }

    /**
     * 根据区域编码获取区域名称
     *
     * @param areaCode
     * @return
     */
    @ApiOperation(value = "根据区域编码获取区域名称", notes = "根据区域编码获取区域名称")
    @GetMapping("bi/gisAreaName")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "areaCode", value = "区域编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "authToken", value = "认证令牌", paramType = "header", dataType = "string")
    })
    @ResponseBody
    public GisInfoVO getGisAreaName(String projectId, @RequestParam(required = false) String areaCode) {
        log.info("getAreaName|||areaCode={}", areaCode);
        R<GisInfoVO> result = addressClient.getGisInfo(areaCode);
        if (result != null && result.getData() != null) {
            return result.getData();
        }
        GisInfoVO gisInfoVO = new GisInfoVO();
        gisInfoVO.setFullAreaName("中国");
        return gisInfoVO;
    }
}
