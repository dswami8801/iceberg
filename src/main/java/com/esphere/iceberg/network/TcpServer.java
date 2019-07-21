package com.esphere.iceberg.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class TcpServer {

	private static Logger LOGGER = Logger.getLogger(TcpServer.class);

	private boolean isRunning = false;

	private RequestDelegator delegator;

	public TcpServer(RequestDelegator delegator) {
		this.delegator = delegator;
	}

	public void start(ServerConfig config) {
		LOGGER.info("Server start Initiated");
		isRunning = true;
		doStart(config);

	}

	public void stop() {
		isRunning = false;
	}

	public boolean isRunning() {
		return isRunning;
	}

	private void doStart(ServerConfig config) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(config.getPort());
			LOGGER.debug("Server Socket created");
			System.out.println(serverSocket.getLocalPort());
			System.out.println(serverSocket.getLocalSocketAddress());

		} catch (IOException e) {
			e.printStackTrace();
		}

		while (isRunning) {
			try {
				Socket socket = serverSocket.accept();
				LOGGER.info("New Client Request Accepted");
				LOGGER.info("Server handler " + delegator.getClass());
				delegator.delegate(socket);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("Port alredy in use");
				System.exit(0);
			}
		}
	}

	public static void main(String[] args) {
		ServerConfig config = new ServerConfig();
		config.setPort(9090);
		RequestDelegator delegator = new RequestDelegator(10);
		new TcpServer(delegator).start(config);
	}

}
