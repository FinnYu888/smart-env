package com.ai.apac.smartenv.pushc.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.ai.apac.smartenv.pushc.entity.NoticeInfo;
import com.ai.apac.smartenv.pushc.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author qianlong
 * @description Email发送服务
 * @Date 2020/5/24 7:58 下午
 **/
@Service
@Slf4j
public class MailServiceImpl implements IMailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送文本邮件
     *
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param cc      抄送地址
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content, String... cc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        if (ArrayUtil.isNotEmpty(cc)) {
            message.setCc(cc);
        }
        mailSender.send(message);
    }

    /**
     * 发送HTML邮件
     *
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param cc      抄送地址
     * @throws MessagingException 邮件发送异常
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content, String... cc)  {
        try{
            Context context = new Context();
            context.setVariable("emailContent", content);

            String emailTemplate = templateEngine.process("simpleEmail", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailTemplate, true);
            if (ArrayUtil.isNotEmpty(cc)) {
                helper.setCc(cc);
            }
            mailSender.send(message);
            log.info("向[{}]发送邮件成功",to);
        }catch(MessagingException ex){
            throw new ServiceException(ResultCode.FAILURE,ex);
        }
    }
}
