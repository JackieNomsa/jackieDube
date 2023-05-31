package com.eviro.assessment.grad001.jackieDube;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = "com/eviro/assessment/grad001/jackieDube")
@Configuration
@SpringBootApplication
public class JackieDubeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JackieDubeApplication.class, args);
	}

}
