package com.logparse.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.logparse.core.logic.parsers.AriannaParser;
import com.logparse.core.logic.parsers.GenericParser;

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan("com.*")
public class CoreApplication {

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.registerBean(AriannaParser.class);
		context.registerBean(GenericParser.class);
		context.refresh();
		SpringApplication.run(CoreApplication.class, args);
	}
}
