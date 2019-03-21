package com.littleone.littleone_web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BottleController {
	// smart-bottle 정보 제공
	
	@GetMapping("/device/smartbottle")
	public String getBottle() {
		return "product/bottle";
	}


	@GetMapping("/device/smartpeepee")
	public String getPeePee(){ return "product/peepee";}

	@GetMapping("/device/smarttemp")
	public String getTemp(){ return "product/temp";}

}
