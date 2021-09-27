package com.eureka.serviceregistery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableEurekaServer
@EnableHystrix
public class ServiceRegisteryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRegisteryApplication.class, args);
	}

}
