package com.gowell.mes.mtr.service;

import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gowell.mes.mtr.config.HttpResult;
import com.gowell.mes.mtr.service.HttpApiService;

@Service
public class EzvizService {
	@Resource
	private HttpApiService httpApiService;

	@Value("${ezviz.Host}")
	private String host;

	@Value("${ezviz.Content-Type}")
	private String contentType;

	@Value("${ezviz.source}")
	private String source;

	@Value("${ezviz.liveon}")
	private String liveon;

	@Value("${ezviz.liveoff}")
	private String liveoff;

	@Value("${ezviz.liveaddress}")
	private String liveaddress;

	@Value("${ezviz.livetoken}")
	private String livetoken;

	@Value("${ezviz.appKey}")
	private String appKey;

	@Value("${ezviz.appSecret}")
	private String appSecret;

	public String openLive() {
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put("Host", host);
		headers.put("Content-Type", contentType);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("accessToken", getEzvizToken());
		map.put("source", source);

		try {
			JSONObject json = getBody(httpApiService.doPost(liveon, headers, map));
			int ret = Integer.parseInt(json.getJSONArray("data").getJSONObject(0).getString("ret"));
			if (ret == HttpStatus.SC_OK || ret == 60062) {
				json = getBody(httpApiService.doPost(liveaddress, headers, map));
				return json.getJSONArray("data").getJSONObject(0).getString("hls");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean closeLive() {
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put("Host", host);
		headers.put("Content-Type", contentType);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("accessToken", getEzvizToken());
		map.put("source", source);

		try {
			JSONObject json = getBody(httpApiService.doPost(liveoff, headers, map));
			int ret = Integer.parseInt(json.getJSONArray("data").getJSONObject(0).getString("ret"));
			if (ret == HttpStatus.SC_OK || ret == 60063) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private String getEzvizToken() {
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put("Host", host);
		headers.put("Content-Type", contentType);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("appKey", appKey);
		map.put("appSecret", appSecret);

		try {
			JSONObject json = getBody(httpApiService.doPost(livetoken, headers, map));
			return json.getJSONObject("data").getString("accessToken");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private JSONObject getBody(HttpResult response) {
		if (response.getCode() == HttpStatus.SC_OK) {
			JSONObject json = new JSONObject(response.getBody());
			if (Integer.parseInt(json.getString("code")) == HttpStatus.SC_OK) {
				return json;
			}
		}
		return null;
	}
}
