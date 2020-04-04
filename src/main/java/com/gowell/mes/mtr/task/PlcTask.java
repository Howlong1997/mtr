package com.gowell.mes.mtr.task;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gowell.mes.mtr.service.SocketApiService;

@Component
@Configuration
@EnableScheduling
public class PlcTask {
	@Resource
	SocketApiService socketApiService;

	@Scheduled(initialDelay = 1000, fixedDelay = 200)
	private void configureTasks() {
		// System.out.println("PlcTask: " + JsonUtils.formatTime());
		socketApiService.execute();
	}
}
