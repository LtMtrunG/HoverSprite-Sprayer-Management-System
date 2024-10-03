package com.group12.springboot.hoversprite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class }, scanBasePackages = {"repository","controller","service","entity"})
@SpringBootApplication
public class HoverspriteApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoverspriteApplication.class, args);
	}

}
