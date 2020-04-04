package com.gowell.mes.mtr.controller;

import java.awt.AWTException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gowell.mes.mtr.Constants;
import com.gowell.mes.mtr.Result;
import com.gowell.mes.mtr.model.DeviceEntity;
import com.gowell.mes.mtr.repository.DeviceJpaRepository;
import com.gowell.mes.mtr.utils.StatusManager;

@RestController
@RequestMapping(path = "/api/mtr/curtain")
public class CurtainController {
	@Autowired
	DeviceJpaRepository deviceRepository;
	
	@Value("${curtain.delay}")
	private Integer delay;
	
	private static Boolean flag=true;

	@Transactional
	@RequestMapping("/aeffect")
	public Result asynEffect(@RequestParam(required = true) String effectId) throws AWTException {
		List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_CURTAIN);
		if (CollectionUtils.isEmpty(devices)) {
			return new Result(-1);
		}
		if(!flag) {
			return new Result(-2, "幕布正在运作，请稍后再试！");
		}

		DeviceEntity device = devices.get(0);
//		 if ((!StringUtils.isEmpty(device.getCmdstring())) || (!"0".equals(device.getProperty1()))) {
//		      return new Result(-2);
//		  }

		if(device.getOnoff() == Integer.valueOf(effectId)) {
			return new Result(-2, "幕布已经为"+(device.getOnoff()==1?"上升":"下降")+"状态");
		}

//		if (!effectId.equals("" + device.getOnoff())) {
//			device.setProperty8("" + System.currentTimeMillis());
//			device.setStatus(Constants.COMMAND_SETUP);
//			device.setCmdstring(effectId);
//			device.setTries(3);
//			deviceRepository.save(device);
//		}
		operation(effectId,device);
		return new Result(0,"操作成功！");
	}
	
	public synchronized void operation(String effectId, DeviceEntity device) throws AWTException {
		Timer timer = new Timer();
		flag = false;
		device.setProperty8("" + System.currentTimeMillis());
		device.setStatus(Constants.COMMAND_SETUP);
		device.setCmdstring(effectId);
		device.setTries(3);
		deviceRepository.save(device);
		new StatusManager().getStatus(deviceRepository);
		timer.schedule(new TimerTask() {
			public void run() {
				flag = true;
				this.cancel();
			}
		}, delay);
	}
}
