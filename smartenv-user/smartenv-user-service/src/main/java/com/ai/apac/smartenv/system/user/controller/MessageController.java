package com.ai.apac.smartenv.system.user.controller;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.system.user.dto.MessageInfoDTO;
import com.ai.apac.smartenv.system.user.dto.RelMessageDTO;
import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.service.IMessageService;
import com.ai.apac.smartenv.system.user.vo.UserMessageCountVO;
import com.ai.apac.smartenv.system.user.vo.UserVO;
import com.ai.apac.smartenv.system.user.wrapper.UserWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.sun.net.httpserver.Authenticator;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @ClassName MessageController
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/2 14:51
 * @Version 1.0
 */
@RestController
@RequestMapping("/message")
@AllArgsConstructor
@Api(value = "消息相关接口", tags = "消息相关接口")
public class MessageController {

    private IMessageService messageService;

    @GetMapping("/list")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表", notes = "传入messageType和isRead")
    @ApiLog(value = "分页查询消息列表")
    public R<IPage<RelMessageDTO>> list(@ApiParam(value = "消息类型", required = true) @RequestParam String messageType, @RequestParam String isRead, Query query) {
        List<RelMessageDTO> relMessageDTOList = messageService.listMessage(messageType,isRead,query.getCurrent(),query.getSize(),"","").getMessageList();
        String count = messageService.countMessage(messageType,isRead);
        // 构造page对象
        IPage<RelMessageDTO> iPage = new Page<>(query.getCurrent(), query.getSize(), true);
        iPage.setTotal(Long.parseLong(count));
        iPage.setRecords(relMessageDTOList);
        return R.data(iPage);
    }

    @PutMapping("/read")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "read message")
    @ApiLog(value = "raad message")
    public R<String> updateMessage(@RequestParam String messageId,@ApiParam(value = "消息类型", required = true) @RequestParam String messageType,@RequestHeader(CommonConstant.ACCEPT_CHANNEL_TYPE) String acceptChannelType) {
        messageService.updateMessage(messageId,messageType,acceptChannelType);
        return R.data("success");
    }

    @PutMapping("/clean")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "clean message")
    @ApiLog(value = "clean message")
    public R<String> cleanMessage(@ApiParam(value = "消息类型", required = true) @RequestParam String messageType) {
        messageService.cleanMessage(messageType);
        return R.data("success");
    }

    @GetMapping("")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "用户消息数")
    @ApiLog(value = "用户消息数")
    public R<UserMessageCountVO> getUserMessage() {
        UserMessageDTO userMessageDTO = messageService.getUserMessage();
        UserMessageCountVO userMessageCountVO = new UserMessageCountVO();
        userMessageCountVO.setAlarmCount("0");
        userMessageCountVO.setAnnounCount("0");
        userMessageCountVO.setEventCount("0");
        userMessageCountVO.setUnReadAlarmCount("0");
        userMessageCountVO.setUnReadAnnounCount("0");
        userMessageCountVO.setUnReadEventCount("0");
        userMessageCountVO.setUserId(AuthUtil.getUserId().toString());
        if(ObjectUtil.isNotEmpty(userMessageDTO)){
            userMessageCountVO = BeanUtil.copy(userMessageDTO, UserMessageCountVO.class);
        }
        return R.data(userMessageCountVO);
    }

}
