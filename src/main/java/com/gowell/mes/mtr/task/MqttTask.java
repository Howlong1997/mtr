package com.gowell.mes.mtr.task;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gowell.mes.mtr.service.MqttApiService;

@Component
@Configuration
@EnableScheduling
public class MqttTask {
	@Resource
	MqttApiService mqttApiService;

	@Scheduled(initialDelay = 1000, fixedDelay = 20)
	private void configureTasks() {
		mqttApiService.reconnect();
	}
}
