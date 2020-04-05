package com.gowell.mes.mtr.controller;


import com.gowell.mes.mtr.service.IntegrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gowell.mes.mtr.Result;

@RestController
@RequestMapping(path = "/api/mtr/lamp")
public class LampController {
    @Autowired
    IntegrateService integrateService;


    @Transactional
    @RequestMapping("/apoweron")
    public Result asynPowerOn(@RequestParam(required = true) String lampId) {
        Result result = new Result();
        Integer code = integrateService.asynPowerOnLamp(lampId);
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/apoweroff")
    public Result asynPowerOff(@RequestParam(required = true) String lampId) {
        Result result = new Result();
        Integer code = integrateService.asynPowerOffLamp(lampId);
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/agroupon")
    public Result asynGroupOn(@RequestParam(required = true) String groupId) {
        Result result = new Result();
        Integer code = integrateService.asynGroupOnLamp(groupId);
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/agroupoff")
    public Result asynGroupOnOff(@RequestParam(required = true) String groupId) {
        Result result = new Result();
        Integer code = integrateService.asynGroupOnOffLamp(groupId);
        result.codeMatch(code);
        return result;
    }
}
