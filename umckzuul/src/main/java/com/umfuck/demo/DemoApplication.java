package com.umfuck.demo;

import com.umfuck.demo.listener.MyApplicationPreparedEventListener;
import com.umfuck.demo.listener.MyApplicationStartedEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DemoApplication.class);
		app.addListeners(new MyApplicationStartedEventListener());//启动监听
		app.addListeners(new MyApplicationPreparedEventListener());
		app.run(args);
	}
}
