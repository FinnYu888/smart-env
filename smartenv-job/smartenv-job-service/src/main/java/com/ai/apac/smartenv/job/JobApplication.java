package com.ai.apac.smartenv.job;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.job.mq.JobProducerSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/6 2:32 下午
 **/
@EnableBladeFeign
@Configuration
@EnableScheduling
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@EnableBinding(JobProducerSource.class)
@SpringCloudApplication
public class JobApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_JOB_NAME, JobApplication.class, args);
    }

}
