package com.lisenup.web.portal.utils;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lisenup.web.portal.models.AnonSession;
import com.lisenup.web.portal.models.AnonSessionRepository;

@Component
public class SessUtils {
	
	@Autowired
	private AnonSessionRepository anonSessionRepository; 

	public AnonSession getOrSetLuCookie(HttpServletRequest request, HttpServletResponse response, String anonCookie) {

		AnonSession returnVal = anonSessionRepository.findOne(anonCookie);
		
		if ( returnVal == null ) {
			returnVal = new AnonSession(UUID.randomUUID().toString(), HttpUtils.getIp(request));
			setLuCookie(request, response, returnVal.getSessId());
			anonSessionRepository.save(returnVal);
		} 
		
		return returnVal;
	}
	
	public AnonSession getOrSetLuCookieHard(HttpServletRequest request, HttpServletResponse response, String anonCookie) {
		
		AnonSession returnVal = anonSessionRepository.findOne(anonCookie);
		
		if ( returnVal == null ) {
			// generate new session
			returnVal = new AnonSession(UUID.randomUUID().toString(), HttpUtils.getIp(request));
			anonSessionRepository.save(returnVal);
		}

		// always hard reset session cookie to provided value
		setLuCookie(request, response, returnVal.getSessId());
		
		return returnVal;
	}
	
	private void setLuCookie(HttpServletRequest request, HttpServletResponse response, String anonCookie) {
		Cookie luCookie = new Cookie("lu", anonCookie);
		luCookie.setMaxAge(60 * 60 * 24 * 365 * 10); // 10 years
		luCookie.setPath("/");
		response.addCookie(luCookie);
	}

}
