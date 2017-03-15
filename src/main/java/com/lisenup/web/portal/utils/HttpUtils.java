package com.lisenup.web.portal.utils;

import javax.servlet.http.HttpServletRequest;

public class HttpUtils {

	public static String getIp(HttpServletRequest request) {
		
		String ipAddress = request.getHeader("X-FORWARDED-FOR"); 
		if (ipAddress == null) {     
		    ipAddress = request.getRemoteAddr(); 
		}

		return ipAddress;
		
	}

}
