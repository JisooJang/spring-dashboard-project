package com.littleone.littleone_web.config;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ErrorConfig extends ServerProperties {
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		// TODO Auto-generated method stub	
		super.customize(container);
		ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/error/403");
		ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");
		ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");
		container.addErrorPages(error403Page, error404Page, error500Page);
	}
}
