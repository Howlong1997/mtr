package com.gowell.mes.mtr.controller;

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
import com.gowell.mes.mtr.utils.StatusManager;

@RestController
@RequestMapping(path = "/api/mtr/glass")
public class GlassController {
	@Autowired
	DeviceJpaRepository deviceRepository;

	@Transactional
	@RequestMapping("/apoweron")
	public Result asynPowerOn(@RequestParam(required = true) String glassId) {
		DeviceEntity device = null;
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
				Constants.DEVICE_GLASS);
		if (!CollectionUtils.isEmpty(devices)) {
			device = devices.get(0);
			if (!StringUtils.isEmpty(device.getCmdstring())) {
				return new Result(-2);
			}
			String mode = device.getProperty1();
			if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
				return new Result(-3);
			}
		}

		devices = deviceRepository.findAllByNameAndCategory(glassId, Constants.DEVICE_GLASS);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		device = devices.get(0);
		//if (!StringUtils.isEmpty(device.getCmdstring())) {
		//	return new Result(-2);
		//}

		//if (device.getOnoff() != Constants.POWER_ON) {
			device.setStatus(Constants.COMMAND_SETUP);
			device.setCmdstring("" + Constants.POWER_ON);
			device.setTries(3);
			deviceRepository.save(device);
		//}

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/apoweroff")
	public Result asynPowerOff(@RequestParam(required = true) String glassId) {
		DeviceEntity device = null;
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
				Constants.DEVICE_GLASS);
		if (!CollectionUtils.isEmpty(devices)) {
			device = devices.get(0);
			if (!StringUtils.isEmpty(device.getCmdstring())) {
				return new Result(-2);
			}
			String mode = device.getProperty1();
			if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
				return new Result(-3);
			}
		}

		devices = deviceRepository.findAllByNameAndCategory(glassId, Constants.DEVICE_GLASS);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		device = devices.get(0);
		//if (!StringUtils.isEmpty(device.getCmdstring())) {
		//	return new Result(-2);
		//}

		//if (device.getOnoff() != Constants.POWER_OFF) {
			device.setStatus(Constants.COMMAND_SETUP);
			device.setCmdstring("" + Constants.POWER_OFF);
			device.setTries(3);
			deviceRepository.save(device);
		//}

		return new StatusManager().getStatus(deviceRepository);
	}

	@RequestMapping("/aeffectoff")
	public Result asynEffectOff() {
		DeviceEntity device = null;
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
				Constants.DEVICE_GLASS);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		device = devices.get(0);
		device.setStatus(Constants.COMMAND_SETUP);
		device.setCmdstring("T0");
		device.setTries(3);
		deviceRepository.save(device);

		return new StatusManager().getStatus(deviceRepository);
	}

	@RequestMapping("/aeffecton")
	public Result asynEffectOn(@RequestParam(required = true) String effectId) {
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
				Constants.DEVICE_GLASS);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_GLASS);
		for (DeviceEntity device : devices) {
			if (device.getName().equals(Constants.DEVICE_GLASS_SPECIAL)) {
				device.setStatus(Constants.COMMAND_SETUP);
				device.setCmdstring("T" + effectId);
				device.setTries(3);
				deviceRepository.save(device);
			} else {
				device.setOnoff(Constants.POWER_OFF);
				device.setStatus(null);
				device.setCmdstring(null);
				device.setTries(0);
				deviceRepository.save(device);
			}
		}

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/agroupon")
	public Result asynGroupOn(@RequestParam(required = true) String groupId) {
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
				Constants.DEVICE_GLASS);
		if (!CollectionUtils.isEmpty(devices)) {
			DeviceEntity device = devices.get(0);
			if (!StringUtils.isEmpty(device.getCmdstring())) {
				return new Result(-2);
			}
			String mode = device.getProperty1();
			if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
				return new Result(-3);
			}
		}

		devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_GLASS);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		for (DeviceEntity device : devices) {
			if (groupId.equals(device.getProperty2())) {
				device.setStatus(Constants.COMMAND_SETUP);
				device.setCmdstring("" + Constants.POWER_ON);
				device.setTries(3);
				deviceRepository.save(device);
			}
		}

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/agroupoff")
	public Result asynGroupOff(@RequestParam(required = true) String groupId) {
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
				Constants.DEVICE_GLASS);
		if (!CollectionUtils.isEmpty(devices)) {
			DeviceEntity device = devices.get(0);
			if (!StringUtils.isEmpty(device.getCmdstring())) {
				return new Result(-2);
			}
			String mode = device.getProperty1();
			if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
				return new Result(-3);
			}
		}

		devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_GLASS);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		for (DeviceEntity device : devices) {
			if (groupId.equals(device.getProperty2())) {
				device.setStatus(Constants.COMMAND_SETUP);
				device.setCmdstring("" + Constants.POWER_OFF);
				device.setTries(3);
				deviceRepository.save(device);
			}
		}

		return new StatusManager().getStatus(deviceRepository);
	}
}
