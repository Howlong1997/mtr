package com.gowell.mes.mtr.controller;


import java.awt.AWTException;
import java.util.List;
import java.util.Optional;


import javax.annotation.Resource;

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
import com.gowell.mes.mtr.model.ScenarioEntity;
import com.gowell.mes.mtr.repository.DeviceJpaRepository;
import com.gowell.mes.mtr.repository.ScenarioJpaRepository;
import com.gowell.mes.mtr.service.SocketApiService;
import com.gowell.mes.mtr.utils.StatusManager;

@RestController
@RequestMapping(path = "/api/mtr/switch")
public class SwitchController {
	@Autowired
	DeviceJpaRepository deviceRepository;

	@Autowired
	ScenarioJpaRepository scenerioRepository;

	@Resource
	SocketApiService socketApiService;



	@RequestMapping("/astatus")
	public Result asynStatus() {
		return new StatusManager().getStatus(deviceRepository, false);
	}

	@Transactional
	@RequestMapping("/apoweron")
	public Result asynPowerOn() {
		
		powerOn(Constants.DEVICE_LAMP, null);

		powerOn(Constants.DEVICE_MONITOR, null);

//		powerOn(Constants.DEVICE_GLASS, null);
		
		powerOn(Constants.DEVICE_PROJECTOR, null);
//		List<DeviceEntity> devices = deviceRepository.findAll();
//		if (!CollectionUtils.isEmpty(devices)) {
//			for (DeviceEntity device : devices) {
//				if (device.getCategory() == Constants.DEVICE_CURTAIN)
//					device.setOnoff(2);
//				else
//					device.setOnoff(Constants.POWER_ON);
//				device.setProperty8(null);
//				device.setProperty9("" + System.currentTimeMillis());
//				device.setStatus(null);
//				device.setCmdstring(null);
//				device.setTries(0);
//				deviceRepository.save(device);
//			}
//		}
		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/apoweroff")
	public Result asynPowerOff() {
		// powerOff(Constants.DEVICE_PROJECTOR, null);
		// powerOff(Constants.DEVICE_MONITOR, null);
		// powerOff(Constants.DEVICE_LAMP, null);
		// powerOff(Constants.DEVICE_GLASS, null);
//		List<DeviceEntity> devices = deviceRepository.findAll();
//		if (!CollectionUtils.isEmpty(devices)) {
//			for (DeviceEntity device : devices) {
//				if (device.getCategory() == Constants.DEVICE_CURTAIN)
//					device.setOnoff(Constants.POWER_ON);
//				else
//					device.setOnoff(Constants.POWER_OFF);
//				device.setProperty8(null);
//				device.setProperty9("" + System.currentTimeMillis());
//				device.setStatus(null);
//				device.setCmdstring(null);
//				device.setTries(0);
//				deviceRepository.save(device);
//			}
//		}
//		powerOff(Constants.DEVICE_CURTAIN, null);
		
		if (socketApiService.execute("M710FFFF"))
			return new StatusManager().getStatus(deviceRepository);
		powerOff(Constants.DEVICE_PROJECTOR, null);
		return new Result(-5);
	}

	@Transactional
	@RequestMapping("/abigpoweron")
	public Result asynBigPowerOn() {
		int lamp = lampGroupOn("1");
		if (lamp != 200) {
			return new Result(lamp);
		}
		powerOn(Constants.DEVICE_MONITOR, null);
		int glass = glassGroupOn("1");
		if (glass != 200) {
			return new Result(glass);
		}

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/abigpoweroff")
	public Result asynBigPowerOff() {
		int lamp = lampGroupOff("1");
		if (lamp != 200) {
			return new Result(lamp);
		}
		powerOff(Constants.DEVICE_MONITOR, null);
		int glass = glassGroupOff("1");
		if (glass != 200) {
			return new Result(glass);
		}

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/asmallpoweron")
	public Result asynSmallPowerOn() throws AWTException {
		int lamp = lampGroupOn("2");
		if (lamp != 200) {
			return new Result(lamp);
		}
		// powerOn(Constants.DEVICE_CURTAIN, null);
//		List<DeviceEntity> devices = deviceRepository.findAll();
//		if (!CollectionUtils.isEmpty(devices)) {
//			for (DeviceEntity device : devices) {
//				if (device.getCategory() == Constants.DEVICE_CURTAIN)
//					device.setOnoff(2);
//				else
//					device.setOnoff(Constants.POWER_ON);
//				device.setProperty8(null);
//				device.setProperty9("" + System.currentTimeMillis());
//				device.setStatus(null);
//				device.setCmdstring(null);
//				device.setTries(0);
//				deviceRepository.save(device);
//			}
//		}
		powerOn(Constants.DEVICE_PROJECTOR, null);
//		Integer i = asynEffect("2");

//		int glass = glassGroupOn("2");
//		if (glass != 200)
//			return new Result(glass);
//		}
//		if (i != 0) {
//			return new Result(i, "幕布运行异常");
//		}

		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/asmallpoweroff")
	public Result asynSmallPowerOff() throws AWTException {
//		CurtainController curtain=new CurtainController();
//		curtain.asynEffect("1");		
//		GlassController glass=new GlassController();
//		glass.asynGroupOff("2");
		int lamp = lampGroupOff("6");
		if (lamp != 200) {
			return new Result(lamp);
		}
		// powerOff(Constants.DEVICE_CURTAIN, null);
//		List<DeviceEntity> devices = deviceRepository.findAll();
//		if (!CollectionUtils.isEmpty(devices)) {
//			for (DeviceEntity device : devices) {
//				if (device.getCategory() == Constants.DEVICE_CURTAIN)
//					device.setOnoff(Constants.POWER_ON);
//				else
//					device.setOnoff(Constants.POWER_OFF);
//				device.setProperty8(null);
//				device.setProperty9("" + System.currentTimeMillis());
//				device.setStatus(null);
//				device.setCmdstring(null);
//				device.setTries(0);
//				deviceRepository.save(device);
//			}
//		}
		
		powerOff(Constants.DEVICE_PROJECTOR, null);
//		int glass = glassGroupOff("2");
//		if (glass != 200) {
//			return new Result(glass);
//		}
		return new StatusManager().getStatus(deviceRepository);
	}


	@Transactional
	@RequestMapping("/plc")
	public Result asynExecute(@RequestParam(required = true) String cmd) {
		if (socketApiService.execute(cmd))
			return new StatusManager().getStatus(deviceRepository);

		return new Result(-5);
	}

	@Transactional
	@RequestMapping("/aprojectoron")
	public Result asynProjectOn() {
		powerOn(Constants.DEVICE_PROJECTOR, null);
		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/aprojectoroff")
	public Result asynProjectOff() {
		powerOff(Constants.DEVICE_PROJECTOR, null);
		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/amonitoron")
	public Result asynMonitorOn() {
		powerOn(Constants.DEVICE_MONITOR, null);
		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/amonitoroff")
	public Result asynMonitorOff() {
		powerOff(Constants.DEVICE_MONITOR, null);
		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/alampon")
	public Result asynLampOn() {
		// powerOn(Constants.DEVICE_LAMP, null);
		// if (socketApiService.execute("M310FFFF"))
		// return new StatusManager().getStatus(deviceRepository);
		// return new Result();
		List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_LAMP);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		for (DeviceEntity device : devices) {
			device.setOnoff(Constants.POWER_ON);
			device.setProperty8(null);
			device.setProperty9("" + System.currentTimeMillis());
			device.setStatus(null);
			device.setCmdstring(null);
			device.setTries(0);
			deviceRepository.save(device);
		}

		String cmdString = "M310FFFF";
		if (socketApiService.execute(cmdString))
			return new StatusManager().getStatus(deviceRepository);

		return new Result(-5);
	}

	@Transactional
	@RequestMapping("/alampoff")
	public Result asynLampOff() {
		// powerOff(Constants.DEVICE_LAMP, null);
		// if (socketApiService.execute("M300FFFF"))
		// return new StatusManager().getStatus(deviceRepository);
		// return new Result(-5);
		// return new Result();
		List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_LAMP);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}

		for (DeviceEntity device : devices) {
			device.setOnoff(Constants.POWER_OFF);
			device.setProperty8(null);
			device.setProperty9("" + System.currentTimeMillis());
			device.setStatus(null);
			device.setCmdstring(null);
			device.setTries(0);
			deviceRepository.save(device);
		}

		String cmdString = "M300FFFF";
		if (socketApiService.execute(cmdString))
			return new StatusManager().getStatus(deviceRepository);

		return new Result(-5);
	}

	@Transactional
	@RequestMapping("/aglasson")
	public Result asynGlassOn() {
		powerOn(Constants.DEVICE_GLASS, null);
		return new StatusManager().getStatus(deviceRepository);
	}

	@Transactional
	@RequestMapping("/aglassoff")
	public Result asynGlassOff() {
		powerOff(Constants.DEVICE_GLASS, null);
		return new StatusManager().getStatus(deviceRepository);
	}

	private void powerOff(int category, String name) {
		List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(category);
		if (!CollectionUtils.isEmpty(devices)) {
			for (DeviceEntity device : devices) {
				if (!StringUtils.isEmpty(name) && !device.getName().equals(name))
					continue;

				device.setStatus(Constants.COMMAND_SETUP);
				if (device.getName().equals(Constants.DEVICE_GLASS_SPECIAL)) {
					device.setCmdstring("T0");
				} else if (device.getCategory() == Constants.DEVICE_CURTAIN) {
					device.setCmdstring("1");
				} else {
					if (device.getCategory() == Constants.DEVICE_GLASS && !StringUtils.isEmpty(device.getProperty2())
							&& "0".equals(device.getProperty2())) {
						continue;
					}
					device.setCmdstring("" + Constants.POWER_OFF);
				}
				device.setTries(3);
				deviceRepository.save(device);
			}
		}
	}

	private void powerOn(int category, String name) {
		List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(category);
		if (!CollectionUtils.isEmpty(devices)) {
			for (DeviceEntity device : devices) {
				if (!StringUtils.isEmpty(name) && !device.getName().equals(name)) {
					continue;
				}
				device.setStatus(Constants.COMMAND_SETUP);
				if (device.getName().equals(Constants.DEVICE_GLASS_SPECIAL)) {
					device.setCmdstring("T0");
				} else if (device.getCategory() == Constants.DEVICE_CURTAIN) {
					device.setCmdstring("2");
				} else {
					device.setCmdstring("" + Constants.POWER_ON);
				}
				device.setTries(3);
				deviceRepository.save(device);
			}
		}

	}

	private int lampGroupOn(String groupId) {
		List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_LAMP);
		if (CollectionUtils.isEmpty(devices)) {
			return -1;
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
			return -5;

		return 200;
	}

	private int lampGroupOff(String groupId) {
		List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_LAMP);
		if (CollectionUtils.isEmpty(devices)) {
			return -1;
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
			return -5;

		return 200;
	}

	private int glassGroupOn(String groupId) {
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
				Constants.DEVICE_GLASS);
		if (!CollectionUtils.isEmpty(devices)) {
			DeviceEntity device = devices.get(0);
			if (!StringUtils.isEmpty(device.getCmdstring())) {
				return -2;
			}
			String mode = device.getProperty1();
			if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
				return -3;
			}
		}

		devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_GLASS);
		if (CollectionUtils.isEmpty(devices)) {
			return -1;
		}

		for (DeviceEntity device : devices) {
			if (groupId.equals(device.getProperty2())) {
				device.setStatus(Constants.COMMAND_SETUP);
				device.setCmdstring("" + Constants.POWER_ON);
				device.setTries(3);
				deviceRepository.save(device);
			}
		}

		return 200;
	}

	private int glassGroupOff(String groupId) {
		List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
				Constants.DEVICE_GLASS);
		if (!CollectionUtils.isEmpty(devices)) {
			DeviceEntity device = devices.get(0);
			if (!StringUtils.isEmpty(device.getCmdstring())) {
				return -2;
			}
			String mode = device.getProperty1();
			if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
				return -3;
			}
		}

		devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_GLASS);
		if (CollectionUtils.isEmpty(devices)) {
			return -1;
		}

