package com.ai.apac.smartenv.pushc.feign;

import com.ai.apac.smartenv.pushc.dto.AlarmEventDTO;
import com.ai.apac.smartenv.pushc.dto.AlarmInfoDTO;
import com.ai.apac.smartenv.pushc.dto.AssessEventDTO;
import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import org.springblade.core.tool.api.R;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/5/25 8:55 下午
 **/
public class IPushcFallbackClient implements IPushcClient{

    /**
     * 发送邮件
     *
     * @param emailDTO 邮件对象
     * @return Menu
     */
    @Override
    public R sendEmail(EmailDTO emailDTO) {
        return R.fail("发送邮件失败");
    }

    @Override
    public R sendAssessEventByMP(AssessEventDTO assessEventDTO) {
        return R.fail("发送事件提醒失败");
    }

    /**
     * 通过微信公众号发送告警通知
     *
     * @param alarmEventDTO
     * @return
     */
    @Override
    public R sendAlarmByMP(AlarmEventDTO alarmEventDTO) {
        return R.fail("发送告警提醒失败");
    }
}
