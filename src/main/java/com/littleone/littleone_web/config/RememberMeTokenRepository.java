package com.littleone.littleone_web.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.littleone.littleone_web.domain.RememberMe;
import com.littleone.littleone_web.repository.RememberMeRepository;

public class RememberMeTokenRepository implements PersistentTokenRepository {

	@Autowired
	RememberMeRepository repository;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		// TODO Auto-generated method stub
		RememberMe rm = new RememberMe(token);
		repository.save(rm);
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		// TODO Auto-generated method stub
		repository.updateToken(series, tokenValue, lastUsed);
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		// TODO Auto-generated method stub
		RememberMe rm = repository.findOne(seriesId);
		if(rm != null) {
			return new PersistentRememberMeToken(rm.getUsername(), rm.getSeries(), rm.getToken(), rm.getLast_used());
		} else {
			return null;
		}
	}

	@Override
	public void removeUserTokens(String username) {
		// TODO Auto-generated method stub
		repository.deleteByUsername(username);
	}

}
