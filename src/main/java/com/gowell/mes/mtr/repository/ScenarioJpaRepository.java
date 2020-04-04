package com.gowell.mes.mtr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gowell.mes.mtr.model.ScenarioEntity;

public interface ScenarioJpaRepository extends JpaRepository<ScenarioEntity, Integer> {
	public List<ScenarioEntity> findAllByOrderByIdAsc();

	public List<ScenarioEntity> findAllByInusedOrderByIdAsc(Integer inused);
}
