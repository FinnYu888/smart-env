package com.ai.apac.smartenv.device.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author qianlong
 * @description 号码本对象
 * @Date 2020/5/15 2:31 下午
 **/
@Data
public class PhoneBookDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 号码
     */
    private String phoneNumber;

    /**
     * 联系人姓名
     */
    private String contactName;

    public PhoneBookDTO(String phoneNumber, String contactName) {
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
    }
}
