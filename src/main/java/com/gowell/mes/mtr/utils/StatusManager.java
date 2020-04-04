package com.gowell.mes.mtr.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.gowell.mes.mtr.Constants;
import com.gowell.mes.mtr.Result;
import com.gowell.mes.mtr.model.DeviceEntity;
import com.gowell.mes.mtr.repository.DeviceJpaRepository;

/**
 * @author Billy
 */
public class StatusManager {
	public Result getStatus(DeviceJpaRepository deviceRepository, boolean mustIdle) {
		Map<String, String> data = null;

		while ((data = format(deviceRepository, mustIdle)) == null) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// System.out.println(JsonUtils.serializeWithoutException(data));
		return new Result(data);
	}

	public Result getStatus(DeviceJpaRepository deviceRepository) {
		return new Result();
	}

	private Map<String, String> format(DeviceJpaRepository deviceRepository, boolean mustIdle) {
		HashMap<String, String> data = new HashMap<String, String>();
		List<DeviceEntity> devices = deviceRepository.findAllByOrderByIdAsc();

		if (!CollectionUtils.isEmpty(devices)) {
			boolean notInAction = true;
			data.put("M0", "0");
			data.put("L0", "0");
			data.put("L0-Busy", "0");
			data.put("L0-1", "0");
			data.put("L0-2", "0");
			data.put("G0", "0");
			data.put("G0-0", "0");
			data.put("G0-1", "0");
			data.put("G0-2", "0");
			for (DeviceEntity device : devices) {
				boolean notInActionOne = true;
				if (!StringUtils.isEmpty(device.getCmdstring())
						&& !Constants.COMMAND_FINISHED.equals(device.getStatus()))
					notInActionOne = false;

				String name = device.getName();
				int onoff = device.getOnoff();
				data.put(name, "" + onoff);
				if (device.getCategory() != Constants.DEVICE_CURTAIN && onoff == Constants.POWER_ON
						|| device.getCategory() == Constants.DEVICE_CURTAIN && 2 == onoff) {
					data.put("M0", "1");
				}

				switch (device.getCategory()) {
				case Constants.DEVICE_LAMP:
					if (!notInActionOne)
						data.put("L0-Busy", "1");
					if (onoff == Constants.POWER_ON) {
						data.put("L0", "1");
						if ("1".equals(device.getProperty2()))
							data.put("L0-1", "1");
						if ("2".equals(device.getProperty2()))
							data.put("L0-2", "1");
					}
					break;
				case Constants.DEVICE_CURTAIN:
					// 幕布
					if (!"0".equals(device.getProperty1()))
						notInActionOne = false;
					break;
				case Constants.DEVICE_PROJECTOR:
					// 投影仪
					data.put(name + "-Brightness", device.getProperty1());
					data.put(name + "-Volume", device.getProperty2());
					data.put(name + "-Mute", device.getProperty3());
					break;
				case Constants.DEVICE_GLASS:
					// 玻璃
					if (Constants.DEVICE_GLASS_SPECIAL.equals(name))
						data.put(name, onoff == 0 ? "0" : device.getProperty1());
					else if (onoff == Constants.POWER_ON) {
						data.put("G0-0", "1");
						if ("1".equals(device.getProperty2()))
							data.put("G0-1", "1");
						if ("2".equals(device.getProperty2()))
							data.put("G0-2", "1");
					}
					break;
				}

				if (!notInActionOne) {
					if (mustIdle)
						return null;

					notInAction = false;
					data.put(name + "-Busy", "1");
				}
			}
			if (!notInAction)
				data.put("Devices-Busy", "1");
		}

		return data;
	}
}
