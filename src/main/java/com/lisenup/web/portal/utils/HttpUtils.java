package com.lisenup.web.portal.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

public class HttpUtils {

	public static String getIp(HttpServletRequest request) {
		
		String ipAddress = request.getHeader("X-FORWARDED-FOR"); 
		if (ipAddress == null) {     
		    ipAddress = request.getRemoteAddr(); 
		}

		return ipAddress;
		
	}

	public static String getGravatarUrl(String email) {
		String emailLower = email.toLowerCase();
		String hashtext = "";
		
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(emailLower.getBytes("UTF-8"));
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashtext.length() < 32 ){
			  hashtext = "0"+hashtext;
			}
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return hashtext;
	}

}
