package com.umfuck.demo;

import com.umfuck.demo.listener.MyApplicationPreparedEventListener;
import com.umfuck.demo.listener.MyApplicationStartedEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DemoApplication.class);
		app.addListeners(new MyApplicationStartedEventListener());//启动监听
		app.addListeners(new MyApplicationPreparedEventListener());
		app.run(args);
	}
}
