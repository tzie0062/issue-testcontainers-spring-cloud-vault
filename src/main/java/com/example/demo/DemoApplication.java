package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static final Logger LOG = LoggerFactory.getLogger(DemoApplication.class);
	@Value("${bar:n/a}")
	private String bar;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public DemoBean demoBean() {
		LOG.info("Got vault-supplied value: {}", bar);
		return new DemoBean(bar);
	}
}
