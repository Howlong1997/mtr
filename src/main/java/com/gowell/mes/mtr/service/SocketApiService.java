package com.gowell.mes.mtr.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.gowell.mes.mtr.Constants;
import com.gowell.mes.mtr.model.DeviceEntity;
import com.gowell.mes.mtr.repository.DeviceJpaRepository;
import com.gowell.mes.mtr.task.DeviceStatus;

@Service
public class SocketApiService implements InitializingBean, DisposableBean {
	@Value("${plc.host}")
	private String host;

	@Value("${plc.port}")
	private int port;

	@Value("${plc.timeout}")
	private int timeout;

	@Value("${plc.tries}")
	private int tries;

	@Value("${plc.interval}")
	private int interval;

	@Autowired
	DeviceJpaRepository deviceRepository;

	private Socket mySocket;

	private String originStatus;

	/**
	 * @return the originStatus
	 */
	public String getOriginStatus() {
		return originStatus;
	}

	/**
	 * 实时执行PLC命令
	 * 
	 * @param cmdString
	 * @return
	 */
	public boolean execute(String cmdString) {
		boolean result = false;
		// Socket socket = openSocket();
		Socket socket = mySocket;
		if (socket != null) {
			// getResponse(socket, 3 * tries);
			sendMessage(cmdString, socket);

			//if (!StringUtils.isEmpty(status1) || !StringUtils.isEmpty(status2))
			result = true;

			// closeSocket(socket);
			// socket = null;
		}
		return result;
	}

	/**
	 * 执行存储的PLC命令
	 */
	public void execute() {
		// System.out.println("configureTasks execute");
		// Socket socket = openSocket();
		Socket socket = mySocket;
		if (socket != null) {
			// String status = getResponse(socket, 3 * tries);
			String status = originStatus;
			if (!execute(socket, status)) {
				// System.out.println("originStatus:" + status);
			}
			// closeSocket(socket);
			// socket = null;
		}
	}

	/**
	 * 打开Socket
	 * 
	 * @return
	 */
	private Socket openSocket() {
		// System.out.println("openSocket");
		Socket socket = null;
		// 创建一个流套接字并将其连接到指定主机上的指定端口号
		try {
			socket = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(host, port);
			socket.connect(socketAddress, 3 * timeout);
		} catch (IOException e) {
			socket = null;
			e.printStackTrace();
		}
		if (socket != null) {
			try {
				socket.setSoTimeout(timeout);
			} catch (SocketException e) {
				// e.printStackTrace();
				closeSocket(socket);
				socket = null;
			}
		}

		return socket;
	}

	/**
	 * 关闭Socket
	 * 
	 * @param socket
	 */
	private void closeSocket(Socket socket) {
		try {
			if (socket != null) {
				// System.out.println("closeSocket");
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行PLC命令
	 * 
	 * @param socket
	 * @param originStatus
	 * @return
	 */
	private boolean execute(Socket socket, String originStatus) {
		boolean inAction = false;
		String cmdString = null;
		String status = null;

		List<DeviceEntity> devices = deviceRepository.findAllByOrderByIdAsc();
		if (!CollectionUtils.isEmpty(devices)) {
			for (DeviceEntity device : devices) {
				if (device.getCategory() == Constants.DEVICE_PROJECTOR)
					continue;
				if (!StringUtils.isEmpty(device.getCmdstring())) {
					inAction = true;
					if (Constants.COMMAND_SETUP.equals(device.getStatus())) {
						if (!StringUtils.isEmpty(cmdString = getCommand(device))) {
							status = sendMessage(cmdString, socket);
							if (!StringUtils.isEmpty(status))
								originStatus = status;
						}
					}
				}
			}

			DeviceStatus instance = new DeviceStatus();
			if (instance.parse(originStatus)) {
				instance.setDevice(devices);
			}
			for (DeviceEntity device : devices) {
				if (device.getCategory() == Constants.DEVICE_PROJECTOR)
					continue;
				device.setProperty9("" + System.currentTimeMillis());
				device.setStatus(null);
				device.setCmdstring(null);
				device.setTries(0);
				deviceRepository.save(device);
			}
		}

		return inAction;
	}

	/**
	 * 生成PLC命令
	 * 
	 * @param device
	 * @return
	 */
	private String getCommand(DeviceEntity device) {
		String cmdString = null;

		switch (device.getCategory()) {
		case Constants.DEVICE_MONITOR:
			cmdString = "S1" + device.getCmdstring() + "0FFFF";
			device.setOnoff(Integer.parseInt(device.getCmdstring()));
			break;
		case Constants.DEVICE_CURTAIN:
			cmdString = "C1" + device.getCmdstring() + "0FFFF";
			device.setOnoff(Integer.parseInt(device.getCmdstring()));
			break;
		case Constants.DEVICE_LAMP:
			cmdString = "L" + device.getProperty1() + device.getCmdstring() + "0FFFF";
			device.setOnoff(Integer.parseInt(device.getCmdstring()));
			break;
		case Constants.DEVICE_GLASS:
			if (device.getCmdstring().startsWith("T")) {
				cmdString = "G" + device.getCmdstring() + "1FFFF";
				//System.out.println(cmdString);
				device.setProperty1(device.getCmdstring().substring(1));
				device.setOnoff("0".equals(device.getProperty1()) ? 0 : 1);
			} else {
				cmdString = "G" + device.getProperty2() + device.getCmdstring() + "0FFFF";
				device.setOnoff(Integer.parseInt(device.getCmdstring()));
			}
			break;
		}

		return cmdString;
	}

	/**
	 * 取得响应
	 * 
	 * @param socket
	 * @param maxTries
	 * @return
	 */
	private String getResponse(Socket socket, int maxTries) {
		String result = null;

		try {
			InputStream inputStream = socket.getInputStream();
			byte[] bytes = new byte[1024];
			int len = -1;

			while (maxTries-- > 0) {
				result = null;
				try {
					if ((len = inputStream.read(bytes)) != -1) {
						result = new String(bytes, 0, len, "UTF-8");
						originStatus = result;
					}
				} catch (SocketTimeoutException e) {
				}

				if (!StringUtils.isEmpty(result) || maxTries == 0)
					break;

				try {
					Thread.sleep(interval);
				} catch (Exception e) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 发送PLC命令并获得响应
	 * 
	 * @param cmdString
	 * @param socket
	 * @return
	 */
	private String sendMessage(String cmdString, Socket socket) {
		String result = null;
		// System.out.println("PLC Request: " + cmdString);

		int maxTries = tries;
		while (maxTries-- > 0) {
			try {
				// 向服务器端发送数据
				OutputStream outputStream = socket.getOutputStream();
				outputStream.write(cmdString.getBytes("UTF-8"));
				outputStream.flush();
				// System.out.println(host + ":" + port + ":" + cmdString + ":");

				// 读取服务器端数据
				result = getResponse(socket, 1);

				try {
					Thread.sleep(interval);
				} catch (Exception e) {
				}

				if (!StringUtils.isEmpty(result))
					break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// System.out.println("PLC Response: " + result);
		return result;
	}

	@Override
	public void destroy() throws Exception {
		if (mySocket != null) {
			// System.out.println("destroy closeSocket");
			closeSocket(mySocket);
			mySocket = null;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// System.out.println("afterPropertiesSet openSocket");
		mySocket = openSocket();
		if (mySocket != null) {
			originStatus = getResponse(mySocket, 3 * tries);
			// System.out.println("status:" + originStatus);
		}		
	}
}
