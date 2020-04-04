package com.gowell.mes.mtr.controller;


import com.gowell.mes.mtr.service.IntegrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gowell.mes.mtr.Result;

@RestController
@RequestMapping(path = "/api/mtr/glass")
public class GlassController {
    @Autowired
    IntegrateService integrateService;

    @Transactional
    @RequestMapping("/apoweron")
    public Result asynPowerOn(@RequestParam(required = true) String glassId) {
        Result result = new Result();
        Integer code = integrateService.asynPowerOnGlass(glassId);
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/apoweroff")
    public Result asynPowerOff(@RequestParam(required = true) String glassId) {
        Result result = new Result();
        Integer code = integrateService.asynPowerOffGlass(glassId);
        result.codeMatch(code);
        return result;
    }

    @RequestMapping("/aeffectoff")
    public Result asynEffectOff() {
        Result result = new Result();
        Integer code = integrateService.asynEffectOff();
        result.codeMatch(code);
        return result;
    }

    @RequestMapping("/aeffecton")
    public Result asynEffectOn(@RequestParam(required = true) String effectId) {
        Result result = new Result();
        Integer code = integrateService.asynEffectOn(effectId);
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/agroupon")
    public Result asynGroupOn(@RequestParam(required = true) String groupId) {
        Result result = new Result();
        Integer code = integrateService.asynGroupOnGlass(groupId);
        result.codeMatch(code);
        return result;
    }

    @Transactional
    @RequestMapping("/agroupoff")
    public Result asynGroupOff(@RequestParam(required = true) String groupId) {
        Result result = new Result();
        Integer code = integrateService.asynGroupOffGlass(groupId);
        result.codeMatch(code);
        return result;
    }
}
