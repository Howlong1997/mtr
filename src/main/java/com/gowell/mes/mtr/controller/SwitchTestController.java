package com.gowell.mes.mtr.controller;

import com.gowell.mes.mtr.Result;
import com.gowell.mes.mtr.service.IntegrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@RequestMapping(path = "/api/mtr/switchTest")
public class SwitchTestController {
    @Autowired
    IntegrateService integrateService;

    @Transactional
    @RequestMapping("/test")
    public Result test(Integer testId, Integer power, String names) throws AWTException {
        Result result = new Result();
        switch (testId) {
            case 1:
                result.setErrMsg(integrateService.masterSwitch(power));
                break;
            case 2:
                result.setErrMsg(integrateService.mainLampSwitch(power, names));
                break;
            case 3:
                result.setErrMsg(integrateService.largeMeetSwitch(power));
                break;
            case 4:
                result.setErrMsg(integrateService.smallMeetSwitch(power));
                break;
        }
        return result;
    }
}
