/**
 * Result.java
 */
package com.gowell.mes.mtr;

import java.io.Serializable;

import com.gowell.mes.mtr.utils.JsonUtils;

/**
 * @author Billy
 */
public class Result implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4260588859031088937L;

	@Override
	public String toString() {
		return JsonUtils.serializeWithoutException(this);
	}

	private boolean success;
	private int errCode;
	private String errMsg;
	private Object data;

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the errCode
	 */
	public int getErrCode() {
		return errCode;
	}

	/**
	 * @param errCode the errCode to set
	 */
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	/**
	 * @return the errMsg
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * @param errMsg the errMsg to set
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	public Result() {
		success = true;
		errCode = 0;
		errMsg = null;
		data = null;
	}

	public Result(int errCode) {
		success = (errCode == 0);
		this.errCode = errCode;
		errMsg = null;
		data = null;
	}

	public Result(String errMsg) {
		this.errMsg = errMsg;
		data = null;

		if (errMsg != null && !errMsg.isEmpty())
			success = false;
		else
			success = true;
		errCode = (success ? 0 : -1);
	}

	public Result(int errCode, String errMsg) {
		success = (errCode == 0);
		this.errCode = errCode;
		this.errMsg = errMsg;
		data = null;
	}

	public Result(boolean success, int errCode, String errMsg, Object data) {
		this.success = success;
		this.errCode = errCode;
		this.errMsg = errMsg;
		this.data = data;
	}

	public Result(Object data) {
		success = true;
		errCode = 0;
		errMsg = null;
		this.data = data;
	}
}
