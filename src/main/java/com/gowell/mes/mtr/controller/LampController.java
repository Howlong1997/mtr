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
        Integer code = integrateService.asynPowerOn(lampId);
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/apoweroff")
    public Result asynPowerOff(@RequestParam(required = true) String lampId) {
        Result result = new Result();
        Integer code = integrateService.asynPowerOff(lampId);
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/agroupon")
    public Result asynGroupOn(@RequestParam(required = true) String groupId) {
        Result result = new Result();
        Integer code = integrateService.asynGroupOn(groupId);
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/agroupoff")
    public Result asynGroupOnOff(@RequestParam(required = true) String groupId) {
        Result result = new Result();
        Integer code = integrateService.asynGroupOnOff(groupId);
        result.codeMatch(code);
        return result;
    }
}
