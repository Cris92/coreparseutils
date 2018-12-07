package com.logparse.core.property;

import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

import com.logparse.core.logic.parsers.AriannaParser;
import com.logparse.core.logic.parsers.GenericParser;

@org.springframework.context.annotation.Configuration
public class Configuration {

	
	@Bean(name="ariannaParser")
	public AriannaParser ariannaParser() {
		return new AriannaParser();
	}
	
	@Bean(name="genericParser")
	public GenericParser genericParser() {
		return new GenericParser();
	}

}
