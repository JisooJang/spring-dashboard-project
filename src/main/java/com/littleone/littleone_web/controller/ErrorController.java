package com.littleone.littleone_web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ErrorController {

	/*@ExceptionHandler(Throwable.class)
    public void handleServerError(HttpServletRequest req, Exception ex, Model model) {
        //todo      
		model.addAttribute("errorMessage", ex.getMessage());
    }*/
	
	@RequestMapping("/error/404") 
	public String error404(HttpServletRequest request) {
		return "/error/404page"; 
	}
	
	@RequestMapping("/error/403") 
	public String error403(HttpServletRequest request) {
		return "/error/403page"; 
	}
	
	@RequestMapping("/error/500") 
	public String error500(HttpServletRequest request) {
		return "/error/500page"; 
	}

}
