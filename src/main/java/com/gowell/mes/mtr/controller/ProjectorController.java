package com.gowell.mes.mtr.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gowell.mes.mtr.Constants;
import com.gowell.mes.mtr.Result;
import com.gowell.mes.mtr.model.DeviceEntity;
import com.gowell.mes.mtr.repository.DeviceJpaRepository;

@RestController
@RequestMapping(path = "/api/mtr/projector")
public class ProjectorController {
	@Autowired
	DeviceJpaRepository deviceRepository;

	@Transactional
	@RequestMapping("/apoweron")
	public Result asynPowerOn() {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
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
		return new Result();
	}

	@Transactional
	@RequestMapping("/apoweroff")
	public Result asynPowerOff() {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
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
		return new Result();
	}

	@Transactional
	@RequestMapping("/abright")
	public Result asynBrightup(@RequestParam int brightness) {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		DeviceEntity device = devices.get(0);
		if (!StringUtils.isEmpty(device.getCmdstring())) {
			return new Result(-2);
		}

		if (device.getOnoff() != Constants.POWER_ON) {
			return new Result(-4);
		}

		device.setStatus(Constants.COMMAND_SETUP);
		device.setCmdstring("B" + brightness);
		device.setTries(3);
		deviceRepository.save(device);
		return new Result();
	}

	@Transactional
	@RequestMapping("/avolume")
	public Result asynVolume(@RequestParam int volume) {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		DeviceEntity device = devices.get(0);
		if (!StringUtils.isEmpty(device.getCmdstring())) {
			return new Result(-2);
		}

		if (device.getOnoff() != Constants.POWER_ON) {
			return new Result(-4);
		}

		device.setStatus(Constants.COMMAND_SETUP);
		device.setCmdstring("V" + volume);
		device.setTries(3);
		deviceRepository.save(device);
		return new Result();
	}

	@Transactional
	@RequestMapping("/amuteon")
	public Result asynMuteOn() {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		DeviceEntity device = devices.get(0);
		if (!StringUtils.isEmpty(device.getCmdstring())) {
			return new Result(-2);
		}

		if (device.getOnoff() != Constants.POWER_ON) {
			return new Result(-4);
		}

		device.setStatus(Constants.COMMAND_SETUP);
		device.setCmdstring("M1");
		device.setTries(3);
		deviceRepository.save(device);
		return new Result();
	}

	@Transactional
	@RequestMapping("/amuteoff")
	public Result asynMuteOff() {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		DeviceEntity device = devices.get(0);
		if (!StringUtils.isEmpty(device.getCmdstring())) {
			return new Result(-2);
		}

		if (device.getOnoff() != Constants.POWER_ON) {
			return new Result(-4);
		}

		device.setStatus(Constants.COMMAND_SETUP);
		device.setCmdstring("M0");
		device.setTries(3);
		deviceRepository.save(device);
		return new Result();
	}

	@RequestMapping("/astatus")
	public Result asynStatus() {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		DeviceEntity device = devices.get(0);
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("onoff", "" + device.getOnoff());
		if (device.getOnoff() == Constants.POWER_ON) {
			data.put("brightness", device.getProperty1());
			data.put("volumn", device.getProperty2());
			data.put("mute", device.getProperty3());
		}

		Result result = new Result();
		result.setData(data);
		return result;
	}
}
