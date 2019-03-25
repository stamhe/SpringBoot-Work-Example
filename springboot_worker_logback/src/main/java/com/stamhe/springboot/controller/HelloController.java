package com.stamhe.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping("/world")
	public String worldAction()
	{
		logger.trace("trace 日志");
		logger.debug("debug 日志");
		logger.info("info 日志");
		logger.warn("warn 日志");
		logger.error("error 日志");
		return "Hello World";
	}
}
