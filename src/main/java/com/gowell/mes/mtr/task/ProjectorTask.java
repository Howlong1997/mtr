package com.gowell.mes.mtr.task;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.gowell.mes.mtr.Constants;
import com.gowell.mes.mtr.config.HttpResult;
import com.gowell.mes.mtr.model.DeviceEntity;
import com.gowell.mes.mtr.repository.DeviceJpaRepository;
import com.gowell.mes.mtr.service.HttpApiService;

@Component
@Configuration
@EnableScheduling
@EnableAsync
public class ProjectorTask {
	@Resource
	private HttpApiService httpApiService;

	@Autowired
	DeviceJpaRepository deviceRepository;

	@Value("${projector.origin}")
	private String origin;

	@Value("${projector.status}")
	private String status;

	@Value("${projector.control}")
	private String control;

	@Async
	@Scheduled(initialDelay = 1000, fixedDelay = 1500)
	public void configureTasks() {
		// System.out.println("ProjectorTask: " + JsonUtils.formatTime());
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
		if (!CollectionUtils.isEmpty(devices)) {
			boolean inAction = false;
			String status = null;
			for (DeviceEntity device : devices) {
				if (!StringUtils.isEmpty(device.getCmdstring())) {
					inAction = true;
					if (Constants.COMMAND_SETUP.equals(device.getStatus())
							|| (Constants.COMMAND_FAILED.equals(device.getStatus())
									&& device.getTries().intValue() > 0)) {
						device.setStatus(Constants.COMMAND_ACTING);
						device.setProperty9("" + System.currentTimeMillis());
						deviceRepository.save(device);
						// System.out.println("status = startAction(device)");
						status = startAction(device);
					} else if (Constants.COMMAND_FINISHED.equals(device.getStatus())) {
						int tries = device.getTries().intValue() - 1;
						if (tries > 0) {
							device.setTries(tries);
						} else {
							device.setStatus(null);
							device.setCmdstring(null);
							device.setTries(0);
						}
						device.setProperty9("" + System.currentTimeMillis());
						deviceRepository.save(device);
					}
				}
			}

			if (!inAction && StringUtils.isEmpty(status)) {
				status = getStatus();
			}

			if (!StringUtils.isEmpty(status)) {
				for (DeviceEntity device : devices) {
					if (StringUtils.isEmpty(device.getCmdstring())) {
						if (parseStatus(device, status)) {
							// System.out.println("saveStatus");
							saveStatus(device);
						} else {
							status = getStatus();
							if (!StringUtils.isEmpty(status) && parseStatus(device, status)) {
								// System.out.println("saveStatus");
								saveStatus(device);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 执行投影仪开关/亮度动作
	 * 
	 * @param device
	 * @return
	 */
	private String startAction(DeviceEntity device) {
		int maxTries = 2;
		while (maxTries-- > 0) {
			HashMap<String, Object> headers = new HashMap<String, Object>();
			headers.put("Origin", origin);
			headers.put("Referer", status);

			HashMap<String, Object> map = new HashMap<String, Object>();
			String cmdString = device.getCmdstring();
			if (cmdString.equals("" + Constants.POWER_ON)) {
				map.put("PowerOn", "Power ON ");
			} else if (cmdString.equals("" + Constants.POWER_OFF)) {
				map.put("PowerOff", "Power OFF ");
			} else if (cmdString.startsWith("B")) {
				Integer brightness = new Integer(cmdString.substring(1));
				map.put("Bright", brightness);
				map.put("Contrast", brightness);
				map.put("Volume", brightness);
				map.put("btnBright", "Set ");
			} else if (cmdString.startsWith("V")) {
				Integer volume = new Integer(cmdString.substring(1));
				map.put("Bright", volume);
				map.put("Contrast", volume);
				map.put("Volume", volume);
				map.put("btnVol", "Set ");
			} else if (cmdString.startsWith("M")) {
				int volumn = 85;
				if (cmdString.startsWith("M0")) {
					volumn = 170;
				}

				map.put("PJSTATE", "ISO-8859-1");
				map.put("DSP_SOURCE", new Integer(1));
				map.put("ERRORSTA", new Integer(112));
				map.put("FREEZE0", "");
				map.put("HIDE0", new Integer(112));
				map.put("inp_objname", "");
				map.put("inp_objvalue", new Integer(0));
				map.put("redio_objname", "Spk");
				map.put("radio_objvalue", new Integer(volumn));
				map.put("btnRadioChg", "");
				map.put("HIDE007", "");
				map.put("HIDE008", "S-Video ");
				map.put("Spk", new Integer(volumn));
			}

			// System.out.println("Projector startAction: " + cmdString);
			try {
				HttpResult response = httpApiService.doPost(control, headers, map);
				if (response.getCode() == HttpStatus.SC_OK) {
					if (cmdString.startsWith("B")) {
						device.setProperty1(cmdString.substring(1));
					}
					if (cmdString.startsWith("V")) {
						device.setProperty2(cmdString.substring(1));
					}
					if (cmdString.startsWith("M")) {
						device.setProperty3(cmdString.substring(1).equals("0") ? "off" : "on");
					}
					device.setStatus(Constants.COMMAND_FINISHED);
					if (cmdString.equals("" + Constants.POWER_ON)) {
						device.setTries(5);
					} else {
						device.setTries(1);
					}
					device.setProperty9("" + System.currentTimeMillis());
					deviceRepository.save(device);
					return null;
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Projector startAction failed: " + cmdString);
			}
		}

		int tries = device.getTries().intValue() - 1;
		if (tries > 0) {
			device.setStatus(Constants.COMMAND_FAILED);
			device.setTries(tries);
		} else {
			device.setStatus(null);
			device.setCmdstring(null);
			device.setTries(0);
		}
		device.setProperty9("" + System.currentTimeMillis());
		deviceRepository.save(device);
		return null;
	}

	/**
	 * 查询投影仪开关/亮度开关状态
	 * 
	 * @return
	 */
	private String getStatus() {
		try {
			return httpApiService.doGet(status);
		} catch (Exception e) {
			// e.printStackTrace();
			// System.out.println("Projector getStatus failed");
		}
		return null;
	}

	private boolean parseStatus(DeviceEntity device, String response) {
		// 按指定模式在字符串查找
		String pattern1 = "NAME=\"PJSTATE2\".+?VALUE=\"(.*?)\"";
		// 创建 Pattern 对象
		Pattern r1 = Pattern.compile(pattern1, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		// 现在创建 matcher 对象
		Matcher m1 = r1.matcher(response);

		int onoff = Constants.POWER_ON;
		if (m1.find()) {
			// System.out.println(m1.group(0));
			String status = m1.group(1).toLowerCase();
			if (status.startsWith("standby")) {
				onoff = Constants.POWER_OFF;
			}
		} else {
			return false;
		}

		String brightness = null;
		if (onoff == Constants.POWER_ON) {
			// 按指定模式在字符串查找
			String pattern2 = "NAME=\"Bright\".+?VALUE=\"(.*?)\"";
			// 创建 Pattern 对象
			Pattern r2 = Pattern.compile(pattern2, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
			// 现在创建 matcher 对象
			Matcher m2 = r2.matcher(response);

			if (m2.find()) {
				// System.out.println(m2.group(0));
				try {
					brightness = m2.group(1);
					// System.out.println(brightness);
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			} else {
				return false;
			}
		}

		String volume = null;
		// 按指定模式在字符串查找
		String pattern3 = "NAME=\"Volume\".+?VALUE=\"(.*?)\"";
		// 创建 Pattern 对象
		Pattern r3 = Pattern.compile(pattern3, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		// 现在创建 matcher 对象
		Matcher m3 = r3.matcher(response);

		if (m3.find()) {
			// System.out.println(m3.group(0));
			try {
				volume = m3.group(1);
				// System.out.println(volume);
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
		} else {
			return false;
		}

		String mute = null;
		// 按指定模式在字符串查找
		String pattern4 = "NAME=\"Spk\".+?CHECKED.+?(Off|On)";
		// 创建 Pattern 对象
		Pattern r4 = Pattern.compile(pattern4, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		// 现在创建 matcher 对象
		Matcher m4 = r4.matcher(response);

		if (m4.find()) {
			// System.out.println(m4.group(0));
			try {
				mute = m4.group(1).toLowerCase();
				// System.out.println(mute);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			return false;
		}

		device.setOnoff(onoff);
		if (onoff == Constants.POWER_ON) {
			device.setProperty1(brightness);
			device.setProperty2(volume);
			device.setProperty3(mute);
		}
		device.setProperty2(volume);
		device.setProperty3(mute);
		return true;
	}

	/**
	 * 保存投影仪开关/亮度状态
	 * 
	 * @param device
	 */
	private void saveStatus(DeviceEntity device) {
		device.setStatus(null);
		device.setCmdstring(null);
		device.setTries(0);
		device.setProperty9("" + System.currentTimeMillis());
		deviceRepository.save(device);
		// System.out.println(device);
	}
}
