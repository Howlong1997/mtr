package com.gowell.mes.mtr.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gowell.mes.mtr.Constants;
import com.gowell.mes.mtr.Result;
import com.gowell.mes.mtr.model.DeviceEntity;
import com.gowell.mes.mtr.repository.DeviceJpaRepository;
import com.gowell.mes.mtr.service.SocketApiService;
import com.gowell.mes.mtr.utils.StatusManager;

@RestController
@RequestMapping(path = "/api/mtr/lamp")
public class LampController {
	@Autowired
	DeviceJpaRepository deviceRepository;

	@Resource
	SocketApiService socketApiService;

	@Transactional
	@RequestMapping("/apoweron")
	public Result asynPowerOn(@RequestParam(required = true) String lampId) {
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(lampId, Constants.DEVICE_LAMP);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		DeviceEntity device = devices.get(0);
		// if (!StringUtils.isEmpty(device.getCmdstring())) {
		// return new Result(-2);
		// }

		// if (device.getOnoff() != Constants.POWER_ON) {
		device.setStatus(Constants.COMMAND_SETUP);
		device.setCmdstring("" + Constants.POWER_ON);
		device.setTries(3);
		deviceRepository.save(device);
		// }

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/apoweroff")
	public Result asynPowerOff(@RequestParam(required = true) String lampId) {
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(lampId, Constants.DEVICE_LAMP);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		DeviceEntity device = devices.get(0);
		// if (!StringUtils.isEmpty(device.getCmdstring())) {
		// return new Result(-2);
		// }

		// if (device.getOnoff() != Constants.POWER_OFF) {
		device.setStatus(Constants.COMMAND_SETUP);
		device.setCmdstring("" + Constants.POWER_OFF);
		device.setTries(3);
		deviceRepository.save(device);
		// }

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/agroupon")
	public Result asynGroupOn(@RequestParam(required = true) String groupId) {
		List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_LAMP);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		// DeviceEntity device = devices.get(0);
		// if (!StringUtils.isEmpty(device.getCmdstring())) {
		// return new Result(-2);
		// }

		for (DeviceEntity device : devices) {
			if (groupId.equals(device.getProperty2())) {
				// device.setStatus(Constants.COMMAND_SETUP);
				// device.setCmdstring("" + Constants.POWER_ON);
				// device.setTries(3);
				device.setOnoff(Constants.POWER_ON);
				device.setProperty8(null);
				device.setProperty9("" + System.currentTimeMillis());
				device.setStatus(null);
				device.setCmdstring(null);
				device.setTries(0);
				deviceRepository.save(device);
			}
		}

		String cmdString = groupId.equals("1") ? "M110FFFF" : "M210FFFF";
		if (!socketApiService.execute(cmdString))
			return new Result(-5);

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/agroupoff")
	public Result asynGroupOnOff(@RequestParam(required = true) String groupId) {
		List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_LAMP);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		// DeviceEntity device = devices.get(0);
		// if (!StringUtils.isEmpty(device.getCmdstring())) {
		// return new Result(-2);
		// }

		for (DeviceEntity device : devices) {
			if (groupId.equals(device.getProperty2())) {
				// device.setStatus(Constants.COMMAND_SETUP);
				// device.setCmdstring("" + Constants.POWER_OFF);
				// device.setTries(3);
				device.setOnoff(Constants.POWER_OFF);
				device.setProperty8(null);
				device.setProperty9("" + System.currentTimeMillis());
				device.setStatus(null);
				device.setCmdstring(null);
				device.setTries(0);
				deviceRepository.save(device);
			}
		}

		String cmdString = groupId.equals("1") ? "M100FFFF" : "M200FFFF";
		if (!socketApiService.execute(cmdString))
			return new Result(-5);

		return new StatusManager().getStatus(deviceRepository);
	}
}
