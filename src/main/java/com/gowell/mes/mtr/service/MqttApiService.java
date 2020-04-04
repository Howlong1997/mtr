/**
 * 
 */
package com.gowell.mes.mtr.service;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.Charset;

import javax.annotation.Resource;

/**
 * @author servi
 *
 */
@Service
public class MqttApiService implements InitializingBean, DisposableBean {
	@Value("${mqtt.url}")
	private String url;
	// private String url = "tcp://127.0.0.1:1883";

	@Value("${mqtt.clientId}")
	private String clientId;

	@Value("${mqtt.receiveTopic}")
	private String receiveTopic;

	@Value("${mqtt.sendTopic}")
	private String sendTopic;

	private int qos = 1;

	private MemoryPersistence memoryPersistence = null;

	private MqttConnectOptions mqttConnectOptions = null;

	private MqttClient mqttClient;

	@Resource
	SocketApiService socketApiService;

	@Override
	public void destroy() throws Exception {
		if (mqttClient != null) {
			mqttClient.unsubscribe(receiveTopic);
			mqttClient.close();
			mqttClient = null;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("url : " + url);
		System.out.println("clientId : " + clientId);
		System.out.println("receiveTopic : " + receiveTopic);
		System.out.println("sendTopic : " + sendTopic);
		try {
			memoryPersistence = new MemoryPersistence();
			// HOST_MQ为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
			mqttClient = new MqttClient(url, clientId, memoryPersistence);

			// MQTT的连接设置
			mqttConnectOptions = new MqttConnectOptions();
			// 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接
			mqttConnectOptions.setCleanSession(false);
			// 设置连接的用户名
			// mqttConnectOptions.setUserName(username);
			// 设置连接的密码
			// mqttConnectOptions.setPassword(password.toCharArray());
			mqttConnectOptions.setConnectionTimeout(50);
			// 设置会话心跳时间 单位为秒 服务器会每隔90秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
			mqttConnectOptions.setKeepAliveInterval(90);

			mqttClient.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable cause) {
					cause.printStackTrace();
					reconnect();
				}

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					System.out.println("Client 接收消息主题 : " + topic);
					System.out.println("Client 接收消息Qos : " + message.getQos());
					System.out.println("Client 接收消息内容 : " + new String(message.getPayload()));

					JSONObject json = new JSONObject(new String(message.getPayload()));
					if (socketApiService.execute(json.getString("command"))) {
						publish(socketApiService.getOriginStatus());
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
				}
			});
			mqttClient.connect(mqttConnectOptions);
			mqttClient.subscribe(receiveTopic, qos);
			System.out.println("connect : " + mqttClient.isConnected());
		} catch (MqttException e) {
			mqttClient = null;
			e.printStackTrace();
		}
	}

	public void reconnect() {
		synchronized (this) {
			if (null != mqttClient && !(mqttClient.isConnected())) {
				while (true) {
					try {
						// mqttClient.reconnect();
						mqttClient.disconnect();
					} catch (MqttException e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(1000);
						mqttClient.connect(mqttConnectOptions);
						mqttClient.subscribe(receiveTopic, qos);
						System.out.println("reconnect : " + mqttClient.isConnected());
						publish("reconnect", clientId);
						break;
					} catch (InterruptedException | MqttException e) {
						// mqttClient = null;
						e.printStackTrace();
						continue;
					}
				}
			}
		}
	}

	private void publish(String topic, String msg) throws MqttException, MqttPersistenceException {
		synchronized (this) {
			// MqttMessage msgPublish = new
			// MqttMessage(msg.getBytes(Charset.forName("utf-8")));
			// msgPublish.setQos(qos);
			// mqttClient.publish(topic, msgPublish);
			MqttMessage message = new MqttMessage();
			message.setPayload(msg.getBytes(Charset.forName("utf-8")));
			message.setQos(qos);

			MqttTopic mqttTopic = mqttClient.getTopic(topic);
			MqttDeliveryToken token = mqttTopic.publish(message);
			token.waitForCompletion(3000L);
		}
	}

	private void publish(String msg) throws MqttException, MqttPersistenceException {
		publish(sendTopic, msg);
	}
}
