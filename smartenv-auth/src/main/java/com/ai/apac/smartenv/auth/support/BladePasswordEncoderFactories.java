/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.auth.support;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义密码工厂
 *
 * @author Chill
 */
public class BladePasswordEncoderFactories {

	/**
	 * Creates a {@link DelegatingPasswordEncoder} with default mappings. Additional
	 * mappings may be added and the encoding will be updated to conform with best
	 * practices. However, due to the nature of {@link DelegatingPasswordEncoder} the
	 * updates should not impact users. The mappings current are:
	 *
	 * <ul>
	 * <li>blade - {@link BladePasswordEncoder} (sha1(md5("password")))</li>
	 * <li>bcrypt - {@link BCryptPasswordEncoder} (Also used for encoding)</li>
	 * <li>noop - {@link BladeNoOpPasswordEncoder}</li>
	 * <li>pbkdf2 - {@link Pbkdf2PasswordEncoder}</li>
	 * <li>scrypt - {@link SCryptPasswordEncoder}</li>
	 * </ul>
	 *
	 * @return the {@link PasswordEncoder} to use
	 */
	@SuppressWarnings("deprecation")
	public static PasswordEncoder createDelegatingPasswordEncoder() {
		String encodingId = "blade";
		Map<String, PasswordEncoder> encoders = new HashMap<>(16);
		encoders.put(encodingId, new BladePasswordEncoder());
		encoders.put("bcrypt", new BCryptPasswordEncoder());
		encoders.put("noop", BladeNoOpPasswordEncoder.getInstance());
		encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
		encoders.put("scrypt", new SCryptPasswordEncoder());

		return new DelegatingPasswordEncoder(encodingId, encoders);
	}

	private BladePasswordEncoderFactories() {
	}

}
