package com.ai.apac.smartenv.websocket.wrapper;

import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.enums.PersonStatusEnum;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.omnic.entity.PicStatus;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonInfoVO;

import static com.ai.apac.smartenv.common.constant.PersonConstant.PersonStatus.OFF_ONLINE_ALARM;

/**
 * @author qianlong
 * @description 视图包装类
 * @Date 2020/3/5 3:18 下午
 **/
public class PersonInfoWrapper {

    public static PersonInfoWrapper build() {
        return new PersonInfoWrapper();
    }

    public PersonInfoVO entityVO(Person person, DeviceInfo deviceInfo, PicStatus picStatus) {
        if(person == null || deviceInfo == null){
            return null;
        }
        PersonInfoVO personInfo = new PersonInfoVO();
        personInfo.setDeviceId(String.valueOf(deviceInfo.getId()));
        personInfo.setDeviceCode(deviceInfo.getDeviceCode());
        personInfo.setPersonId(String.valueOf(person.getId()));
        personInfo.setPersonName(person.getPersonName());
        if(picStatus != null){
            personInfo.setStatus(picStatus.getPicStatus());
            personInfo.setStatusName(PersonStatusEnum.getDescByValue(personInfo.getStatus()));
        }
        personInfo.setShowFlag(false);
        String statusImg = PersonCache.getPersonStatusImg(personInfo.getStatus());
        personInfo.setIcon(statusImg);
        return personInfo;
    }

}
