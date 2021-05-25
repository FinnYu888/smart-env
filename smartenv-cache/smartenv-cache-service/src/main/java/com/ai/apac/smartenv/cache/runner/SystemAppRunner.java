package com.ai.apac.smartenv.cache.runner;

import com.ai.apac.smartenv.alarm.cache.AlarmRuleInfoCache;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.system.cache.*;
import com.ai.apac.smartenv.system.entity.CityWeather;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/11 10:23 下午
 **/
@Component
@Order(value=9)
public class SystemAppRunner implements CommandLineRunner {

    @Override
    public void run(String... strings) throws Exception {
//        try {
//            //将所有角色信息加载到缓存中去
//            RoleCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            //将所有部门信息加载到缓存中去
//            DeptCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            //将所有菜单加载到缓存中去
//            MenuCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            TenantCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            DictCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            DictBizCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            PersonCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            CityCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            VehicleCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            ScheduleCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            //将所有告警规则加载到缓存中去
//            AlarmRuleInfoCache.reload();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
