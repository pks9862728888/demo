package com.example.demo.applications;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.demo.package.*")
public class DemoContainerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DemoContainerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello world from main app");
		System.exit(0);
	}
}
