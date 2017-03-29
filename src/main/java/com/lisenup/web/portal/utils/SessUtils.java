package com.lisenup.web.portal.utils;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lisenup.web.portal.models.AnonSession;
import com.lisenup.web.portal.models.AnonSessionRepository;

@Component
public class SessUtils {
	
	@Autowired
	private AnonSessionRepository anonSessionRepository; 

	public AnonSession getOrSetLuCookie(HttpServletResponse response, String anonCookie) {

		AnonSession returnVal = anonSessionRepository.findOne(anonCookie);
		
		if ( returnVal == null ) {
			returnVal = new AnonSession(UUID.randomUUID().toString());
			Cookie luCookie = new Cookie("lu", returnVal.getSessId());
			luCookie.setMaxAge(60 * 60 * 24 * 365 * 10); // 10 years
			luCookie.setPath("/");
			anonSessionRepository.save(returnVal);
			response.addCookie(luCookie);
		} 
		
		return returnVal;
	}

}