		for (DeviceEntity device : devices) {
			if (groupId.equals(device.getProperty2())) {
				device.setStatus(Constants.COMMAND_SETUP);
				device.setCmdstring("" + Constants.POWER_OFF);
				device.setTries(3);
				deviceRepository.save(device);
			}
		}

		return 200;
	}

	@Transactional
	@RequestMapping("/activate")
	public Result activate(@RequestParam(required = true) int id) {
		Optional<ScenarioEntity> scenario = scenerioRepository.findById(id);
		if (!scenario.isPresent()) {
			return new Result(-1);
		}

		ScenarioEntity data = scenario.get();
		int srcCategory = data.getCategory().intValue() / 10;

		List<ScenarioEntity> list = scenerioRepository.findAllByInusedOrderByIdAsc(Constants.POWER_ON);
		if (!CollectionUtils.isEmpty(list)) {
			for (ScenarioEntity entity : list) {
				if (!entity.getId().equals(data.getId())) {
					int category = entity.getCategory().intValue() / 10;
					if (srcCategory == category) {
						entity.setInused(Constants.POWER_OFF);
						scenerioRepository.save(entity);
					}
				}
			}
		}

		if (data.getInused() == Constants.POWER_OFF) {
			data.setInused(Constants.POWER_ON);
			scenerioRepository.save(data);
		}

		if (!StringUtils.isEmpty(data.getProjector())) {
			if ("1".equals(data.getProjector())) {
				powerOn(Constants.DEVICE_PROJECTOR, null);
			} else if ("0".equals(data.getProjector())) {
				powerOff(Constants.DEVICE_PROJECTOR, null);
			}
		}

		if (!StringUtils.isEmpty(data.getPlccmd())) {
			if (socketApiService.execute(data.getPlccmd()))
				return new StatusManager().getStatus(deviceRepository);

			return new Result(-5);
		}

		if (!StringUtils.isEmpty(data.getMonitor())) {
			if ("1".equals(data.getMonitor())) {
				powerOn(Constants.DEVICE_MONITOR, null);
			} else if ("0".equals(data.getMonitor())) {
				powerOff(Constants.DEVICE_MONITOR, null);
			}
		}

		if (!StringUtils.isEmpty(data.getCurtain())) {
			if ("2".equals(data.getCurtain())) {
				powerOn(Constants.DEVICE_CURTAIN, null);
			} else if ("1".equals(data.getCurtain())) {
				powerOff(Constants.DEVICE_CURTAIN, null);
			}
		}

		if (!StringUtils.isEmpty(data.getLamp())) {
			String lampSetting = data.getLamp().trim();
			for (int i = 0, count = lampSetting.length(); i < count; i++) {
				if ("1".equals(lampSetting.substring(i, i + 1))) {
					powerOn(Constants.DEVICE_LAMP, "L" + (i + 1));
				} else if ("0".equals(lampSetting.substring(i, i + 1))) {
					powerOff(Constants.DEVICE_LAMP, "L" + (i + 1));
				}
			}
		}

		if (!StringUtils.isEmpty(data.getGlass())) {
			String glassSetting = data.getGlass().trim();
			if (glassSetting.startsWith("T")) {
				List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
						Constants.DEVICE_GLASS);
				if (!CollectionUtils.isEmpty(devices)) {
					for (DeviceEntity device : devices) {
						device.setStatus(Constants.COMMAND_SETUP);
						device.setCmdstring(glassSetting);
						device.setTries(3);
						deviceRepository.save(device);
					}
				}
			} else {
				if ("1".equals(glassSetting)) {
					powerOn(Constants.DEVICE_GLASS, null);
				} else if ("0".equals(glassSetting)) {
					powerOff(Constants.DEVICE_GLASS, null);
				}
			}
		}

		return new StatusManager().getStatus(deviceRepository);
	}
}
