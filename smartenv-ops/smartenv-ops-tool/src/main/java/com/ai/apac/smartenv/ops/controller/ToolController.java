package com.ai.apac.smartenv.ops.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/11 8:34 上午
 **/
@RestController
public class ToolController {

    @GetMapping("/ops/sayHello")
    public String sayHello(){
        return "hello";
    }
}
