package com.gowell.mes.mtr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gowell.mes.mtr.Result;
import com.gowell.mes.mtr.service.EzvizService;
import com.gowell.mes.mtr.service.HttpApiService;

@RestController
@RequestMapping(path = "/api/mtr/ezviz")
public class EzvizController {
	
	@Autowired
	HttpApiService httpApiService;
	
	@Autowired
	EzvizService ezvizService;
	
	@Transactional
	@RequestMapping("/liveon")
	public Result asynLiveOn() {
		Result result = new Result();
		String liveAddress = ezvizService.openLive();
		result.setData(liveAddress);

		if (StringUtils.isEmpty(liveAddress)) {
			result.setErrCode(-1);
			result.setSuccess(false);
		} else {
			result.setErrCode(0);
			result.setSuccess(true);
		}

		return result;
	}

	@Transactional
	@RequestMapping("/liveoff")
	public Result asynLiveOff() {
		Result result = new Result();
		boolean isOK = ezvizService.closeLive();
		result.setData(isOK);

		if (isOK) {
			result.setErrCode(0);
			result.setSuccess(true);
		} else {
			result.setErrCode(-1);
			result.setSuccess(false);
		}

		return result;
	}
}
