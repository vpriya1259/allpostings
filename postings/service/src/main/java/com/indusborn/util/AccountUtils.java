/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indusborn.util;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.indusborn.ui.domain.UserVO;


/**
 * Some static helper methods related to Accounts.
 * @author Keith Donald
 */
public final class AccountUtils {

	/**
	 * Programatically sign-in the member holding the provided Account.
	 * Sets the Account in the SecurityContext, which will associate this Account with the user Session.
	 */
	public static void signin(UserVO userVO) {
		SecurityContextHolder.getContext().setAuthentication(authenticationTokenFor(userVO));
	}
	
	/**
	 * Construct a Spring Security Authentication token from an Account object.
	 * Useful for treating the Account as a Principal in Spring Security.
	 */
	public static Authentication authenticationTokenFor(UserVO userVO) {
		return new UsernamePasswordAuthenticationToken(userVO, null, (Collection<GrantedAuthority>)null);		
	}
	
	private AccountUtils() {
	}
}
