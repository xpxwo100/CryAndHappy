package com.waydeep.umuck;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.waydeep.umuck.dao")
@EnableTransactionManagement
public class UmuckApplication {
	public static void main(String[] args) {
		SpringApplication.run(UmuckApplication.class, args);
	}
}
