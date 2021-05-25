package com.ai.apac.smartenv.device.controller;

import com.ai.apac.smartenv.device.dto.PhoneBookDTO;
import com.ai.apac.smartenv.device.dto.SosNumberDTO;
import com.ai.apac.smartenv.device.service.IWatchService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/5/21 11:38 上午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/watchDevice")
@Api(value = "手表操作接口", tags = "手表操作接口")
public class WatchController extends BladeController {

    @Autowired
    private IWatchService watchService;

    /**
     * 向手表发送消息
     *
     * @param deviceCode
     * @param message
     * @return
     */
    @PostMapping("/{deviceCode}/message")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "向手表发送消息", notes = "向手表发送消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编号", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "message", value = "需要发送的文字消息,不能超过15个字符", paramType = "query", dataType = "string")
    })
    @ApiLog(value = "向手表发送消息")
    public R sendMessage(@PathVariable String deviceCode, String message) {
        if (message.trim().length() > 15) {
            throw new ServiceException("不能超过15个字符");
        }
        watchService.sendMessage(deviceCode, message);
        return R.status(true);
    }

    /**
     * 发送文字消息并转语音后发给手表
     *
     * @param deviceCode
     * @param message
     * @return
     */
    @PostMapping("/{deviceCode}/text2Voice")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "发送文字消息并转语音后发给手表", notes = "发送文字消息并转语音后发给手表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编号", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "message", value = "需要发送的文字消息,不能超过40个字符", paramType = "query", dataType = "string")
    })
    @ApiLog(value = "发送文字消息并转语音后发给手表")
    public R sendTextMsg2Voice(@PathVariable String deviceCode, String message) {
        watchService.sendMessage2Voice(deviceCode, message);
        return R.status(true);
    }

    /**
     * 手表设置SOS号码
     *
     * @param deviceCode
     * @param sosNumberDTO
     * @return
     */
    @PostMapping("/{deviceCode}/sosNumber")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "手表设置SOS号码", notes = "手表设置SOS号码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编号", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "sosNumberDTO", value = "号码对象", paramType = "body", dataType = "SosNumberDTO")
    })
    @ApiLog(value = "手表设置SOS号码")
    public R setSosNumber(@PathVariable String deviceCode, @RequestBody SosNumberDTO sosNumberDTO) {
        watchService.setSosNumber(deviceCode, sosNumberDTO.getPhoneNumber(), sosNumberDTO.getPriority());
        return R.status(true);
    }

    /**
     * 手表批量设置SOS号码
     *
     * @param deviceCode
     * @param sosNumberList
     * @return
     */
    @PostMapping("/{deviceCode}/sosNumberList")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "手表批量设置SOS号码", notes = "手表批量设置SOS号码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编号", paramType = "path", dataType = "string")
    })
    @ApiLog(value = "手表批量设置SOS号码")
    public R setSosNumber(@PathVariable String deviceCode, @RequestBody List<String> sosNumberList) {
        watchService.batchSetSosNumber(deviceCode, sosNumberList);
        return R.status(true);
    }

    /**
     * 手表删除SOS号码
     *
     * @param deviceCode
     * @param priority
     * @return
     */
    @DeleteMapping("/{deviceCode}/sosNumber")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "手表删除SOS号码", notes = "手表删除SOS号码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编号", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "priority", value = "优先级", paramType = "query", dataType = "integer")
    })
    @ApiLog(value = "手表删除SOS号码")
    public R setSosNumber(@PathVariable String deviceCode, Integer priority) {
        watchService.setSosNumber(deviceCode, "00000000", priority);
        return R.status(true);
    }

    /**
     * 手表数据上报时间间隔设置
     *
     * @param deviceCode
     * @param interval
     * @return
     */
    @PostMapping("/{deviceCode}/uploadSetting")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "手表数据上报时间间隔设置", notes = "手表数据上报时间间隔设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编号", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "interval", value = "时间间隔(单位是秒)", paramType = "query", dataType = "integer")
    })
    @ApiLog(value = "手表数据上报时间间隔设置")
    public R uploadSetting(@PathVariable String deviceCode, Integer interval) {
        watchService.setUploadDataFrequency(deviceCode, interval);
        return R.status(true);
    }

    /**
     * 设置租户所有手表数据上报时间间隔设置
     *
     * @param interval
     * @return
     */
    @PostMapping("/tenant/uploadSetting")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "设置租户所有手表数据上报时间间隔设置", notes = "设置租户所有手表数据上报时间间隔设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "interval", value = "时间间隔(单位是秒)", paramType = "query", dataType = "integer")
    })
    public R uploadSetting(BladeUser bladeUser, Integer interval) {
        watchService.setUploadDataFrequencyForTenant(bladeUser.getTenantId(), interval);
        return R.status(true);
    }

    /**
     * 发起手表定位
     *
     * @param deviceCode
     * @return
     */
    @PostMapping("/{deviceCode}/getLocation")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "发起手表定位", notes = "发起手表定位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编号", paramType = "path", dataType = "string")
    })
    @ApiLog(value = "手表数据上报时间间隔设置")
    public R uploadSetting(@PathVariable String deviceCode) {
        watchService.getLocation(deviceCode);
        return R.status(true);
    }

    /**
     * 发起手表之间的通话
     *
     * @param deviceCode
     * @return
     */
    @PostMapping("/{deviceCode}/call")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "发起手表之间的通话", notes = "发起手表之间的通话")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编号", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "destPhone", value = "目标号码", paramType = "query", dataType = "string")
    })
    @ApiLog(value = "发起手表之间的通话")
    public R call(@PathVariable String deviceCode, String destPhone) {
        watchService.call(deviceCode, destPhone);
        return R.status(true);
    }

    /**
     * 操作员呼叫目标员工手表
     *
     * @param destPersonId
     * @param bladeUser
     * @return
     */
    @PostMapping("/adminCallPerson")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "操作员呼叫目标员工手表", notes = "操作员呼叫目标员工手表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "destPersonId", value = "目标员工ID", paramType = "query", dataType = "string")
    })
    @ApiLog(value = "操作员呼叫目标员工手表")
    public R adminCallPerson(String destPersonId, BladeUser bladeUser) {
        watchService.call(bladeUser, Long.valueOf(destPersonId));
        return R.status(true);
    }

    /**
     * 校验是否可以呼叫目标人员的手表
     *
     * @param destPersonId
     * @return
     */
    @GetMapping("/getCanCall/{destPersonId}")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "是否可以呼叫手表", notes = "是否可以呼叫手表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "destPersonId", value = "目标员工ID", paramType = "path", dataType = "string")
    })
    public R isCanCall(@PathVariable String destPersonId) {
        try {
            return R.data(watchService.isCanCall(Long.valueOf(destPersonId)));
        } catch (Exception ex) {
            R<Boolean> result = R.success(ex.getMessage());
            result.setData(false);
            result.setMsg(ex.getMessage());
            return result;
        }
    }

    /**
     * 设置手表电话本
     *
     * @param deviceCode
     * @param phoneBookList
     * @return
     */
    @PostMapping("/{deviceCode}/phoneBook")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "设置手表电话本", notes = "设置手表电话本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编号", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "phoneBookList", value = "电话本信息", paramType = "body", dataType = "List<PhoneBookDTO>")
    })
    @ApiLog(value = "设置手表电话本")
    public R adminCallPerson(@PathVariable String deviceCode, @RequestBody List<PhoneBookDTO> phoneBookList) {
        watchService.setPhoneBook(deviceCode, phoneBookList);
        return R.status(true);
    }
}
