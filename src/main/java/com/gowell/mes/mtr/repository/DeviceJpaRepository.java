package com.gowell.mes.mtr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gowell.mes.mtr.model.DeviceEntity;

public interface DeviceJpaRepository extends JpaRepository<DeviceEntity, Integer> {
	public List<DeviceEntity> findAllByOrderByIdAsc();

	public List<DeviceEntity> findAllByCategory(Integer category);

	public List<DeviceEntity> findAllByCategoryOrderByIdAsc(Integer category);

	public List<DeviceEntity> findAllByNameAndCategory(String name, Integer category);
}
