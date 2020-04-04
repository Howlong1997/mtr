package com.gowell.mes.mtr.task;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.gowell.mes.mtr.Constants;
import com.gowell.mes.mtr.model.DeviceEntity;
import com.gowell.mes.mtr.utils.JsonUtils;

/*
M11: L11 L21 ON/OFF
M21: L31 L41 ON/OFF
M31: L11 L21 L31 L41 ON/OFF
M41: C21 S11 L10 L20 L30 L40
M51: S11 L10 L20
M61: C21 L30 L40
M71: ALL OFF
 */
public class DeviceStatus implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4732612419689296495L;

	@Override
	public String toString() {
		return JsonUtils.serializeWithoutException(this);
	}

	/**
	 * 灯光
	 */
	private String lampStatus = "";

	/**
	 * 大屏
	 */
	private String monitorStatus = "";

	/**
	 * 幕布
	 */
	private String curtainStatus = "";

	/**
	 * 玻璃特效
	 */
	private String glassEffect = "";

	/**
	 * 玻璃
	 */
	private String glassStatus = "";

	/**
	 * 重置
	 */
	public void reset() {
		monitorStatus = "";
		curtainStatus = "";
		glassEffect = "";
		glassStatus = "";
	}

	/**
	 * 状态解析
	 * 
	 * @param status
	 */
	public boolean parse(String status) {
		if (StringUtils.isEmpty(status))
			return false;

		// 按指定模式在字符串查找
		String pattern = "S:([01])\\s*?;C:([012])\\s*?;L:([0-9]+)\\s*?;G(:[0-9]+|T[0-9]:[0-9]+)\\s*?;";
		// 创建 Pattern 对象
		Pattern r = Pattern.compile(pattern);
		// 现在创建 matcher 对象
		Matcher m = r.matcher(status);

		if (!m.find()) {
			return false;
		}

		// 大屏
		monitorStatus = m.group(1);

		// 幕布
		curtainStatus = m.group(2);

		// 灯光
		lampStatus = Integer.toBinaryString(Integer.parseInt(m.group(3)));
		lampStatus = "0000" + lampStatus;
		lampStatus = lampStatus.substring(lampStatus.length() - 4);

		// 玻璃
		String mode = m.group(4);
		if (mode.startsWith("T")) {
			glassEffect = mode.substring(1, 2);
			glassStatus = mode.substring(3);
		} else {
			glassEffect = "0";
			glassStatus = mode.substring(1);
		}
		glassStatus = Integer.toBinaryString(Integer.parseInt(glassStatus));
		glassStatus = "00000000" + glassStatus;
		glassStatus = glassStatus.substring(glassStatus.length() - 8);

		return true;
	}

	/**
	 * 格式化数据
	 * 
	 * @return
	 */
	private Map<String, String> format() {
		HashMap<String, String> data = new HashMap<String, String>();

		// 灯光
		if (!StringUtils.isEmpty(lampStatus)) {
			data.put("L4", lampStatus.substring(0, 1));
			data.put("L3", lampStatus.substring(1, 2));
			data.put("L2", lampStatus.substring(2, 3));
			data.put("L1", lampStatus.substring(3, 4));
		}

		// 玻璃
		if (!StringUtils.isEmpty(glassStatus)) {
			data.put("G0", glassEffect);
			data.put("G8", glassStatus.substring(0, 1));
			data.put("G7", glassStatus.substring(1, 2));
			data.put("G6", glassStatus.substring(2, 3));
			data.put("G5", glassStatus.substring(3, 4));
			data.put("G4", glassStatus.substring(4, 5));
			data.put("G3", glassStatus.substring(5, 6));
			data.put("G2", glassStatus.substring(6, 7));
			data.put("G1", glassStatus.substring(7, 8));
		}

		return data;
	}

	/**
	 * 设置状态
	 * 
	 * @param devices
	 * @return
	 */
	public boolean setDevice(List<DeviceEntity> devices) {
		Map<String, String> status = format();
		if (!CollectionUtils.isEmpty(devices)) {
			for (DeviceEntity device : devices) {
				String name = device.getName();
				switch (device.getCategory()) {
				case Constants.DEVICE_MONITOR:
					device.setOnoff(Integer.parseInt(monitorStatus));
					break;
				case Constants.DEVICE_CURTAIN:
					device.setProperty1(curtainStatus);
					break;
				case Constants.DEVICE_LAMP:
				case Constants.DEVICE_GLASS:
					if (status.containsKey(name)) {
						if (Constants.DEVICE_GLASS_SPECIAL.equals(name)) {
							device.setProperty1(status.get(name));
							device.setOnoff("0".equals(device.getProperty1()) ? 0 : 1);
						} else {
							device.setOnoff(Integer.parseInt(status.get(name)));
						}
					}
					break;
				}
			}
		}
		return true;
	}
}
