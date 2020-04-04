package com.gowell.mes.mtr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gowell.mes.mtr.Constants;
import com.gowell.mes.mtr.Result;
import com.gowell.mes.mtr.model.DeviceEntity;
import com.gowell.mes.mtr.repository.DeviceJpaRepository;
import com.gowell.mes.mtr.utils.StatusManager;

@RestController
@RequestMapping(path = "/api/mtr/monitor")
public class MonitorController {
	@Autowired
	DeviceJpaRepository deviceRepository;

	@Transactional
	@RequestMapping("/apoweron")
	public Result asynPowerOn() {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_MONITOR);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		DeviceEntity device = devices.get(0);
		if (!StringUtils.isEmpty(device.getCmdstring())) {
			return new Result(-2);
		}

		if (device.getOnoff() != Constants.POWER_ON) {
			device.setStatus(Constants.COMMAND_SETUP);
			device.setCmdstring("" + Constants.POWER_ON);
			device.setTries(3);
			deviceRepository.save(device);
		}

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/apoweroff")
	public Result asynPowerOff() {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_MONITOR);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		DeviceEntity device = devices.get(0);
		if (!StringUtils.isEmpty(device.getCmdstring())) {
			return new Result(-2);
		}

		if (device.getOnoff() != Constants.POWER_OFF) {
			device.setStatus(Constants.COMMAND_SETUP);
			device.setCmdstring("" + Constants.POWER_OFF);
			device.setTries(3);
			deviceRepository.save(device);
		}

		return new StatusManager().getStatus(deviceRepository);
	}
}
