package com.gowell.mes.mtr.controller;

import java.awt.AWTException;


import com.gowell.mes.mtr.service.IntegrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gowell.mes.mtr.Result;

@RestController
@RequestMapping(path = "/api/mtr/curtain")
public class CurtainController {
    @Autowired
    IntegrateService integrateService;

    @Transactional
    @RequestMapping("/aeffect")
    public Result asynEffect(@RequestParam(required = true) String effectId) throws AWTException {
        Result result = new Result();
        Integer code = integrateService.asynEffectCurtain(effectId);
        result.codeMatch(code);
        return result;
    }
}
