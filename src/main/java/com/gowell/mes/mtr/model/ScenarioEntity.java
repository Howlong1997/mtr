package com.gowell.mes.mtr.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.gowell.mes.mtr.utils.JsonUtils;

@Entity
@Table(name = "mtr_scenario")
public class ScenarioEntity implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2090158673376155162L;

	@Override
	public String toString() {
		return JsonUtils.serializeWithoutException(this);
	}

	@Id
	@SequenceGenerator(name = "mtr_scenario_seq", sequenceName = "mtr_scenario_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mtr_scenario_seq")
	private Integer id;

	private Integer category;

	private Integer inused;

	private String name;

	@Column(nullable = true)
	private String monitor;

	@Column(nullable = true)
	private String lamp;

	@Column(nullable = true)
	private String projector;

	@Column(nullable = true)
	private String curtain;

	@Column(nullable = true)
	private String glass;

	@Column(nullable = true)
	private String plccmd;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the category
	 */
	public Integer getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Integer category) {
		this.category = category;
	}

	/**
	 * @return the inused
	 */
	public Integer getInused() {
		return inused;
	}

	/**
	 * @param inused the inused to set
	 */
	public void setInused(Integer inused) {
		this.inused = inused;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the monitor
	 */
	public String getMonitor() {
		return monitor;
	}

	/**
	 * @param monitor the monitor to set
	 */
	public void setMonitor(String monitor) {
		this.monitor = monitor;
	}

	/**
	 * @return the lamp
	 */
	public String getLamp() {
		return lamp;
	}

	/**
	 * @param lamp the lamp to set
	 */
	public void setLamp(String lamp) {
		this.lamp = lamp;
	}

	/**
	 * @return the projector
	 */
	public String getProjector() {
		return projector;
	}

	/**
	 * @param projector the projector to set
	 */
	public void setProjector(String projector) {
		this.projector = projector;
	}

	/**
	 * @return the curtain
	 */
	public String getCurtain() {
		return curtain;
	}

	/**
	 * @param curtain the curtain to set
	 */
	public void setCurtain(String curtain) {
		this.curtain = curtain;
	}

	/**
	 * @return the glass
	 */
	public String getGlass() {
		return glass;
	}

	/**
	 * @param glass the glass to set
	 */
	public void setGlass(String glass) {
		this.glass = glass;
	}

	/**
	 * @return the plccmd
	 */
	public String getPlccmd() {
		return plccmd;
	}

	/**
	 * @param plccmd the plccmd to set
	 */
	public void setPlccmd(String plccmd) {
		this.plccmd = plccmd;
	}
}
