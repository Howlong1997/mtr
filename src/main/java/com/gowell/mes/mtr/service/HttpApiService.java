package com.gowell.mes.mtr.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gowell.mes.mtr.config.HttpResult;

@Service
public class HttpApiService {
	@Autowired
	CloseableHttpClient httpClient;

	@Autowired
	RequestConfig config;

	/**
	 * 不带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String doGet(String url) throws Exception {
		// 声明 http get 请求
		HttpGet httpGet = new HttpGet(url);
		//System.out.println("Get Request Cookie:");
		// System.out.println(httpGet.getHeaders("Cookie"));
		//HttpUtils.showHeaders(httpGet.getAllHeaders());
		// 装载配置信息
		httpGet.setConfig(config);
		// 发起请求
		CloseableHttpResponse response = httpClient.execute(httpGet);
		// 判断状态码是否为200
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			//System.out.println("Get Response Cookie:");
			// System.out.println(response.getHeaders("Cookie"));
			//HttpUtils.showHeaders(response.getAllHeaders());
			if (response.containsHeader("Set-Cookie")) {
				//System.out.println("Set-Cookie");
			}
			// 返回响应体的内容
			return EntityUtils.toString(response.getEntity(), "utf-8");
		}
		return null;
	}

	/**
	 * 带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
	 * 
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String doGet(String url, Map<String, Object> map) throws Exception {
		URIBuilder uriBuilder = new URIBuilder(url);
		if (map != null) {
			// 遍历map,拼接请求参数
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
			}
		}
		// 调用不带参数的get请求
		return doGet(uriBuilder.build().toString());
	}

	/**
	 * 带参数的post请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public HttpResult doPost(String url, Map<String, Object> headers, Map<String, Object> map) throws Exception {
		// 声明httpPost请求
		HttpPost httpPost = new HttpPost(url);
		// 加入配置信息
		httpPost.setConfig(config);
		if (headers != null) {
			for (Map.Entry<String, Object> entry : headers.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue().toString());
			}
		}
		//System.out.println("Post Request Cookie:");
		// System.out.println(httpPost.getHeaders("Cookie"));
		//HttpUtils.showHeaders(httpPost.getAllHeaders());

		// 判断map是否为空，不为空则进行遍历，封装from表单对象
		if (map != null) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
			}

			// 构造from表单对象
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, "utf-8");
			// 把表单放到post里
			httpPost.setEntity(urlEncodedFormEntity);
		}

		// 发起请求
		CloseableHttpResponse response = httpClient.execute(httpPost);
		//System.out.println("Post Response Cookie:");
		// System.out.println(response.getHeaders("Cookie"));
		//HttpUtils.showHeaders(response.getAllHeaders());
		if (response.containsHeader("Set-Cookie")) {
			//System.out.println("Set-Cookie");
			return new HttpResult(HttpStatus.SC_INTERNAL_SERVER_ERROR,
					EntityUtils.toString(response.getEntity(), "utf-8"));
		}
		return new HttpResult(response.getStatusLine().getStatusCode(),
				EntityUtils.toString(response.getEntity(), "utf-8"));
	}

	/**
	 * 不带参数post请求
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public HttpResult doPost(String url, Map<String, Object> headers) throws Exception {
		return doPost(url, headers, null);
	}
}
