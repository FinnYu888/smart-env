package com.ai.apac.smartenv.pushc.controller;

import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import com.ai.apac.smartenv.pushc.service.IMailService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author qianlong
 * @description 邮件发送服务
 * @Date 2020/5/24 8:12 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/pushc/email")
@Api(value = "Email发送服务", tags = "Email发送服务")
public class MailController {

    @Autowired
    private IMailService mailService;

    /**
     * 发送简单文本邮件
     */
    @PostMapping("/simpleMail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "发送简单文本邮件", notes = "发送简单文本邮件")
    @ApiLog(value = "发送简单文本邮件")
    public R sendSimpleMail(@RequestBody EmailDTO emailDTO) {
        mailService.sendSimpleMail(emailDTO.getReceiver(), emailDTO.getSubject(), emailDTO.getContent(), null);
        return R.status(true);
    }

    /**
     * 发送HTML文本邮件
     */
    @PostMapping("/htmlMail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "发送HTML文本邮件", notes = "发送HTML文本邮件")
    @ApiLog(value = "发送HTML文本邮件")
    public R sendHtmlMail(@RequestBody EmailDTO emailDTO) {
        mailService.sendHtmlMail(emailDTO.getReceiver(), emailDTO.getSubject(), emailDTO.getContent(), null);
        return R.status(true);
    }
}
