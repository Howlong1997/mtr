package com.gowell.mes.mtr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@SpringBootApplication
public class MtrApplication {
	public static void main(String[] args) {
		SpringApplication.run(MtrApplication.class, args);
	}
}
