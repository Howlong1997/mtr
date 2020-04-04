package com.gowell.mes.mtr.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gowell.mes.mtr.Result;
import com.gowell.mes.mtr.model.ScenarioEntity;
import com.gowell.mes.mtr.repository.ScenarioJpaRepository;

@RestController
@RequestMapping(path = "/api/mtr/mode")
public class MeetingModeController {
	@Autowired
	ScenarioJpaRepository scenerioRepository;

	@RequestMapping("/list")
	public Result list() {
		List<ScenarioEntity> scenarioes = scenerioRepository.findAllByOrderByIdAsc();
		Result result = new Result();
		result.setData(scenarioes);
		return result;
	}

	@RequestMapping("/detail")
	public Result getDetail(@RequestParam(required = true) int id) {
		Optional<ScenarioEntity> scenario = scenerioRepository.findById(id);
		if (!scenario.isPresent()) {
			return new Result(-1);
		}
		Result result = new Result();
		result.setData(scenario.get());
		return result;
	}

	@Transactional
	@RequestMapping("/delete")
	public Result delete(@RequestParam(required = true) int id) {
		scenerioRepository.deleteById(id);
		return new Result();
	}

	@Transactional
	@RequestMapping("/update")
	public Result update(ScenarioEntity data) {
		Optional<ScenarioEntity> scenario = scenerioRepository.findById(data.getId());
		if (!scenario.isPresent()) {
			return new Result(-1);
		}
		scenerioRepository.save(data);
		return new Result();
	}

	@Transactional
	@RequestMapping("/create")
	public Result create(ScenarioEntity data) {
		data.setId(null);
		scenerioRepository.save(data);
		return new Result();
	}
}
