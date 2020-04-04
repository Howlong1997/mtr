package com.gowell.mes.mtr.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gowell.mes.mtr.utils.JsonUtils;

@Entity
@Table(name = "mtr_device")
public class DeviceEntity implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2658996887671565286L;

	@Override
	public String toString() {
		return JsonUtils.serializeWithoutException(this);
	}

	@Id
	private Integer id;

	private String name;

	private Integer category;

	private Integer onoff;

	@Column(nullable = true)
	private String property1;

	@Column(nullable = true)
	private String property2;

	@Column(nullable = true)
	private String property3;

	@Column(nullable = true)
	private String property4;

	@Column(nullable = true)
	private String property5;

	@Column(nullable = true)
	private String property6;

	@Column(nullable = true)
	private String property7;

	@Column(nullable = true)
	private String property8;

	@Column(nullable = true)
	private String property9;

	@Column(nullable = true)
	private String status;

	@Column(nullable = true)
	private String cmdstring;

	private Integer tries;

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
	 * @return the onoff
	 */
	public Integer getOnoff() {
		return onoff;
	}

	/**
	 * @param onoff the onoff to set
	 */
	public void setOnoff(Integer onoff) {
		this.onoff = onoff;
	}

	/**
	 * @return the property1
	 */
	public String getProperty1() {
		return property1;
	}

	/**
	 * @param property1 the property1 to set
	 */
	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	/**
	 * @return the property2
	 */
	public String getProperty2() {
		return property2;
	}

	/**
	 * @param property2 the property2 to set
	 */
	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	/**
	 * @return the property3
	 */
	public String getProperty3() {
		return property3;
	}

	/**
	 * @param property3 the property3 to set
	 */
	public void setProperty3(String property3) {
		this.property3 = property3;
	}

	/**
	 * @return the property4
	 */
	public String getProperty4() {
		return property4;
	}

	/**
	 * @param property4 the property4 to set
	 */
	public void setProperty4(String property4) {
		this.property4 = property4;
	}

	/**
	 * @return the property5
	 */
	public String getProperty5() {
		return property5;
	}

	/**
	 * @param property5 the property5 to set
	 */
	public void setProperty5(String property5) {
		this.property5 = property5;
	}

	/**
	 * @return the property6
	 */
	public String getProperty6() {
		return property6;
	}

	/**
	 * @param property6 the property6 to set
	 */
	public void setProperty6(String property6) {
		this.property6 = property6;
	}

	/**
	 * @return the property7
	 */
	public String getProperty7() {
		return property7;
	}

	/**
	 * @param property7 the property7 to set
	 */
	public void setProperty7(String property7) {
		this.property7 = property7;
	}

	/**
	 * @return the property8
	 */
	public String getProperty8() {
		return property8;
	}

	/**
	 * @param property8 the property8 to set
	 */
	public void setProperty8(String property8) {
		this.property8 = property8;
	}

	/**
	 * @return the property9
	 */
	public String getProperty9() {
		return property9;
	}

	/**
	 * @param property9 the property9 to set
	 */
	public void setProperty9(String property9) {
		this.property9 = property9;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the cmdstring
	 */
	public String getCmdstring() {
		return cmdstring;
	}

	/**
	 * @param cmdstring the cmdstring to set
	 */
	public void setCmdstring(String cmdstring) {
		this.cmdstring = cmdstring;
	}

	/**
	 * @return the tries
	 */
	public Integer getTries() {
		return tries;
	}

	/**
	 * @param tries the tries to set
	 */
	public void setTries(Integer tries) {
		this.tries = tries;
	}
}
