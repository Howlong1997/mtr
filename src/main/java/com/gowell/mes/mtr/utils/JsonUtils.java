/**
 * JsonUtil.java
 */
package com.gowell.mes.mtr.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Billy
 */
public class JsonUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

	private static ObjectMapper mapper = new ObjectMapper();

	public static String serializeWithoutException(Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (IOException e) {
			LOGGER.error("Failed to serialize in JSON.", e);
			return null;
		}
	}
	
	public ObjectMapper getMapper() {
		return mapper;
	}
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/DD HH:mm:ss.SSS");

	public static String formatTime() {
		return sdf.format(System.currentTimeMillis());
	}
}
