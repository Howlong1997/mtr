package com.gowell.mes.mtr.controller;

import com.gowell.mes.mtr.service.IntegrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gowell.mes.mtr.Result;

@RestController
@RequestMapping(path = "/api/mtr/monitor")
public class MonitorController {
    @Autowired
    IntegrateService integrateService;

    @Transactional
    @RequestMapping("/apoweron")
    public Result asynPowerOn() {
        Result result = new Result();
        Integer code = integrateService.asynPowerOn();
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/apoweroff")
    public Result asynPowerOff() {
        Result result = new Result();
        Integer code = integrateService.asynPowerOff();
        result.codeMatch(code);
        return result;
    }
}
