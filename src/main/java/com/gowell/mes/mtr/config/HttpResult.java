package com.gowell.mes.mtr.config;

public class HttpResult {
	// 响应的状态码
	private int code;
	
	// 响应的响应体
	private String body;

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	
	public HttpResult(int code, String body) {
		this.code = code;
		this.body = body;
	}
}
